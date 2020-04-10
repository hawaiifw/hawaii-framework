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

import org.hawaiiframework.logging.web.filter.KibanaLogCleanupFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link KibanaLogCleanupFilter}.
 */
@Configuration
public class KibanaLogCleanupFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public KibanaLogCleanupFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
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
    public FilterRegistrationBean<KibanaLogCleanupFilter> kibanaLogCleanupFilterRegistration(
            final KibanaLogCleanupFilter kibanaLogCleanupFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getKibanaLogCleanup();
        return createFilterRegistrationBean(kibanaLogCleanupFilter, filterProperties);
    }

}
