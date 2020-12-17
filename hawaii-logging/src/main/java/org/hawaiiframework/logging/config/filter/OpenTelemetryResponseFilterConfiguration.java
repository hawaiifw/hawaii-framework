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

import io.opentelemetry.api.trace.Tracer;
import org.hawaiiframework.logging.opentelemetry.OpenTelemetryResponseFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configuration to add open telemetry trace ids to the http servlet responses.
 */
@Configuration
@ConditionalOnClass(Tracer.class)
@ConditionalOnProperty(prefix = "hawaii.logging.open-telemetry-response", name = "enabled", matchIfMissing = true)
public class OpenTelemetryResponseFilterConfiguration {

    /**
     * The the logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * Autowired constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties the logging configuration properties
     */
    public OpenTelemetryResponseFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link OpenTelemetryResponseFilter} bean.
     *
     * @return the {@link OpenTelemetryResponseFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.open-telemetry-response", name = "enabled", matchIfMissing = true)
    public OpenTelemetryResponseFilter openTelemetryResponseFilter() {
        final HttpHeaderLoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getOpenTelemetryResponse();
        return new OpenTelemetryResponseFilter(filterProperties.getHttpHeader());
    }

    /**
     * Register the {@link #openTelemetryResponseFilter()} bean.
     *
     * @param openTelemetryResponseFilter the openTelemetryResponseFilter
     * @return the {@link #openTelemetryResponseFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.open-telemetry-response", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<OpenTelemetryResponseFilter> openTelemetryResponseFilterRegistration(
            final OpenTelemetryResponseFilter openTelemetryResponseFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getOpenTelemetryResponse();
        return createFilterRegistrationBean(openTelemetryResponseFilter, filterProperties);
    }
}
