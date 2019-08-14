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

import org.apache.commons.io.IOUtils;
import org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.HTTP_STATUS;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.RESPONSE_BODY;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isInternalRedirect;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.markAsInternalRedirect;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.unmarkAsInternalRedirect;

/**
 * Filter that logs the input and output of each HTTP request. It also logs the duration of the request.
 * <p>
 * For more inspiration see AbstractRequestLoggingFilter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class RequestResponseLogFilter extends AbstractGenericFilterBean {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    /**
     * The content types to log.
     */
    private final Set<String> contentTypesToLog = new HashSet<>();

    /**
     * The configuration for the logging.
     */
    private final HawaiiLoggingConfigurationProperties configuration;

    /**
     * The request response log util.
     */
    private final HttpRequestResponseLogUtil httpRequestResponseLogUtil;

    /**
     * The constructor.
     */
    public RequestResponseLogFilter(final HawaiiLoggingConfigurationProperties configuration,
            final HttpRequestResponseLogUtil httpRequestResponseLogUtil) {
        super();
        this.configuration = configuration;
        this.httpRequestResponseLogUtil = httpRequestResponseLogUtil;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        LOGGER.trace("Request dispatcher type is '{}'; is forward is '{}'.", httpServletRequest.getDispatcherType(),
                isInternalRedirect(httpServletRequest));

        // Get the request URI (formatted) (for instance, GET /foo/bar?xyz=pqr&abc=11).
        final String requestUri = httpRequestResponseLogUtil.getRequestUri(httpServletRequest);

        // Create a new wrapped request, which we can use to get the body from.
        final ContentCachingWrappedResponse wrappedResponse = new ContentCachingWrappedResponse(response);
        final ResettableHttpServletRequest wrappedRequest = new ResettableHttpServletRequest(httpServletRequest, wrappedResponse);

        if (!isInternalRedirect(httpServletRequest)) {
            logRequest(requestUri, wrappedRequest);
        }

        // Do filter
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            if (wrappedResponse.isRedirect()) {
                markAsInternalRedirect(wrappedRequest);
            } else {
                unmarkAsInternalRedirect(wrappedRequest);
                logResponse(requestUri, wrappedRequest, wrappedResponse, HttpStatus.valueOf(wrappedResponse.getStatusCode()));
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    private void logRequest(final String requestUri, final ResettableHttpServletRequest wrappedRequest)
            throws IOException {
        final int contentLength = wrappedRequest.getContentLength();
        final String contentType = wrappedRequest.getContentType();

        KibanaLogFields.setLogType(REQUEST_BODY);
        LOGGER.info("Invoked '{}' with content type '{}' and size of '{}' bytes.", requestUri, contentType, contentLength);
        try {
            if (mayLogContentType(contentType)) {
                LOGGER.info("Request is:\n{}", httpRequestResponseLogUtil.formatRequest(requestUri, wrappedRequest));
            }
        } finally {
            wrappedRequest.reset();
        }
        KibanaLogFields.unsetLogType();
    }

    private void logResponse(final String request, final HttpServletRequest servletRequest,
            final ContentCachingWrappedResponse wrappedResponse, final HttpStatus httpStatus)
            throws IOException {

        final int contentLength = wrappedResponse.getContentSize();
        final String contentType = wrappedResponse.getContentType();

        KibanaLogFields.set(HTTP_STATUS, httpStatus.value());
        KibanaLogFields.setLogType(RESPONSE_BODY);
        LOGGER.info("Response '{}' is '{} {}' with content type '{}' and size of '{}' bytes.", request,
                httpStatus.value(), httpStatus.getReasonPhrase(), contentType, contentLength);

        if (mayLogContentType(contentType)) {
            LOGGER.info("Response is:\n{}", getResponseLogString(servletRequest, wrappedResponse, httpStatus, contentLength));
        }
        KibanaLogFields.unsetLogType();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private String getResponseLogString(final HttpServletRequest servletRequest, final ContentCachingResponseWrapper response,
            final HttpStatus httpStatus, final int contentSize)
            throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentSize)) {
            IOUtils.copy(response.getContentInputStream(), baos);

            final String statusLine = format("%s %s %s", servletRequest.getProtocol(), httpStatus.value(), httpStatus.getReasonPhrase());
            final HttpHeaders headers = httpRequestResponseLogUtil.getHeaders(response);
            return httpRequestResponseLogUtil.createLogString(statusLine, headers, baos.toByteArray(), response.getCharacterEncoding());
        }
    }

    private boolean mayLogContentType(final String contentType) {
        if (contentType == null || contentTypesToLog.isEmpty()) {
            return true;
        }

        final String[] strings = contentType.split(";");
        final String type = strings[0];
        return contentTypesToLog.contains(type);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void initFilterBean() {
        contentTypesToLog.addAll(configuration.getAllowedContentTypes());
    }

}
