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
package org.hawaiiframework.logging.util;

import static java.util.UUID.randomUUID;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolver for UUID values from HTTP Servlet Request Headers.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class UuidResolver {

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UuidResolver.class);

  /**
   * Resolve the UUID from the header with name {@code headerName}.
   *
   * <p>If the value is not set in the {@code request} then a new UUID will be generated.
   *
   * @param request THe request to get a UUID value from.
   * @param headerName THe header to get the UUID value from.
   * @return The resolved UUID, or a new uuid.
   */
  public UUID resolve(HttpServletRequest request, String headerName) {
    UUID uuid = null;

    String txIdHeader = request.getHeader(headerName);
    if (StringUtils.isNotBlank(txIdHeader)) {
      LOGGER.trace("Found header '{}' with value '{}' in request.", headerName, txIdHeader);
      try {
        uuid = UUID.fromString(txIdHeader);
      } catch (IllegalArgumentException e) {
        LOGGER.error("Could not create UUID from header.", e);
      }
    }

    if (uuid == null) {
      uuid = randomUUID();
      LOGGER.trace("Generated new UUID '{}'.", uuid);
    }

    return uuid;
  }
}
