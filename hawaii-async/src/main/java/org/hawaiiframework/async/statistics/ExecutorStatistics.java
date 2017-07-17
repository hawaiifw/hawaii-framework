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

package org.hawaiiframework.async.statistics;


import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Executor statistics.
 * <p>
 * The executor statistics contain the number of threads used, the maximum number configured, the queued task count, the executed task
 * count and the aborted task count.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ExecutorStatistics {

    /**
     * The thread pool task executor we're interested in.
     */
    private final ThreadPoolTaskExecutor executor;

    /**
     * The number of aborted tasks.
     */
    private final AtomicLong abortedTaskCount = new AtomicLong(0L);

    /**
     * Create a new statistics instance for the {@code executor}.
     *
     * @param executor The executor we're holding the statistics for.
     */
    public ExecutorStatistics(final ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * Increase the number of aborted tasks.
     */
    public void incrementAbortedTaskCount() {
        abortedTaskCount.incrementAndGet();
    }

    /**
     * Get the executor's pool size.
     *
     * @return The current number of threads in use by the executor.
     */
    public Integer getPoolSize() {
        return executor.getPoolSize();
    }

    /**
     * Get the executor's max pool size.
     *
     * @return The executor's max pool size.
     */
    public Integer getMaxPoolSize() {
        return executor.getMaxPoolSize();
    }

    /**
     * Get the executor's queued task size.
     *
     * @return The number of queued tasks.
     */
    public Integer getQueueSize() {
        return executor.getThreadPoolExecutor().getQueue().size();
    }

    /**
     * Get the number of completed tasks.
     *
     * @return The executor's completed task count.
     */
    public Long getCompletedTaskCount() {
        return executor.getThreadPoolExecutor().getCompletedTaskCount();
    }

    /**
     * Get the number of aborted tasks.
     *
     * @return The executor's aborted task count.
     */
    public Long getAbortedTaskCount() {
        return abortedTaskCount.get();
    }

}
