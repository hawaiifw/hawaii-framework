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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

/** Media type voter allows configuration of allowed media types. */
public class MediaTypeVoter {

  /** The logger. */
  private static final Logger LOGGER = getLogger(MediaTypeVoter.class);

  /** The configured content types. */
  private final List<MediaType> contentTypes;

  private final boolean matchIfEmpty;

  /** The constructor with content types and whether to match if there are no content types. */
  public MediaTypeVoter(List<MediaType> contentTypes, boolean matchIfEmpty) {
    this.contentTypes = contentTypes;
    this.matchIfEmpty = matchIfEmpty;
    LOGGER.debug("Configured content types: '{}'.", contentTypes);
  }

  /** Check if the {@code mediaType} is one of the configured content type. */
  public boolean mediaTypeMatches(String contentType) {
    return mediaTypeMatches(parseMediaType(contentType));
  }

  /** Check if the {@code mediaType} is one of the configured content type. */
  public boolean mediaTypeMatches(MediaType mediaType) {
    boolean matches = false;

    if (mediaType == null || contentTypes == null || contentTypes.isEmpty()) {
      matches = matchIfEmpty;
    } else {

      for (MediaType allowedType : contentTypes) {
        boolean includes = allowedType.includes(mediaType);
        LOGGER.trace("Type '{}' contains '{}': '{}'.", allowedType, mediaType, includes);
        if (includes) {
          matches = true;
        }
      }

      LOGGER.debug("Media type '{}' does not match, since it is not configured.", mediaType);
    }

    return matches;
  }

  private static MediaType parseMediaType(String contentType) {
    if (isNotBlank(contentType)) {
      try {
        return MediaType.parseMediaType(contentType);
      } catch (InvalidMediaTypeException exception) {
        LOGGER.info("Got error parsing content type '{}'.", contentType, exception);
      }
    }

    return null;
  }
}
