package org.hawaiiframework.async.task_listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for listeners. */
@Configuration
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
