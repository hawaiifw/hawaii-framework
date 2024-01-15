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

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.UserDetailsFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import org.hawaiiframework.logging.web.filter.UserDetailsFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

/** Configures the {@link UserDetailsFilter}. */
@Configuration
@ConditionalOnClass(UserDetails.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class UserDetailsFilterConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX = "hawaii.logging.filters.user-details";

  private static final Logger LOGGER = getLogger(UserDetailsFilterConfiguration.class);

  @Value("${" + CONFIG_PREFIX + ".order:200}")
  private int filterOrder;

  /**
   * Create the {@link UserDetailsFilter} bean.
   *
   * @return the {@link UserDetailsFilter} bean
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public UserDetailsFilter userDetailsFilter() {
    LOGGER.trace("Configuration: order '{}'.", filterOrder);
    return new UserDetailsFilter();
  }

  /**
   * Register the {@link #userDetailsFilter()} bean.
   *
   * @param userDetailsFilter the user details filter
   * @return the {@link #userDetailsFilter()} bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public FilterRegistrationBean<UserDetailsFilter> userDetailsFilterRegistration(
      UserDetailsFilter userDetailsFilter) {
    return createFilterRegistrationBean(
        userDetailsFilter, filterOrder, EnumSet.of(DispatcherType.REQUEST));
  }
}
