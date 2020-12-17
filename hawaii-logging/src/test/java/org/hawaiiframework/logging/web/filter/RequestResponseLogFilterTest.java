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

import org.hawaiiframework.logging.config.HawaiiLoggingConfigurationProperties;
import org.hawaiiframework.logging.http.DefaultHawaiiRequestResponseLogger;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class RequestResponseLogFilterTest {

    private static final String A_REQUEST_URI = "/idm/rest/public/registration/customer";
    private static final String A_QUERY_STRING = "token=4a8ff079-9223-41cc-a072-9c8656216479&bla=bladiebla";
    private static final String A_METHOD = "POST";
    private static final String A_CONTENT_TYPE = "text/plain";
    
    private RequestResponseLogFilter filter;
    private Logger logger;
    private HttpServletRequest request;

    @Before
    public void setup() {
        logger = mock(Logger.class);

        PowerMockito.mockStatic(LoggerFactory.class);
        Mockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        Mockito.when(LoggerFactory.getLogger(anyString())).thenReturn(logger);

        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(A_REQUEST_URI);
        when(request.getQueryString()).thenReturn(A_QUERY_STRING);
        when(request.getContentType()).thenReturn(A_CONTENT_TYPE);
        when(request.getMethod()).thenReturn(A_METHOD);

        final HttpRequestResponseLogUtil httpRequestResponseLogUtil = mock(HttpRequestResponseLogUtil.class);

        final DefaultHawaiiRequestResponseLogger logger = new DefaultHawaiiRequestResponseLogger(httpRequestResponseLogUtil, mock(HawaiiLoggingConfigurationProperties.class));

        filter = new RequestResponseLogFilter(logger);
    }

    @Test
    public void thatRequestParamsAreLogged() throws ServletException, IOException {
        final ArgumentCaptor<Object> method = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<Object> url = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<Object> contentType = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<Object> size = ArgumentCaptor.forClass(Object.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        // Keep otherwise log will fail.
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, mock(FilterChain.class));

        verify(logger, atLeastOnce()).info(eq("Invoked '{} {}' with content type '{}' and size of '{}' bytes."), method.capture(), url.capture(), contentType.capture(), size.capture());

        assertThat(get(method), is(equalTo(A_METHOD)));
        assertThat(get(url), is(equalTo(A_REQUEST_URI)));
        assertThat(getMediaType(contentType), is(equalTo(MediaType.parseMediaType(A_CONTENT_TYPE))));
        assertThat(getInt(size), is(equalTo(0)));
    }

    private String get(final ArgumentCaptor<Object> captor) {
        return (String) captor.getAllValues().get(0);
    }

    private Integer getInt(final ArgumentCaptor<Object> captor) {
        return (Integer) captor.getAllValues().get(0);
    }

    private MediaType getMediaType(final ArgumentCaptor<Object> captor) {
        return (MediaType) captor.getAllValues().get(0);
    }
}
