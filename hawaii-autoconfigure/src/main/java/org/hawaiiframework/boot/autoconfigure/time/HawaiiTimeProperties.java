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

package org.hawaiiframework.boot.autoconfigure.time;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@ConfigurationProperties("hawaii.time")
@SuppressWarnings("PMD.DataClass")
public class HawaiiTimeProperties {

  private boolean enabled = true;

  private String timezone = "UTC";

  /**
   * Whether hawaii time is enabled.
   *
   * @return whether the time is enabled.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Whether hawaii time is enabled.
   *
   * @param enabled The flag to set.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Retrieve the time zone.
   *
   * @return the time zone.
   */
  public String getTimezone() {
    return timezone;
  }

  /**
   * Set the time zone.
   *
   * @param timezone The time zone to set.
   */
  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
