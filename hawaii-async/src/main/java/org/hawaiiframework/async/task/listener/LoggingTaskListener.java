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

package org.hawaiiframework.async.task.listener;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_DURATION;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_START;

import org.hawaiiframework.async.statistics.TaskStatistics;
import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.logging.model.AutoCloseableKibanaLogField;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task listener that logs start and end calls.
 *
 * <p>Ordered at '0'.
 */
public class LoggingTaskListener implements TaskListener {

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTaskListener.class);

  private SharedTaskContext sharedTaskContext;

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void setSharedTaskContext(SharedTaskContext sharedTaskContext) {
    this.sharedTaskContext = sharedTaskContext;
  }

  @Override
  @SuppressWarnings({"try", "unused"})
  public void startExecution() {
    try (AutoCloseableKibanaLogField callStart =
        KibanaLogFields.tagCloseable(LOG_TYPE, CALL_START)) {
      LOGGER.info(
          "Performing task '{}' with id '{}'.",
          sharedTaskContext.getTaskName(),
          sharedTaskContext.getTaskId());
    }
  }

  @Override
  @SuppressWarnings({"PMD.LawOfDemeter", "try", "unused"})
  public void finish() {
    TaskStatistics taskStatistics = sharedTaskContext.getTaskStatistics();
    String duration = formatTime(taskStatistics.getTotalTime());

    try (AutoCloseableKibanaLogField callEnd = KibanaLogFields.tagCloseable(LOG_TYPE, CALL_END);
        AutoCloseableKibanaLogField durationField =
            KibanaLogFields.tagCloseable(CALL_DURATION, duration)) {

      LOGGER.info(
          "Task '{}' with id '{}' took '{}' msec ('{}' queue time, '{}' execution time).",
          sharedTaskContext.getTaskName(),
          sharedTaskContext.getTaskId(),
          duration,
          formatTime(taskStatistics.getQueueTime()),
          formatTime(taskStatistics.getExecutionTime()));
    }
  }

  private static String formatTime(double time) {
    return String.format("%.2f", time / 1E6);
  }
}
