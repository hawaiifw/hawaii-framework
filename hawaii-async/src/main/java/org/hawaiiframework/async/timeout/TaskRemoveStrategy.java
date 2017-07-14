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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Removes the scheduled task from the executor's queue.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class TaskRemoveStrategy implements TaskAbortStrategy {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRemoveStrategy.class);

    /**
     * The executor holding the timeout guard tasks.
     */
    private final ThreadPoolExecutor executor;

    /**
     * The timeout guard task for this task.
     */
    private final Runnable task;

    /**
     * The task's type, i.e. 'guarded' or 'timeout guard'.
     */
    private final String taskType;

    /**
     * Create a new instance.
     *
     * @param executor The executor executing or queueing the {@code task}.
     * @param task     The task.
     * @param taskType The type of the task.
     */
    public TaskRemoveStrategy(final ThreadPoolExecutor executor, final Runnable task, final String taskType) {
        this.executor = executor;
        this.task = task;
        this.taskType = taskType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invoke() {
        final SharedTaskContext sharedTaskContext = SharedTaskContextHolder.get();

        return remove(sharedTaskContext);
    }

    private boolean remove(final SharedTaskContext sharedTaskContext) {
        LOGGER.trace("Removing {} task with id '{}'.", taskType, sharedTaskContext.getTaskId());
        final boolean wasRemoved = executor.remove(task);
        LOGGER.trace("Removal was {}successful.", wasRemoved ? "" : "not ");

        return wasRemoved;
    }
}
