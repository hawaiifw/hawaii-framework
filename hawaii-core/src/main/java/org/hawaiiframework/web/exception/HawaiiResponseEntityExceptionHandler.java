/*
 * Copyright 2015-2017 the original author or authors.
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

import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * This class creates proper HTTP response bodies for exceptions.
 *
 * @author Marcel Overdijk
 * @author Ivan Melotte
 * @since 2.0.0
 */
@ControllerAdvice
public class HawaiiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ValidationErrorResourceAssembler validationErrorResourceAssembler;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public HawaiiResponseEntityExceptionHandler(final ValidationErrorResourceAssembler validationErrorResourceAssembler) {
        this.validationErrorResourceAssembler =
                requireNonNull(validationErrorResourceAssembler, "'validationErrorResourceAssembler' must not be null");
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
    public ResponseEntity handleHttpException(final HttpException e, final WebRequest request) {
        final HttpStatus status = e.getHttpStatus();
        return ResponseEntity.status(status).body(buildErrorResponseBody(e, status, request));
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
    public ResponseEntity handleValidationException(final ValidationException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final ErrorResponseResource body = buildErrorResponseBody(e, status, request);
        final List<ValidationError> errors = e.getValidationResult().getErrors();
        if (errors != null && !errors.isEmpty()) {
            body.setErrors(validationErrorResourceAssembler.toResources(errors));
        }
        return ResponseEntity.status(status).body(body);
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
    public ResponseEntity handleThrowable(final Throwable t, final WebRequest request) {
        logger.error("", t);
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(buildErrorResponseBody(t, status, request));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        return ResponseEntity.status(status).body(buildErrorResponseBody(ex, status, request));
    }

    /**
     * Builds a meaningful response body for the given throwable, HTTP status and request.
     *
     * @param t       the exception
     * @param status  the HTTP status
     * @param request the current request
     * @return an error response
     */
    protected ErrorResponseResource buildErrorResponseBody(final Throwable t, final HttpStatus status, final WebRequest request) {
        final ErrorResponseResource resource = new ErrorResponseResource();
        addRequestInfo(request, resource);
        addHttpStatus(status, resource);
        addErrorMessage(t, resource);
        return resource;
    }

    /**
     * Add servlet request information to the {@code ErrorResponseResource} instance.
     *
     * @param request  the current request
     * @param resource the current error response resource
     */
    private void addRequestInfo(final WebRequest request, final ErrorResponseResource resource) {
        if (request instanceof ServletWebRequest) {
            final ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletWebRequest.getNativeRequest();
            resource.setUri(httpServletRequest.getRequestURI());
            resource.setQuery(httpServletRequest.getQueryString());
            resource.setMethod(httpServletRequest.getMethod());
            resource.setContentType(httpServletRequest.getContentType());
        }
    }

    /**
     * Add HTTP status information to the {@code ErrorResponseResource} instance.
     *
     * @param status   the current HTTP status
     * @param resource the current error response resource
     */
    private void addHttpStatus(final HttpStatus status, final ErrorResponseResource resource) {
        resource.setStatusCode(status.value());
        resource.setStatusMessage(status.getReasonPhrase());
    }

    /**
     * Add the exception message to the {@code ErrorResponseResource} instance.
     *
     * @param t        the current exception
     * @param resource the current error response resource
     */
    private void addErrorMessage(final Throwable t, final ErrorResponseResource resource) {
        final String message = t.getMessage();
        if (!StringUtils.isEmpty(message)) {
            resource.setErrorMessage(message);
        }
    }
}
