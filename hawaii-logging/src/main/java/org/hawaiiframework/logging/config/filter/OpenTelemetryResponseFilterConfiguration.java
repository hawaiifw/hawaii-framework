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
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.OpenTelemetryResponseFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration to add open telemetry trace ids to the http servlet responses.
 */
@Configuration
@ConditionalOnClass(Tracer.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class OpenTelemetryResponseFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.open-telemetry-response";

    private static final Logger LOGGER = getLogger(OpenTelemetryResponseFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".http-header}")
    private String headerName;

    @Value("${" + CONFIG_PREFIX + ".order}")
    private int filterOrder;

    /**
     * Create the {@link OpenTelemetryResponseFilter} bean.
     *
     * @return the {@link OpenTelemetryResponseFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public OpenTelemetryResponseFilter openTelemetryResponseFilter() {
        LOGGER.trace("Configuration: header '{}', order '{}'.", headerName, filterOrder);
        return new OpenTelemetryResponseFilter(headerName);
    }

    /**
     * Register the {@link #openTelemetryResponseFilter()} bean.
     *
     * @param openTelemetryResponseFilter the openTelemetryResponseFilter
     * @return the {@link #openTelemetryResponseFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<OpenTelemetryResponseFilter> openTelemetryResponseFilterRegistration(
            final OpenTelemetryResponseFilter openTelemetryResponseFilter) {
        return createFilterRegistrationBean(openTelemetryResponseFilter, filterOrder);
    }
}
