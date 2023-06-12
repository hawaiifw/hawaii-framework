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

package org.hawaiiframework.async.timeout;

/**
 * A strategy to abort a running task.
 * <p>
 * The abort strategy does not know what sort of task it needs to abort. It is up to the implementation to handle all the logic.
 * <p>
 * Note, the abort <b>must</b> ensure that all open resources are released in order to avoid deadlocks.
 * <p>
 * For a common case, think about a HTTP request that needs to be stopped if it takes too long.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public interface TaskAbortStrategy {

    /**
     * Invoke the strategy in order to abort the task (that registered this abort strategy).
     *
     * @return {@code true} if the task has been aborted.
     */
    boolean invoke();
}
