package org.hawaiiframework.async.task_listener;

import org.springframework.stereotype.Component;

/**
 * Task Context Provider for Kibana log fields.
 */
@Component
public class KibanaLogFieldsTaskListenerProvider implements TaskListenerProvider {

    @Override
    public TaskListener provide() {
        return new KibanaLogFieldsTaskListener();
    }
}
