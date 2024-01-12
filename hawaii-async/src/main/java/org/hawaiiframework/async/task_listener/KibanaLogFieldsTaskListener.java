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

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TASK_ID;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.logging.model.KibanaLogContext;
import org.hawaiiframework.logging.model.KibanaLogFields;

/**
 * Task listener for Kibana log fields.
 *
 * <p>Ordered at -100.
 */
public class KibanaLogFieldsTaskListener implements TaskListener {

  private final KibanaLogContext kibanaLogContext;
  private SharedTaskContext sharedTaskContext;

  /** The constructor. */
  public KibanaLogFieldsTaskListener() {
    kibanaLogContext = KibanaLogFields.getContext();
  }

  @Override
  public int getOrder() {
    return -100;
  }

  @Override
  public void setSharedTaskContext(SharedTaskContext sharedTaskContext) {
    this.sharedTaskContext = sharedTaskContext;
  }

  @Override
  public void startExecution() {
    KibanaLogFields.populateFromContext(kibanaLogContext);
    String taskId = sharedTaskContext.getTaskId();
    KibanaLogFields.tag(TASK_ID, taskId);
    KibanaLogFields.tag(CALL_ID, taskId);
    KibanaLogFields.tag(CALL_TYPE, sharedTaskContext.getTaskName());
  }

  @Override
  public void finish() {
    KibanaLogFields.clear();
  }
}
