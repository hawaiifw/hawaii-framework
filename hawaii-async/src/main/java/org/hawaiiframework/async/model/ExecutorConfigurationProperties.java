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

package org.hawaiiframework.async.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for the asynchronous execution. The properties consist of two parts:
 *
 * <ol>
 *   <li>Executors
 *   <li>Systems, containing Calls
 * </ol>
 *
 * <p>From each of the executor configurations, a {@link
 * org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor} will be created. The default
 * executor is the fallback for any task not specifically configured to an executor.
 *
 * <p>A system is a group of tasks, assumed to belong to a single system, for example a backend
 * system. Both a system and a call can be assigned to an executor, where the task configuration
 * takes precedence.
 *
 * <p>Note that any task not specifically assigned to an executor will default to the global default
 * executor.
 *
 * @since 2.0.0
 * @author Rutger Lubbers
 * @author Paul Klos
 */
@SuppressWarnings("PMD.DataClass")
public class ExecutorConfigurationProperties {

  /** The separator for a task name. Set to a period. */
  private static final String SEPARATOR = "\\.";

  /**
   * The default queue.
   *
   * <p>Will be used to set the default executor on the Spring async framework.
   */
  private String defaultExecutor;

  /**
   * The default timeout.
   *
   * <p>This is the fallback in case no timeout is specified on either system or call.
   */
  private Integer defaultTimeout = 10;

  /** The (core) pool size of the async task timeout executor. */
  private Integer asyncTimeoutExecutorPoolSize = 4;

  /** The configured executors. */
  private List<ExecutorProperties> executors = new ArrayList<>();

  /** The configured systems. */
  private List<SystemProperties> systems = new ArrayList<>();

  /**
   * Getter for the default executor.
   *
   * @return the default executor
   */
  public String getDefaultExecutor() {
    return defaultExecutor;
  }

  /**
   * Setter for the default executor.
   *
   * @param defaultExecutor the default executor
   */
  public void setDefaultExecutor(String defaultExecutor) {
    this.defaultExecutor = defaultExecutor;
  }

  /**
   * Getter for default timeout.
   *
   * @return the default timeout
   */
  public Integer getDefaultTimeout() {
    return defaultTimeout;
  }

  /**
   * Setter for the default timeout.
   *
   * @param defaultTimeout the default timeout
   */
  public void setDefaultTimeout(Integer defaultTimeout) {
    this.defaultTimeout = defaultTimeout;
  }

  /**
   * Getter for the async task timeout executor pool size.
   *
   * @return the core pool size to use.
   */
  public Integer getAsyncTimeoutExecutorPoolSize() {
    return asyncTimeoutExecutorPoolSize;
  }

  /**
   * Setter for the async task timeout executor pool size.
   *
   * @param asyncTimeoutExecutorPoolSize the core pool size to set.
   */
  public void setAsyncTimeoutExecutorPoolSize(Integer asyncTimeoutExecutorPoolSize) {
    this.asyncTimeoutExecutorPoolSize = asyncTimeoutExecutorPoolSize;
  }

  /**
   * Getter for the executors.
   *
   * @return the executors
   */
  public List<ExecutorProperties> getExecutors() {
    return executors;
  }

  /**
   * Setter for the executors.
   *
   * @param executors the executors
   */
  public void setExecutors(List<ExecutorProperties> executors) {
    this.executors = executors;
  }

  /**
   * Convenience method to add properties for a single executor.
   *
   * @param executor the executor properties
   */
  public void addExecutor(ExecutorProperties executor) {
    if (executors == null) {
      executors = new ArrayList<>();
    }
    executors.add(executor);
  }

  /**
   * Getter for the systems.
   *
   * @return the systems
   */
  public List<SystemProperties> getSystems() {
    return systems;
  }

  /**
   * Setter for the systems.
   *
   * @param systems the systems
   */
  public void setSystems(List<SystemProperties> systems) {
    this.systems = systems;
  }

  /**
   * Convenience method to add properties for a single system.
   *
   * @param system the system properties
   */
  public void addSystem(SystemProperties system) {
    if (systems == null) {
      systems = new ArrayList<>();
    }
    systems.add(system);
  }

  /**
   * Determine the timeout for a task.
   *
   * <p>The task name must be formatted as <code>{system}.{task}</code>. If a specific timeout is
   * configured for the task, it is returned. Otherwise, the system's default timeout or the general
   * default timeout is returned as a fallback value.
   *
   * @param taskName the task name
   * @return the timeout, or null if the task or system doesn't exist
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public Integer getTaskTimeout(String taskName) {
    String[] parts = taskName.split(SEPARATOR);

    SystemProperties systemProperties = getSystemPropertiesForName(parts[0]);

    Integer timeout = null;

    if (systemProperties != null) {
      TaskProperties taskProperties = systemProperties.getTaskPropertiesForName(parts[1]);
      if (taskProperties != null) {
        timeout = taskProperties.getTimeout();
      }
      if (timeout == null) {
        timeout = systemProperties.getDefaultTimeout();
      }
    }
    if (timeout == null) {
      timeout = getDefaultTimeout();
    }
    return timeout;
  }

  /**
   * Retrieve the properties for the system with the given name.
   *
   * @param systemName the system name
   * @return the properties
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public SystemProperties getSystemPropertiesForName(String systemName) {
    return systems.stream()
        .filter(systemProperties -> systemProperties.nameMatches(systemName))
        .findFirst()
        .orElse(null);
  }
}
