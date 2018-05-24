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

import org.hawaiiframework.logging.util.ClientIpResolver;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.logging.web.filter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Configures the logging based on the application properties.
 *
 * This class creates filter beans for the enabled filters.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(HawaiiLoggingConfigurationProperties.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HawaiiLoggingConfiguration {

    @Autowired
    private HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled")
    public FilterRegistrationBean registerKibanaLogFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLog();
        final Filter filter = new KibanaLogFilter(createClientIpResolver(filterProperties));
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log-cleanup", name = "enabled")
    public FilterRegistrationBean registerKibanaLogCleanupFilter() {
        final Filter filter = new KibanaLogCleanupFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLogCleanup();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled")
    public FilterRegistrationBean registerRequestDurationFilter() {
        final Filter filter = new RequestDurationFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestDuration();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-id", name = "enabled")
    public FilterRegistrationBean registerRequestIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestId();
        final Filter filter = new RequestIdFilter(filterProperties.getHttpHeader());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public FilterRegistrationBean registerRequestResponseLogFilter() {
        final RequestResponseLogFilterConfiguration filterProperties = hawaiiLoggingConfigurationProperties.getRequestResponse();
        final Filter filter = new RequestResponseLogFilter(filterProperties, httpRequestResponseLogUtil());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.transaction-id", name = "enabled")
    public FilterRegistrationBean registerTransactionIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getTransactionId();
        final Filter filter = new TransactionIdFilter(filterProperties.getHttpHeader());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled")
    public FilterRegistrationBean registerUserDetailsFilter() {
        final Filter filter = new UserDetailsFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getUserDetails();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public HttpRequestResponseLogUtil httpRequestResponseLogUtil() {
        return new HttpRequestResponseLogUtil();
    }

    private ClientIpResolver createClientIpResolver(final HttpHeaderLoggingFilterProperties filterProperties) {
        return new ClientIpResolver(filterProperties.getHttpHeader());
    }

    private FilterRegistrationBean createFilterRegistrationBean(final Filter filter, final LoggingFilterProperties filterProperties) {
        final FilterRegistrationBean result = new FilterRegistrationBean(filter);
        result.setOrder(filterProperties.getOrder());
        return result;
    }

}
