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

package org.hawaiiframework.async.config;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.model.ExecutorProperties;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.hawaiiframework.async.AsyncExecutorConfiguration.ASYNC_TIMEOUT_EXECUTOR;

/**
 * Factory to create executors for the asynchronous execution of methods using the {@link org.springframework.scheduling.annotation.Async}
 * annotation.
 * <p>
 * This factory creates the executors as defined in the {@code executors} section in the async configuration.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 3.0.0
 */
public class AsyncExecutorFactory {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorFactory.class);

    /**
     * Set of executor names defined in the properties.
     */
    private final Set<String> executorNames = new HashSet<>();

    private final BeanRegistrar registrar;

    private final ExecutorConfigurationProperties properties;

    /**
     * The constructor.
     *
     * @param registrar  The bean registrar.
     * @param properties The executor properties.
     */
    public AsyncExecutorFactory(final BeanRegistrar registrar, final ExecutorConfigurationProperties properties) {
        this.registrar = registrar;
        this.properties = properties;
    }

    /**
     * Create executors from the configured executor properties.
     */
    public void createExecutors() {
        final ConstructorArgumentValues taskExecutorConstructorValues = new ConstructorArgumentValues();
        taskExecutorConstructorValues.addIndexedArgumentValue(0, properties.getAsyncTimeoutExecutorPoolSize());
        registrar.registerBean(ASYNC_TIMEOUT_EXECUTOR, ScheduledThreadPoolExecutor.class, taskExecutorConstructorValues);

        for (final ExecutorProperties executorProperties : properties.getExecutors()) {
            if (!executorNames.add(executorProperties.getName())) {
                throw new HawaiiException(
                        String.format("Configuration contained multiple definitions of '%s'.", executorProperties.getName()));
            }

            LOGGER.info("Registering executor '{}'.", executorProperties);
            registrar.registerBean(executorProperties.getName(), ThreadPoolTaskExecutor.class);
        }
    }

    /**
     * Get the executor names.
     *
     * @return The, never {@code null}, executor names.
     */
    public Set<String> getExecutorNames() {
        return executorNames;
    }
}
