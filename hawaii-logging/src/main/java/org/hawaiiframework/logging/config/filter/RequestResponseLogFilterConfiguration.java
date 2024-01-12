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
import static org.hawaiiframework.logging.config.filter.RequestResponseLogFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.web.filter.RequestResponseLogFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configures the {@link RequestResponseLogFilter}. */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class RequestResponseLogFilterConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX = "hawaii.logging.filters.request-response";

  private static final Logger LOGGER = getLogger(RequestResponseLogFilterConfiguration.class);

  @Value("${" + CONFIG_PREFIX + ".order:-300}")
  private int filterOrder;

  /**
   * Create the request/response logging filter bean.
   *
   * @param filterVoter The filter voter.
   * @param hawaiiLogger The logger.
   * @return the {@link RequestResponseLogFilter} bean
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public RequestResponseLogFilter requestResponseLogFilter(
      FilterVoter filterVoter, HawaiiRequestResponseLogger hawaiiLogger) {
    LOGGER.trace("Configuration: order '{}'.", filterOrder);
    return new RequestResponseLogFilter(hawaiiLogger, filterVoter);
  }

  /**
   * Create and register the {@link RequestResponseLogFilter} bean.
   *
   * @param requestResponseLogFilter The filter to register.
   * @return the requestResponseLogFilter bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public FilterRegistrationBean<RequestResponseLogFilter> requestResponseLogFilterRegistration(
      RequestResponseLogFilter requestResponseLogFilter) {
    return createFilterRegistrationBean(requestResponseLogFilter, filterOrder);
  }
}
