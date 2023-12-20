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

import org.hawaiiframework.logging.util.ClientIpResolver;
import org.hawaiiframework.logging.web.filter.ClientIpLogFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.ClientIpLogFilterConfiguration.CONFIG_PREFIX;
import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures the {@link ClientIpLogFilter}.
 */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = false)
public class ClientIpLogFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.client-ip-log";

    private static final Logger LOGGER = getLogger(ClientIpLogFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".http-header:X-Hawaii-Frontend-IP-Address}")
    private String headerName;

    @Value("${" + CONFIG_PREFIX + ".order:-800}")
    private int filterOrder;


    /**
     * Create the {@link ClientIpLogFilter} bean.
     *
     * @return the {@link ClientIpLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public ClientIpLogFilter clientIpLogFilter() {
        LOGGER.trace("Configuration: header '{}', order '{}'.", headerName, filterOrder);
        return new ClientIpLogFilter(createClientIpResolver());
    }

    /**
     * Register the {@link #clientIpLogFilter()} bean.
     *
     * @param clientIpLogFilter the {@link #clientIpLogFilter()} bean.
     * @return the {@link ClientIpLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<ClientIpLogFilter> kibanaLogFilterRegistration(final ClientIpLogFilter clientIpLogFilter) {
        return createFilterRegistrationBean(clientIpLogFilter, filterOrder);
    }

    /**
     * Helper method to create the client ip resolver required by {@link ClientIpLogFilter}.
     *
     * @return the client ip resolver
     */
    private ClientIpResolver createClientIpResolver() {
        return new ClientIpResolver(headerName);
    }
}
