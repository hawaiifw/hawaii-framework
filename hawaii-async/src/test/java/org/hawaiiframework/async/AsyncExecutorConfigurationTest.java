package org.hawaiiframework.async;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.model.ExecutorProperties;
import org.hawaiiframework.async.model.SystemProperties;
import org.hawaiiframework.async.model.TaskProperties;
import org.hawaiiframework.async.statistics.ExecutorStatisticsView;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Rule
    public ExpectedException thrown= ExpectedException.none();

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
        assertEquals(defaultExecutorProperties.getMaxPendingRequests(),
                (Integer) executor.getThreadPoolExecutor().getQueue().remainingCapacity());
        assertEquals(defaultExecutorProperties.getName() + "-", executor.getThreadNamePrefix());
    }

    private void doIt() {
        configuration.postProcessBeanDefinitionRegistry(beanFactory);
        configuration.postProcessBeanFactory(beanFactory);
    }

    /*
     * Configure a system with a task, without specifying executors on either level.
     * The expectation is that the task will default to the default executor.
     * This is tested by enqueueing a task and checking that the active count on the
     * default executor increases.
     */
    @Test
    public void thatSystemExecutorDefaultsToDefaultSettings() throws Exception {
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

        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        assertEquals(0, defaultExecutor.getActiveCount());

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);
        taskExecutor.execute(createRunnable(taskLatch, testLatch, null));

        testLatch.await();
        assertEquals(1, defaultExecutor.getActiveCount());

        taskLatch.countDown();
    }

    /*
    * Configure a system with a task, with specifying an executors for the system but not the task.
    * The expectation is that the task will default to the system's executor.
    * This is tested by enqueueing a task and checking that the active count on the
    * default executor increases.
    */
    @Test
    public void thatSystemExecutorIsUsedForTaskWithoutExecutor() throws Exception {
        String systemExecutorName = "coffee-bar-executor";
        addExecutorProperties(systemExecutorName, 10, 10, 1, 60);

        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");
        systemProperties.setDefaultExecutor(systemExecutorName);
        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-mocha");
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor executor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());
        assertNotNull(executor);


        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        ThreadPoolTaskExecutor systemExecutor = (ThreadPoolTaskExecutor) beanFactory.getBean(systemExecutorName);
        assertEquals(0, defaultExecutor.getActiveCount());
        assertEquals(0, systemExecutor.getActiveCount());

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);
        executor.execute(createRunnable(taskLatch, testLatch, null));
        testLatch.await();

        assertEquals(0, defaultExecutor.getActiveCount());
        assertEquals(1, systemExecutor.getActiveCount());

        taskLatch.countDown();
    }

    /*
    * Configure a system with a task, with specifying executors on each level (global, system and task).
    * The expectation is that the task will use it's own executor.
    * This is tested by enqueueing a task and checking that the active count on the
    * default executor increases.
    */
    @Test
    public void thatTaskExecutorIsUsedWhenConfigured() throws Exception {
        String systemExecutorName = "coffee-bar-executor";
        addExecutorProperties(systemExecutorName, 10, 10, 1, 60);

        String taskExecutorName = "task-executor";
        addExecutorProperties(taskExecutorName, 9, 18, 2, 63);

        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");
        systemProperties.setDefaultExecutor(systemExecutorName);
        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-mocha");
        taskProperties.setExecutor(taskExecutorName);
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);

        doIt();

        DelegatingExecutor executor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());
        assertNotNull(executor);


        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        ThreadPoolTaskExecutor systemExecutor = (ThreadPoolTaskExecutor) beanFactory.getBean(systemExecutorName);
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) beanFactory.getBean(taskExecutorName);
        assertEquals(0, defaultExecutor.getActiveCount());
        assertEquals(0, systemExecutor.getActiveCount());
        assertEquals(0, taskExecutor.getActiveCount());

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);
        executor.execute(createRunnable(taskLatch, testLatch, null));
        testLatch.await();

        assertEquals(0, defaultExecutor.getActiveCount());
        assertEquals(0, systemExecutor.getActiveCount());
        assertEquals(1, taskExecutor.getActiveCount());

        taskLatch.countDown();
    }

    private Runnable createRunnable(final CountDownLatch taskLatch, final CountDownLatch testLatch,
            final TaskAbortStrategy taskAbortStrategy) {
        return () -> {
            try {
                SharedTaskContextHolder.setTaskAbortStrategy(taskAbortStrategy);
                testLatch.countDown();
                taskLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private ExecutorProperties addExecutorProperties(final String executorName, int corePoolSize, int maxPoolSize, int maxPendingRequests,
            int keepAliveSeconds) {
        ExecutorProperties executorProperties = new ExecutorProperties();
        executorProperties.setName(executorName);
        executorProperties.setCorePoolSize(corePoolSize);
        executorProperties.setMaxPoolSize(maxPoolSize);
        executorProperties.setMaxPendingRequests(maxPendingRequests);
        executorProperties.setKeepAliveTime(keepAliveSeconds);
        properties.addExecutor(executorProperties);
        return executorProperties;
    }


    @Test
    public void testTaskTimeoutGuardIsRemovedUponNormalExecution() throws Exception {
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");

        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-espresso");
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor taskExecutor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());

        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        assertEquals(0, defaultExecutor.getActiveCount());

        ScheduledThreadPoolExecutor asyncTimeoutExecutor = (ScheduledThreadPoolExecutor) beanFactory.getBean("asyncTimeoutExecutor");
        assertEquals(0, asyncTimeoutExecutor.getActiveCount());
        assertEquals(0, asyncTimeoutExecutor.getQueue().size());

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);

        /*
         * The task abort strategy is set by the runnable,
         * A task abort task is scheduled with the proper timeout.
         * The guard task is removed after completion.
         */
        taskExecutor.execute(createRunnable(taskLatch, testLatch, null));

        testLatch.await();
        assertEquals(1, defaultExecutor.getActiveCount());
        assertEquals(1, asyncTimeoutExecutor.getQueue().size());

        taskLatch.countDown();

        // wait for task to complete.
        while (defaultExecutor.getActiveCount() == 1) {
            Thread.sleep(10);
        }
        // The task should have removed its guard.
        assertEquals(0, asyncTimeoutExecutor.getQueue().size());

        // now check some statistics.
        final ExecutorStatisticsView executorStatistics = taskExecutor.getExecutorStatistics();
        assertEquals((Long) 1L, executorStatistics.getCompletedTaskCount());
    }

    @Test
    public void testTaskTimeoutGuardIsInvoked() throws Exception {
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");

        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-espresso");
        taskProperties.setTimeout(1);

        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor taskExecutor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());

        ThreadPoolTaskExecutor defaultExecutor = (ThreadPoolTaskExecutor) configuration.getAsyncExecutor();
        assertEquals(0, defaultExecutor.getActiveCount());

        ScheduledThreadPoolExecutor asyncTimeoutExecutor = (ScheduledThreadPoolExecutor) beanFactory.getBean("asyncTimeoutExecutor");
        assertEquals(0, asyncTimeoutExecutor.getActiveCount());
        assertEquals(0, asyncTimeoutExecutor.getQueue().size());

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);

        /*
         * The task abort strategy is set by the runnable,
         * A task abort task is scheduled with the proper timeout.
         * The guard task is removed after completion.
         */
        final AtomicBoolean abortInvoked = new AtomicBoolean();

        final TaskAbortStrategy taskAbortStrategy = () -> {
            abortInvoked.set(true);


            return true;
        };
        taskExecutor.execute(createRunnable(taskLatch, testLatch, taskAbortStrategy));

        testLatch.await();
        assertEquals(1, defaultExecutor.getActiveCount());
        assertEquals(1, asyncTimeoutExecutor.getQueue().size());

        // wait for async task to complete.
        while (asyncTimeoutExecutor.getQueue().size() == 1) {
            Thread.sleep(10);
        }
        // The task should have remove its guard.
        assertEquals(0, asyncTimeoutExecutor.getQueue().size());
        assertTrue(abortInvoked.get());

        // now check some statistics.
        final ExecutorStatisticsView executorStatistics = taskExecutor.getExecutorStatistics();
        assertEquals((Long) 0L, executorStatistics.getCompletedTaskCount());
        assertEquals((Long) 1L, executorStatistics.getAbortedTaskCount());
    }


    @Test
    public void testThatTasksAreRejectedWhenTheThreadPoolAndQueueAreFull() throws Exception {
        String systemExecutorName = "coffee-bar-executor";
        addExecutorProperties(systemExecutorName, 1, 1, 1, 60);

        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");
        systemProperties.setDefaultExecutor(systemExecutorName);

        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-espresso");
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor taskExecutor =
                (DelegatingExecutor) beanFactory.getBean(systemProperties.getName() + "." + taskProperties.getMethod());


        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);

        /*
         * The task abort strategy is set by the runnable,
         * A task abort task is scheduled with the proper timeout.
         * The guard task is removed after completion.
         */
        taskExecutor.execute(createRunnable(taskLatch, testLatch, null));
        taskExecutor.execute(createRunnable(taskLatch, testLatch, null));

        thrown.expect(TaskRejectedException.class);
        taskExecutor.execute(createRunnable(taskLatch, testLatch, null));
    }

    @Test
    public void testThatTasksAreRemovedFromTheQueueWhenTheyTimeout() throws Exception {
        String systemExecutorName = "coffee-bar-executor";
        addExecutorProperties(systemExecutorName, 1, 1, 1, 60);

        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName("coffee-bar");
        systemProperties.setDefaultExecutor(systemExecutorName);

        TaskProperties taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-mocha");
        taskProperties.setTimeout(10000);
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);

        taskProperties = new TaskProperties();
        taskProperties.setMethod("serve-water");
        taskProperties.setTimeout(1);
        systemProperties.addTask(taskProperties);
        properties.addSystem(systemProperties);
        doIt();

        DelegatingExecutor mochaTaskExecutor =
                (DelegatingExecutor) beanFactory.getBean("coffee-bar.serve-mocha");

        DelegatingExecutor waterTaskExecutor =
                (DelegatingExecutor) beanFactory.getBean("coffee-bar.serve-water");

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final CountDownLatch mochaLatch = new CountDownLatch(1);
        final CountDownLatch waterLatch = new CountDownLatch(1);
        final CountDownLatch timeoutLatch = new CountDownLatch(1);


        final TaskAbortStrategy taskAbortStrategy = () -> {
            timeoutLatch.countDown();
            return true;
        };

        /*
         * The task abort strategy is set by the runnable,
         * A task abort task is scheduled with the proper timeout.
         * The guard task is removed after completion.
         */
        mochaTaskExecutor.execute(createRunnable(taskLatch, mochaLatch, taskAbortStrategy));
        waterTaskExecutor.execute(createRunnable(taskLatch, waterLatch, taskAbortStrategy));

        timeoutLatch.await(2, TimeUnit.SECONDS);
        // now check some statistics.
        final ExecutorStatisticsView executorStatistics = waterTaskExecutor.getExecutorStatistics();
        assertEquals((Long) 1L, executorStatistics.getAbortedTaskCount());
    }
}
