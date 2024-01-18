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

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.hawaiiframework.logging.config.MediaTypeVoter;
import org.hawaiiframework.logging.model.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.KibanaLogTypeNames;
import org.hawaiiframework.logging.util.HttpRequestResponseBodyLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseDebugLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseHeadersLogUtil;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

/** General logger. */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "PMD.ExcessiveImports"})
public class DefaultHawaiiRequestResponseLogger implements HawaiiRequestResponseLogger {

  /** The logger to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultHawaiiRequestResponseLogger.class);

  /** The util to use for generating request / response headers log statements. */
  private final HttpRequestResponseHeadersLogUtil headersLogUtil;

  /** The util to use for generating request / response body log statements. */
  private final HttpRequestResponseBodyLogUtil bodyLogUtil;

  /** The util to use for generating request / response debug log statements. */
  private final HttpRequestResponseDebugLogUtil debugLogUtil;

  /** The media type voter to allow request calls to be logged. */
  private final MediaTypeVoter mediaTypeVoter;

  /** The media type voter to suppress body contents for. */
  private final MediaTypeVoter bodyExcludedMediaTypeVoter;

  /**
   * The constructor.
   *
   * @param headersLogUtil The util to use for generating request / response headers log statements.
   * @param bodyLogUtil The util to use for generating request / response body log statements.
   * @param debugLogUtil The util to use for generating request / response debug log statements.
   * @param mediaTypeVoter A media type voter to check for logging.
   * @param bodyExcludedMediaTypeVoter A media type voter to check to suppress body contents
   *     logging.
   */
  public DefaultHawaiiRequestResponseLogger(
      HttpRequestResponseHeadersLogUtil headersLogUtil,
      HttpRequestResponseBodyLogUtil bodyLogUtil,
      HttpRequestResponseDebugLogUtil debugLogUtil,
      MediaTypeVoter mediaTypeVoter,
      MediaTypeVoter bodyExcludedMediaTypeVoter) {
    this.bodyLogUtil = bodyLogUtil;
    this.headersLogUtil = headersLogUtil;
    this.debugLogUtil = debugLogUtil;
    this.mediaTypeVoter = mediaTypeVoter;
    this.bodyExcludedMediaTypeVoter = bodyExcludedMediaTypeVoter;
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingThrowable")
  public void logRequest(ResettableHttpServletRequest wrappedRequest) throws IOException {
    String method = wrappedRequest.getMethod();
    String requestUri = wrappedRequest.getRequestURI();
    int contentLength = wrappedRequest.getContentLength();
    // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
    String contentType = getContentType(wrappedRequest);
    boolean contentTypeCanBeLogged = contentTypeCanBeLogged(contentType);
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
      if (contentTypeCanBeLogged) {
        requestBody = bodyLogUtil.getTxRequestBody(wrappedRequest);
      }
      addBodyTag(contentTypeCanBeLogged, TX_REQUEST_BODY, requestBody);
    } catch (Throwable throwable) {
      LOGGER.error("Error getting payload for request.", throwable);
      throw throwable;
    } finally {
      LOGGER.info(
          "Invoked '{} {}' with content type '{}' and size of '{}' bytes.",
          method,
          requestUri,
          contentType,
          contentLength);
      if (contentTypeCanBeLogged && LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Request is:\n{}",
            debugLogUtil.getTxRequestDebugOutput(wrappedRequest, requestHeaders, requestBody));
      }

      // Keep request uri in all other logging!
      KibanaLogFields.clear(
          LOG_TYPE, TX_REQUEST_METHOD, TX_REQUEST_SIZE, TX_REQUEST_HEADERS, TX_REQUEST_BODY);
      wrappedRequest.reset();
    }
  }

  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public void logRequest(HttpRequest request, byte[] body) {
    try {
      KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.CALL_REQUEST_BODY);

      HttpMethod method = request.getMethod();
      KibanaLogFields.tag(CALL_REQUEST_METHOD, method.name());

      String requestUri = request.getURI().toString();
      KibanaLogFields.tag(CALL_REQUEST_URI, requestUri);

      int contentLength = body.length;
      KibanaLogFields.tag(CALL_REQUEST_SIZE, contentLength);

      String requestHeaders = headersLogUtil.getCallRequestHeaders(request);
      KibanaLogFields.tag(CALL_REQUEST_HEADERS, requestHeaders);

      // contentType can be null (a GET for example, doesn't have a Content-Type header usually)
      MediaType contentType = getContentType(request);
      boolean contentTypeCanBeLogged = contentTypeCanBeLogged(contentType);
      String requestBody = bodyLogUtil.getCallRequestBody(body);
      addBodyTag(contentTypeCanBeLogged, CALL_REQUEST_BODY, requestBody);

      LOGGER.info(
          "Calling '{} {}' with content type '{}' and size of '{}' bytes.",
          method,
          requestUri,
          contentType,
          contentLength);
      if (contentTypeCanBeLogged && LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Call is:\n{}",
            debugLogUtil.getCallRequestDebugOutput(
                method, requestUri, requestHeaders, requestBody));
      }
    } finally {
      KibanaLogFields.clear(
          LOG_TYPE,
          CALL_REQUEST_METHOD,
          CALL_REQUEST_URI,
          CALL_REQUEST_SIZE,
          CALL_REQUEST_HEADERS,
          CALL_REQUEST_BODY);
    }
  }

  private boolean contentTypeCanBeLogged(MediaType contentType) {
    return mediaTypeVoter.mediaTypeMatches(contentType)
        && !bodyExcludedMediaTypeVoter.mediaTypeMatches(contentType);
  }

  private boolean contentTypeCanBeLogged(String contentType) {
    return mediaTypeVoter.mediaTypeMatches(contentType)
        && !bodyExcludedMediaTypeVoter.mediaTypeMatches(contentType);
  }

  @Override
  public void logResponse(
      HttpServletRequest servletRequest, ContentCachingWrappedResponse wrappedResponse) {

    try {
      KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.RESPONSE_BODY);

      HttpStatus httpStatus = addHttpStatusTag(wrappedResponse);

      int contentLength = wrappedResponse.getContentSize();
      KibanaLogFields.tag(TX_RESPONSE_SIZE, contentLength);

      String responseHeaders = headersLogUtil.getTxResponseHeaders(wrappedResponse);
      KibanaLogFields.tag(TX_RESPONSE_HEADERS, responseHeaders);

      String contentType = getContentType(wrappedResponse);
      boolean contentTypeCanBeLogged = contentTypeCanBeLogged(contentType);
      String responseBody = bodyLogUtil.getTxResponseBody(wrappedResponse);
      addBodyTag(contentTypeCanBeLogged, TX_RESPONSE_BODY, responseBody);

      String requestUri = servletRequest.getRequestURI();
      LOGGER.info(
          "Response '{}' is '{}' with content type '{}' and size of '{}' bytes.",
          requestUri,
          httpStatus,
          contentType,
          contentLength);
      if (contentTypeCanBeLogged && LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Response is:\n{}",
            debugLogUtil.getTxResponseDebugOutput(
                servletRequest.getProtocol(), httpStatus, responseHeaders, responseBody));
      }
    } finally {
      KibanaLogFields.clear(LOG_TYPE, TX_RESPONSE_SIZE, TX_RESPONSE_HEADERS, CALL_RESPONSE_BODY);
    }
  }

  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public void logResponse(ClientHttpResponse response) throws IOException {
    try {
      KibanaLogFields.tag(LOG_TYPE, KibanaLogTypeNames.CALL_RESPONSE_BODY);

      MediaType contentType = getContentType(response);
      boolean contentTypeCanBeLogged = contentTypeCanBeLogged(contentType);
      String responseBody = bodyLogUtil.getCallResponseBody(response);
      addBodyTag(contentTypeCanBeLogged, CALL_RESPONSE_BODY, responseBody);

      int contentLength = responseBody.length();
      KibanaLogFields.tag(CALL_RESPONSE_SIZE, contentLength);

      String responseHeaders = headersLogUtil.getCallResponseHeaders(response);
      KibanaLogFields.tag(CALL_RESPONSE_HEADERS, responseHeaders);

      HttpStatusCode httpStatus = response.getStatusCode();
      if (httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection()) {
        KibanaLogFields.tag(CALL_STATUS, SUCCESS);
      } else {
        KibanaLogFields.tag(CALL_STATUS, FAILURE);
      }

      LOGGER.info(
          "Got response '{}' with content type '{}' and size of '{}' bytes.",
          httpStatus,
          contentType,
          contentLength);
      if (contentTypeCanBeLogged && LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Got response '{}':\n{}",
            httpStatus,
            debugLogUtil.getCallResponseDebugOutput(responseHeaders, responseBody));
      }
    } finally {
      KibanaLogFields.clear(
          LOG_TYPE, CALL_RESPONSE_SIZE, CALL_RESPONSE_HEADERS, CALL_RESPONSE_BODY);
    }
  }

  private static void addBodyTag(
      boolean contentTypeCanBeLogged, KibanaLogFieldNames tag, String responseBody) {
    if (contentTypeCanBeLogged) {
      KibanaLogFields.tag(tag, responseBody);
    } else {
      KibanaLogFields.tag(tag, "invalid mime type for logging");
    }
  }

  private static HttpStatus addHttpStatusTag(ContentCachingWrappedResponse wrappedResponse) {
    if(KibanaLogFields.get(TX_STATUS) == null){
      HttpStatus httpStatus = HttpStatus.valueOf(wrappedResponse.getStatus());
      KibanaLogFields.tag(TX_STATUS, httpStatus.value());
      return httpStatus;
    } else {
      return HttpStatus.valueOf(KibanaLogFields.get(TX_STATUS));
    }
  }

  private static MediaType getContentType(HttpRequest request) {
    return getContentType(request.getHeaders());
  }

  private static MediaType getContentType(ClientHttpResponse response) {
    return getContentType(response.getHeaders());
  }

  @SuppressWarnings("PMD.LooseCoupling")
  private static MediaType getContentType(HttpHeaders httpHeaders) {
    return httpHeaders.getContentType();
  }

  private static String getContentType(ServletRequest wrappedRequest) {
    return wrappedRequest.getContentType();
  }

  private static String getContentType(ServletResponse response) {
    return response.getContentType();
  }
}
