/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging.config;

import org.hawaiiframework.logging.scheduled.ScheduledAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/** Configuration for adding transaction id's to scheduled methods. */
@ConditionalOnProperty(prefix = "hawaii.logging.scheduled", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(name = "org.aspectj.lang.ProceedingJoinPoint")
public class ScheduledConfiguration {

  /**
   * A scheduled aspect bean.
   *
   * @return The scheduled aspect bean.
   */
  @Bean
  public ScheduledAspect scheduledAspect() {
    return new ScheduledAspect();
  }
}
