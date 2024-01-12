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

import static org.slf4j.LoggerFactory.getLogger;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.hawaiiframework.logging.model.PathDefinition;
import org.slf4j.Logger;

/**
 * Request voter allows configuration of excluded URL request based on paths patterns and HTTP
 * methods.
 */
public class RequestVoter {

  /** The logger. */
  private static final Logger LOGGER = getLogger(RequestVoter.class);

  /** The excluded paths. */
  private final List<PathDefinition> exclusions;

  /**
   * The constructor.
   *
   * @param properties The properties.
   */
  public RequestVoter(HawaiiLoggingConfigurationProperties properties) {
    exclusions = properties.getExcludePaths();
    LOGGER.info("Excluded paths '{}'.", exclusions);
  }

  /**
   * Return {@code true} if the {@code request} is allowed.
   *
   * @param request The request to check.
   * @return {@code true} if the {@code request} is allowed.
   */
  public boolean allowed(HttpServletRequest request) {
    if (!isEmpty()) {
      String method = request.getMethod();
      String path = request.getServletPath();
      for (PathDefinition exclusion : exclusions) {
        boolean excluded = exclusion.matches(method, path);
        LOGGER.trace("Request'{} {}' matches '{}': '{}'.", method, path, exclusion, excluded);
        if (excluded) {
          LOGGER.trace(
              "Request'{} {}' excluded because of match with '{}'.", method, path, exclusion);
          return false;
        }
      }
    }

    return true;
  }

  private boolean isEmpty() {
    return exclusions == null || exclusions.isEmpty();
  }
}
