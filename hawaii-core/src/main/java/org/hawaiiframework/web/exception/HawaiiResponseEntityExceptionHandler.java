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

import org.hawaiiframework.converter.ModelConverter;
import org.hawaiiframework.exception.ApiException;
import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * This class creates proper HTTP response bodies for exceptions.
 * <p>
 * In this implementation, the response enrichers are stored in a map, with the class name as key. This means that any enricher can
 * be stored only once. Also, enrichers are not ordered. Subclasses may implement another mechanism if required. This would mean that
 * the following methods would need to be overwritten:
 * <ul>
 * <li>{@link #addResponseEnricher(ErrorResponseEnricher)}</li>
 * <li>{@link #removeResponseEnricher(ErrorResponseEnricher)} (optionally)</li>
 * <li>{@link #configureResponseEnrichers()}</li>
 * <li>{@link #getResponseEnrichers()}</li>
 * </ul>
 *
 * @author Marcel Overdijk
 * @author Ivan Melotte
 * @author Paul Klos
 * @author Richard den Adel
 * @since 2.0.0
 */
@ControllerAdvice
public class HawaiiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ModelConverter<ValidationError, ValidationErrorResource> validationErrorResourceAssembler;
    private final ExceptionResponseFactory exceptionResponseFactory;
    private final Map<String, ErrorResponseEnricher> errorResponseEnrichers = new ConcurrentHashMap<>();

    public HawaiiResponseEntityExceptionHandler(
            final ModelConverter<ValidationError, ValidationErrorResource> validationErrorResourceAssembler,
            final ExceptionResponseFactory exceptionResponseFactory) {
        this.validationErrorResourceAssembler =
                requireNonNull(validationErrorResourceAssembler, "'validationErrorResourceAssembler' must not be null");
        this.exceptionResponseFactory =
                requireNonNull(exceptionResponseFactory, "'exceptionResponseFactory' must not be null");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        configureResponseEnrichers();
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
        return handleExceptionInternal(
                e,
                buildErrorResponseBody(e, status, request),
                null,
                status,
                request);
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
        return handleExceptionInternal(e, buildErrorResponseBody(e, status, request), null, status, request);
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
    public ResponseEntity handleApiException(final ApiException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(e, buildErrorResponseBody(e, status, request), null, status, request);
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
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
            @Nullable final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        return ResponseEntity.status(status).headers(headers).body(body);
    }

    /**
     * Builds a meaningful response body for the given throwable, HTTP status and request.
     * <p>
     * This method constructs an {@link ErrorResponseResource} using {@link #exceptionResponseFactory} and then applies
     * the error response enrichers returned from {@link #getResponseEnrichers()} to complete the response.
     *
     * @param throwable the exception
     * @param status    the HTTP status
     * @param request   the current request
     * @return an error response
     */
    protected ErrorResponseResource buildErrorResponseBody(
            final Throwable throwable,
            final HttpStatus status,
            final WebRequest request) {
        final ErrorResponseResource resource = exceptionResponseFactory.create(throwable);
        getResponseEnrichers().forEach(enricher -> enricher.enrich(resource, request, status));
        return resource;
    }

    /**
     * Registers a {@link ErrorResponseEnricher}.
     *
     * @param errorResponseEnricher the error response enricher
     */
    protected void addResponseEnricher(final ErrorResponseEnricher errorResponseEnricher) {
        errorResponseEnrichers.put(errorResponseEnricher.getClass().getName(), errorResponseEnricher);
    }

    /**
     * De-registers a {@link ErrorResponseEnricher}.
     *
     * @param errorResponseEnricher the error response enricher
     */
    protected void removeResponseEnricher(final ErrorResponseEnricher errorResponseEnricher) {
        errorResponseEnrichers.remove(errorResponseEnricher.getClass().getName());
    }

    /**
     * De-registers a {@link ErrorResponseEnricher} based on its class name.
     *
     * @param className the class name of the {@link ErrorResponseEnricher} to remove
     */
    protected void removeResponseEnricher(final Class<? extends ErrorResponseEnricher> className) {
        errorResponseEnrichers.remove(className.getName());
    }

    /**
     * Configures the error response enrichers.
     *
     * <p>Subclasses may override this method to remove existing or add additional listeners,
     * using {@link #addResponseEnricher(ErrorResponseEnricher)} and {@link #removeResponseEnricher(ErrorResponseEnricher)}.</p>
     * <p>
     * The default implementation adds the following listeners:
     * <ul>
     * <li>{@link ErrorResponseStatusEnricher}</li>
     * <li>{@link ErrorMessageResponseEnricher}</li>
     * <li>{@link RequestInfoErrorResponseEnricher}</li>
     * <li>{@link ValidationErrorResponseEnricher}</li>
     * <li>{@link ApiErrorResponseEnricher}</li>
     * </ul>
     */
    protected void configureResponseEnrichers() {
        addResponseEnricher(new ErrorResponseStatusEnricher());
        addResponseEnricher(new ErrorMessageResponseEnricher());
        addResponseEnricher(new RequestInfoErrorResponseEnricher());
        addResponseEnricher(new ValidationErrorResponseEnricher(validationErrorResourceAssembler));
        addResponseEnricher(new ApiErrorResponseEnricher());
    }

    /**
     * Returns a collection of registered response enrichers.
     *
     * @return the response enrichers
     */
    protected Collection<ErrorResponseEnricher> getResponseEnrichers() {
        return errorResponseEnrichers.values();
    }
}
