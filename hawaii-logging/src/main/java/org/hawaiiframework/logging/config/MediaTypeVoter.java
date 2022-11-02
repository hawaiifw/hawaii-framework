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

import org.slf4j.Logger;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Media type voter allows configuration of allowed media types.
 */
public class MediaTypeVoter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = getLogger(MediaTypeVoter.class);

    /**
     * The allowed content types.
     */
    private final List<MediaType> allowedContentTypes;

    /**
     * The constructor.
     *
     * @param properties The properties.
     */
    public MediaTypeVoter(final HawaiiLoggingConfigurationProperties properties) {
        allowedContentTypes = properties.getAllowedContentTypes();
        LOGGER.info("Created. Allowed content types: '{}'.", allowedContentTypes);
    }

    /**
     * Returns {@code true} if the {@code contentType} is allowed.
     * @param contentType The content type to check.
     * @return  {@code true} if the {@code contentType} is allowed.
     */
    public boolean mediaTypeAllowed(final String contentType) {
        return mediaTypeAllowed(parseMediaType(contentType));
    }

    /**
     * Returns {@code true} if the {@code mediaType} is allowed.
     * @param mediaType The media type to check.
     * @return  {@code true} if the {@code mediaType} is allowed.
     */
    public boolean mediaTypeAllowed(final MediaType mediaType) {
        boolean allowed = false;

        if (mediaType == null || allowedContentTypes == null || allowedContentTypes.isEmpty()) {
            allowed = true;
        } else {

            for (final MediaType allowedType : allowedContentTypes) {
                final boolean includes = allowedType.includes(mediaType);
                LOGGER.trace("Type '{}' contains '{}': '{}'.", allowedType, mediaType, includes);
                if (includes) {
                    allowed = true;
                }
            }

            LOGGER.debug("Media type '{}' is excluded, since it is not configured in 'hawaii.logging.allowed-content-types'.", mediaType);
        }

        return allowed;
    }

    private MediaType parseMediaType(final String contentType) {
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
