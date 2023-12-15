package org.hawaiiframework.logging.web.util;

/**
 * Record for wrapped HTTP {@code request} and {@code response}.
 *
 * @param request The wrapped http servlet request.
 * @param response The wrapped response.
 *
 * @author Giuseppe Collura
 * @since 6.0.0.m4
 */
public record WrappedHttpRequestResponse(ResettableHttpServletRequest request,
                                         ContentCachingWrappedResponse response) {

}
