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

import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.logging.web.filter.RequestResponseLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link RequestResponseLogFilter}.
 */
@Configuration
public class RequestResponseLogFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingConfigurationProperties The logging configuration properties.
     */
    public RequestResponseLogFilterConfiguration(final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        this.hawaiiLoggingConfigurationProperties = hawaiiLoggingConfigurationProperties;
    }

    /**
     * Create the request/response logging filter bean.
     *
     * @return the {@link RequestResponseLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public RequestResponseLogFilter requestResponseLogFilter(final HttpRequestResponseLogUtil httpRequestResponseLogUtil) {
        return new RequestResponseLogFilter(hawaiiLoggingConfigurationProperties.getRequestResponse(), httpRequestResponseLogUtil);
    }

    /**
     * Create and register the {@link RequestResponseLogFilter} bean.
     *
     * @return the {@link #requestResponseLogFilter(HttpRequestResponseLogUtil)} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public FilterRegistrationBean<RequestResponseLogFilter> requestResponseLogFilterRegistration(
            final RequestResponseLogFilter requestResponseLogFilter) {
        final RequestResponseLogFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestResponse();
        return createFilterRegistrationBean(requestResponseLogFilter, filterProperties);
    }

}
