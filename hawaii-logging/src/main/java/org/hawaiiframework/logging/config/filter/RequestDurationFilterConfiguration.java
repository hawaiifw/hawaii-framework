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

import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.web.filter.RequestDurationFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.RequestDurationFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures the {@link RequestDurationFilter}.
 */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class RequestDurationFilterConfiguration {

    /**
     * The configuration properties' prefix.
     */
    public static final String CONFIG_PREFIX = "hawaii.logging.filters.request-duration";

    private static final Logger LOGGER = getLogger(RequestDurationFilterConfiguration.class);

    @Value("${" + CONFIG_PREFIX + ".order}")
    private int filterOrder;

    /**
     * Create the {@link RequestDurationFilter} bean.
     *
     * @param filterVoter The filter voter.
     * @return the {@link RequestDurationFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public RequestDurationFilter requestDurationFilter(final FilterVoter filterVoter) {
        LOGGER.trace("Configuration: order '{}'.", filterOrder);
        return new RequestDurationFilter(filterVoter);
    }

    /**
     * Register the {@link #requestDurationFilter(FilterVoter)} bean.
     *
     * @param requestDurationFilter the request duration filter
     * @return the {@link #requestDurationFilter(FilterVoter)} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<RequestDurationFilter> requestDurationFilterRegistration(
            final RequestDurationFilter requestDurationFilter) {
        return createFilterRegistrationBean(requestDurationFilter, filterOrder);
    }
}
