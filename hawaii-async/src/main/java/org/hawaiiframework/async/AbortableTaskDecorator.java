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

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.async.timeout.TaskRemoveStrategy;
import org.hawaiiframework.async.timeout.TimeoutGuardTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Task decorator to copy the MDC from the calling thread to the executing thread..
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class AbortableTaskDecorator implements TaskDecorator {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbortableTaskDecorator.class);

    /**
     * The executor that will execute the runnable we're decorating.
     */
    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * The executor aborts tasks that have timed-out.
     * <p>
     * That is, it is fed with tasks with a timeout / delay, after which these tasks will
     * abort the "guarded" task. The guarded task is the task that actually does something. this is the runnable we're decorating.
     */
    private final ScheduledThreadPoolExecutor timeoutExecutor;

    /**
     * Construct an instance.
     */
    public AbortableTaskDecorator(final ThreadPoolTaskExecutor taskExecutor, final ScheduledThreadPoolExecutor timeoutExecutor) {
        this.taskExecutor = taskExecutor;
        this.timeoutExecutor = timeoutExecutor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * We create a new {@link AbortableTaskRunnable} that holds the current thread's MDC. This is for logging purposes, so that logging
     * within the {@code runnable} is done with the correct logging context. For instance, the transaction id is logged and the current
     * user etc.
     * <p>
     * Next to this, we create an schedule a {@link TimeoutGuardTask}. This task will stop the runnable we're decorating if the configured
     * timeout has lapsed.
     * <p>
     * The {@link AbortableTaskRunnable} will stop the execution of the {@link TimeoutGuardTask} after it completes.
     */
    @Override
    public Runnable decorate(final Runnable runnable) {
        final SharedTaskContext sharedTaskContext = SharedTaskContextHolder.get();
        try {
            return getRunnable(runnable, sharedTaskContext);
        } finally {
            SharedTaskContextHolder.remove();
        }
    }

    private Runnable getRunnable(final Runnable runnable, final SharedTaskContext sharedTaskContext) {
        createTimeoutGuardTask(sharedTaskContext);

        return createGuardedTask(runnable, sharedTaskContext);
    }

    /**
     * Create the guarded task. This is the task we actually want to perform. Or in other words, the task containing the business logic.
     * <p>
     * This task is guarded by a {@link TimeoutGuardTask}. The {@link TimeoutGuardTask} will try to stop the guarded task upon timeout.
     */
    private Runnable createGuardedTask(final Runnable runnable, final SharedTaskContext sharedTaskContext) {
        final Runnable guardedTask = new AbortableTaskRunnable(runnable, sharedTaskContext);

        sharedTaskContext.setTaskRemoveStrategy(new TaskRemoveStrategy(taskExecutor.getThreadPoolExecutor(), guardedTask, "guarded"));

        return guardedTask;
    }

    /**
     * Creates a timeout guard task for the current task. It will retrieve the timeout and schedule a guard task with this timeout.
     * <p>
     * Next to this it will register it's removal strategy in the {@link SharedTaskContext}. This allows the guarded task, after completion,
     * to remove the timeout guard task.
     */
    private void createTimeoutGuardTask(final SharedTaskContext sharedTaskContext) {
        final Integer timeout = sharedTaskContext.getTimeout();
        LOGGER.debug("Setting timeout of '{}' second(s) for task '{}' with id '{}'.", timeout, sharedTaskContext.getTaskName(),
                sharedTaskContext.getTaskId());

        final TimeoutGuardTask timeoutGuardTask = new TimeoutGuardTask(sharedTaskContext);
        final RunnableScheduledFuture<?> scheduledTimeoutGuardTask =
                (RunnableScheduledFuture) timeoutExecutor.schedule(timeoutGuardTask, timeout, TimeUnit.SECONDS);

        sharedTaskContext
                .setTimeoutGuardTaskRemoveStrategy(new TaskRemoveStrategy(timeoutExecutor, scheduledTimeoutGuardTask, "timeout guard"));
    }
}
