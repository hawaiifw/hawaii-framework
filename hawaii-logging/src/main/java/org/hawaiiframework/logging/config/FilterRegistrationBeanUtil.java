package org.hawaiiframework.logging.config;

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
