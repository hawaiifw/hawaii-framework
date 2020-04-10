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
package org.hawaiiframework.logging.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hawaiiframework.logging.util.LogUtil.indent;

/**
 * Utility for logging requests / responses.
 * <p>
 * The utility can be used to generate HTTP request / response log strings. Both for incoming service calls as outgoing calls (i.e. calls
 * to backend systems).
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class HttpRequestResponseLogUtil {

    /**
     * Masks passwords in json strings.
     */
    private static final PasswordMaskerUtil PASSWORD_MASKER = new PasswordMaskerUtil();

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * The indent to use.
     */
    private static final String INDENT = "  ";

    /**
     * Create {@link HttpHeaders} for the {@code request}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public HttpHeaders getHeaders(final HttpServletRequest request) {
        final HttpHeaders headers = new HttpHeaders();

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                headers.add(headerName, values.nextElement());
            }
        }

        return headers;
    }

    /**
     * Create {@link HttpHeaders} for the {@code response}.
     */
    public HttpHeaders getHeaders(final HttpServletResponse response) {
        final HttpHeaders headers = new HttpHeaders();
        for (final String headerName : response.getHeaderNames()) {
            for (final String headerValue : response.getHeaders(headerName)) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

    /**
     * Create a request line for the {@code requestUri} and {@code protocol}.
     * <p>
     * For example: {@code GET /doc/test.html HTTP/1.1}.
     */
    public String createRequestLine(final String request, final String protocol) {
        return format("%s %s", request, protocol);
    }

    /**
     * Create a log string for the given {@code headers} and {@code body} with the platform's default charset.
     */
    public String createLogString(final HttpHeaders headers, final String body) {
        return createLogString(null, headers, body.getBytes(Charset.defaultCharset()), Charset.defaultCharset());
    }

    /**
     * Create a log string for the given {@code headers} and {@code body} with the platform's default charset.
     */
    public String createLogString(final HttpHeaders headers, final byte[] body) {
        return createLogString(null, headers, body, Charset.defaultCharset());
    }

    /**
     * Create a log string for the given {@code headers} and {@code body} with the given {@code characterEncoding}.
     */
    public String createLogString(final HttpHeaders headers, final byte[] body, final String characterEncoding) {
        return createLogString(null, headers, body, Charset.forName(characterEncoding));
    }

    /**
     * Create a log string for the given {@code headers} and {@code body} with the given {@code charset}.
     */
    public String createLogString(final HttpHeaders headers, final byte[] body, final Charset charset) {
        return createLogString(null, headers, body, charset);
    }

    /**
     * Create a log string for the given {@code requestLine}, {@code headers} and {@code body}.
     */
    public String createLogString(final String requestLine, final HttpHeaders headers, final byte[] body, final String characterEncoding) {
        return createLogString(requestLine, headers, body, Charset.forName(characterEncoding));
    }

    /**
     * Create a log string for the given {@code requestLine}, {@code headers} and {@code body}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public String createLogString(final String requestLine, final HttpHeaders headers, final byte[] body, final Charset charset) {
        return createLogString(requestLine, headers, new String(body, charset));
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    public String createLogString(final HttpServletRequest servletRequest,
            final ContentCachingResponseWrapper response,
            final HttpStatus httpStatus,
            final int contentSize) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentSize)) {
            final String statusLine = format("%s %s %s", servletRequest.getProtocol(), httpStatus.value(), httpStatus.getReasonPhrase());
            final HttpHeaders headers = getHeaders(response);
            return createLogString(statusLine, headers, baos.toByteArray(), response.getCharacterEncoding());
        }
    }

    /**
     * Create a log string for the given {@code requestLine}, {@code headers} and {@code body}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public String createLogString(final String requestLine, final HttpHeaders headers, final String body) {
        final StringBuilder builder = new StringBuilder();
        if (requestLine != null) {
            builder.append(requestLine);
            builder.append(NEW_LINE);
        }
        appendHeaders(builder, headers);
        if (body != null && !body.isEmpty()) {
            builder.append(NEW_LINE);
            builder.append(body);
        }
        final String value = builder.toString();

        // remove clear text password values and indent the multi line body.
        return indent(PASSWORD_MASKER.maskPasswordsIn(value), INDENT);
    }

    private void appendHeaders(final StringBuilder builder, final HttpHeaders headers) {
        final List<String> headerNames = new ArrayList<>(headers.keySet());
        Collections.sort(headerNames);

        for (final String headerName : headerNames) {
            builder.append(headerName);
            builder.append(": ");
            builder.append(String.join(", ", headers.get(headerName)));
            builder.append(NEW_LINE);
        }
    }

    /**
     * Transform the request into a log line.
     */

    public String getRequestUri(final HttpServletRequest servletRequest) {
        final StringBuilder result = new StringBuilder(servletRequest.getMethod());
        result.append(' ').append(servletRequest.getRequestURI());
        if (servletRequest.getQueryString() != null) {
            result.append('?').append(servletRequest.getQueryString());
        }
        return result.toString();
    }

    /**
     * Format the request as a nicely formatted string.
     * <p>
     * Note that this will read the request! Use {@link org.hawaiiframework.logging.web.filter.ResettableHttpServletRequest} for instance
     * to reset the input.
     */
    public String formatRequest(final String request, final HttpServletRequest servletRequest) throws IOException {
        final String requestLine = createRequestLine(request, servletRequest.getProtocol());
        final HttpHeaders headers = getHeaders(servletRequest);
        final String body = getPostBody(servletRequest);
        return createLogString(requestLine, headers, body);
    }

    private String getPostBody(final HttpServletRequest servletRequest) throws IOException {
        final String body = IOUtils.toString(servletRequest.getInputStream(), servletRequest.getCharacterEncoding());
        if (StringUtils.isNotBlank(body)) {
            return body;
        }

        return getPostParametersBody(servletRequest);
    }

    private String getPostParametersBody(final HttpServletRequest request) {
        final Map<String, String[]> parameters = request.getParameterMap();
        return getPostParametersBody(request, parameters);
    }

    private String getPostParametersBody(final HttpServletRequest request, final Map<String, String[]> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> parameterNames = new ArrayList<>(parameters.keySet());
        Collections.sort(parameterNames);
        for (final String parameterName : parameterNames) {
            final String[] parameterValues = request.getParameterValues(parameterName);
            if (parameterValues != null) {
                for (final String value : parameterValues) {
                    stringBuilder.append(parameterName).append('=').append(value).append('\n');
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getResponseBody(final ClientHttpResponse response) throws IOException {
        final StringBuilder inputStringBuilder = new StringBuilder();

        return getResponseBody(inputStringBuilder, response);
    }

    private String getResponseBody(final StringBuilder inputStringBuilder, final ClientHttpResponse response) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(response.getBody(), UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append(NEW_LINE);
                line = bufferedReader.readLine();
            }
        }

        return inputStringBuilder.toString();
    }
}
