/*
 * Copyright 2015-2019 the original author or authors.
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
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_START;

/**
 * Delegating Runnable that copies the MDC to the executing thread before running the delegate.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class AbortableTaskRunnable extends HawaiiAsyncRunnable {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbortableTaskRunnable.class);

    /**
     * The delegate.
     */
    private final Runnable delegate;

    /**
     * Construct a new instance.
     *
     * @param delegate          the delegate to run.
     * @param sharedTaskContext the abort strategy to set.
     */
    public AbortableTaskRunnable(@NotNull final Runnable delegate, @NotNull final SharedTaskContext sharedTaskContext) {
        super(requireNonNull(sharedTaskContext));
        this.delegate = requireNonNull(delegate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRun() {
        sharedTaskContext.startExecution();

        final String taskId = sharedTaskContext.getTaskId();

        KibanaLogFields.setLogType(CALL_START);
        KibanaLogFields.set(CALL_ID, taskId);
        KibanaLogFields.set(CALL_TYPE, sharedTaskContext.getTaskName());

        LOGGER.info("Performing task '{}' with id '{}'.", sharedTaskContext.getTaskName(), taskId);

        KibanaLogFields.unsetLogType();

        try {
            delegate.run();
        } finally {
            sharedTaskContext.finish();
            final TaskStatistics taskStatistics = sharedTaskContext.getTaskStatistics();
            KibanaLogFields.setLogType(CALL_END);
            LOGGER.info("Task '{}' with id '{}' took '{}' msec ('{}' queue time, '{}' execution time).", sharedTaskContext.getTaskName(),
                    taskId, taskStatistics.getTotalTime() / 1E6, taskStatistics.getQueueTime() / 1E6,
                    taskStatistics.getExecutionTime() / 1E6);
            KibanaLogFields.unsetLogType();
        }
    }

}
