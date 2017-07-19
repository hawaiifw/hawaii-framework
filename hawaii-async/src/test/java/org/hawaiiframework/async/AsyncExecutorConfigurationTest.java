package org.hawaiiframework.async;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.model.ExecutorProperties;
import org.hawaiiframework.async.model.SystemProperties;
import org.hawaiiframework.async.model.TaskProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AsyncExecutorConfigurationTest {
    private AsyncExecutorConfiguration configuration;

    private ExecutorConfigurationProperties properties = new ExecutorConfigurationProperties();

    @Mock
    private AsyncPropertiesLoader loader;

    private DefaultListableBeanFactory beanFactory;
    private ExecutorProperties defaultExecutorProperties;

    @Before
    public void setup() throws Exception {
        beanFactory = new DefaultListableBeanFactory();
        when(loader.loadProperties()).thenReturn(properties);
        configuration = new AsyncExecutorConfiguration();

        defaultExecutorProperties = addExecutorProperties("default", 10, 10, 1, 60);
        properties.setDefaultExecutor(defaultExecutorProperties.getName());

        properties.setAsyncTimeoutExecutorPoolSize(3);

        configuration.setAsyncPropertiesLoader(loader);
    }

    @Test
    public void thatDefaultExecutorIsCreated() throws Exception {
        doIt();
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) beanFactory.getBean(defaultExecutorProperties.getName());
        assertTrue(executor == defaultExecutor);
        final ScheduledThreadPoolExecutor asyncTimeoutExecutor = (ScheduledThreadPoolExecutor) beanFactory.getBean("asyncTimeoutExecutor");
        assertNotNull(asyncTimeoutExecutor);
        assertEquals(properties.getAsyncTimeoutExecutorPoolSize(), (Integer) asyncTimeoutExecutor.getCorePoolSize());
        assertEquals(defaultExecutorProperties.getCorePoolSize(), (Integer) executor.getCorePoolSize());
        assertEquals(defaultExecutorProperties.getMaxPoolSize(), (Integer) executor.getMaxPoolSize());
        assertEquals(defaultExecutorProperties.getKeepAliveTime(), (Integer) executor.getKeepAliveSeconds());
        assertEquals(defaultExecutorProperties.getMaxPendingRequests(), (Integer) executor.getThreadPoolExecutor().getQueue().remainingCapacity());
        assertEquals(defaultExecutorProperties.getName() + "-", executor.getThreadNamePrefix());
    }

    private void doIt() {
        configuration.postProcessBeanDefinitionRegistry(beanFactory);
        configuration.postProcessBeanFactory(beanFactory);
    }

    /*
     * Configure a system with a call, without specifying executors on either level.
     * The expectation is that the task will default to the default executor.
     * This is tested by enqueueing a task and checking that the active count on the
     * default executor increases.
     */
    @Test
    public void thatCallExecutorDefaultsToDefaultSettings() throws Exception {
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");
        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-espresso");
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor taskExecutor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());
        assertNotNull(taskExecutor);
        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);
        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        assertEquals(0, defaultExecutor.getActiveCount());
        taskExecutor.execute(() -> {
            try {
                testLatch.countDown();
                taskLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        testLatch.await();
        assertEquals(1, defaultExecutor.getActiveCount());
        taskLatch.countDown();
    }

    private ExecutorProperties addExecutorProperties(final String defaultExecutorName, int corePoolSize, int maxPoolSize, int maxPendingRequests, int keepAliveSeconds) {
        ExecutorProperties executorProperties = new ExecutorProperties();
        executorProperties.setName(defaultExecutorName);
        executorProperties.setCorePoolSize(corePoolSize);
        executorProperties.setMaxPoolSize(maxPoolSize);
        executorProperties.setMaxPendingRequests(maxPendingRequests);
        executorProperties.setKeepAliveTime(keepAliveSeconds);
        properties.addExecutor(executorProperties);
        return executorProperties;
    }

}
