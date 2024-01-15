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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean.WRAPPED_REQUEST_RESPONSE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.config.MediaTypeVoter;
import org.hawaiiframework.logging.config.RequestVoter;
import org.hawaiiframework.logging.http.DefaultHawaiiRequestResponseLogger;
import org.hawaiiframework.logging.util.HttpRequestResponseBodyLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseDebugLogUtil;
import org.hawaiiframework.logging.util.HttpRequestResponseHeadersLogUtil;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseLogFilterTest {

  private static final String A_REQUEST_URI = "/idm/rest/public/registration/customer";
  private static final String A_QUERY_STRING =
      "token=4a8ff079-9223-41cc-a072-9c8656216479&bla=bladiebla";
  private static final String A_METHOD = "POST";
  private static final String A_CONTENT_TYPE = "text/plain";

  private RequestResponseLogFilter filter;
  private Logger logger;

  private TestLogAppender testLogAppender;

  private HttpServletRequest request;
  private HttpServletResponse response;

  @Before
  public void setup() {
    testLogAppender = new TestLogAppender();
    ch.qos.logback.classic.Logger logger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(testLogAppender);

    testLogAppender.start();

    response = mock(ContentCachingWrappedResponse.class);
    when(response.getStatus()).thenReturn(200);

    request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(A_REQUEST_URI);
    when(request.getQueryString()).thenReturn(A_QUERY_STRING);
    when(request.getContentType()).thenReturn(A_CONTENT_TYPE);
    when(request.getMethod()).thenReturn(A_METHOD);
    when(request.getAttribute(WRAPPED_REQUEST_RESPONSE)).thenReturn(null, response);

    HttpRequestResponseHeadersLogUtil headersLogUtil =
        mock(HttpRequestResponseHeadersLogUtil.class);
    HttpRequestResponseBodyLogUtil bodyLogUtil = mock(HttpRequestResponseBodyLogUtil.class);
    HttpRequestResponseDebugLogUtil debugLogUtil = mock(HttpRequestResponseDebugLogUtil.class);

    HawaiiLoggingConfigurationProperties properties = new HawaiiLoggingConfigurationProperties();
    MediaTypeVoter mediaTypeVoter = new MediaTypeVoter(properties.getAllowedContentTypes(), true);
    MediaTypeVoter suppressedMediaTypeVoter =
        new MediaTypeVoter(properties.getBodyExcludedContentTypes(), false);
    RequestVoter requestVoter = new RequestVoter(properties);

    DefaultHawaiiRequestResponseLogger requestResponseLogger =
        new DefaultHawaiiRequestResponseLogger(
            headersLogUtil, bodyLogUtil, debugLogUtil, mediaTypeVoter, suppressedMediaTypeVoter);
    FilterVoter filterVoter = new FilterVoter(mediaTypeVoter, requestVoter);
    filter = new RequestResponseLogFilter(requestResponseLogger, filterVoter);
  }

  @Test
  public void thatRequestParamsAreLogged() throws ServletException, IOException {
    filter.doFilterInternal(request, response, mock(FilterChain.class));

    String message = "Invoked '{} {}' with content type '{}' and size of '{}' bytes.";
    ILoggingEvent logEvent = testLogAppender.findEventForMessage(message);
    assertThat(logEvent, notNullValue());

    Object[] argumentArray = logEvent.getArgumentArray();
    assertThat((String) argumentArray[0], is("POST"));
    assertThat((String) argumentArray[1], is(A_REQUEST_URI));
    assertThat(argumentArray[2], is(A_CONTENT_TYPE));
    assertThat((int) argumentArray[3], is(0));
  }
}
