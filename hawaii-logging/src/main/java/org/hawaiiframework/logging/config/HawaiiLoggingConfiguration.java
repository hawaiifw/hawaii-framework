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

    /**
     * Create and register the {@link KibanaLogFilter} bean.
     *
     * @return the {@link KibanaLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled")
    public FilterRegistrationBean kibanaLogFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLog();
        final Filter filter = new KibanaLogFilter(createClientIpResolver(filterProperties));
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link KibanaLogCleanupFilter} bean.
     *
     * @return the {@link KibanaLogCleanupFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log-cleanup", name = "enabled")
    public FilterRegistrationBean kibanaLogCleanupFilter() {
        final Filter filter = new KibanaLogCleanupFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLogCleanup();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link RequestDurationFilter} bean.
     *
     * @return the {@link RequestDurationFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled")
    public FilterRegistrationBean requestDurationFilter() {
        final Filter filter = new RequestDurationFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestDuration();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link RequestIdFilter} bean.
     *
     * @return the {@link RequestIdFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-id", name = "enabled")
    public FilterRegistrationBean requestIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestId();
        final Filter filter = new RequestIdFilter(filterProperties.getHttpHeader());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link RequestResponseLogFilter} bean.
     *
     * @return the {@link RequestResponseLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public FilterRegistrationBean requestResponseLogFilter() {
        final RequestResponseLogFilterConfiguration filterProperties = hawaiiLoggingConfigurationProperties.getRequestResponse();
        final Filter filter = new RequestResponseLogFilter(filterProperties, httpRequestResponseLogUtil());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link TransactionIdFilter} bean.
     *
     * @return the {@link TransactionIdFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.transaction-id", name = "enabled")
    public FilterRegistrationBean transactionIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getTransactionId();
        final Filter filter = new TransactionIdFilter(filterProperties.getHttpHeader());
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create and register the {@link UserDetailsFilter} bean.
     *
     * @return the {@link UserDetailsFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled")
    public FilterRegistrationBean userDetailsFilter() {
        final Filter filter = new UserDetailsFilter();
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getUserDetails();
        return createFilterRegistrationBean(filter, filterProperties);
    }

    /**
     * Create a {@link HttpRequestResponseLogUtil} bean.
     *
     * This is reuired for the {@link RequestResponseLogFilter}, see {@link #requestResponseLogFilter()}.
     *
     * @return the bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public HttpRequestResponseLogUtil httpRequestResponseLogUtil() {
        return new HttpRequestResponseLogUtil();
    }

    /**
     * Helper method to create the client ip resolver required by {@link KibanaLogFilter}.
     *
     * @param filterProperties the configuration properties
     * @return the client ip resolver
     */
    private ClientIpResolver createClientIpResolver(final HttpHeaderLoggingFilterProperties filterProperties) {
        return new ClientIpResolver(filterProperties.getHttpHeader());
    }

    /**
     * Helper method to wrap a filter in a {@link FilterRegistrationBean} with the configured order.
     *
     * @param filter the filter
     * @param filterProperties the configuration properties
     * @return the wrapped filter
     */
    private FilterRegistrationBean createFilterRegistrationBean(final Filter filter, final LoggingFilterProperties filterProperties) {
        final FilterRegistrationBean result = new FilterRegistrationBean(filter);
        result.setOrder(filterProperties.getOrder());
        return result;
    }

}
