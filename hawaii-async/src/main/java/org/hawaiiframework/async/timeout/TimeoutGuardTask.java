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

import org.hawaiiframework.logging.model.MdcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Task that stops another scheduled task if the scheduled task's timeout has been reached.
 * <p>
 * We first try to remove the scheduled task from the executor, so it will not be executed.
 * If this fails we will try to abort the scheduled tasks.
 * <p>
 * For this the guarded task must register it's {@link TaskAbortStrategy} in the {@link SharedTaskContext}.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class TimeoutGuardTask implements Runnable {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutGuardTask.class);

    /**
     * The MDC context used to copy the (logging) context to the thread executing this task.
     */
    private final MdcContext mdcContext;

    /**
     * The {@code guardedTask}'s abort strategy. May be null.
     */
    private final SharedTaskContext sharedTaskContext;

    /**
     * Create a new {@link TimeoutGuardTask} instance with the given {@code mdcContext} and {@code taskExecutor}.
     *
     * @param mdcContext The Logging context to set
     */
    public TimeoutGuardTask(@NotNull final MdcContext mdcContext, final SharedTaskContext sharedTaskContext) {
        this.mdcContext = requireNonNull(mdcContext);
        this.sharedTaskContext = sharedTaskContext;
    }

    /**
     * {@inheritDoc}
     * <p>
     * When this method is run, the {@code guardedTask} is running longer than it's timeout.
     */
    @Override
    public void run() {
        mdcContext.populateMdc();
        SharedTaskContextHolder.register(sharedTaskContext);
        MDC.put("task_id", sharedTaskContext.getTaskId());
        LOGGER.trace("Executing guard task.");

        try {
            sharedTaskContext.timeout();
        } finally {
            MDC.clear();
            SharedTaskContextHolder.remove();
        }
    }

}
