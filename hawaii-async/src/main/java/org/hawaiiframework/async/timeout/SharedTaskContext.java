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

package org.hawaiiframework.async.timeout;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.statistics.ExecutorStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * The strategy is used by the {@link TimeoutGuardTask} to stop a running guarded task.
 * <p>
 * This class is called shared since it is shared by the guarded task and the guard task in order to communicate the task abort strategy.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class SharedTaskContext {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedTaskContext.class);

    /**
     * The registered task removal strategy.
     */
    private TaskRemoveStrategy taskRemoveStrategy;

    /**
     * The registered task abort strategy.
     */
    private TaskAbortStrategy taskAbortStrategy;


    /**
     * The registered timeout guard task removal strategy.
     */
    private TaskRemoveStrategy timeoutGuardTaskRemoveStrategy;

    /**
     * The task name.
     */
    private final String taskName;

    /**
     * The task's id.
     */
    private final String taskId;

    /**
     * The executor configuration properties.
     * <p>
     * Used to determine the timeout for {@link #taskName}.
     */
    private final ExecutorConfigurationProperties executorConfigurationProperties;

    /**
     * The executors statistics.
     */
    private final ExecutorStatistics executorStatistics;

    /**
     * Flag to indicate that the task has been aborted.
     */
    private boolean aborted;
    /**
     * Construct an instance.
     *
     * @param taskName                        the task name
     * @param executorConfigurationProperties the executor configuration properties
     */
    public SharedTaskContext(final String taskName,
            final ExecutorConfigurationProperties executorConfigurationProperties,
            final ExecutorStatistics executorStatistics) {
        this.taskName = taskName;
        this.executorConfigurationProperties = executorConfigurationProperties;
        this.taskId = UUID.randomUUID().toString();
        this.executorStatistics = executorStatistics;
    }

    /**
     * Register the task removal strategy.
     *
     * @param taskRemoveStrategy The {@link TaskRemoveStrategy} to share.
     */
    public void setTaskRemoveStrategy(final TaskRemoveStrategy taskRemoveStrategy) {
        this.taskRemoveStrategy = taskRemoveStrategy;
    }

    /**
     * Register the timeout guard task removal strategy.
     *
     * @param timeoutGuardTaskRemoveStrategy the {@link TaskRemoveStrategy} to set.
     */
    public void setTimeoutGuardTaskRemoveStrategy(final TaskRemoveStrategy timeoutGuardTaskRemoveStrategy) {
        this.timeoutGuardTaskRemoveStrategy = timeoutGuardTaskRemoveStrategy;
    }

    /**
     * Register the task abort strategy.
     *
     * @param taskAbortStrategy The {@link TaskAbortStrategy} to share.
     */
    public void setTaskAbortStrategy(final TaskAbortStrategy taskAbortStrategy) {
        this.taskAbortStrategy = taskAbortStrategy;
    }

    /**
     * Invoke the {@code taskAbortStrategy}.
     */
    public void abortBusinessTask() {
        if (taskAbortStrategy != null) {
            taskAbortStrategy.invoke();
            aborted = true;
        }
    }

    /**
     * Get the timeout for the task this strategy is for.
     *
     * @return the timeout
     * @see ExecutorConfigurationProperties#getTaskTimeout(String)
     */
    public Integer getTimeout() {
        return executorConfigurationProperties.getTaskTimeout(taskName);
    }

    /**
     * Get the task's id.
     *
     * @return The task's id.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Retrieve the task's name.
     *
     * @return The task's name.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Has the task been aborted?
     */
    public boolean isAborted() {
        return aborted;
    }

    /**
     * Remove the guarded task from the queue, or else abort the running guarded task.
     */
    public void timeout() {
        if (isNotRemovedFromExecutor()) {
            LOGGER.debug("Aborting task '{}' with id '{}.", getTaskName(), getTaskId());
            abortBusinessTask();
        }
    }

    /**
     * Try to remove the not running task from the executor's queue. If we can remove the task, then it was not running.
     * And since it is then removed, it will not run at all.
     */
    private boolean isNotRemovedFromExecutor() {
        if (taskRemoveStrategy == null) {
            throw new IllegalStateException("TimeoutGuardTask is executing, but task remove strategy is set.");
        }

        final boolean isRemoved = taskRemoveStrategy.invoke();
        if (isRemoved) {
            LOGGER.debug("Removed task '{}' from the executors queue.", getTaskId());
        }

        return !isRemoved;
    }

    /**
     * This method is invoked when the guarded task is finished.
     * <p>
     * It will perform cleanups. The finish should always be invoked.
     */
    public void finish() {
        if (!isAborted()) {
            timeoutGuardTaskRemoveStrategy.invoke();
        }
        if (isAborted()) {
            executorStatistics.incrementAbortedTaskCount();
        }
    }
}
