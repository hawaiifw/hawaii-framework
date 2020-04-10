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

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.hawaiiframework.logging.http.client.LoggingClientHttpRequestInterceptor;
import org.hawaiiframework.logging.http.DefaultHawaiiRequestResponseLogger;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.sql.DataSourceProxyConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configures the logging based on the application properties.
 * <p>
 * This class creates filter beans for the enabled filters.
 *
 * @author Wouter Eerdekens
 * @author Paul Klos
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(HawaiiLoggingConfigurationProperties.class)
@Import({
        DataSourceProxyConfiguration.class,
        HawaiiLoggingFilterConfiguration.class,
        StatementLoggerQueryExecutionListenerConfiguration.class
})
public class HawaiiLoggingConfiguration {


    /**
     * Create a {@link HttpRequestResponseLogUtil} bean.
     *
     * @return the bean.
     */
    @Bean
    public HttpRequestResponseLogUtil httpRequestResponseLogUtil() {
        return new HttpRequestResponseLogUtil();
    }

    @Bean
    public HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties() {
        return new HawaiiLoggingConfigurationProperties();
    }

    @Bean
    public HawaiiRequestResponseLogger hawaiiLogger(final HttpRequestResponseLogUtil requestResponseLogUtil,
            final HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties) {
        return new DefaultHawaiiRequestResponseLogger(requestResponseLogUtil, hawaiiLoggingConfigurationProperties);
    }

    /**
     * Create a {@link LoggingClientHttpRequestInterceptor} bean.
     * @return the bean.
     */
    @Bean
    public LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor() {
        return new LoggingClientHttpRequestInterceptor(hawaiiLogger(httpRequestResponseLogUtil(), hawaiiLoggingConfigurationProperties()));
    }

    /**
     * Configures the Apache CXF bus to use logging interceptors.
     * @return the Apache CXF bus.
     */
    @Bean
    @ConditionalOnProperty(name = "hawaii.logging.soap.enabled")
    public Bus busConfiguration() {
        final Bus bus = BusFactory.getDefaultBus();
        bus.getInInterceptors().add(loggingInInterceptor());
        bus.getInFaultInterceptors().add(loggingInInterceptor());
        bus.getOutInterceptors().add(loggingOutInterceptor());
        bus.getOutFaultInterceptors().add(loggingOutInterceptor());
        return bus;
    }

    private LoggingInInterceptor loggingInInterceptor() {
        final LoggingInInterceptor inInterceptor = new LoggingInInterceptor();
        inInterceptor.setPrettyLogging(true);
        return inInterceptor;
    }

    private LoggingOutInterceptor loggingOutInterceptor() {
        final LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor();
        outInterceptor.setPrettyLogging(true);
        return outInterceptor;
    }
}
