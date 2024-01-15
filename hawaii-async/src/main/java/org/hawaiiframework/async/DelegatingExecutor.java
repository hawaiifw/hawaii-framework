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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.statistics.ExecutorStatistics;
import org.hawaiiframework.async.statistics.ExecutorStatisticsView;
import org.hawaiiframework.async.task.listener.TaskListener;
import org.hawaiiframework.async.task.listener.TaskListenerFactory;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Task executor that delegates to the task executor configured for a task.
 *
 * <p>In addition to the delegate, the async configuration properties and the task name is stored,
 * so we know which task this executor is for and we are able to determine the timeout.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class DelegatingExecutor implements AsyncTaskExecutor, SchedulingTaskExecutor {

  /** The serial version UID. */
  private static final long serialVersionUID = -8533500008410021569L;

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingExecutor.class);

  /** The delegate {@link ThreadPoolTaskExecutor}. */
  private final ThreadPoolTaskExecutor delegate;

  /** The configuration properties. */
  private final ExecutorConfigurationProperties executorConfigurationProperties;

  /** The name of the task that will be run by the {@link #delegate}. */
  private final String taskName;

  /** This executor's statistics. */
  private final ExecutorStatistics executorStatistics;

  /** The TaskListener factories. */
  private final Collection<TaskListenerFactory> taskListenerFactories;

  /**
   * Constructor.
   *
   * @param delegate the delegate
   * @param executorConfigurationProperties the configuration properties
   * @param taskListenerFactories The task context providers.
   * @param taskName the task name
   */
  public DelegatingExecutor(
      ThreadPoolTaskExecutor delegate,
      ExecutorConfigurationProperties executorConfigurationProperties,
      Collection<TaskListenerFactory> taskListenerFactories,
      String taskName) {
    this.delegate = delegate;
    this.executorConfigurationProperties = executorConfigurationProperties;
    this.executorStatistics = new ExecutorStatistics(delegate);
    this.taskListenerFactories = taskListenerFactories;
    this.taskName = taskName;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Configures a {@link SharedTaskContextHolder} before delegating execution.
   */
  @Override
  public void execute(@NonNull Runnable task) {
    initializeTask();
    delegate.execute(task);
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated see super class for explanation. Configures a {@link SharedTaskContextHolder}
   *     before delegating execution.
   */
  @Deprecated
  @Override
  public void execute(@NonNull Runnable task, long startTimeout) {
    initializeTask();
    delegate.execute(task, startTimeout);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Configures a {@link SharedTaskContextHolder} before delegating execution.
   */
  @Override
  public Future<?> submit(@NonNull Runnable task) {
    initializeTask();
    return delegate.submit(task);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Configures a {@link SharedTaskContextHolder} before delegating execution.
   *
   * @param task The task to submit to be executed.
   * @param <T> The return type of the task.
   * @return A future for the task.
   */
  @Override
  public <T> Future<T> submit(@NonNull Callable<T> task) {
    initializeTask();
    return delegate.submit(task);
  }

  private void initializeTask() {
    SharedTaskContext sharedTaskContext =
        new SharedTaskContext(
            taskName, executorConfigurationProperties, executorStatistics, createTaskListeners());
    LOGGER.info(
        "Scheduling task '{}' with id '{}'.",
        sharedTaskContext.getTaskName(),
        sharedTaskContext.getTaskId());
    LOGGER.info(
        "Executor '{}' has '{}/{}' threads, '{}' queued entries, '{}' total executions and '{}' aborted executions.",
        taskName,
        executorStatistics.getPoolSize(),
        executorStatistics.getMaxPoolSize(),
        executorStatistics.getQueueSize(),
        executorStatistics.getCompletedTaskCount(),
        executorStatistics.getAbortedTaskCount());

    SharedTaskContextHolder.register(sharedTaskContext);
  }

  private List<TaskListener> createTaskListeners() {
    return taskListenerFactories.stream().map(TaskListenerFactory::create).collect(toList());
  }

  /**
   * Return the view on the statistics.
   *
   * @return A view on the executor's statistics.
   */
  public ExecutorStatisticsView getExecutorStatistics() {
    return new ExecutorStatisticsView(executorStatistics);
  }

  /**
   * For testing purposes.
   *
   * @param executor The executor to check this delegate against.
   * @return {@code true} if this delegating executor has the given {@code executor}.
   */
  public boolean hasDelegate(ThreadPoolTaskExecutor executor) {
    return delegate.equals(executor);
  }

  /**
   * For testing purposes. Return the number of currently active threads.
   *
   * @return the number of threads
   * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
   */
  public int getActiveCount() {
    return delegate.getActiveCount();
  }

  @Override
  public boolean prefersShortLivedTasks() {
    return delegate.prefersShortLivedTasks();
  }
}
