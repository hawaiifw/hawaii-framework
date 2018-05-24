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

/**
 * Properties representing configuration for an individual task.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class TaskProperties {

    /**
     * The method name.
     */
    private String method;

    /**
     * The executor to use for this task.
     */
    private String executor;

    /**
     * The timeout for this task in seconds.
     * <p>
     * Overrides the system timeout, see {@link SystemProperties#defaultTimeout}.
     */
    private Integer timeout;

    /**
     * Getter for method name.
     *
     * @return the method name
     */
    public String getMethod() {
        return method;
    }

    /**
     * Setter for method name.
     *
     * @param method the method name
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Getter for executor.
     *
     * @return the executor
     */
    public String getExecutor() {
        return executor;
    }

    /**
     * Setter for executor.
     *
     * @param executor the executor
     */
    public void setExecutor(final String executor) {
        this.executor = executor;
    }

    /**
     * Getter for timeout.
     *
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Setter for timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(final Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Determine if this TaskProperties' name matches the given name.
     *
     * @param taskName the name to check
     * @return true if the names match
     */
    public boolean isNameMatch(final String taskName) {
        return method.equals(taskName);
    }
}
