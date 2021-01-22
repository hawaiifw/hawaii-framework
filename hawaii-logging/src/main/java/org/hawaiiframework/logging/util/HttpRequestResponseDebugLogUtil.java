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

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static org.hawaiiframework.logging.util.IndentUtil.indent;

/**
 * Utility for logging requests / responses.
 * <p>
 * The utility can be used to generate HTTP request / response log strings. Both for incoming service calls as outgoing calls (i.e. calls
 * to backend systems).
 *
 * @author Rutger Lubbers
 * @since 3.0.0
 */
public class HttpRequestResponseDebugLogUtil {

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Create a request line for the {@code requestUri} and {@code protocol}.
     * <p>
     * For example: {@code GET /doc/test.html HTTP/1.1}.
     */
    private String createRequestLine(final String method, final String request, final String protocol) {
        return format("%s %s %s", method, request, protocol);
    }

    /**
     * Create log string where the parts have been masked already.
     */
    private String createLogString(final String requestLine, final String headers, final String body) {
        final StringBuilder builder = new StringBuilder();
        if (requestLine != null) {
            builder.append(requestLine);
            builder.append(NEW_LINE);
        }
        if (headers != null && !headers.isEmpty()) {
            builder.append(headers);
        }
        if (body != null && !body.isEmpty()) {
            builder.append(NEW_LINE);
            builder.append(body);
        }
        final String value = builder.toString();

        return indent(value);
    }

    public String getTxRequestDebugOutput(final HttpServletRequest servletRequest, final String headers, final String body) {
        final String requestLine =
                createRequestLine(servletRequest.getMethod(), servletRequest.getRequestURI(), servletRequest.getProtocol());
        return createLogString(requestLine, headers, body);
    }

    public String getTxResponseDebugOutput(final String protocol, final HttpStatus httpStatus, final String headers, final String body) {

        final String statusLine = format("%s %s", protocol, httpStatus);
        return createLogString(statusLine, headers, body);
    }

    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public String getCallRequestDebugOutput(final String method, final String requestUri, final String headers, final String body) {
        final String requestLine = createRequestLine(method, requestUri, "");
        return createLogString(requestLine, headers, body);
    }

    public String getCallResponseDebugOutput(final String headers, final String body) {
        return createLogString(null, headers, body);
    }

}
