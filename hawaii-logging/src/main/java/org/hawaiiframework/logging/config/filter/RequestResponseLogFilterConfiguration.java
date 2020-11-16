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

import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.web.filter.RequestResponseLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link RequestResponseLogFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled", matchIfMissing = true)
@Configuration
public class RequestResponseLogFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    private final HawaiiRequestResponseLogger hawaiiLogger;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public RequestResponseLogFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties,
            final HawaiiRequestResponseLogger hawaiiLogger) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
        this.hawaiiLogger = hawaiiLogger;
    }

    /**
     * Create the request/response logging filter bean.
     *
     * @return the {@link RequestResponseLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled", matchIfMissing = true)
    public RequestResponseLogFilter requestResponseLogFilter() {
        return new RequestResponseLogFilter(hawaiiLogger);
    }

    /**
     * Create and register the {@link RequestResponseLogFilter} bean.
     *
     * @return the requestResponseLogFilter bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<RequestResponseLogFilter> requestResponseLogFilterRegistration(
            final RequestResponseLogFilter requestResponseLogFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getRequestResponse();
        return createFilterRegistrationBean(requestResponseLogFilter, filterProperties);
    }

}
