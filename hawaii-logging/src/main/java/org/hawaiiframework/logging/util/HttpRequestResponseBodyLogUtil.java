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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.web.filter.ContentCachingWrappedResponse;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility for logging requests / responses.
 * <p>
 * The utility can be used to generate HTTP request / response log strings. Both for incoming service calls as outgoing calls (i.e. calls
 * to backend systems).
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class HttpRequestResponseBodyLogUtil {

    /**
     * Masks passwords in json strings.
     */
    private static final PasswordMaskerUtil PASSWORD_MASKER = new PasswordMaskerUtil();

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Get the request body.
     * With password masking.
     *
     * @param servletRequest The servlet request.
     * @return The body.
     * @throws IOException In case the body could not be read.
     */
    public String getTxRequestBody(final HttpServletRequest servletRequest) throws IOException {
        final String body = getPostBody(servletRequest);
        return PASSWORD_MASKER.maskPasswordsIn(body);
    }

    /**
     * Get the response body.
     * With password masking.
     *
     * @param servletResponse The servlet response.
     * @return The body.
     */
    public String getTxResponseBody(final ContentCachingWrappedResponse servletResponse) {
        return toString(servletResponse.getContentAsByteArray(), servletResponse.getCharacterEncoding());
    }

    /**
     * Get the call request body.
     * With password masking.
     *
     * @param body The http request body.
     * @return The body.
     */
    public String getCallRequestBody(final byte[] body) {
        return toString(body, Charset.defaultCharset());
    }

    /**
     * Get the call response body.
     * With password masking.
     *
     * @param response The http response.
     * @return The body.
     */
    public String getCallResponseBody(final ClientHttpResponse response) throws IOException {
        final StringBuilder inputStringBuilder = new StringBuilder();

        return getResponseBody(inputStringBuilder, response);
    }

    private String toString(final byte[] body, final String charset) {
        return toString(body, Charset.forName(charset));
    }

    private String toString(final byte[] body, final Charset charset) {
        return new String(body, charset);
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
