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
import org.springframework.util.AntPathMatcher;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Path voter allows configuration of excluded paths.
 */
public class PathVoter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = getLogger(PathVoter.class);

    /**
     * The excluded paths.
     */
    private final List<String> excludedPaths;

    /**
     * The constructor.
     *
     * @param properties The properties.
     */
    public PathVoter(final HawaiiLoggingConfigurationProperties properties) {
        excludedPaths = properties.getExcludePaths();
        LOGGER.info("Excluded paths '{}'.", excludedPaths);
    }

    /**
     * Return {@code true} if the {@code servletPath} is allowed.
     *
     * @param servletPath The path to check.
     * @return {@code true} if the {@code servletPath} is allowed.
     */
    public boolean pathAllowed(final String servletPath) {
        if (!hasExcludedPaths()) {
            final AntPathMatcher pathMatcher = new AntPathMatcher();
            for (final String excludedPath : excludedPaths) {
                final boolean excluded = pathMatcher.match(excludedPath, servletPath);
                LOGGER.trace("Excluded path '{}' matches '{}': '{}'.", excludedPath, servletPath, excluded);
                if (excluded) {
                    LOGGER.debug("Path '{}' excluded because of match with '{}'.", servletPath, excludedPath);
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasExcludedPaths() {
        return excludedPaths == null || excludedPaths.isEmpty();
    }
}
