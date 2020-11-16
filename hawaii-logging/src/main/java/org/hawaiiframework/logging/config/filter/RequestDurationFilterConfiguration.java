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

package org.hawaiiframework.logging.config.filter;

import org.hawaiiframework.logging.web.filter.RequestDurationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link RequestDurationFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled", matchIfMissing = true)
@Configuration
public class RequestDurationFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public RequestDurationFilterConfiguration(final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link RequestDurationFilter} bean.
     *
     * @return the {@link RequestDurationFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled", matchIfMissing = true)
    public RequestDurationFilter requestDurationFilter() {
        return new RequestDurationFilter();
    }

    /**
     * Register the {@link #requestDurationFilter()} bean.
     *
     * @param requestDurationFilter the request duration filter
     * @return the {@link #requestDurationFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<RequestDurationFilter> requestDurationFilterRegistration(
            final RequestDurationFilter requestDurationFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getRequestDuration();
        return createFilterRegistrationBean(requestDurationFilter, filterProperties);
    }
}
