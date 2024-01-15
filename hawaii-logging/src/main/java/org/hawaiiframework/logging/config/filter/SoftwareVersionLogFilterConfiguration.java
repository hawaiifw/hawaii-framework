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
import static org.hawaiiframework.logging.config.filter.SoftwareVersionLogFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.web.filter.SoftwareVersionLogFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the {@link SoftwareVersionLogFilter}.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M18
 */
@Configuration
@ConditionalOnBean(BuildProperties.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class SoftwareVersionLogFilterConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX = "hawaii.logging.filters.software-version";

  private static final Logger LOGGER = getLogger(SoftwareVersionLogFilterConfiguration.class);

  @Value("${" + CONFIG_PREFIX + ".order:-1200}")
  private int filterOrder;

  /**
   * Create the software version logging filter bean.
   *
   * @param buildProperties The build properties.
   * @param filterVoter The filter voter.
   * @return the {@link SoftwareVersionLogFilter} bean
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public SoftwareVersionLogFilter softwareVersionLogFilter(
      BuildProperties buildProperties, FilterVoter filterVoter) {
    LOGGER.trace("Configuration: order '{}'.", filterOrder);
    return new SoftwareVersionLogFilter(buildProperties, filterVoter);
  }

  /**
   * Create and register the {@link SoftwareVersionLogFilter} bean.
   *
   * @param softwareVersionLogFilter The filter to register.
   * @return the {@link SoftwareVersionLogFilter} bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public FilterRegistrationBean<SoftwareVersionLogFilter> softwareVersionLogFilterRegistration(
      SoftwareVersionLogFilter softwareVersionLogFilter) {
    return createFilterRegistrationBean(softwareVersionLogFilter, filterOrder);
  }
}
