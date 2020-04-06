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

import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.statistics.ExecutorStatistics;
import org.hawaiiframework.async.statistics.ExecutorStatisticsView;
import org.hawaiiframework.async.task_listener.TaskListener;
import org.hawaiiframework.async.task_listener.TaskListenerFactory;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Task executor that delegates to the task executor configured for a task.
 * <p>
 * In addition to the delegate, the async configuration properties and the task name is stored,
 * so we know which task this executor is for and we are able to determine the timeout.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class DelegatingExecutor implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -8533500008410021569L;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingExecutor.class);

    /**
     * The delegate {@link ThreadPoolTaskExecutor}.
     */
    private final ThreadPoolTaskExecutor delegate;

    /**
     * The configuration properties.
     */
    private final ExecutorConfigurationProperties executorConfigurationProperties;

    /**
     * The name of the task that will be run by the {@link #delegate}.
     */
    private final String taskName;

    /**
     * This executor's statistics.
     */
    private final ExecutorStatistics executorStatistics;

    /**
     * The TaskListener factories.
     */
    private final Collection<TaskListenerFactory> taskListenerFactories;

    /**
     * Constructor.
     *
     * @param delegate                        the delegate
     * @param executorConfigurationProperties the configuration properties
     * @param taskListenerFactories            The task context providers.
     * @param taskName                        the task name
     */
    public DelegatingExecutor(
            final ThreadPoolTaskExecutor delegate,
            final ExecutorConfigurationProperties executorConfigurationProperties,
            final Collection<TaskListenerFactory> taskListenerFactories,
            final String taskName) {
        this.delegate = delegate;
        this.executorConfigurationProperties = executorConfigurationProperties;
        this.executorStatistics = new ExecutorStatistics(delegate);
        this.taskListenerFactories = taskListenerFactories;
        this.taskName = taskName;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public void execute(@NonNull final Runnable task) {
        initializeTask();
        delegate.execute(task);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public void execute(@NonNull final Runnable task, final long startTimeout) {
        initializeTask();
        delegate.execute(task, startTimeout);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public Future<?> submit(@NonNull final Runnable task) {
        initializeTask();
        return delegate.submit(task);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public <T> Future<T> submit(@NonNull final Callable<T> task) {
        initializeTask();
        return delegate.submit(task);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        initializeTask();
        return delegate.submitListenable(task);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configures a {@link SharedTaskContextHolder} before delegating execution.
     */
    @Override
    public <T> ListenableFuture<T> submitListenable(@NotNull final Callable<T> task) {
        initializeTask();
        return delegate.submitListenable(task);
    }

    private void initializeTask() {
        final SharedTaskContext sharedTaskContext = new SharedTaskContext(taskName, executorConfigurationProperties,
                executorStatistics, createTaskListeners());
        LOGGER.info("Scheduling task '{}' with id '{}'.", sharedTaskContext.getTaskName(), sharedTaskContext.getTaskId());
        LOGGER.info("Executor '{}' has '{}/{}' threads, '{}' queued entries, '{}' total executions and '{}' aborted executions.", taskName,
                executorStatistics.getPoolSize(), executorStatistics.getMaxPoolSize(), executorStatistics.getQueueSize(),
                executorStatistics.getCompletedTaskCount(), executorStatistics.getAbortedTaskCount());

        SharedTaskContextHolder.register(sharedTaskContext);
    }

    private List<TaskListener> createTaskListeners() {
        return taskListenerFactories.stream().map(TaskListenerFactory::create).collect(Collectors.toList());
    }

    /**
     * Return the view on the statistics.
     */
    public ExecutorStatisticsView getExecutorStatistics() {
        return new ExecutorStatisticsView(executorStatistics);
    }

    /**
     * For testing purposes.
     */
    public boolean hasDelegate(final ThreadPoolTaskExecutor executor) {
        return delegate.equals(executor);
    }

    /**
     * For testing purposes.
     */
    public int getActiveCount() {
        return delegate.getActiveCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean prefersShortLivedTasks() {
        return delegate.prefersShortLivedTasks();
    }
}
