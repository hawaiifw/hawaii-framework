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
import org.hawaiiframework.logging.config.RequestResponseLogFilterConfiguration;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.RequestId;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.HTTP_STATUS;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.RESPONSE_BODY;

/**
 * Filter that logs the input and output of each HTTP request. It also logs the duration of the request.
 * <p>
 * For more inspiration see AbstractRequestLoggingFilter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class RequestResponseLogFilter extends OncePerRequestFilter {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    /**
     * The content types to log.
     */
    private final Set<String> contentTypesToLog = new HashSet<>();

    /**
     * The maximum length to log to console.
     */
    private long maxContentLength;

    /**
     * The log directory.
     */
    private File logDir;

    /**
     * The configuration for the logging.
     */
    private final RequestResponseLogFilterConfiguration configuration;

    /**
     * The request response log util.
     */
    private final HttpRequestResponseLogUtil httpRequestResponseLogUtil;

    /**
     * The constructor.
     */
    @Autowired
    public RequestResponseLogFilter(final RequestResponseLogFilterConfiguration configuration,
            final HttpRequestResponseLogUtil httpRequestResponseLogUtil) {
        super();
        this.configuration = requireNonNull(configuration);
        this.httpRequestResponseLogUtil = requireNonNull(httpRequestResponseLogUtil);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {

        // Get the request URI (formatted).
        final String method = httpServletRequest.getMethod();
        final String requestUri = getLogLine(httpServletRequest);

        // Create a new wrapped request, which we can use to get the body from.
        final ResettableHttpServletRequest wrappedRequest = new ResettableHttpServletRequest(httpServletRequest);

        logRequest(requestUri, method, wrappedRequest);

        final ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Do filter
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logResponse(requestUri, method, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(final String request, final String method, final ResettableHttpServletRequest wrappedRequest)
            throws IOException {
        final int contentLength = wrappedRequest.getContentLength();
        final String contentType = wrappedRequest.getContentType();

        KibanaLogFields.setLogType(REQUEST_BODY);
        LOGGER.info("Invoked '{} {}' with content type '{}' and size of '{}' bytes.", method, request, contentType, contentLength);

        if (contentLength > 0 && mayLogLength(contentLength) && mayLogContentType(contentType)) {
            LOGGER.debug("Request is:\n{}", getRequestLogString(wrappedRequest));
        }
        KibanaLogFields.unsetLogType();
    }

    private Object getRequestLogString(final ResettableHttpServletRequest wrappedRequest) throws IOException {
        try {
            final String body = IOUtils.toString(wrappedRequest.getInputStream(), wrappedRequest.getCharacterEncoding());
            final HttpHeaders headers = httpRequestResponseLogUtil.getHeaders(wrappedRequest);
            return httpRequestResponseLogUtil.createLogString(headers, body);
        } finally {
            wrappedRequest.reset();
        }
    }

    private void logResponse(final String request, final String method, final ContentCachingResponseWrapper wrappedResponse)
            throws IOException {
        final int contentLength = wrappedResponse.getContentSize();
        final String contentType = wrappedResponse.getContentType();

        final String statusString = getHttpStatusString(wrappedResponse.getStatusCode());
        KibanaLogFields.set(HTTP_STATUS, getResponseStatus(wrappedResponse));
        KibanaLogFields.setLogType(RESPONSE_BODY);
        LOGGER.info("Response '{} {}' is '{}' with content type '{}' and size of '{}' bytes.", method, request,
                statusString, contentType, contentLength);

        if (contentLength > 0) {
            if (mayLogLength(contentLength) && mayLogContentType(contentType)) {
                LOGGER.debug("Response is:\n{}", getResponseLogString(wrappedResponse, contentLength));
            } else if (mayLogToFile()) {
                if (!logDir.exists() && !logDir.mkdirs()) {
                    LOGGER.error("Error creating directory '{}'", logDir.getAbsolutePath());
                }
                if (logDir.exists()) {
                    final String requestId = RequestId.get();
                    final File file = new File(logDir, requestId);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        IOUtils.copy(wrappedResponse.getContentInputStream(), fos);
                    }
                    LOGGER.info("Wrote output to file '{}'.", file.getAbsolutePath());
                } else {
                    LOGGER.error("Somehow we cannot create '{}'.", logDir.getAbsolutePath());
                }
            }
        }
        KibanaLogFields.unsetLogType();
    }

    private String getResponseLogString(final ContentCachingResponseWrapper response, final int contentSize)
            throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentSize)) {
            IOUtils.copy(response.getContentInputStream(), baos);

            final HttpHeaders headers = httpRequestResponseLogUtil.getHeaders(response);
            return httpRequestResponseLogUtil.createLogString(headers, baos.toString(response.getCharacterEncoding()));
        }
    }

    private String getResponseStatus(final HttpServletResponse response) {
        return Integer.toString(response.getStatus());
    }

    /**
     * Transform the request into a log line.
     */
    private String getLogLine(final HttpServletRequest request) {
        if (request.getRequestURI() == null) {
            return null;
        }

        final StringBuilder result = new StringBuilder(request.getRequestURI());
        if (request.getQueryString() != null) {
            result.append('?').append(request.getQueryString());
        }
        return result.toString();
    }

    private boolean mayLogToFile() {
        return configuration.isFallbackToFile();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private String getHttpStatusString(final int statusCode) {
        final HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return statusCode + " " + httpStatus.getReasonPhrase();
    }

    private boolean mayLogLength(final int length) {
        return length < maxContentLength;
    }

    private boolean mayLogContentType(final String contentType) {
        if (contentTypesToLog.isEmpty()) {
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
    protected void initFilterBean() throws ServletException {
        contentTypesToLog.addAll(configuration.getAllowedContentTypes());
        maxContentLength = configuration.getMaxLogSizeInBytes();
        logDir = new File(configuration.getDirectory());
    }

}
