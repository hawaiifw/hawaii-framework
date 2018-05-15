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

package org.hawaiiframework.async;

import org.hawaiiframework.async.statistics.TaskStatistics;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.MdcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogCallResultTypes.TIME_OUT;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CALL_DURATION;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CALL_TYPE;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.CALL_START;

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
     * The number of milliseconds (10E-3)per nanoseconds (1E-9).
     */
    private static final double MSEC_PER_NANOSEC = 1E6;

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
    public AbortableTaskRunnable(final MdcContext mdcContext, final Runnable delegate,
                                 final SharedTaskContext sharedTaskContext) {
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
        KibanaLogFields.set(CALL_ID, taskId);
        KibanaLogFields.set(CALL_TYPE, sharedTaskContext.getTaskName());

        SharedTaskContextHolder.register(sharedTaskContext);
        try {

            /*
             * Perform the task.
             */
            KibanaLogFields.setLogType(CALL_START);
            LOGGER.info("Performing task '{}' with id '{}'.", sharedTaskContext.getTaskName(), taskId);
            KibanaLogFields.unsetLogType();

            delegate.run();
        } finally {
            sharedTaskContext.finish();
            logStatistics(taskId, sharedTaskContext.getTaskStatistics());
            MDC.clear();
            SharedTaskContextHolder.remove();
        }
    }

    private void logStatistics(final String taskId, final TaskStatistics taskStatistics) {
        KibanaLogFields.setLogType(CALL_END);

        final String duration = formatTime(taskStatistics.getTotalTime());
        KibanaLogFields.set(CALL_DURATION, duration);
        if (sharedTaskContext.isAborted()) {
            KibanaLogFields.setCallResult(TIME_OUT);
        }
        LOGGER.info("Task '{}' with id '{}' took '{}' msec ('{}' queue time, '{}' execution time).", sharedTaskContext.getTaskName(),
                taskId, duration, formatTime(taskStatistics.getQueueTime()), formatTime(taskStatistics.getExecutionTime()));
        KibanaLogFields.unsetLogType();
    }

    private String formatTime(final Long time) {
        return String.format("%.2f", time / MSEC_PER_NANOSEC);
    }
}
