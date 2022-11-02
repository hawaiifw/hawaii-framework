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

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
public abstract class AbstractGenericFilterBean extends GenericFilterBean {

    /**
     * {@inheritDoc}
     *
     * @param request     The servlet request.
     * @param response    The servlet response.
     * @param filterChain The filter chain.
     * @throws ServletException if an I/O related error has occurred during the processing
     * @throws IOException      if an exception occurs that interferes with the filter's normal operation
     */
    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        if (!(request instanceof final HttpServletRequest httpRequest) || !(response instanceof final HttpServletResponse httpResponse)) {
            throw new ServletException("AbstractGenericFilterBean just supports HTTP requests");
        }

        doFilterInternal(httpRequest, httpResponse, filterChain);
    }

    /**
     * Same contract as for {@code doFilter}.
     *
     * @param httpRequest  The http servlet request.
     * @param httpResponse The http servlet response.
     * @param filterChain  The filter chain.
     * @throws ServletException if an I/O related error has occurred during the processing
     * @throws IOException      if an exception occurs that interferes with the filter's normal operation
     */
    protected abstract void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            FilterChain filterChain) throws ServletException, IOException;
}
