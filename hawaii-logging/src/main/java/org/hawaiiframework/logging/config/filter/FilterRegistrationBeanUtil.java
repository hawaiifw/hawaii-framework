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

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

/**
 * Utility for registering filter registration beans for servlet filters.
 */
public final class FilterRegistrationBeanUtil {

    public static final EnumSet<DispatcherType> ALL_DISPATCHER_TYPES = EnumSet.allOf(DispatcherType.class);

    /**
     * Utility constructor.
     */
    private FilterRegistrationBeanUtil() {
        // Do nothing.
    }

    /**
     * Helper method to wrap a filter in a {@link FilterRegistrationBean} with the configured order.
     *
     * @param filter           the filter
     * @param filterProperties the configuration properties
     * @return the wrapped filter
     */
    public static FilterRegistrationBean createFilterRegistrationBean(
            final Filter filter,
            final LoggingFilterProperties filterProperties) {
        final FilterRegistrationBean<?> result = new FilterRegistrationBean<>(filter);
        result.setOrder(filterProperties.getOrder());
        result.setDispatcherTypes(ALL_DISPATCHER_TYPES);
        return result;
    }

    /**
     * Helper method to wrap a filter in a {@link FilterRegistrationBean} with the configured order.
     *
     * @param filter           the filter
     * @param filterProperties the configuration properties
     * @param dispatcherTypes  the request dispatcher types the filter is used for
     * @return the wrapped filter
     */
    public static FilterRegistrationBean createFilterRegistrationBean(
            final Filter filter,
            final LoggingFilterProperties filterProperties,
            final EnumSet<DispatcherType> dispatcherTypes) {
        final FilterRegistrationBean<?> result = new FilterRegistrationBean<>(filter);
        result.setOrder(filterProperties.getOrder());
        result.setDispatcherTypes(dispatcherTypes);
        return result;
    }
}
