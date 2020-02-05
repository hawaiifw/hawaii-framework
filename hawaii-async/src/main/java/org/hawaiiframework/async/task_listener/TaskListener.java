package org.hawaiiframework.async.task_listener;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.springframework.core.Ordered;

/**
 * A context for shared data between the async task and it's caller.
 * The caller can inject data into the task's context.
 */
public interface TaskListener extends Ordered {

    /**
     * Set the shared task context in this task context. Invoked before the set.
     *
     * @param sharedTaskContext the shared task context.
     */
    default void setSharedTaskContext(SharedTaskContext sharedTaskContext) {
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
