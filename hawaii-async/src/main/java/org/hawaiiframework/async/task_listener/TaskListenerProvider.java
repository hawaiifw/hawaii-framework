package org.hawaiiframework.async.task_listener;

/**
 * Provider for task contexts. Providers can be used to pass data between the caller and the async tasks.
 */
public interface TaskListenerProvider {

    /**
     * Create a task context.
     *
     * @return The task context for this provider.
     */
    TaskListener provide();
}
