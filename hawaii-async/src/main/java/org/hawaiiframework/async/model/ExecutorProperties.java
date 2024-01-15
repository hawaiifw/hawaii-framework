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
 * Configuration properties for a single executor.
 *
 * @since 2.0.0
 * @author Rutger Lubbers
 * @author Paul Klos
 */
@SuppressWarnings("PMD.DataClass")
public class ExecutorProperties {

  /**
   * The executor name.
   *
   * <p>The name is also used as the thread name prefix, see {@link
   * org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setThreadNamePrefix(String)}
   */
  private String name;

  /** The core pool size. */
  private Integer corePoolSize;

  /**
   * The keep-alive time.
   *
   * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setKeepAliveSeconds(int)
   */
  private Integer keepAliveTime;

  /**
   * The maximum number of pending requests.
   *
   * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setQueueCapacity(int)
   */
  private Integer maxPendingRequests;

  /**
   * The maximum pool size.
   *
   * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setMaxPoolSize(int)
   */
  private Integer maxPoolSize;

  /**
   * Getter for name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for core pool size.
   *
   * @return the core pool size
   */
  public Integer getCorePoolSize() {
    return corePoolSize;
  }

  /**
   * Setter for core pool size.
   *
   * @param corePoolSize the core pool size
   */
  public void setCorePoolSize(Integer corePoolSize) {
    this.corePoolSize = corePoolSize;
  }

  /**
   * Getter for keep-alive time.
   *
   * @return the keep-alive time
   */
  public Integer getKeepAliveTime() {
    return keepAliveTime;
  }

  /**
   * Setter for keep-alive time.
   *
   * @param keepAliveTime the keep-alive time
   */
  public void setKeepAliveTime(Integer keepAliveTime) {
    this.keepAliveTime = keepAliveTime;
  }

  /**
   * Getter for max pending requests.
   *
   * @return the max pending requests
   */
  public Integer getMaxPendingRequests() {
    return maxPendingRequests;
  }

  /**
   * Setter for name.
   *
   * @param maxPendingRequests the max pending requests
   */
  public void setMaxPendingRequests(Integer maxPendingRequests) {
    this.maxPendingRequests = maxPendingRequests;
  }

  /**
   * Getter for max pool size.
   *
   * @return the max pool size
   */
  public Integer getMaxPoolSize() {
    return maxPoolSize;
  }

  /**
   * Setter for max pool size.
   *
   * @param maxPoolSize the max pool size
   */
  public void setMaxPoolSize(Integer maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

    @Override
  public String toString() {
    return String.format(
        "%s (core size '%s', max size '%s', max pending '%s', keep alive '%s')",
        getName(),
        getCorePoolSize(),
        getMaxPoolSize(),
        getMaxPendingRequests(),
        getKeepAliveTime());
  }
}
