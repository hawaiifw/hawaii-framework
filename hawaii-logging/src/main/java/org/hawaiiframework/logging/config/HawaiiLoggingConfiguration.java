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

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.hawaiiframework.logging.config.filter.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.http.client.LoggingClientHttpRequestInterceptor;
import org.hawaiiframework.logging.sql.StatementLoggerQueryExecutionListener;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.sql.DataSourceProxyConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@Import(DataSourceProxyConfiguration.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HawaiiLoggingConfiguration {

    /**
     * Create a statement logger query execution listener.
     *
     * @return The listener that logs JDBC statements.
     */
    @ConditionalOnClass(QueryExecutionListener.class)
    @Bean
    public StatementLoggerQueryExecutionListener statementLoggerQueryExecutionListener() {
        return new StatementLoggerQueryExecutionListener();
    }

    /**
     * Create a {@link HttpRequestResponseLogUtil} bean.
     *
     * @return the bean.
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public HttpRequestResponseLogUtil httpRequestResponseLogUtil() {
        return new HttpRequestResponseLogUtil();
    }

    /**
     * Create a {@link LoggingClientHttpRequestInterceptor} bean.
     *
     * @param requestResponseLogUtil The HTTP request response log utility.
     * @return the bean.
     */
    @Bean
    public LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor(
            final HttpRequestResponseLogUtil requestResponseLogUtil) {
        return new LoggingClientHttpRequestInterceptor(requestResponseLogUtil);
    }
}
