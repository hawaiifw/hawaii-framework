/*
 * Copyright 2015-2018 the original author or authors.
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

import org.hawaiiframework.async.config.AsyncExecutorFactory;
import org.hawaiiframework.async.config.AsyncExecutorInitializer;
import org.hawaiiframework.async.config.BeanRegistrar;
import org.hawaiiframework.async.config.DelegatingExecutorFactory;
import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.sql.DataSourceProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

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
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
@Configuration
@Import(DataSourceProxyConfiguration.class)
@EnableAsync
public class AsyncExecutorConfiguration implements BeanDefinitionRegistryPostProcessor, AsyncConfigurer, EnvironmentAware {

    /**
     * Async (task) timeout executor bean name.
     */
    public static final String ASYNC_TIMEOUT_EXECUTOR = "asyncTimeoutExecutor";

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorConfiguration.class);

    /**
     * Async configuration bean name.
     */
    private static final String EXECUTOR_CONFIGURATION_PROPERTIES = "executorConfigurationProperties";

    /**
     * The configuration properties.
     */
    private ExecutorConfigurationProperties properties;

    /**
     * Set of executor names defined in the properties.
     */
    private final Set<String> executorNames = new HashSet<>();

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
     * Utility to add beans to Spring's bean definition registry.
     */
    private BeanRegistrar registrar;

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull final BeanDefinitionRegistry registry) throws BeansException {
        LOGGER.trace("Creating beans for async executors.");
        registrar = new BeanRegistrar(registry);

        final ExecutorConfigurationProperties properties = getProperties();
        registrar.registerBean(EXECUTOR_CONFIGURATION_PROPERTIES, ExecutorConfigurationProperties.class);

        final AsyncExecutorFactory factory = new AsyncExecutorFactory(registrar, properties);
        factory.createExecutors();

        executorNames.addAll(factory.getExecutorNames());
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Configured method names are aliased to their corresponding executor, such that bean lookup works.</p>
     */
    @Override
    public void postProcessBeanFactory(@NonNull final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        LOGGER.trace("Initializing beans for async executors.");
        final ExecutorConfigurationProperties properties = getProperties();
        beanFactory.initializeBean(properties, EXECUTOR_CONFIGURATION_PROPERTIES);

        final AsyncExecutorInitializer executorInitializer = new AsyncExecutorInitializer(beanFactory, properties);
        executorInitializer.initializeExecutors();
        defaultExecutor = executorInitializer.getDefaultExecutor();

        final DelegatingExecutorFactory delegatingExecutorFactory =
            new DelegatingExecutorFactory(beanFactory, registrar, properties, executorNames);
        delegatingExecutorFactory.createDelegatingExecutors();
    }

    /**
     * {@inheritDoc}
     *
     * @return the default executor.
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
     * {@inheritDoc}
     * <p>Use the environment to obtain the location of the configuration file.</p>
     */
    @Override
    public void setEnvironment(@NonNull final Environment environment) {
        setAsyncPropertiesLoader(new AsyncPropertiesLoader(environment.getProperty("hawaii.async.configuration")));
    }

    /**
     * Setter for the properties loaded.
     * <p>
     * Added for testability.
     *
     * @param asyncPropertiesLoader the loader
     */
    public void setAsyncPropertiesLoader(final AsyncPropertiesLoader asyncPropertiesLoader) {
        this.asyncPropertiesLoader = asyncPropertiesLoader;
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
}
