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

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * HTTP request interceptor to set a task id as a header on an HTTP request.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class TaskIdSupplierHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The header name to set.
     */
    private final String headerName;

    /**
     * Default constructor with 'X-Hawaii-Task-Id' as {@code headername}.
     */
    public TaskIdSupplierHttpRequestInterceptor() {
        this("X-Hawaii-Task-Id");
    }

    /**
     * Constructor that sets the header name.
     *
     * @param headerName The header name to use.
     */
    public TaskIdSupplierHttpRequestInterceptor(final String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        final String taskId = SharedTaskContextHolder.getTaskId();
        if (StringUtils.isNotBlank(taskId)) {
            request.getHeaders().add(headerName, taskId);
        }
        return execution.execute(request, body);
    }

}
