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

import org.hawaiiframework.logging.config.RequestResponseLogFilterConfiguration;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class RequestResponseLogFilterTest {

    private static final String A_REQUEST_URI = "/idm/rest/public/registration/customer";
    private static final String A_QUERY_STRING = "token=4a8ff079-9223-41cc-a072-9c8656216479&bla=bladiebla";
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

        final HttpRequestResponseLogUtil httpRequestResponseLogUtil = mock(HttpRequestResponseLogUtil.class);
        when(httpRequestResponseLogUtil.getRequestUri(request)).thenReturn("some uri");

        filter = new RequestResponseLogFilter(mock(RequestResponseLogFilterConfiguration.class), httpRequestResponseLogUtil);


    }

    @Test
    public void thatRequestParamsAreLogged() throws ServletException, IOException {
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        filter.doFilterInternal(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        verify(logger, atLeastOnce()).info(anyString(), captor.capture(), any(), any());

        final List<Object> allValues = captor.getAllValues();
        String loggedUrl = (String) allValues.get(0);

        assertThat(loggedUrl, is(equalTo("some uri")));
    }

}
