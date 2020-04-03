package org.hawaiiframework.async.task_listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for listeners.
 */
@Configuration
@Import(OpenTracingAsyncTaskListenerFactoryConfiguration.class)
public class TaskListenerFactoryConfiguration {

    /**
     * Kibana log fields listener.
     *
     * @return The bean.
     */
    @Bean
    public KibanaLogFieldsTaskListenerFactory kibanaLogFieldsTaskListenerFactory() {
        return new KibanaLogFieldsTaskListenerFactory();
    }

    /**
     * Logging listener, logs the start, end and statistics of a task.
     *
     * @return The bean.
     */
    @Bean
    public LoggingTaskListenerFactory loggingTaskListenerFactory() {
        return new LoggingTaskListenerFactory();
    }
}
