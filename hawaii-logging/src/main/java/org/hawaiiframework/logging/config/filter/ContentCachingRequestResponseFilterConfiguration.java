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
import static org.slf4j.LoggerFactory.getLogger;

import org.hawaiiframework.logging.web.filter.ContentCachingRequestResponseFilter;
import org.hawaiiframework.logging.web.filter.TransactionTypeFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/** Configures the {@link TransactionTypeFilter}. */
@Configuration
@Conditional(ContentCachingRequired.class)
public class ContentCachingRequestResponseFilterConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX =
      "hawaii.logging.filters.content-caching-request-response";

  private static final Logger LOGGER =
      getLogger(ContentCachingRequestResponseFilterConfiguration.class);

  @Value("${" + CONFIG_PREFIX + ".order:-17400}")
  private int filterOrder;

  /**
   * Create the caching request/response filter bean.
   *
   * @return the {@link ContentCachingRequestResponseFilter} bean
   */
  @Bean
  @Conditional(ContentCachingRequired.class)
  public ContentCachingRequestResponseFilter contentCachingRequestResponseFilter() {
    LOGGER.trace("Configuration: order '{}'.", filterOrder);
    return new ContentCachingRequestResponseFilter();
  }

  /**
   * Create and register the {@link ContentCachingRequestResponseFilter} bean.
   *
   * @param filter The filter to register.
   * @return the {@code filter} bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @Conditional(ContentCachingRequired.class)
  public FilterRegistrationBean<ContentCachingRequestResponseFilter>
      contentCachingRequestResponseFilterRegistration(ContentCachingRequestResponseFilter filter) {
    return createFilterRegistrationBean(filter, filterOrder);
  }
}
