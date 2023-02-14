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

import org.hawaiiframework.logging.web.filter.RequestIdFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.RequestIdFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures the {@link RequestIdFilter}.
 */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class RequestIdFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.request-id";

    private static final Logger LOGGER = getLogger(RequestIdFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".http-header:X-Hawaii-Request-Id}")
    private String headerName;

    @Value("${" + CONFIG_PREFIX + ".order:-400}")
    private int filterOrder;

    /**
     * Create the {@link RequestIdFilter} bean.
     *
     * @return the {@link RequestIdFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public RequestIdFilter requestIdFilter() {
        LOGGER.trace("Configuration: header '{}', order '{}'.", headerName, filterOrder);
        return new RequestIdFilter(headerName);
    }

    /**
     * Register the {@link RequestIdFilter} bean.
     *
     * @param requestIdFilter the {@link RequestIdFilter} bean.
     * @return the {@link #requestIdFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<RequestIdFilter> requestIdFilterRegistration(final RequestIdFilter requestIdFilter) {
        return createFilterRegistrationBean(requestIdFilter, filterOrder);
    }


}
