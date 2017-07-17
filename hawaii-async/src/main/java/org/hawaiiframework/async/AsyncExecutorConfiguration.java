/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.async;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.model.ExecutorProperties;
import org.hawaiiframework.async.model.SystemProperties;
import org.hawaiiframework.async.model.TaskProperties;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Configuration class to set up asynchronous executors.
 * <p>
 * <p>The loaded configuration properties are themselves registered as a bean, so they may be in other classes
 * needing them, see {@link #EXECUTOR_CONFIGURATION_PROPERTIES}.</p>
 * <p>
 * <p>For each configured task a bean {@link DelegatingExecutor} with the name <code>{system_name}.{task_name}</code> is created. The
 * task's configured executor is set in the {@link DelegatingExecutor} as it's delegate.</p>
 * <p>
 * <p>A method annotated with {@link org.springframework.scheduling.annotation.Async} can specify its full task name,
 * i.e. <code>{system_name}.{task_name}</code> as the annotation value. The corresponding delegating executor bean will be retrieved by this
 * name.
 * <p>
 * <b>NOTE:</b> each async task <b>MUST</b> be specified in the configuration, otherwise an exception will be raised.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
@Configuration
@EnableAsync
public class AsyncExecutorConfiguration implements BeanDefinitionRegistryPostProcessor, AsyncConfigurer, EnvironmentAware {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorConfiguration.class);

    /**
     * Async configuration bean name.
     */
    private static final String EXECUTOR_CONFIGURATION_PROPERTIES = "executorConfigurationProperties";

    /**
     * Async (task) timeout executor bean name.
     */
    private static final String ASYNC_TIMEOUT_EXECUTOR = "asyncTimeoutExecutor";

    /**
     * The configuration properties.
     */
    private ExecutorConfigurationProperties properties;

    /**
     * Set of executor names defined in the properties.
     */
    private final Set<String> executorNames = new HashSet<>();

    /**
     * Spring's bean definition registry, cached for later use.
     */
    private BeanDefinitionRegistry beanDefinitionRegistry;

    /**
     * The default executor.
     * <p>
     * This is determined from the configuration properties and returned in {@link #getAsyncExecutor()}.
     *
     * @see AsyncConfigurer
     */
    private TaskExecutor defaultExecutor;

    /**
     * The loader for the async configuration.
     */
    private AsyncPropertiesLoader asyncPropertiesLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
        // Just to be sure
        getProperties();
        beanDefinitionRegistry = registry;

        createGenericBeanDefinition(registry, EXECUTOR_CONFIGURATION_PROPERTIES, ExecutorConfigurationProperties.class);

        final ConstructorArgumentValues taskExecutorConstructorValues = new ConstructorArgumentValues();
        taskExecutorConstructorValues.addIndexedArgumentValue(0, properties.getAsyncTimeoutExecutorPoolSize());
        createGenericBeanDefinition(registry, ASYNC_TIMEOUT_EXECUTOR, ScheduledThreadPoolExecutor.class, taskExecutorConstructorValues);

        createExecutors(registry);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Configured method names are aliased to their corresponding executor, such that bean lookup works.</p>
     */
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final ExecutorConfigurationProperties properties = getProperties();

        // Is this sane? May be used to get the timeout per call
        beanFactory.initializeBean(properties, EXECUTOR_CONFIGURATION_PROPERTIES);

        final ScheduledThreadPoolExecutor asyncTimeoutExecutor = (ScheduledThreadPoolExecutor) beanFactory.getBean(ASYNC_TIMEOUT_EXECUTOR);
        asyncTimeoutExecutor.setThreadFactory(new BasicThreadFactory.Builder().namingPattern("async-timeout-%d").daemon(true).build());
        beanFactory.initializeBean(asyncTimeoutExecutor, ASYNC_TIMEOUT_EXECUTOR);

        initializeExecutors(beanFactory);

        createSystems(beanFactory);
    }

    /**
     * {@inheritDoc}
     *
     * @return the default executor, as determined from {@link #isDefaultExecutor(ExecutorProperties)}
     */
    @Override
    public Executor getAsyncExecutor() {
        return defaultExecutor;
    }

    /**
     * {@inheritDoc}
     *
     * @return null, i.e. use the Spring default
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    /**
     * Create a {@link GenericBeanDefinition} of the specified class and register it with the registry.
     *
     * @param beanDefinitionRegistry the registry
     * @param beanName               the bean name
     * @param clazz                  the bean class
     */
    private void createGenericBeanDefinition(
            final BeanDefinitionRegistry beanDefinitionRegistry,
            final String beanName,
            final Class<?> clazz) {
        createGenericBeanDefinition(beanDefinitionRegistry, beanName, clazz, null);
    }

    /**
     * Create a {@link GenericBeanDefinition} of the specified class and register it with the registry.
     *
     * @param beanDefinitionRegistry    the registry
     * @param beanName                  the bean name
     * @param clazz                     the bean class
     * @param constructorArgumentValues the constructor arguments.
     */
    private void createGenericBeanDefinition(
            final BeanDefinitionRegistry beanDefinitionRegistry,
            final String beanName,
            final Class<?> clazz,
            final ConstructorArgumentValues constructorArgumentValues) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setAutowireMode(ConfigurableListableBeanFactory.AUTOWIRE_NO);
        beanDefinition.setDependencyCheck(GenericBeanDefinition.DEPENDENCY_CHECK_NONE);
        beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * {@inheritDoc}
     * <p>Use the environment to obtain the location of the configuration file.</p>
     */
    @Override
    public void setEnvironment(final Environment environment) {
        asyncPropertiesLoader = new AsyncPropertiesLoader(environment.getProperty("hawaii.async.configuration"));
    }

    /**
     * Create executors from the configured executor properties.
     *
     * @param registry the bean definition registry
     */
    private void createExecutors(final BeanDefinitionRegistry registry) {
        for (final ExecutorProperties executorProperties : properties.getExecutors()) {
            if (!executorNames.add(executorProperties.getName())) {
                throw new HawaiiException(
                        String.format("Configuration contained multiple definitions of '%s'.", executorProperties.getName()));
            }

            LOGGER.info("Registering executor '{}'.", executorProperties);
            createGenericBeanDefinition(registry, executorProperties.getName(), ThreadPoolTaskExecutor.class);
        }
    }

    /**
     * Create bean aliases for the configured systems.
     * <p>
     * For each task in the system an {@link DelegatingExecutor} is created with the relevant executor as its delegate. This is either the
     * configured executor for the task, or the task's system default executor.
     * <p>
     * If neither have a configured executor, no alias is created and the call will default to the default executor.
     *
     * @param beanFactory the bean factory
     */
    private void createSystems(final ConfigurableListableBeanFactory beanFactory) {
        for (final SystemProperties systemProperties : properties.getSystems()) {
            final String systemName = systemProperties.getName();

            // This is the fallback if no executor is defined for a task
            final String systemExecutor = systemProperties.getDefaultExecutor();
            if (!executorNames.contains(systemExecutor)) {
                throw new HawaiiException(
                        String.format("Executor '%s' of system '%s' is not defined.", systemExecutor, systemName));
            }

            for (final TaskProperties taskProperties : systemProperties.getTasks()) {
                final String executorName = taskProperties.getExecutor();

                final String taskName = String.format("%s.%s", systemName, taskProperties.getMethod());
                if (StringUtils.isNotBlank(executorName)) {
                    if (!executorNames.contains(executorName)) {
                        throw new HawaiiException(
                                String.format("Executor '%s' of task '%s' is not defined.", executorName, taskName));
                    }
                    LOGGER.debug("Configuring task '{}' to use executor '{}'.", taskName, executorName);
                } else {
                    LOGGER.info("No executor defined for task '{}'. Falling back to default.", taskName, systemExecutor);
                }

                final String executor = StringUtils.isNotBlank(executorName) ? executorName : systemExecutor;

                createTaskExecutorDelegate(beanFactory, taskName, executor);
            }
        }
    }

    private void createTaskExecutorDelegate(
            final ConfigurableListableBeanFactory beanFactory,
            final String taskName,
            final String executor) {
        final TaskExecutor delegate = (TaskExecutor) beanFactory.getBean(executor);
        final ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, delegate);
        constructorArgumentValues.addIndexedArgumentValue(1, properties);
        constructorArgumentValues.addIndexedArgumentValue(2, taskName);
        createGenericBeanDefinition(beanDefinitionRegistry, taskName, DelegatingExecutor.class, constructorArgumentValues);
    }

    /**
     * Configure a task executor from its configuration properties.
     *
     * @param beanFactory        the bean factory
     * @param executorProperties the executor properties
     */
    private TaskExecutor initializeExecutor(
            final ConfigurableListableBeanFactory beanFactory,
            final ExecutorProperties executorProperties) {
        LOGGER.info("Creating executor '{}'.", executorProperties);
        final ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) beanFactory.getBean(executorProperties.getName());
        taskExecutor.setThreadFactory(null);
        taskExecutor.setThreadNamePrefix(executorProperties.getName() + "-");
        taskExecutor.setCorePoolSize(executorProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(executorProperties.getMaxPendingRequests());
        taskExecutor.setKeepAliveSeconds(executorProperties.getKeepAliveTime());

        final ScheduledThreadPoolExecutor timeoutExecutor = (ScheduledThreadPoolExecutor) beanFactory.getBean(ASYNC_TIMEOUT_EXECUTOR);
        taskExecutor.setTaskDecorator(new AbortableTaskDecorator(taskExecutor, timeoutExecutor));

        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * Lazy load the properties.
     *
     * @return the properties
     */
    private ExecutorConfigurationProperties getProperties() {
        if (properties == null) {
            properties = asyncPropertiesLoader.loadProperties();
        }
        return properties;
    }

    /**
     * Initialize all configured executors in the bean factory and determine the default executor.
     *
     * @param beanFactory the bean factory
     */
    private void initializeExecutors(final ConfigurableListableBeanFactory beanFactory) {
        for (final ExecutorProperties executorProperties : properties.getExecutors()) {
            final TaskExecutor executor = initializeExecutor(beanFactory, executorProperties);
            if (isDefaultExecutor(executorProperties)) {
                defaultExecutor = executor;
            }
        }
    }

    /**
     * Match the name from the executor properties to the global default executor name.
     *
     * @param executorProperties the executor properties to check
     * @return true if the names match
     * @see #isDefaultExecutor(String)
     */
    private boolean isDefaultExecutor(final ExecutorProperties executorProperties) {
        return isDefaultExecutor(executorProperties.getName());
    }

    /**
     * Match the executor name to the global default executor name.
     *
     * @param executorName the executor name
     * @return true if the names match
     */
    private boolean isDefaultExecutor(final String executorName) {
        if (StringUtils.isBlank(executorName)) {
            return false;
        }
        return executorName.equals(properties.getDefaultExecutor());
    }

}
