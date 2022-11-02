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
package org.hawaiiframework.logging.util;

import junit.framework.TestCase;
import org.hawaiiframework.logging.web.filter.ContentCachingWrappedResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.web.DelegatingServletInputStream;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpRequestResponseBodyLogUtilTest extends TestCase {

    @Mock
    private PasswordMaskerUtil passwordMasker;

    @Mock
    private ContentCachingWrappedResponse servletResponse;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private ClientHttpResponse clientResponse;

    private HttpRequestResponseBodyLogUtil httpRequestResponseBodyLogUtil;

    @Before
    public void setup() {
        httpRequestResponseBodyLogUtil = new HttpRequestResponseBodyLogUtil(passwordMasker);
    }

    @Test
    public void thatTxRequestBodyIsPasswordMasked() throws IOException {
        String request = "foo";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
        DelegatingServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);

        when(servletRequest.getInputStream()).thenReturn(servletInputStream);
        when(servletRequest.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.toString());

        httpRequestResponseBodyLogUtil.getTxRequestBody(servletRequest);
        verify(passwordMasker, times(1)).maskPasswordsIn(request);
    }

    @Test
    public void thatTxResponseBodyIsPasswordMasked() {
        String response = "foo";
        when(servletResponse.getContentAsByteArray()).thenReturn(response.getBytes(StandardCharsets.UTF_8));
        when(servletResponse.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.toString());

        httpRequestResponseBodyLogUtil.getTxResponseBody(servletResponse);
        verify(passwordMasker, times(1)).maskPasswordsIn(response);
    }

    @Test
    public void thatCallRequestBodyIsPasswordMasked() {
        String response = "foo";

        httpRequestResponseBodyLogUtil.getCallRequestBody(response.getBytes());
        verify(passwordMasker, times(1)).maskPasswordsIn(response);
    }


    @Test
    public void thatCallResponseBodyIsPasswordMasked() throws IOException {
        String response = "foo";

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        when(clientResponse.getBody()).thenReturn(inputStream);

        httpRequestResponseBodyLogUtil.getCallResponseBody(clientResponse);
        verify(passwordMasker, times(1)).maskPasswordsIn(response + "\n");
    }
}
