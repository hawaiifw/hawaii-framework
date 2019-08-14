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
package org.hawaiiframework.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the configuration properties for the logging filter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Component
@ConfigurationProperties(prefix = "hawaii.logging")
@SuppressWarnings("PMD.DataClass")
public class HawaiiLoggingConfigurationProperties {

    /**
     * The content types we log to the console / log file(s).
     */
    private List<String> allowedContentTypes = new ArrayList<>();

    /**
     * The logging filters configuration properties.
     */
    @NestedConfigurationProperty
    private HawaiiLoggingFiltersConfigurationProperties filters;

    /**
     * Getter for the allowed content types.
     *
     * @return the allowed content types
     */
    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    /**
     * Setter for the allowed content types.
     *
     * @param allowedContentTypes the allowed content types
     */
    public void setAllowedContentTypes(final List<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }

    /**
     * Getter for the logging filters configuration properties.
     * @return the properties
     */
    public HawaiiLoggingFiltersConfigurationProperties getFilters() {
        return filters;
    }

    /**
     * Setter for the logging filters configuration properties.
     * @param filters the properties
     */
    public void setFilters(final HawaiiLoggingFiltersConfigurationProperties filters) {
        this.filters = filters;
    }
}
