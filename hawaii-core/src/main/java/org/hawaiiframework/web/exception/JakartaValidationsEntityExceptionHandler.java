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

import static org.springframework.http.HttpHeaders.EMPTY;

import jakarta.validation.ValidationException;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class creates proper HTTP response bodies for exceptions.
 *
 * <p>Separate controller advice with it's own order, so that is used before the {@link
 * HawaiiResponseEntityExceptionHandler}. This is needed because in the code we tend to wrap
 * exceptions within Hawaii exceptions and the Hawaii exception is preferred above the cause within
 * that exceptions.
 *
 * <p>(in other words, don't delete this file or merge this file with the {@link
 * HawaiiResponseEntityExceptionHandler}!)
 *
 * @since 6.0.0
 */
@Order(0)
@ControllerAdvice
public class JakartaValidationsEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final ErrorResponseEntityBuilder errorResponseEntityBuilder;

  /**
   * Constructor with an {@code errorResponseEntityBuilder}.
   */
  public JakartaValidationsEntityExceptionHandler(
      ErrorResponseEntityBuilder errorResponseEntityBuilder) {
    super();
    this.errorResponseEntityBuilder = errorResponseEntityBuilder;
  }

  /**
   * Handles {@code ValidationException} instances.
   *
   * <p>The response status is: 400 Bad Request.
   *
   * @param exception the exception
   * @param request the current request
   * @return a response entity reflecting the current exception
   */
  @ExceptionHandler(ValidationException.class)
  @ResponseBody
  public ResponseEntity<Object> handleValidationException(
      ValidationException exception, WebRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return handleExceptionInternal(
        exception, buildErrorResponseBody(exception, status, request), EMPTY, status, request);
  }

  private ErrorResponseResource buildErrorResponseBody(
      Throwable throwable, HttpStatus status, WebRequest request) {
    return errorResponseEntityBuilder.buildErrorResponseBody(throwable, status, request);
  }
}
