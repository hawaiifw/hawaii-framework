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
package org.hawaiiframework.async.statistics;

/** A view on the {@link ExecutorStatistics}. */
@SuppressWarnings("PMD.DataClass")
public class ExecutorStatisticsView {

  /** The pool size in use. */
  private final Integer poolSize;

  /** The configured max pool size. */
  private final Integer maxPoolSize;

  /** The queue size. */
  private final Integer queueSize;

  /** The number of completed tasks. */
  private final Long completedTaskCount;

  /** The number of aborted tasks. */
  private final Long abortedTaskCount;

  /**
   * Create a view on {@link ExecutorStatistics}.
   *
   * @param statistics The statistics to use.
   */
  public ExecutorStatisticsView(ExecutorStatistics statistics) {
    this.poolSize = statistics.getPoolSize();
    this.maxPoolSize = statistics.getMaxPoolSize();
    this.queueSize = statistics.getQueueSize();
    this.completedTaskCount = statistics.getCompletedTaskCount();
    this.abortedTaskCount = statistics.getAbortedTaskCount();
  }

  /**
   * Get the executor's pool size.
   *
   * @return The current number of threads in use by the executor.
   */
  public Integer getPoolSize() {
    return poolSize;
  }

  /**
   * Get the executor's max pool size.
   *
   * @return The executor's max pool size.
   */
  public Integer getMaxPoolSize() {
    return maxPoolSize;
  }

  /**
   * Get the executor's queued task size.
   *
   * @return The number of queued tasks.
   */
  public Integer getQueueSize() {
    return queueSize;
  }

  /**
   * Get the number of completed tasks.
   *
   * @return The executor's completed task count.
   */
  public Long getCompletedTaskCount() {
    return completedTaskCount;
  }

  /**
   * Get the number of aborted tasks.
   *
   * @return The executor's aborted task count.
   */
  public Long getAbortedTaskCount() {
    return abortedTaskCount;
  }
}
