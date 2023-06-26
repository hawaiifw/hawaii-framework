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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpHeaders.EMPTY;

/**
 * This class creates proper HTTP response bodies for exceptions.
 *
 * @since 6.0.0
 */
@Order(0)
@ControllerAdvice
public class SpringSecurityResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorResponseEntityBuilder errorResponseEntityBuilder;

    public SpringSecurityResponseEntityExceptionHandler(final ErrorResponseEntityBuilder errorResponseEntityBuilder) {
        this.errorResponseEntityBuilder = errorResponseEntityBuilder;
    }
    /**
     * Handles {@code AccessDeniedException} instances.
     * <p>
     * The response status is: 403 Forbidden.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Object> accessDeniedException(final AccessDeniedException ex, final WebRequest request) {
        final HttpStatus status = HttpStatus.FORBIDDEN;
        return handleExceptionInternal(ex, buildErrorResponseBody(ex, status, request), EMPTY, status, request);
    }

    private ErrorResponseResource buildErrorResponseBody(final Throwable throwable, final HttpStatus status, final WebRequest request) {
        return errorResponseEntityBuilder.buildErrorResponseBody(throwable, status, request);
    }
}
