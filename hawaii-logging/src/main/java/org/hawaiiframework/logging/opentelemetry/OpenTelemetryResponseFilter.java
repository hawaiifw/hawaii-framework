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
package org.hawaiiframework.logging.opentelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that adds the trace id to the response header.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M21
 */
public class OpenTelemetryResponseFilter extends AbstractGenericFilterBean {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryResponseFilter.class);

    /**
     * The configured header name to write the trace id in.
     */
    private final String headerName;

    /**
     * Constructor.
     *
     * @param headerName the name of the header to response with.
     */
    public OpenTelemetryResponseFilter(final String headerName) {
        this.headerName = StringUtils.defaultString(headerName, "trace-id");
        LOGGER.info("Configured to use '{}'.", this.headerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final Span activeSpan = Span.current();
        if (activeSpan != null) {
            addHeader(response, activeSpan.getSpanContext());
        }

        filterChain.doFilter(request, response);
    }

    private void addHeader(final HttpServletResponse response, final SpanContext context) {
        addHeader(response, context.getTraceIdAsHexString());
    }

    private void addHeader(final HttpServletResponse response, final String value) {
        if (!response.containsHeader(headerName)) {
            response.setHeader(headerName, value);
        }
    }
}
