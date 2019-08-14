/*
 * Copyright 2015-2019 the original author or authors.
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
package org.hawaiiframework.logging.http.client;

import org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.BACKEND_FAILURE;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.SUCCESS;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.TIME_OUT;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_METHOD;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_RESPONSE_BODY;

/**
 * A logging client http request interceptor.
 * <p>
 * This logs the input and output of each call.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * The Hawaii logging configuration properties.
     */
    private final HawaiiLoggingConfigurationProperties configuration;

    /**
     * The request/response log util to use for generating log statements.
     */
    private final HttpRequestResponseLogUtil httpRequestResponseLogUtil;

    /**
     * The constructor.
     */
    public LoggingClientHttpRequestInterceptor(
            final HawaiiLoggingConfigurationProperties configuration,
            final HttpRequestResponseLogUtil httpRequestResponseLogUtil) {
        this.configuration = configuration;
        this.httpRequestResponseLogUtil = httpRequestResponseLogUtil;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        try {
            logRequest(request, body);
            final ClientHttpResponse response = execution.execute(request, body);
            logResponse(response);
            return response;
        } catch (IOException t) {
            KibanaLogFields.setLogType(CALL_END);
            KibanaLogFields.setCallResult(TIME_OUT);
            LOGGER.info("Got timeout from backend.");
            throw t;
        }
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        KibanaLogFields.set(CALL_METHOD, request.getMethodValue());
        KibanaLogFields.setLogType(CALL_REQUEST_BODY);
        LOGGER.info("Called '{} {}':\n{}", request.getMethod(), request.getURI(),
                httpRequestResponseLogUtil.createLogString(request.getHeaders(), body));

        KibanaLogFields.unsetLogType();
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        final HttpStatus statusCode = response.getStatusCode();
        final String statusText = response.getStatusText();
        String body = "";
        if (contentTypeCanBeLogged(getContentType(response))) {
            body = readResponseBody(response);
        }
        logResponse(statusCode, statusText, response.getHeaders(), body);
    }

    private void logResponse(final HttpStatus statusCode, final String statusText, final HttpHeaders headers, final String body) {
        KibanaLogFields.setLogType(CALL_RESPONSE_BODY);
        if (statusCode.is2xxSuccessful() || statusCode.is3xxRedirection()) {
            KibanaLogFields.setCallResult(SUCCESS);
        } else {
            KibanaLogFields.setCallResult(BACKEND_FAILURE);
        }

        LOGGER.info("Got response '{} {}':\n{}", statusCode, statusText, httpRequestResponseLogUtil.createLogString(headers, body));

        KibanaLogFields.unsetLogType();
    }

    private String readResponseBody(final ClientHttpResponse response) throws IOException {
        final StringBuilder inputStringBuilder = new StringBuilder();

        return readResponseBody(inputStringBuilder, response);
    }

    private String readResponseBody(final StringBuilder inputStringBuilder, final ClientHttpResponse response) throws IOException {
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

    private String getContentType(final ClientHttpResponse response) {
        final HttpHeaders httpHeaders = getHttpHeaders(response);
        final MediaType contentType = getMediaType(httpHeaders);
        return getContentTypeAsString(contentType);
    }

    private HttpHeaders getHttpHeaders(final ClientHttpResponse response) {
        return response.getHeaders();
    }

    private MediaType getMediaType(final HttpHeaders httpHeaders) {
        return httpHeaders.getContentType();
    }

    private String getContentTypeAsString(final MediaType mediaType) {
        return mediaType.getType() + '/' + mediaType.getSubtype();
    }

    private boolean contentTypeCanBeLogged(final String contentType) {
        return getAllowedContentTypes().contains(contentType);
    }

    private List<String> getAllowedContentTypes() {
        return configuration.getAllowedContentTypes();
    }

}
