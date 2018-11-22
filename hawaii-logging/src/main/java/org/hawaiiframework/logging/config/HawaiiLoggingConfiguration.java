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
import org.hawaiiframework.logging.web.filter.KibanaLogCleanupFilter;
import org.hawaiiframework.logging.web.filter.KibanaLogFilter;
import org.hawaiiframework.logging.web.filter.RequestDurationFilter;
import org.hawaiiframework.logging.web.filter.RequestIdFilter;
import org.hawaiiframework.logging.web.filter.RequestResponseLogFilter;
import org.hawaiiframework.logging.web.filter.TransactionIdFilter;
import org.hawaiiframework.logging.web.filter.UserDetailsFilter;
import org.hawaiiframework.logging.web.filter.ClassMethodNameFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

/**
 * Configures the logging based on the application properties.
 *
 * This class creates filter beans for the enabled filters.
 *
 * @author Wouter Eerdekens
 * @author Paul Klos
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(HawaiiLoggingConfigurationProperties.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HawaiiLoggingConfiguration {

    private static final EnumSet<DispatcherType> ALL_DISPATCHER_TYPES = EnumSet.allOf(DispatcherType.class);

    /**
     * The the logging configuration properties.
     */
    private final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

    /**
     * Autowired constructor.
     *
     * @param hawaiiLoggingConfigurationProperties the logging configuration properties
     */
    public HawaiiLoggingConfiguration(
            final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        this.hawaiiLoggingConfigurationProperties = hawaiiLoggingConfigurationProperties;
    }

    /**
     * Create the {@link KibanaLogFilter} bean.
     *
     * @return the {@link KibanaLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled")
    public KibanaLogFilter kibanaLogFilter() {
        return new KibanaLogFilter(createClientIpResolver(hawaiiLoggingConfigurationProperties.getKibanaLog()));
    }

    /**
     * Register the {@link #kibanaLogFilter()} bean.
     *
     * @param kibanaLogFilter the Kibana log filter
     * @return the {@link KibanaLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled")
    public FilterRegistrationBean kibanaLogFilterRegistration(final KibanaLogFilter kibanaLogFilter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLog();
        return createFilterRegistrationBean(kibanaLogFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create the {@link KibanaLogCleanupFilter} bean.
     *
     * @return the {@link KibanaLogCleanupFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log-cleanup", name = "enabled")
    public KibanaLogCleanupFilter kibanaLogCleanupFilter() {
        return new KibanaLogCleanupFilter();
    }

    /**
     * Register the {@link #kibanaLogCleanupFilter()} bean.
     *
     * @param kibanaLogCleanupFilter the Kibana log cleanup filter
     * @return the {@link #kibanaLogCleanupFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log-cleanup", name = "enabled")
    public FilterRegistrationBean kibanaLogCleanupFilterRegistration(final KibanaLogCleanupFilter kibanaLogCleanupFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getKibanaLogCleanup();
        return createFilterRegistrationBean(kibanaLogCleanupFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create the {@link RequestDurationFilter} bean.
     *
     * @return the {@link RequestDurationFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled")
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
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled")
    public FilterRegistrationBean requestDurationFilterRegistration(final RequestDurationFilter requestDurationFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestDuration();
        return createFilterRegistrationBean(requestDurationFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create the {@link RequestIdFilter} bean.
     *
     * @return the {@link RequestIdFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-id", name = "enabled")
    public RequestIdFilter requestIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestId();
        return new RequestIdFilter(filterProperties.getHttpHeader());
    }

    /**
     * Register the {@link RequestIdFilter} bean.
     *
     * @return the {@link #requestIdFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-id", name = "enabled")
    public FilterRegistrationBean requestIdFilterRegistration(final RequestIdFilter requestIdFilter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getRequestId();
        return createFilterRegistrationBean(requestIdFilter, filterProperties, ALL_DISPATCHER_TYPES);
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
    public FilterRegistrationBean requestResponseLogFilterRegistration(final RequestResponseLogFilter requestResponseLogFilter) {
        final RequestResponseLogFilterConfiguration filterProperties = hawaiiLoggingConfigurationProperties.getRequestResponse();
        return createFilterRegistrationBean(requestResponseLogFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create the {@link TransactionIdFilter} bean.
     *
     * @return the {@link TransactionIdFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.transaction-id", name = "enabled")
    public TransactionIdFilter transactionIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getTransactionId();
        return new TransactionIdFilter(filterProperties.getHttpHeader());
    }

    /**
     * Register the {@link #transactionIdFilter()} bean.
     *
     * @param transactionIdFilter the transaction id filter
     * @return the {@link #transactionIdFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.transaction-id", name = "enabled")
    public FilterRegistrationBean transactionIdFilterRegistration(final TransactionIdFilter transactionIdFilter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getTransactionId();
        return createFilterRegistrationBean(transactionIdFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create the {@link ClassMethodNameFilter} bean.
     *
     * @param applicationContext the application context of the Spring Boot Application
     * @return the {@link ClassMethodNameFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.class-method-name", name = "enabled")
    public ClassMethodNameFilter classMethodNameFilter(final ApplicationContext applicationContext) {
        return new ClassMethodNameFilter(applicationContext);
    }

    /**
     * Register the {@link ClassMethodNameFilter} bean.
     *
     * @param classMethodNameFilter the class method name filter
     * @return the {@link ClassMethodNameFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.class-method-name", name = "enabled")
    public FilterRegistrationBean classMethodNameFilterRegistration(final ClassMethodNameFilter classMethodNameFilter) {
        final var filterProperties = hawaiiLoggingConfigurationProperties.getClassMethodName();
        return createFilterRegistrationBean(classMethodNameFilter, filterProperties, ALL_DISPATCHER_TYPES);
    }

    /**
     * Create a {@link HttpRequestResponseLogUtil} bean.
     *
     * This is required for the {@link RequestResponseLogFilter}, see {@link #requestResponseLogFilter(HttpRequestResponseLogUtil)}.
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
     * @param dispatcherTypes the request dispatcher types the filter is used for
     * @return the wrapped filter
     */
    private FilterRegistrationBean createFilterRegistrationBean(
            final Filter filter,
            final LoggingFilterProperties filterProperties,
            final EnumSet<DispatcherType> dispatcherTypes) {
        final FilterRegistrationBean<?> result = new FilterRegistrationBean<>(filter);
        result.setOrder(filterProperties.getOrder());
        result.setDispatcherTypes(dispatcherTypes);
        return result;
    }

    /**
     * Configures the user details filter.
     * This configuration is separated so the Hawaii logging configuration can be used without Spring Security.
     */
    @Configuration
    @ConditionalOnClass(UserDetails.class)
    static class UserDetailsFilterConfiguration {

        /**
         * The the logging configuration properties.
         */
        private final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

        /**
         * Autowired constructor.
         *
         * @param hawaiiLoggingConfigurationProperties the logging configuration properties
         */
        UserDetailsFilterConfiguration(
                final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
            this.hawaiiLoggingConfigurationProperties = hawaiiLoggingConfigurationProperties;
        }

        /**
         * Create the {@link UserDetailsFilter} bean.
         *
         * @return the {@link UserDetailsFilter} bean
         */
        @Bean
        @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled")
        public UserDetailsFilter userDetailsFilter() {
            return new UserDetailsFilter();
        }

        /**
         * Register the {@link #userDetailsFilter()} bean.
         *
         * @param userDetailsFilter the user details filter
         * @return the {@link #userDetailsFilter()} bean, wrapped in a {@link FilterRegistrationBean}
         */
        @Bean
        @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled")
        public FilterRegistrationBean userDetailsFilterRegistration(final UserDetailsFilter userDetailsFilter) {
            final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getUserDetails();
            final FilterRegistrationBean<UserDetailsFilter> result = new FilterRegistrationBean<>(userDetailsFilter);
            result.setOrder(filterProperties.getOrder());
            result.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
            return result;
        }
    }
}
