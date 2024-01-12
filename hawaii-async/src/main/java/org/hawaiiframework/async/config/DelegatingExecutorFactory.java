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

package org.hawaiiframework.async.config;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.async.DelegatingExecutor;
import org.hawaiiframework.async.model.ExecutorConfigurationProperties;
import org.hawaiiframework.async.model.SystemProperties;
import org.hawaiiframework.async.model.TaskProperties;
import org.hawaiiframework.async.task_listener.TaskListenerFactory;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.task.TaskExecutor;

/**
 * Utility to initialize executors for the asynchronous execution of methods using the @{@link
 * org.springframework.scheduling.annotation.Async}annotation.
 *
 * <p>From the configuration, method per system an executor is registered to be used in the
 * {@code @Async("system.method")} annotation.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 3.0.0
 */
public class DelegatingExecutorFactory {

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingExecutorFactory.class);

  /** Spring's bean factory. */
  private final ConfigurableListableBeanFactory beanFactory;

  /** Utility to register beans in Spring. */
  private final BeanRegistrar registrar;

  /** The executor configuration. */
  private final ExecutorConfigurationProperties configuration;

  /** The set of executor names. */
  private final Set<String> executorNames;

  /**
   * The constructor.
   *
   * @param beanFactory Spring's bean factory.
   * @param registrar Utility to register beans in Spring.
   * @param configuration The executor configuration.
   * @param executorNames The set of executor names.
   */
  public DelegatingExecutorFactory(
      ConfigurableListableBeanFactory beanFactory,
      BeanRegistrar registrar,
      ExecutorConfigurationProperties configuration,
      Set<String> executorNames) {
    this.beanFactory = beanFactory;
    this.registrar = registrar;
    this.configuration = configuration;
    this.executorNames = executorNames;
  }

  /**
   * Create bean aliases for the configured systems.
   *
   * <p>For each task in the system an {@link DelegatingExecutor} is created with the relevant
   * executor as its delegate. This is either the configured executor for the task, or the task's
   * system default executor.
   *
   * <p>If neither have a configured executor, no alias is created and the call will default to the
   * default executor.
   */
  public void createDelegatingExecutors() {
    for (SystemProperties systemProperties : configuration.getSystems()) {
      String systemName = systemProperties.getName();

      // This is the fallback if no executor is defined for a task
      String systemExecutor = systemProperties.getDefaultExecutor();
      if (StringUtils.isNotBlank(systemExecutor) && !executorNames.contains(systemExecutor)) {
        throw new HawaiiException(
            String.format(
                "Executor '%s' of system '%s' is not defined.", systemExecutor, systemName));
      }

      for (TaskProperties taskProperties : systemProperties.getTasks()) {
        String executorName = taskProperties.getExecutor();

        String taskName = String.format("%s.%s", systemName, taskProperties.getMethod());
        if (StringUtils.isNotBlank(executorName)) {
          if (!executorNames.contains(executorName)) {
            throw new HawaiiException(
                String.format(
                    "Executor '%s' of task '%s' is not defined.", executorName, taskName));
          }
          LOGGER.debug("Configuring task '{}' to use executor '{}'.", taskName, executorName);
        } else {
          LOGGER.info(
              "No executor defined for task '{}'. Falling back to system executor '{}'.",
              taskName,
              systemExecutor);
        }

        String executor =
            defaultIfBlank(
                executorName, defaultIfBlank(systemExecutor, configuration.getDefaultExecutor()));
        createTaskExecutorDelegate(taskName, executor);
      }
    }
  }

  /** Creates a delegating executor for the given task name. */
  private void createTaskExecutorDelegate(String taskName, String executor) {
    Map<String, TaskListenerFactory> beansOfType =
        beanFactory.getBeansOfType(TaskListenerFactory.class);
    TaskExecutor delegate = (TaskExecutor) beanFactory.getBean(executor);
    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
    constructorArgumentValues.addIndexedArgumentValue(0, delegate);
    constructorArgumentValues.addIndexedArgumentValue(1, configuration);
    constructorArgumentValues.addIndexedArgumentValue(2, beansOfType.values());
    constructorArgumentValues.addIndexedArgumentValue(3, taskName);
    LOGGER.debug("Registering delegate '{}' to for executor '{}'.", taskName, executor);
    registrar.registerBean(taskName, DelegatingExecutor.class, constructorArgumentValues);
  }
}
