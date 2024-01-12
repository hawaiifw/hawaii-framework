/*
 * Copyright 2015-2018 the original author or authors.
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
package org.hawaiiframework.logging.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Adapter "interface" to be able to write FilterBeans that can be "once per request" or "for every dispatch in the request" without having
 * to change code.
 * <p>
 * So, if a filter changes from {@link AbstractGenericFilterBean} to {@link org.springframework.web.filter.OncePerRequestFilter} then
 * the filter code remains the same.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public abstract class AbstractGenericFilterBean extends OncePerRequestFilter {

    protected static final String WRAPPED_REQUEST_RESPONSE = WrappedHttpRequestResponse.class.getName();

    /**
     * Retrieve the {@link WrappedHttpRequestResponse} given the {@code httpServletRequest}.
     *
     * @param httpServletRequest The http servlet request.
     * @return The, possibly {@code null}, {@link WrappedHttpRequestResponse}.
     */
    protected WrappedHttpRequestResponse getWrapped(final HttpServletRequest httpServletRequest) {
        return (WrappedHttpRequestResponse) httpServletRequest.getAttribute(
            WRAPPED_REQUEST_RESPONSE);
    }

    /**
     * Retrieve or create the {@link WrappedHttpRequestResponse}.
     *
     * @param httpServletRequest The http servlet request.
     * @param httpServletResponse The http servlet response.
     * @return a, never {@code null} {@link WrappedHttpRequestResponse}.
     */
    protected WrappedHttpRequestResponse getWrapped(final HttpServletRequest httpServletRequest,
        final HttpServletResponse httpServletResponse) {
        WrappedHttpRequestResponse wrapped = getWrapped(httpServletRequest);
        if (wrapped != null) {
            return wrapped;
        }

        final ContentCachingWrappedResponse wrappedResponse = new ContentCachingWrappedResponse(
            httpServletResponse);
        final ResettableHttpServletRequest wrappedRequest = new ResettableHttpServletRequest(
            httpServletRequest, wrappedResponse);

        wrapped = new WrappedHttpRequestResponse(wrappedRequest,
            wrappedResponse);

        httpServletRequest.setAttribute(WRAPPED_REQUEST_RESPONSE, wrapped);

        return wrapped;
    }
}
