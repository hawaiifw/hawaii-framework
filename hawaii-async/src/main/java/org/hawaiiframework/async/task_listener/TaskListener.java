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

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.springframework.core.Ordered;

/**
 * An interface that allows listeners to be informed about the task's lifecycle.
 * <p>
 * The setSharedTaskContext method is called before the task is being run.
 * Then the startExecution method is called (prior to starting the task).
 * Optionally the timeout method is called.
 * Then the finish is called last.
 */
public interface TaskListener extends Ordered {

    /**
     * Set the shared task context in this task context. Invoked before the set.
     *
     * @param sharedTaskContext the shared task context.
     */
    default void setSharedTaskContext(final SharedTaskContext sharedTaskContext) {
        // Default empty implementation.
    }

    /**
     * Called just before the task is started.
     */
    default void startExecution() {
        // Default empty implementation.
    }

    /**
     * Called just after a task is finished.
     */
    default void finish() {
        // Default empty implementation.
    }

    /**
     * Called just after a task has timed-out.
     */
    default void timeout() {
        // Default empty implementation.
    }
}
