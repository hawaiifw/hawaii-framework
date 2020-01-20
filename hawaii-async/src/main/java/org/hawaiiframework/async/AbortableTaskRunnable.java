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

import org.hawaiiframework.async.statistics.TaskStatistics;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.MdcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_DURATION;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TASK_ID;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;

/**
 * Delegating Runnable that copies the MDC to the executing thread before running the delegate.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class AbortableTaskRunnable implements Runnable {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbortableTaskRunnable.class);

    /**
     * The MDC context that is set to the thread.
     */
    private final MdcContext mdcContext;

    /**
     * The delegate.
     */
    private final Runnable delegate;

    /**
     * The abort strategy to set on the executing thread's ThreadLocal {@link SharedTaskContextHolder}.
     */
    private final SharedTaskContext sharedTaskContext;

    /**
     * Construct a new instance.
     *
     * @param mdcContext        the MDC context (of the calling thread).
     * @param delegate          the delegate to run.
     * @param sharedTaskContext the abort strategy to set.
     */
    public AbortableTaskRunnable(@NotNull final MdcContext mdcContext, @NotNull final Runnable delegate,
        @NotNull final SharedTaskContext sharedTaskContext) {
        this.mdcContext = requireNonNull(mdcContext);
        this.delegate = requireNonNull(delegate);
        this.sharedTaskContext = requireNonNull(sharedTaskContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        mdcContext.populateMdc();
        sharedTaskContext.startExecution();

        final String taskId = sharedTaskContext.getTaskId();
        KibanaLogFields.set(TASK_ID, taskId);
        KibanaLogFields.set(CALL_ID, taskId);

        SharedTaskContextHolder.register(sharedTaskContext);
        try {
            LOGGER.trace("Performing task '{}' with id '{}'.", sharedTaskContext.getTaskName(), taskId);
            delegate.run();
        } finally {
            sharedTaskContext.finish();
            final TaskStatistics taskStatistics = sharedTaskContext.getTaskStatistics();
            final String duration = String.format("%.2f", taskStatistics.getTotalTime() / 1E6);
            KibanaLogFields.setLogType(CALL_END);
            KibanaLogFields.set(CALL_DURATION, duration);
            LOGGER.info("Task '{}' with id '{}' took '{}' msec ('{}' queue time, '{}' execution time).",
                sharedTaskContext.getTaskName(),
                taskId,
                duration,
                taskStatistics.getQueueTime() / 1E6,
                taskStatistics.getExecutionTime() / 1E6);
            KibanaLogFields.clear();
            SharedTaskContextHolder.remove();
        }
    }

}
