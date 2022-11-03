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

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voter for various logging filters.
 * Allows configuration of excluded paths and included content types.
 * See {@link HawaiiLoggingConfigurationProperties}.
 */
public class FilterVoter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = getLogger(FilterVoter.class);

    /**
     * The media type voter.
     */
    private final MediaTypeVoter mediaTypeVoter;

    /**
     * The path voter.
     */
    private final PathVoter pathVoter;

    /**
     * The constructor.
     *
     * @param mediaTypeVoter The media type voter.
     * @param pathVoter      The path voter.
     */
    public FilterVoter(final MediaTypeVoter mediaTypeVoter, final PathVoter pathVoter) {
        this.mediaTypeVoter = mediaTypeVoter;
        this.pathVoter = pathVoter;
    }

    /**
     * Returns {@code true} if the filter should be enabled.
     *
     * @param request The request.
     * @return {@code true} if the filter should be enabled.
     */
    public boolean enabled(final HttpServletRequest request) {
        Boolean isEnabled = (Boolean) request.getAttribute(getAttributeName());
        LOGGER.trace("Got '{}' from attribute.", isEnabled);
        if (isEnabled == null) {
            // must compute
            final boolean mediaTypeAllowed = mediaTypeVoter.mediaTypeAllowed(request.getContentType());
            final boolean pathAllowed = pathVoter.pathAllowed(request.getServletPath());
            isEnabled = mediaTypeAllowed && pathAllowed;
            request.setAttribute(getAttributeName(), isEnabled);
        }
        LOGGER.trace("Is enabled: '{}'.", isEnabled);

        return isEnabled;
    }

    private String getAttributeName() {
        return this.getClass().getPackageName() + ".FILTER_VOTER";
    }

}
