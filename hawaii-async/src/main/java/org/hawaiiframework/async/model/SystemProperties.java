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

package org.hawaiiframework.async.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for a system.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class SystemProperties {

    /**
     * The system name.
     */
    private String name;

    /**
     * The default executor.
     * <p>
     * This is the fallback executor that will be used if no executor is configured for a task.
     */
    private String defaultExecutor;

    /**
     * The default timeout.
     */
    private Integer defaultTimeout;

    /**
     * The tasks.
     */
    private List<TaskProperties> tasks = new ArrayList<>();

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

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
    public void setDefaultExecutor(final String defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    /**
     * Getter for the default timeout.
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
    public void setDefaultTimeout(final Integer defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    /**
     * Getter for the tasks.
     *
     * @return the tasks
     */
    public List<TaskProperties> getTasks() {
        return tasks;
    }

    /**
     * Setter for the tasks.
     *
     * @param tasks the tasks
     */
    public void setTasks(final List<TaskProperties> tasks) {
        this.tasks = tasks;
    }

    /**
     * Retrieve the properties for a named task.
     *
     * @param taskName the task name
     * @return the properties
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public TaskProperties getTaskPropertiesForName(final String taskName) {
        return tasks.stream().filter(taskProperties -> taskProperties.isNameMatch(taskName)).findFirst().orElse(null);
    }

    /**
     * Check if this {@link SystemProperties} name matches the given name.
     *
     * @param systemName the name to check
     * @return true if the names match
     */
    public boolean nameMatches(final String systemName) {
        return name.equals(systemName);
    }
}
