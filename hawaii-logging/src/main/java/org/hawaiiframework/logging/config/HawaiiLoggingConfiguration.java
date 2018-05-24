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

import org.hawaiiframework.logging.util.ClientIpResolver;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.logging.web.filter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the logging based on the application properties.
 *
 * This class creates filter beans for the enabled filters.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(HawaiiLoggingConfigurationProperties.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HawaiiLoggingConfiguration {

    @Autowired
    private HawaiiLoggingConfigurationProperties hawaiiLoggingConfigurationProperties;

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana", name = "enabled")
    public KibanaLogFilter kibanaLogFilter() {
        return new KibanaLogFilter(clientIpResolver());
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana", name = "enabled")
    public KibanaLogCleanupFilter kibanaLogCleanupFilter() {
        return new KibanaLogCleanupFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-duration", name = "enabled")
    public RequestDurationFilter requestDurationFilter() {
        return new RequestDurationFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-id", name = "enabled")
    public RequestIdFilter requestIdFilter() {
        return new RequestIdFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public RequestResponseLogFilter requestResponseLogFilter() {
        return new RequestResponseLogFilter(hawaiiLoggingConfigurationProperties.getRequestResponse(), httpRequestResponseLogUtil());
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.transaction-id", name = "enabled")
    public TransactionIdFilter transactionIdFilter() {
        return new TransactionIdFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled")
    public UserDetailsFilter userDetailsFilter() {
        return new UserDetailsFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.kibana", name = "enabled")
    public ClientIpResolver clientIpResolver() {
        return new ClientIpResolver();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.request-response", name = "enabled")
    public HttpRequestResponseLogUtil httpRequestResponseLogUtil() {
        return new HttpRequestResponseLogUtil();
    }

}
