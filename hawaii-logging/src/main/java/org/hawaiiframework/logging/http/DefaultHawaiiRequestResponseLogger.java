package org.hawaiiframework.logging.http;

import org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.model.AutoCloseableKibanaLogField;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.hawaiiframework.logging.web.filter.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.filter.ResettableHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.*;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.*;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.*;

/**
 * General logger.
 */
public class DefaultHawaiiRequestResponseLogger implements HawaiiRequestResponseLogger {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHawaiiRequestResponseLogger.class);

    /**
     * The request/response log util to use for generating log statements.
     */
    private final HttpRequestResponseLogUtil httpRequestResponseLogUtil;

    /**
     * The configuration to use.
     */
    private final HawaiiLoggingConfigurationProperties configuration;

    public DefaultHawaiiRequestResponseLogger(final HttpRequestResponseLogUtil httpRequestResponseLogUtil,
            final HawaiiLoggingConfigurationProperties configuration) {
        this.httpRequestResponseLogUtil = httpRequestResponseLogUtil;
        this.configuration = configuration;
    }

    /**
     * Log incoming request.
     */
    @Override
    public void logRequest(final ResettableHttpServletRequest wrappedRequest) throws IOException {
        final String method = wrappedRequest.getMethod();
        final String requestUri = wrappedRequest.getRequestURI();
        final int contentLength = wrappedRequest.getContentLength();
        final MediaType contentType = getContentType(wrappedRequest);

        try (AutoCloseableKibanaLogField requestBody = KibanaLogFields.logType(REQUEST_BODY);
                AutoCloseableKibanaLogField requestSize = KibanaLogFields.tagCloseable(TX_REQUEST_SIZE, contentLength);
        ) {
            LOGGER.info("Invoked '{} {}' with content type '{}' and size of '{}' bytes.", method, requestUri, contentType, contentLength);
            try {
                if (contentTypeCanBeLogged(contentType)) {
                    LOGGER.info("Request is:\n{}", httpRequestResponseLogUtil.formatRequest(method, requestUri, wrappedRequest));
                }
            } finally {
                wrappedRequest.reset();
            }
        }
    }

    /**
     * Log outgoing request.
     */
    @Override
    public void logRequest(final HttpRequest request, final byte[] body) {
        final String method = request.getMethodValue();
        final String requestUri = request.getURI().toString();
        final int contentLength = body.length;
        // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
        final MediaType contentType = getContentType(request);

        try (AutoCloseableKibanaLogField callMethod = KibanaLogFields.tagCloseable(CALL_METHOD, request.getMethodValue());
                AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(CALL_REQUEST_BODY)) {

            LOGGER.info("Calling '{} {}' with content type '{}' and size of '{}' bytes.", method, requestUri, contentType, contentLength);
            if (contentTypeCanBeLogged(contentType)) {
                LOGGER.info("Call is:\n{}", httpRequestResponseLogUtil.formatRequest(method, requestUri, request.getHeaders(), body));
            }
        }
    }

    /**
     * Log response of incoming request.
     */
    @Override
    public void logResponse(final HttpServletRequest servletRequest,
            final ContentCachingWrappedResponse wrappedResponse)
            throws IOException {

        final String request = httpRequestResponseLogUtil.getRequestUri(servletRequest);
        final int contentLength = wrappedResponse.getContentSize();
        final MediaType contentType = getContentType(wrappedResponse);
        final HttpStatus httpStatus = HttpStatus.valueOf(wrappedResponse.getStatus());

        KibanaLogFields.set(HTTP_STATUS, httpStatus.value());
        try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(RESPONSE_BODY);
                AutoCloseableKibanaLogField responseSize = KibanaLogFields.tagCloseable(TX_RESPONSE_SIZE, contentLength);) {
            LOGGER.info("Response '{}' is '{}' with content type '{}' and size of '{}' bytes.", request, httpStatus, contentType,
                    contentLength);
            if (contentTypeCanBeLogged(contentType)) {
                LOGGER.info("Response is:\n{}",
                        httpRequestResponseLogUtil.createLogString(servletRequest, wrappedResponse, httpStatus));
            }
        }
    }

    /**
     * Log response of an outgoing request.
     */
    @Override
    public void logResponse(final ClientHttpResponse response) throws IOException {
        final HttpStatus httpStatus = response.getStatusCode();
        final MediaType contentType = getContentType(response);

        LOGGER.info("Got response '{} {}' with content type '{}'.", httpStatus.value(), httpStatus.getReasonPhrase(), contentType);
        if (contentTypeCanBeLogged(contentType)) {
            logResponse(httpStatus, response);
        }
    }

    private void logResponse(final HttpStatus httpStatus, final ClientHttpResponse response) throws IOException {
        try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(CALL_RESPONSE_BODY)) {
            if (httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection()) {
                KibanaLogFields.callResult(SUCCESS);
            } else {
                KibanaLogFields.callResult(BACKEND_FAILURE);
            }
            final String body = httpRequestResponseLogUtil.getResponseBody(response);
            final String logString = httpRequestResponseLogUtil.createLogString(response.getHeaders(), body);

            LOGGER.info("Got response '{}':\n{}", httpStatus, logString);
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

    private MediaType getContentType(final ServletRequest wrappedRequest) {
        return parse(wrappedRequest.getContentType());
    }

    private MediaType getContentType(final ServletResponse response) {
        return parse(response.getContentType());
    }

    private MediaType parse(final String contentType) {
        if (isNotBlank(contentType)) {
            try {
                return MediaType.parseMediaType(contentType);
            } catch (InvalidMediaTypeException exception) {
                LOGGER.info("Got error parsing content type '{}'.", contentType, exception);
            }
        }

        return null;
    }

    private boolean contentTypeCanBeLogged(final MediaType contentType) {
        // If nothing is configured, then all content types are allowed.
        if (contentType == null || getAllowedContentTypes() == null || getAllowedContentTypes().isEmpty()) {
            return true;
        } else {
            return getAllowedContentTypes().contains(asString(contentType));
        }
    }

    private String asString(final MediaType mediaType) {
        return mediaType.getType() + '/' + mediaType.getSubtype();
    }

    private List<String> getAllowedContentTypes() {
        return configuration.getAllowedContentTypes();
    }

}
