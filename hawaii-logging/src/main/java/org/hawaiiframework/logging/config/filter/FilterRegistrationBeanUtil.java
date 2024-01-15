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

import static org.slf4j.LoggerFactory.getLogger;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/** Utility for registering filter registration beans for servlet filters. */
public final class FilterRegistrationBeanUtil {

  private static final Logger LOGGER = getLogger(FilterRegistrationBeanUtil.class);

  @SuppressWarnings("PMD.LooseCoupling")
  private static final EnumSet<DispatcherType> ALL_DISPATCHER_TYPES =
      EnumSet.allOf(DispatcherType.class);

  /** Utility constructor. */
  private FilterRegistrationBeanUtil() {
    // Do nothing.
  }

  /**
   * Helper method to wrap a filter in a {@link FilterRegistrationBean} with the configured order.
   *
   * @param filter the filter.
   * @param filterOrder the filter's order.
   * @param <T> the specific filter type.
   * @return the filter registration.
   */
  public static <T extends Filter> FilterRegistrationBean<T> createFilterRegistrationBean(
      T filter, int filterOrder) {
    return createFilterRegistrationBean(filter, filterOrder, ALL_DISPATCHER_TYPES);
  }

  /**
   * Helper method to wrap a filter in a {@link FilterRegistrationBean} with the configured order.
   *
   * @param filter the filter
   * @param filterOrder the filter's order.
   * @param dispatcherTypes the request dispatcher types the filter is used for
   * @param <T> the specific filter type.
   * @return the filter registration.
   */
  @SuppressWarnings("PMD.LooseCoupling")
  public static <T extends Filter> FilterRegistrationBean<T> createFilterRegistrationBean(
      T filter, int filterOrder, EnumSet<DispatcherType> dispatcherTypes) {
    LOGGER.trace("Setting filter order for '{}' to '{}'.", filter, filterOrder);
    FilterRegistrationBean<T> result = new FilterRegistrationBean<>(filter);
    result.setOrder(filterOrder);
    result.setDispatcherTypes(dispatcherTypes);
    return result;
  }
}
