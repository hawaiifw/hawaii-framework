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
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that adds the trace id to the response header.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M21
 */
public class OpenTelemetryTraceIdResponseFilter extends AbstractGenericFilterBean {

  /** The logger to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OpenTelemetryTraceIdResponseFilter.class);

  /** The configured header name to write the trace id in. */
  private final String headerName;

  /**
   * Constructor.
   *
   * @param headerName the name of the header to response with.
   */
  public OpenTelemetryTraceIdResponseFilter(String headerName) {
    super();
    this.headerName = Objects.toString(headerName, "traceid");
    LOGGER.info("Configured to use '{}'.", this.headerName);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Span activeSpan = Span.current();
    if (activeSpan != null) {
      addHeader(response, activeSpan.getSpanContext());
    }

    filterChain.doFilter(request, response);
  }

  private void addHeader(HttpServletResponse response, SpanContext context) {
    addHeader(response, context.getTraceId());
  }

  private void addHeader(HttpServletResponse response, String value) {
    if (!response.containsHeader(headerName)) {
      response.setHeader(headerName, value);
    }
  }
}
