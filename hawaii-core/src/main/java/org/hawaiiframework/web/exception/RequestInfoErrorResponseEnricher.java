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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This enricher copies information from the original web request onto the error response resource.
 *
 * The enricher captures the following request information:
 *
 * <ul>
 *     <li>The request uri</li>
 *     <li>Query parameters</li>
 *     <li>The request method</li>
 *     <li>The requested content type</li>
 * </ul>
 * @author Paul Klos
 * @since 2.0.0
 */
public class RequestInfoErrorResponseEnricher implements ErrorResponseEnricher {

    /**
     * {@inheritDoc}
     *
     * <p><strong>NOTE:</strong> This enricher only applies if the request is a {@link ServletWebRequest}.
     */
    @Override
    public void doEnrich(
            final ErrorResponseResource errorResponseResource,
            final Throwable throwable,
            final WebRequest request,
            final HttpStatus httpStatus) {
        if (request instanceof ServletWebRequest) {
            final ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletWebRequest.getNativeRequest();
            errorResponseResource.setUri(httpServletRequest.getRequestURI());
            errorResponseResource.setQuery(httpServletRequest.getQueryString());
            errorResponseResource.setMethod(httpServletRequest.getMethod());
            errorResponseResource.setContentType(httpServletRequest.getContentType());
        }
    }
}
