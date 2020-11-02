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
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.BACKEND_FAILURE;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.SUCCESS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_METHOD;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.HTTP_STATUS;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_RESPONSE_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.RESPONSE_BODY;

/**
 * General logger.
 */
@SuppressWarnings("PMD.ExcessiveImports")
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

    @Override
    public void logRequest(final HttpRequest request, final byte[] body) {
        try (AutoCloseableKibanaLogField callMethod = KibanaLogFields.tagCloseable(CALL_METHOD, request.getMethodValue());
                AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(CALL_REQUEST_BODY)) {

            // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
            final String contentType = getContentType(request);
            if (contentType == null || contentTypeCanBeLogged(contentType)) {
                LOGGER.info("Called '{} {}':\n{}", request.getMethod(), request.getURI(),
                        httpRequestResponseLogUtil.createLogString(request.getHeaders(), body));
            }
        }
    }

    @Override
    public void logRequest(final ResettableHttpServletRequest wrappedRequest) throws IOException {
        final String requestUri = wrappedRequest.getRequestURI();
        final int contentLength = wrappedRequest.getContentLength();
        final String contentType = wrappedRequest.getContentType();

        try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(REQUEST_BODY)) {
            LOGGER.info("Invoked '{}' with content type '{}' and size of '{}' bytes.", requestUri, contentType, contentLength);
            try {
                if (contentTypeCanBeLogged(contentType)) {
                    LOGGER.info("Request is:\n{}", httpRequestResponseLogUtil.formatRequest(requestUri, wrappedRequest));
                }
            } finally {
                wrappedRequest.reset();
            }
        }
    }

    @Override
    public void logResponse(final ClientHttpResponse response) throws IOException {
        if (contentTypeCanBeLogged(getContentType(response))) {
            final HttpStatus statusCode = response.getStatusCode();
            final String statusText = response.getStatusText();

            final String body = httpRequestResponseLogUtil.getResponseBody(response);
            logResponse(statusCode, statusText, response.getHeaders(), body);
        }
    }

    @Override
    public void logResponse(final HttpServletRequest servletRequest,
            final ContentCachingWrappedResponse wrappedResponse)
            throws IOException {

        final String request = httpRequestResponseLogUtil.getRequestUri(servletRequest);
        final int contentLength = wrappedResponse.getContentSize();
        final String contentType = wrappedResponse.getContentType();
        final HttpStatus httpStatus = HttpStatus.valueOf(wrappedResponse.getStatus());

        KibanaLogFields.set(HTTP_STATUS, httpStatus.value());
        try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(RESPONSE_BODY)) {
            LOGGER.info("Response '{}' is '{} {}' with content type '{}' and size of '{}' bytes.", request,
                    httpStatus.value(), httpStatus.getReasonPhrase(), contentType, contentLength);
            if (contentTypeCanBeLogged(contentType) && contentLength != 0) {
                LOGGER.info("Response is:\n{}",
                        httpRequestResponseLogUtil.createLogString(servletRequest, wrappedResponse, httpStatus));
            }
        }
    }

    private void logResponse(final HttpStatus statusCode, final String statusText, final HttpHeaders headers, final String body) {
        try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(CALL_RESPONSE_BODY)) {
            if (statusCode.is2xxSuccessful() || statusCode.is3xxRedirection()) {
                KibanaLogFields.callResult(SUCCESS);
            } else {
                KibanaLogFields.callResult(BACKEND_FAILURE);
            }

            LOGGER.info("Got response '{} {}':\n{}", statusCode, statusText, httpRequestResponseLogUtil.createLogString(headers, body));
        }
    }

    public String getContentType(final ClientHttpResponse response) {
        final HttpHeaders httpHeaders = getHttpHeaders(response);
        return getContentType(httpHeaders);
    }

    public String getContentType(final HttpRequest request) {
        final HttpHeaders httpHeaders = getHttpHeaders(request);
        return getContentType(httpHeaders);
    }

    private String getContentType(final HttpHeaders httpHeaders) {
        final MediaType contentType = getMediaType(httpHeaders);

        if (contentType == null) {
            return null;
        }
        return getContentTypeAsString(contentType);
    }

    private HttpHeaders getHttpHeaders(final ClientHttpResponse response) {
        return response.getHeaders();
    }

    private HttpHeaders getHttpHeaders(final HttpRequest request) {
        return request.getHeaders();
    }


    private MediaType getMediaType(final HttpHeaders httpHeaders) {
        return httpHeaders.getContentType();
    }

    private String getContentTypeAsString(final MediaType mediaType) {
        return mediaType.getType() + '/' + mediaType.getSubtype();
    }

    public boolean contentTypeCanBeLogged(final String contentType) {
        //Assume nothing has been configured so we allow everything
        if (getAllowedContentTypes() == null || getAllowedContentTypes().isEmpty()) {
            return true;
        } else {
            return getAllowedContentTypes().contains(contentType);
        }
    }

    private List<String> getAllowedContentTypes() {
        return configuration.getAllowedContentTypes();
    }

}
