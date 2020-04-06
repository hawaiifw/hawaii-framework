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

import io.opentracing.Tracer;
import io.opentracing.contrib.api.TracerObserver;
import org.hawaiiframework.logging.opentracing.KibanaLogFieldsTracerObserver;
import org.hawaiiframework.logging.opentracing.OpentracingResponseFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;

/**
 * Configuration to map / weave opentracing with kibana logging.
 */
@Configuration
@ConditionalOnClass({Tracer.class, TracerObserver.class})
@ConditionalOnProperty(prefix = "hawaii.logging.opentracing", name = "enabled", matchIfMissing = true)
public class OpentracingResponseFilterConfiguration {

    /**
     * The the logging configuration properties.
     */
    private final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

    /**
     * Autowired constructor.
     *
     * @param hawaiiLoggingConfigurationProperties the logging configuration properties
     */
    public OpentracingResponseFilterConfiguration(final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        this.hawaiiLoggingConfigurationProperties = hawaiiLoggingConfigurationProperties;
    }

    /**
     * Register the kibana log fields tracer observer to add trace id and span id to kibana log fields.
     *
     * @return The {@link KibanaLogFieldsTracerObserver} observer.
     */
    @Bean
    public TracerObserver kibanaLogFieldsTracerObserver() {
        return new KibanaLogFieldsTracerObserver();
    }

    /**
     * Create the {@link OpentracingResponseFilter} bean.
     *
     * @return the {@link OpentracingResponseFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.opentracing-response", name = "enabled", matchIfMissing = true)
    public OpentracingResponseFilter opentracingResponseFilter() {
        return new OpentracingResponseFilter();
    }

    /**
     * Register the {@link #opentracingResponseFilter()} bean.
     *
     * @param opentracingResponseFilter the opentracingResponseFilter
     * @return the {@link #opentracingResponseFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.opentracing-response", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<OpentracingResponseFilter> opentracingResponseFilterRegistration(
            final OpentracingResponseFilter opentracingResponseFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingConfigurationProperties.getOpentracingResponse();
        return createFilterRegistrationBean(opentracingResponseFilter, filterProperties);
    }
}
