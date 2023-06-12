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
package org.hawaiiframework.logging.http;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.hawaiiframework.logging.config.MediaTypeVoter;
import org.hawaiiframework.logging.model.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.KibanaLogTypeNames;
import org.hawaiiframework.logging.util.HttpRequestResponseBodyLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseDebugLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseHeadersLogUtil;
import org.hawaiiframework.logging.web.filter.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.filter.ResettableHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.FAILURE;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.SUCCESS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_HEADERS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_METHOD;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_SIZE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_URI;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_RESPONSE_BODY;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_RESPONSE_HEADERS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_RESPONSE_SIZE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_STATUS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_HEADERS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_METHOD;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_SIZE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_URI;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_RESPONSE_BODY;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_RESPONSE_HEADERS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_RESPONSE_SIZE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_STATUS;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isLogged;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.markLogged;

/**
 * General logger.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "checkstyle:ClassFanOutComplexity"})
public class DefaultHawaiiRequestResponseLogger implements HawaiiRequestResponseLogger {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHawaiiRequestResponseLogger.class);


    /**
     * The util to use for generating request / response headers log statements.
     */
    private final HttpRequestResponseHeadersLogUtil headersLogUtil;

    /**
     * The util to use for generating request / response body log statements.
     */
    private final HttpRequestResponseBodyLogUtil bodyLogUtil;

    /**
     * The util to use for generating request / response debug log statements.
     */
    private final HttpRequestResponseDebugLogUtil debugLogUtil;

    /**
     * The media type voter.
     */
    private final MediaTypeVoter mediaTypeVoter;

    /**
     * The constructor.
     *
     * @param headersLogUtil The util to use for generating request / response headers log statements.
     * @param bodyLogUtil    The util to use for generating request / response body log statements.
     * @param debugLogUtil   The util to use for generating request / response debug log statements.
     * @param mediaTypeVoter The media type voter.
     */
    public DefaultHawaiiRequestResponseLogger(final HttpRequestResponseHeadersLogUtil headersLogUtil,
            final HttpRequestResponseBodyLogUtil bodyLogUtil,
            final HttpRequestResponseDebugLogUtil debugLogUtil,
            final MediaTypeVoter mediaTypeVoter) {
        this.bodyLogUtil = bodyLogUtil;
        this.headersLogUtil = headersLogUtil;
        this.debugLogUtil = debugLogUtil;
        this.mediaTypeVoter = mediaTypeVoter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logRequest(final ResettableHttpServletRequest wrappedRequest) throws IOException {
        if (isLogged(wrappedRequest)) {
            return;
        }
        markLogged(wrappedRequest);
        final String method = wrappedRequest.getMethod();
        final String requestUri = wrappedRequest.getRequestURI();
        final int contentLength = wrappedRequest.getContentLength();
        // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
        final String contentType = getContentType(wrappedRequest);
        final boolean contentTypeCanBeLogged = mediaTypeVoter.mediaTypeAllowed(contentType);
        String requestHeaders = "";
        String requestBody = "";
        try {
            // This might force the request to be read, hence in the try, so the request can be reset.
            requestHeaders = headersLogUtil.getTxRequestHeaders(wrappedRequest);

            KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.REQUEST_BODY);

            KibanaLogFields.tag(TX_REQUEST_METHOD, method);
            KibanaLogFields.tag(TX_REQUEST_URI, requestUri);
            KibanaLogFields.tag(TX_REQUEST_SIZE, contentLength);
            KibanaLogFields.tag(TX_REQUEST_HEADERS, requestHeaders);
            requestBody = bodyLogUtil.getTxRequestBody(wrappedRequest);
            addBodyTag(contentTypeCanBeLogged, TX_REQUEST_BODY, requestBody);

        } catch (Throwable t) {
            LOGGER.error("Error getting payload for request.", t);
            throw t;
        } finally {
            LOGGER.info("Invoked '{} {}' with content type '{}' and size of '{}' bytes.", method, requestUri, contentType,
                    contentLength);
            if (contentTypeCanBeLogged) {
                LOGGER.debug("Request is:\n{}",
                        debugLogUtil.getTxRequestDebugOutput(wrappedRequest, requestHeaders, requestBody));
            }

            // Keep request uri in all other logging!
            KibanaLogFields.clear(LOG_TYPE, TX_REQUEST_METHOD, TX_REQUEST_SIZE, TX_REQUEST_HEADERS, TX_REQUEST_BODY);
            wrappedRequest.reset();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logRequest(final HttpRequest request, final byte[] body) {
        try {
            final HttpMethod method = request.getMethod();
            final String requestUri = request.getURI().toString();
            final int contentLength = body.length;
            // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
            final MediaType contentType = getContentType(request);
            final boolean contentTypeCanBeLogged = mediaTypeVoter.mediaTypeAllowed(contentType);
            final String requestHeaders = headersLogUtil.getCallRequestHeaders(request);
            final String requestBody = bodyLogUtil.getCallRequestBody(body);

            KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.CALL_REQUEST_BODY);

            KibanaLogFields.tag(CALL_REQUEST_METHOD, method.name());
            KibanaLogFields.tag(CALL_REQUEST_URI, requestUri);

            KibanaLogFields.tag(CALL_REQUEST_SIZE, contentLength);
            KibanaLogFields.tag(CALL_REQUEST_HEADERS, requestHeaders);
            addBodyTag(contentTypeCanBeLogged, CALL_REQUEST_BODY, requestBody);

            LOGGER.info("Calling '{} {}' with content type '{}' and size of '{}' bytes.", method, requestUri, contentType, contentLength);
            if (contentTypeCanBeLogged) {
                LOGGER.debug("Call is:\n{}",
                        debugLogUtil.getCallRequestDebugOutput(method, requestUri, requestHeaders, requestBody));
            }
        } finally {
            KibanaLogFields
                    .clear(LOG_TYPE, CALL_REQUEST_METHOD, CALL_REQUEST_URI, CALL_REQUEST_SIZE, CALL_REQUEST_HEADERS, CALL_REQUEST_BODY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logResponse(final HttpServletRequest servletRequest, final ContentCachingWrappedResponse wrappedResponse) {

        try {
            final String requestURI = servletRequest.getRequestURI();
            final int contentLength = wrappedResponse.getContentSize();
            final String contentType = getContentType(wrappedResponse);
            final boolean contentTypeCanBeLogged = mediaTypeVoter.mediaTypeAllowed(contentType);
            final HttpStatus httpStatus = HttpStatus.valueOf(wrappedResponse.getStatus());
            final String responseHeaders = headersLogUtil.getTxResponseHeaders(wrappedResponse);
            final String responseBody = bodyLogUtil.getTxResponseBody(wrappedResponse);

            KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.RESPONSE_BODY);

            KibanaLogFields.tag(TX_RESPONSE_SIZE, contentLength);
            KibanaLogFields.tag(TX_RESPONSE_HEADERS, responseHeaders);

            addBodyTag(contentTypeCanBeLogged, TX_RESPONSE_BODY, responseBody);

            KibanaLogFields.tag(TX_STATUS, httpStatus.value());

            LOGGER.info("Response '{}' is '{}' with content type '{}' and size of '{}' bytes.", requestURI, httpStatus, contentType,
                    contentLength);
            if (contentTypeCanBeLogged) {
                LOGGER.debug("Response is:\n{}",
                        debugLogUtil.getTxResponseDebugOutput(servletRequest.getProtocol(), httpStatus, responseHeaders, responseBody));
            }
        } finally {
            KibanaLogFields.clear(LOG_TYPE, TX_RESPONSE_SIZE, TX_RESPONSE_HEADERS, CALL_RESPONSE_BODY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logResponse(final ClientHttpResponse response) throws IOException {
        try {
            final HttpStatusCode httpStatus = response.getStatusCode();
            final MediaType contentType = getContentType(response);
            final boolean contentTypeCanBeLogged = mediaTypeVoter.mediaTypeAllowed(contentType);
            final String responseHeaders = headersLogUtil.getCallResponseHeaders(response);
            final String responseBody = bodyLogUtil.getCallResponseBody(response);
            final int contentLength = responseBody.length();

            KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.CALL_RESPONSE_BODY);

            KibanaLogFields.tag(CALL_RESPONSE_SIZE, contentLength);
            KibanaLogFields.tag(CALL_RESPONSE_HEADERS, responseHeaders);
            addBodyTag(contentTypeCanBeLogged, CALL_RESPONSE_BODY, responseBody);

            if (httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection()) {
                KibanaLogFields.tag(CALL_STATUS, SUCCESS);
            } else {
                KibanaLogFields.tag(CALL_STATUS, FAILURE);
            }

            LOGGER.info("Got response '{}' with content type '{}' and size of '{}' bytes.", httpStatus, contentType, contentLength);
            if (contentTypeCanBeLogged) {
                LOGGER.debug("Got response '{}':\n{}", httpStatus,
                        debugLogUtil.getCallResponseDebugOutput(responseHeaders, responseBody));
            }
        } finally {
            KibanaLogFields.clear(LOG_TYPE, CALL_RESPONSE_SIZE, CALL_RESPONSE_HEADERS, CALL_RESPONSE_BODY);
        }
    }

    private void addBodyTag(final boolean contentTypeCanBeLogged, final KibanaLogFieldNames tag, final String responseBody) {
        if (contentTypeCanBeLogged) {
            KibanaLogFields.tag(tag, responseBody);
        } else {
            KibanaLogFields.tag(tag, "invalid mime type for logging");
        }
    }

    private MediaType getContentType(final HttpRequest request) {
        return getContentType(request.getHeaders());
    }

    private MediaType getContentType(final ClientHttpResponse response) {
        return getContentType(response.getHeaders());
    }

    private MediaType getContentType(final HttpHeaders httpHeaders) {
        return httpHeaders.getContentType();
    }

    private String getContentType(final ServletRequest wrappedRequest) {
        return wrappedRequest.getContentType();
    }

    private String getContentType(final ServletResponse response) {
        return response.getContentType();
    }

}
