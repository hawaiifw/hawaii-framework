package org.hawaiiframework.async.task_listener;

import org.springframework.stereotype.Component;

/**
 * Provider for logging task events.
 */
@Component
public class LoggingTaskListenerProvider implements TaskListenerProvider {

    @Override
    public TaskListener provide() {
        return new LoggingTaskListener();
    }
}
