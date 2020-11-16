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

import org.hawaiiframework.logging.web.filter.UserDetailsFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Configures the {@link UserDetailsFilter}.
 */
@ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled", matchIfMissing = true)
@Configuration
@ConditionalOnClass(UserDetails.class)
public class UserDetailsFilterConfiguration {


    /**
     * The the logging configuration properties.
     */
    private final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties;

    /**
     * Autowired constructor.
     *
     * @param hawaiiLoggingFilterConfigurationProperties the logging configuration properties
     */
    UserDetailsFilterConfiguration(
            final HawaiiLoggingFilterConfigurationProperties hawaiiLoggingFilterConfigurationProperties) {
        this.hawaiiLoggingFilterConfigurationProperties = hawaiiLoggingFilterConfigurationProperties;
    }

    /**
     * Create the {@link UserDetailsFilter} bean.
     *
     * @return the {@link UserDetailsFilter} bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled", matchIfMissing = true)
    public UserDetailsFilter userDetailsFilter() {
        return new UserDetailsFilter();
    }

    /**
     * Register the {@link #userDetailsFilter()} bean.
     *
     * @param userDetailsFilter the user details filter
     * @return the {@link #userDetailsFilter()} bean, wrapped in a {@link FilterRegistrationBean}
     */
    @ConditionalOnProperty(prefix = "hawaii.logging.filters.user-details", name = "enabled", matchIfMissing = true)
    @Bean
    public FilterRegistrationBean<UserDetailsFilter> userDetailsFilterRegistration(final UserDetailsFilter userDetailsFilter) {
        final LoggingFilterProperties filterProperties = hawaiiLoggingFilterConfigurationProperties.getUserDetails();
        final FilterRegistrationBean<UserDetailsFilter> result = new FilterRegistrationBean<>(userDetailsFilter);
        result.setOrder(filterProperties.getOrder());
        result.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
        return result;
    }
}
