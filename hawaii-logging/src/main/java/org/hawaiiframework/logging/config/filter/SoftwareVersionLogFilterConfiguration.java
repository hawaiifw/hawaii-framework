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

import org.hawaiiframework.logging.web.filter.SoftwareVersionLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configures the {@link SoftwareVersionLogFilter}.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M18
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.software-version", name = "enabled", matchIfMissing = true)
@Configuration
public class SoftwareVersionLogFilterConfiguration {

    /**
     * The logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;


    /**
     * The build properties.
     */
    private final BuildProperties buildProperties;

    /**
     * The constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties The filter configuration properties.
     * @param buildProperties  The build properties.
     */
    @Autowired
    public SoftwareVersionLogFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties,
            final BuildProperties buildProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
        this.buildProperties = buildProperties;
    }

    /**
     * Create the software version logging filter bean.
     *
     * @return the {@link SoftwareVersionLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.software-version", name = "enabled", matchIfMissing = true)
    public SoftwareVersionLogFilter softwareVersionLogFilter() {
        return new SoftwareVersionLogFilter(buildProperties);
    }

    /**
     * Create and register the {@link SoftwareVersionLogFilter} bean.
     *
     * @return the  {@link SoftwareVersionLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.software-version", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<SoftwareVersionLogFilter> softwareVersionLogFilterRegistration(
            final SoftwareVersionLogFilter softwareVersionLogFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getSoftwareVersion();
        return createFilterRegistrationBean(softwareVersionLogFilter, filterProperties);
    }

}
