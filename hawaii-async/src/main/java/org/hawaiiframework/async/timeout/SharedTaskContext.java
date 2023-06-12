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

package org.hawaiiframework.async.timeout;

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.statistics.ExecutorStatistics;
import org.hawaiiframework.async.statistics.TaskStatistics;
import org.hawaiiframework.async.task_listener.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The strategy is used by the {@link TimeoutGuardTask} to stop a running guarded task.
 * <p>
 * This class is called shared since it is shared by the guarded task and the guard task in order to communicate the task abort strategy.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class SharedTaskContext {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedTaskContext.class);

    private static final OrderComparator ORDER_COMPARATOR = new OrderComparator();
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
     * The set of task contexts, shared data between caller and async task.
     */
    private final List<TaskListener> taskListeners;

    /**
     * The executors statistics.
     */
    private final ExecutorStatistics executorStatistics;

    /**
     * Flag to indicate that the task has been aborted.
     */
    private boolean aborted;

    /**
     * The task's statistics.
     */
    private final TaskStatistics taskStatistics;

    /**
     * Construct an instance.
     *
     * @param taskName                        the task name
     * @param executorConfigurationProperties the executor configuration properties
     * @param executorStatistics              executor statisctics.
     * @param taskListeners                   task listeners.
     */
    public SharedTaskContext(final String taskName,
            final ExecutorConfigurationProperties executorConfigurationProperties,
            final ExecutorStatistics executorStatistics,
            final List<TaskListener> taskListeners) {
        this.taskName = taskName;
        this.executorConfigurationProperties = executorConfigurationProperties;
        this.taskListeners = taskListeners;
        this.taskId = UUID.randomUUID().toString();
        this.taskStatistics = new TaskStatistics();
        this.executorStatistics = executorStatistics;

        taskListeners.forEach(context -> context.setSharedTaskContext(this));
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
            executorStatistics.incrementAbortedTaskCount();
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
     *
     * @return {@code true} if the task has been aborted.
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

        taskListeners.stream().sorted(ORDER_COMPARATOR).forEach(TaskListener::timeout);
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
            executorStatistics.incrementAbortedTaskCount();
            LOGGER.debug("Removed task '{}' from the executors queue.", getTaskId());
        }

        return !isRemoved;
    }

    /**
     * Signal the start of the task's execution.
     */
    public void startExecution() {
        taskStatistics.startExecution();
        taskListeners.stream().sorted(ORDER_COMPARATOR).forEach(listener -> {
            logListener(listener, "startExecution()");
            listener.startExecution();
        });
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
        taskStatistics.stopExecution();
        taskListeners.stream().sorted(Collections.reverseOrder(ORDER_COMPARATOR)).forEach(listener -> {
            logListener(listener, "finish()");
            try {
                listener.finish();
            } catch (Throwable ignored) {
                // Do nothing.
            }
        });
        SharedTaskContextHolder.remove();
    }

    private void logListener(final TaskListener listener, final String method) {
        LOGGER.trace("Calling listener '{}#{}'.", listener.getClass().getSimpleName(), method);
    }

    /**
     * Return the task's execution statistics.
     *
     * @return The task statistics.
     */
    public TaskStatistics getTaskStatistics() {
        return taskStatistics;
    }

}
