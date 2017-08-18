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

import org.hawaiiframework.async.http.HawaiiHttpComponentsClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Is used by the guarded task to register it's task abort command (if applicable).
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public final class SharedTaskContextHolder {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedTaskContextHolder.class);

    /**
     * The ThreadLocal to hold the {@link SharedTaskContext} per thread.
     */
    private static ThreadLocal<SharedTaskContext> threadLocalStore = new ThreadLocal<>();

    private SharedTaskContextHolder() {
        // Utility without constructor.
    }

    /**
     * Initialize the {@link SharedTaskContextHolder} with the {@code sharedTaskContext}.
     * <p>
     * By registering the {@code sharedTaskContext} the strategy is made available for other code within the executing thread. This
     * allows the guard task to abort the task being executed without knowing how.
     * <p>
     * The task itself is responsible to register a {@link TaskAbortStrategy} in the {@code sharedTaskContext}. This can be done by
     * calling the {@link #setTaskAbortStrategy(TaskAbortStrategy)}.
     * <p>
     * As an example, see the {@link HawaiiHttpComponentsClientHttpRequestFactory}.
     *
     * @param sharedTaskContext the shared strategy to hold for the current thread.
     */
    public static void register(final SharedTaskContext sharedTaskContext) {
        threadLocalStore.set(sharedTaskContext);
    }

    /**
     * Set the current thread's {@code taskAbortStrategy}.
     * <p>
     * This sets the {@code taskAbortStrategy} into the thread's {@link SharedTaskContext}.
     *
     * @param taskAbortStrategy The strategy to set.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static void setTaskAbortStrategy(final TaskAbortStrategy taskAbortStrategy) {
        final SharedTaskContext sharedTaskContext = get();
        if (sharedTaskContext != null) {
            LOGGER.trace("Setting task abort strategy.");
            sharedTaskContext.setTaskAbortStrategy(taskAbortStrategy);
        }
    }

    /**
     * Getter for #sharedTaskAbortStrategy.
     *
     * @return the shared task abort strategy
     */
    public static SharedTaskContext get() {
        return threadLocalStore.get();
    }

    /**
     * Return the task id for the current thread's task.
     *
     * It delegates to the thread local {@link SharedTaskContext#getTaskId()}.
     *
     * @return The task's id.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String getTaskId() {
        final SharedTaskContext sharedTaskContext = get();
        if (sharedTaskContext == null) {
            return null;
        }
        return sharedTaskContext.getTaskId();
    }

    /**
     * Removes the current {@link SharedTaskContext} from the ThreadLocal store.
     */
    public static void remove() {
        threadLocalStore.remove();
    }
}
