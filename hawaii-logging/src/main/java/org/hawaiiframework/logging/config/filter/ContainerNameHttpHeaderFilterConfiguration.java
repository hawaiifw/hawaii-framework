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

import org.hawaiiframework.logging.web.filter.ContainerNameHttpHeaderFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link ContainerNameHttpHeaderFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.container-name", name = "enabled", matchIfMissing = true)
@Configuration
public class ContainerNameHttpHeaderFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The logging configuration properties.
     */
    public ContainerNameHttpHeaderFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link ContainerNameHttpHeaderFilter} bean.
     *
     * @return the {@link ContainerNameHttpHeaderFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.container-name", name = "enabled", matchIfMissing = true)
    public ContainerNameHttpHeaderFilter containerNameHttpHeaderFilter() {
        final ContainerNameHttpHeaderFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getContainerName();
        return new ContainerNameHttpHeaderFilter(filterProperties);
    }


    /**
     * Register the {@link ContainerNameHttpHeaderFilter} bean.
     *
     * @param filter the filter to set.
     * @return the {@link #containerNameHttpHeaderFilter()} bean, wrapped in a {@link ContainerNameHttpHeaderFilter}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.container-name", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<ContainerNameHttpHeaderFilter> containerNameHttpHeaderFilterRegistration(
            final ContainerNameHttpHeaderFilter filter) {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getContainerName();
        return createFilterRegistrationBean(filter, filterProperties);
    }

}
