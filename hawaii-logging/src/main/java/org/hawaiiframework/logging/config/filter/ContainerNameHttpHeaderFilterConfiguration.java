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
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.ContainerNameHttpHeaderFilterConfiguration.CONFIG_PREFIX;
import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures the {@link ContainerNameHttpHeaderFilter}.
 */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class ContainerNameHttpHeaderFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.container-name";

    private static final Logger LOGGER = getLogger(ContainerNameHttpHeaderFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".http-header:X-Hawaii-Hostname}")
    private String headerName;

    @Value("${" + CONFIG_PREFIX + ".order:-1100}")
    private int filterOrder;

    @Value("${" + CONFIG_PREFIX + ".hostname:localhost}")
    private String hostname;

    /**
     * Create the {@link ContainerNameHttpHeaderFilter} bean.
     *
     * @return the {@link ContainerNameHttpHeaderFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public ContainerNameHttpHeaderFilter containerNameHttpHeaderFilter() {
        LOGGER.trace("Configuration: header '{}', order '{}', hostname '{}'.", headerName, filterOrder, hostname);
        return new ContainerNameHttpHeaderFilter(headerName, hostname);
    }

    /**
     * Register the {@link ContainerNameHttpHeaderFilter} bean.
     *
     * @param filter the filter to set.
     * @return the {@link #containerNameHttpHeaderFilter()} bean, wrapped in a {@link ContainerNameHttpHeaderFilter}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<ContainerNameHttpHeaderFilter> containerNameHttpHeaderFilterRegistration(
            final ContainerNameHttpHeaderFilter filter) {
        return createFilterRegistrationBean(filter, filterOrder);
    }
}
