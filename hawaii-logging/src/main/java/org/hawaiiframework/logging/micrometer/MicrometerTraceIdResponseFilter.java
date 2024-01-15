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

package org.hawaiiframework.logging.micrometer;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
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
 * @since 6.0.0
 */
public class MicrometerTraceIdResponseFilter extends AbstractGenericFilterBean {

  /** The logger to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MicrometerTraceIdResponseFilter.class);

  /** The configured header name to write the trace id in. */
  private final String headerName;

  /** The tracer. */
  private final Tracer tracer;

  /**
   * Constructor.
   *
   * @param headerName the name of the header to response with.
   * @param tracer the tracer.
   */
  public MicrometerTraceIdResponseFilter(String headerName, Tracer tracer) {
    super();
    this.headerName = Objects.toString(headerName, "traceid");
    this.tracer = tracer;
    LOGGER.info("Configured to use '{}'.", this.headerName);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String traceId = getTraceId();
    if (traceId != null) {
      addHeader(response, traceId);
    }
    filterChain.doFilter(request, response);
  }

  private String getTraceId() {
    return getTraceId(tracer.currentTraceContext());
  }

  private static String getTraceId(CurrentTraceContext currentTraceContext) {
    TraceContext context = currentTraceContext.context();
    return getTraceId(context);
  }

  private static String getTraceId(TraceContext context) {
    if (context == null) {
      return null;
    }
    return context.traceId();
  }

  private void addHeader(HttpServletResponse response, String value) {
    if (!response.containsHeader(headerName)) {
      response.setHeader(headerName, value);
    }
  }
}
