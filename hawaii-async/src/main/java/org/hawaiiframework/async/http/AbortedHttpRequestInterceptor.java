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

package org.hawaiiframework.async.http;

import org.hawaiiframework.async.exception.TaskTimeoutException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.SocketException;

/**
 * HTTP request interceptor to set a task id as a header on an HTTP request.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class AbortedHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        try {
            return execution.execute(request, body);
        } catch (SocketException e) {
            throw new TaskTimeoutException("Task timed out.", e);
        }
    }

}
