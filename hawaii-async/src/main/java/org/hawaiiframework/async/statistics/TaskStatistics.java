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

/**
 * Statistics about a task.
 *
 * <p>It registers, in nano-seconds, the queue time, the execution time and the total time (queue
 * time + execution time).
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public final class TaskStatistics {

  /** The timestamp the task is put into the executor's queue. */
  private Long queueStart;

  /** The timestamp the task is picked up by a thread and is run. */
  private Long execStart;

  /** The timestamp the task's execution has ended. */
  private Long execEnd;

  /** The constructor, sets the queue start time. */
  public TaskStatistics() {
    enqueue();
  }

  /** Set the timestamp the task is offered for execution. */
  public void enqueue() {
    queueStart = System.nanoTime();
  }

  /** Set the timestamp the task is actually run. */
  public void startExecution() {
    execStart = System.nanoTime();
  }

  /** Set the timestamp the task is finished running. */
  public void stopExecution() {
    execEnd = System.nanoTime();
  }

  /**
   * Get the time (in nano seconds) the task was in the queue.
   *
   * @return the interval the task was queued.
   */
  public Long getQueueTime() {
    return diff(queueStart, execStart);
  }

  /**
   * Get the time (in nano seconds) the task was executing.
   *
   * @return the interval the task was running.
   */
  public Long getExecutionTime() {
    return diff(execStart, execEnd);
  }

  /**
   * Get the time (in nano seconds) the task took from start (offer / queue) to completion.
   *
   * <p>This is the same as queue time + execution time.
   *
   * @return the total time the task took to complete.
   */
  public Long getTotalTime() {
    return diff(queueStart, execEnd);
  }

  private static Long diff(Long queueStart, Long execEnd) {
    return execEnd - queueStart;
  }
}
