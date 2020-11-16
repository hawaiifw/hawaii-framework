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

import org.hawaiiframework.logging.util.ClientIpResolver;
import org.hawaiiframework.logging.web.filter.KibanaLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link KibanaLogFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled", matchIfMissing = true)
@Configuration
public class KibanaLogFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public KibanaLogFilterConfiguration(final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link KibanaLogFilter} bean.
     *
     * @return the {@link KibanaLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled", matchIfMissing = true)
    public KibanaLogFilter kibanaLogFilter() {
        return new KibanaLogFilter(createClientIpResolver(hawaiiLoggingFilterConfigurationProperties.getKibanaLog()));
    }

    /**
     * Register the {@link #kibanaLogFilter()} bean.
     *
     * @param kibanaLogFilter the Kibana log filter
     * @return the {@link KibanaLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana-log", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<KibanaLogFilter> kibanaLogFilterRegistration(final KibanaLogFilter kibanaLogFilter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getKibanaLog();
        return createFilterRegistrationBean(kibanaLogFilter, filterProperties);
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
}
