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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     */
    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("AbstractGenericFilterBean just supports HTTP requests");
        }
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        doFilterInternal(httpRequest, httpResponse, filterChain);
    }

    /**
     * Same contract as for {@code doFilter}.
     */
    protected abstract void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            FilterChain filterChain) throws ServletException, IOException;
}
