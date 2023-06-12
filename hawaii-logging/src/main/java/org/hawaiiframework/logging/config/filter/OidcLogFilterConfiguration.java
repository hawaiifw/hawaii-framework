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

import org.hawaiiframework.logging.oidc.OidcLogFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.OidcLogFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration to add OIDC fields to the Kibana log.
 */
@Configuration
@ConditionalOnClass(com.nimbusds.jwt.PlainJWT.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class OidcLogFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.oidc";

    private static final Logger LOGGER = getLogger(OidcLogFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".order:-900}")
    private int filterOrder;

    /**
     * Create the {@link OidcLogFilter} bean.
     *
     * @return the {@link OidcLogFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public OidcLogFilter oidcLogFilter() {
        LOGGER.trace("Configuration: order '{}'.", filterOrder);
        return new OidcLogFilter();
    }

    /**
     * Register the {@link #oidcLogFilter()} bean.
     *
     * @param oidcLogFilter the {@link #oidcLogFilter()} bean.
     * @return the {@link #oidcLogFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<OidcLogFilter> oidcLogFilterRegistration(
            final OidcLogFilter oidcLogFilter) {
        return createFilterRegistrationBean(oidcLogFilter, filterOrder);
    }

}
