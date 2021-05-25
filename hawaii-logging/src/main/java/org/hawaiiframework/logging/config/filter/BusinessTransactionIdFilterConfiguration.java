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

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

import org.hawaiiframework.logging.web.filter.BusinessTransactionIdFilter;
import org.hawaiiframework.logging.web.filter.TransactionIdFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the {@link TransactionIdFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.business-transaction-id", name = "enabled", matchIfMissing = true)
@Configuration
public class BusinessTransactionIdFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public BusinessTransactionIdFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link TransactionIdFilter} bean.
     *
     * @return the {@link TransactionIdFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.business-transaction-id", name = "enabled", matchIfMissing = true)
    public BusinessTransactionIdFilter businessTransactionIdFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getBusinessTransactionId();
        return new BusinessTransactionIdFilter(filterProperties.getHttpHeader());
    }

    /**
     * Register the {@link #businessTransactionIdFilter()} bean.
     *
     * @param businessTransactionIdFilter the business transaction id filter
     * @return the {@link #businessTransactionIdFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.business-transaction-id", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<BusinessTransactionIdFilter> businessTransactionIdFilterRegistration(
            final BusinessTransactionIdFilter businessTransactionIdFilter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getBusinessTransactionId();
        return createFilterRegistrationBean(businessTransactionIdFilter, filterProperties);
    }

}
