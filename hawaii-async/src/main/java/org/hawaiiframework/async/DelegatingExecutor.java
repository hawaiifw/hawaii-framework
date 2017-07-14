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

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

/**
 * Task executor that delegates to the task executor configured for a task.
 * <p>
 * In addition to the delegate, the async configuration properties and the task name is stored,
 * so we know which task this executor is for and we are able to determine the timeout.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class DelegatingExecutor implements TaskExecutor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingExecutor.class);

    /**
     * The delegate {@link TaskExecutor}.
     */
    private final TaskExecutor delegate;

    /**
     * The configuration properties.
     */
    private final ExecutorConfigurationProperties executorConfigurationProperties;

    /**
     * The name of the task that will be run by the {@link #delegate}.
     */
    private final String taskName;

    /**
     * Constructor.
     *
     * @param delegate                        the delegate
     * @param executorConfigurationProperties the configuration properties
     * @param taskName                        the task name
     */
    public DelegatingExecutor(
            final TaskExecutor delegate,
            final ExecutorConfigurationProperties executorConfigurationProperties,
            final String taskName) {
        this.delegate = delegate;
        this.executorConfigurationProperties = executorConfigurationProperties;
        this.taskName = taskName;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public void execute(final Runnable task) {
        final SharedTaskContext sharedTaskContext = new SharedTaskContext(taskName, executorConfigurationProperties);
        LOGGER.info("Scheduling task '{}' with id '{}'.", sharedTaskContext.getTaskName(), sharedTaskContext.getTaskId());
        SharedTaskContextHolder.register(sharedTaskContext);
        delegate.execute(task);
    }
}
