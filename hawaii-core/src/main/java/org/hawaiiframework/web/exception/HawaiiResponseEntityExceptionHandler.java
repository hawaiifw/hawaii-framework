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

import org.hawaiiframework.exception.ApiException;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpHeaders.EMPTY;

/**
 * This class creates proper HTTP response bodies for exceptions.
 *
 * @author Marcel Overdijk
 * @author Ivan Melotte
 * @author Paul Klos
 * @author Richard den Adel
 * @since 2.0.0
 */
@Order
@ControllerAdvice
public class HawaiiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ErrorResponseEntityBuilder errorResponseEntityBuilder;

    public HawaiiResponseEntityExceptionHandler(final ErrorResponseEntityBuilder errorResponseEntityBuilder) {
        this.errorResponseEntityBuilder = errorResponseEntityBuilder;
    }

    /**
     * Handles {@code HttpException} instances.
     * <p>
     * Each {@code HttpException} has an associated {@code HttpStatus} that is used as the response status.
     *
     * @param e       the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public ResponseEntity<Object> handleHttpException(final HttpException e, final WebRequest request) {
        final HttpStatus status = e.getHttpStatus();
        return handleExceptionInternal(
                e,
                buildErrorResponseBody(e, status, request),
                EMPTY,
                status,
                request);
    }

    /**
     * Handles {@code MethodArgumentNotValidException} instances.
     * <p>
     * The response status is: 400 Bad Request.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {

        return handleExceptionInternal(ex, buildErrorResponseBody(ex, HttpStatus.valueOf(status.value()), request), EMPTY, status, request);
    }

    /**
     * Handles {@code ValidationException} instances.
     * <p>
     * The response status is: 400 Bad Request.
     *
     * @param e       the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleValidationException(final ValidationException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(e, buildErrorResponseBody(e, status, request), EMPTY, status, request);
    }

    /**
     * Handles {@code ValidationException} instances.
     * <p>
     * The response status is: 400 Bad Request.
     *
     * @param e       the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<Object> handleApiException(final ApiException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(e, buildErrorResponseBody(e, status, request), EMPTY, status, request);
    }

    /**
     * Handles {@code Throwable} instances. This method acts as a fallback handler.
     *
     * @param t       the exception
     * @param request the current request
     * @return a response entity reflecting the current exception
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handleThrowable(final Throwable t, final WebRequest request) {
        logger.error("Unhandled exception", t);
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(buildErrorResponseBody(t, status, request));
    }

    private ErrorResponseResource buildErrorResponseBody(final Throwable throwable, final HttpStatus status, final WebRequest request) {
        return errorResponseEntityBuilder.buildErrorResponseBody(throwable, status, request);
    }
}
