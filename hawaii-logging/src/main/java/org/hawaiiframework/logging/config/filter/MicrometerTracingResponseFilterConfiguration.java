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

import io.micrometer.tracing.Tracer;
import org.hawaiiframework.logging.micrometer.MicrometerTraceIdResponseFilter;
import org.hawaiiframework.logging.opentelemetry.OpenTelemetryTraceIdResponseFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.OpenTelemetryTraceIdResponseFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration to add open telemetry trace ids to the http servlet responses.
 */
@Configuration
@ConditionalOnClass(Tracer.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class MicrometerTracingResponseFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.micrometer-tracing-response";

    private static final Logger LOGGER = getLogger(MicrometerTracingResponseFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".http-header}")
    private String headerName;

    @Value("${" + CONFIG_PREFIX + ".order}")
    private int filterOrder;

    /**
     * Create the {@link OpenTelemetryTraceIdResponseFilter} bean.
     *
     * @param tracer The tracer.
     * @return the {@link OpenTelemetryTraceIdResponseFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public MicrometerTraceIdResponseFilter micrometerTraceIdResponseFilter(final Tracer tracer) {
        LOGGER.trace("Configuration: header '{}', order '{}'.", headerName, filterOrder);
        return new MicrometerTraceIdResponseFilter(headerName, tracer);
    }

    /**
     * Register the {@link #micrometerTraceIdResponseFilter(Tracer)} bean.
     *
     * @param micrometerTraceIdResponseFilter the micrometerTraceIdResponseFilter
     * @return the {@link #micrometerTraceIdResponseFilter(Tracer)} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<MicrometerTraceIdResponseFilter> micrometerTraceIdResponseFilterRegistration(
            final MicrometerTraceIdResponseFilter micrometerTraceIdResponseFilter) {
        return createFilterRegistrationBean(micrometerTraceIdResponseFilter, filterOrder);
    }
}
