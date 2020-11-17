/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.async.task_listener;

import org.hawaiiframework.async.statistics.TaskStatistics;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.logging.model.AutoCloseableKibanaLogField;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_DURATION;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_START;

/**
 * Task listener that logs start and end calls.
 * <p>
 * Ordered at '0'.
 */
public class LoggingTaskListener implements TaskListener {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTaskListener.class);

    private SharedTaskContext sharedTaskContext;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setSharedTaskContext(final SharedTaskContext sharedTaskContext) {
        this.sharedTaskContext = sharedTaskContext;
    }

    @Override
    public void startExecution() {
        try (AutoCloseableKibanaLogField callStart = KibanaLogFields.logType(CALL_START)) {
            LOGGER.info("Performing task '{}' with id '{}'.", sharedTaskContext.getTaskName(), sharedTaskContext.getTaskId());
        }
    }

    @Override
    public void finish() {
        final TaskStatistics taskStatistics = sharedTaskContext.getTaskStatistics();
        final String duration = formatTime(taskStatistics.getTotalTime());

        try (AutoCloseableKibanaLogField callEnd = KibanaLogFields.logType(CALL_END);
                AutoCloseableKibanaLogField durationField = KibanaLogFields.tagCloseable(CALL_DURATION, duration)) {

            LOGGER.info("Task '{}' with id '{}' took '{}' msec ('{}' queue time, '{}' execution time).",
                    sharedTaskContext.getTaskName(),
                    sharedTaskContext.getTaskId(),
                    duration,
                    formatTime(taskStatistics.getQueueTime()),
                    formatTime(taskStatistics.getExecutionTime()));
        }

    }

    private String formatTime(final double time) {
        return String.format("%.2f", time / 1E6);
    }
}
