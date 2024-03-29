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

package org.hawaiiframework.cache.redis.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Redis configuration properties. */
@SuppressWarnings("constructor")
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisCacheConfigurationProperties {

  /** The default expiration. */
  private Duration defaultExpiration;

  public Duration getDefaultExpiration() {
    return defaultExpiration;
  }

  public void setDefaultExpiration(Duration defaultExpiration) {
    this.defaultExpiration = defaultExpiration;
  }
}
