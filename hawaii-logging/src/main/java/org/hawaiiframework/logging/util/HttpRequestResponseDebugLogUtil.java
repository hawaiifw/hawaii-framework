/*
 * Copyright 2015-2021 the original author or authors.
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

package org.hawaiiframework.logging.util;

import static java.lang.String.format;
import static org.hawaiiframework.logging.util.IndentUtil.indent;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * Utility for logging requests / responses.
 *
 * <p>The utility can be used to generate HTTP request / response log strings. Both for incoming
 * service calls as outgoing calls (i.e. calls to backend systems).
 *
 * @author Rutger Lubbers
 * @since 3.0.0
 */
public class HttpRequestResponseDebugLogUtil {

  /** The configured newline to look for. */
  private static final String NEW_LINE = System.lineSeparator();

  /**
   * Create a request line for the {@code requestUri} and {@code protocol}.
   *
   * <p>For example: {@code GET /doc/test.html HTTP/1.1}.
   */
  private static String createRequestLine(String method, String request, String protocol) {
    return format("%s %s %s", method, request, protocol);
  }

  /** Create log string where the parts have been masked already. */
  private static String createLogString(String requestLine, String headers, String body) {
    StringBuilder builder = new StringBuilder();
    if (requestLine != null) {
      builder.append(requestLine)
          .append(NEW_LINE);
    }
    if (headers != null && !headers.isEmpty()) {
      builder.append(headers);
    }
    if (body != null && !body.isEmpty()) {
      builder.append(NEW_LINE)
          .append(body);
    }
    String value = builder.toString();

    return indent(value);
  }

  /**
   * Create a servlet request log output, containing the request line, headers and body.
   *
   * @param servletRequest The request.
   * @param headers The headers.
   * @param body The body.
   * @return a formatted multi-line string with the HTTP request.
   */
  public String getTxRequestDebugOutput(
      HttpServletRequest servletRequest, String headers, String body) {
    String requestLine =
        createRequestLine(
            servletRequest.getMethod(),
            servletRequest.getRequestURI(),
            servletRequest.getProtocol());
    return createLogString(requestLine, headers, body);
  }

  /**
   * Create a servlet response log output, containing the request line, headers and body.
   *
   * @param protocol The request's protocol.
   * @param httpStatus The http status.
   * @param headers The headers.
   * @param body The body.
   * @return a formatted multi-line string with the HTTP response.
   */
  public String getTxResponseDebugOutput(
      String protocol, HttpStatus httpStatus, String headers, String body) {

    String statusLine = format("%s %s", protocol, httpStatus);
    return createLogString(statusLine, headers, body);
  }

  /**
   * Create a http request log output, containing the request line, headers and body.
   *
   * @param method The method.
   * @param requestUri The request URI.
   * @param headers The headers.
   * @param body The body.
   * @return a formatted multi-line string with the HTTP request.
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public String getCallRequestDebugOutput(
      HttpMethod method, String requestUri, String headers, String body) {
    return getCallRequestDebugOutput(method.name(), requestUri, headers, body);
  }

  /**
   * Create a http request log output, containing the request line, headers and body.
   *
   * @param method The method.
   * @param requestUri The request URI.
   * @param headers The headers.
   * @param body The body.
   * @return a formatted multi-line string with the HTTP request.
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public String getCallRequestDebugOutput(
      String method, String requestUri, String headers, String body) {
    String requestLine = createRequestLine(method, requestUri, "");
    return createLogString(requestLine, headers, body);
  }

  /**
   * Create a http response log output.
   *
   * @param headers The headers.
   * @param body The body.
   * @return a formatted multi-line string with the HTTP response.
   */
  public String getCallResponseDebugOutput(String headers, String body) {
    return createLogString(null, headers, body);
  }
}
