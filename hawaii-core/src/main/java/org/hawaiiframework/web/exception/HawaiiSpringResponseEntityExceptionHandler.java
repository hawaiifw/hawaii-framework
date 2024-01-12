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

import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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
public class HawaiiSpringResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final ErrorResponseEntityBuilder errorResponseEntityBuilder;

  /**
   * Constructor with an {@code errorResponseEntityBuilder}.
   */
  public HawaiiSpringResponseEntityExceptionHandler(
      ErrorResponseEntityBuilder errorResponseEntityBuilder) {
    super();
    this.errorResponseEntityBuilder = errorResponseEntityBuilder;
  }

  /**
   * Handles {@code AccessDeniedException} instances.
   *
   * <p>The response status is: 403 Forbidden.
   *
   * @param exception the exception
   * @param request the current request
   * @return a response entity reflecting the current exception
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseBody
  public ResponseEntity<Object> accessDeniedException(
      AccessDeniedException exception, WebRequest request) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return handleExceptionInternal(
        exception, buildErrorResponseBody(exception, status, request), EMPTY, status, request);
  }

  /**
   * The overridden message not readable exception handler.
   *
   * @param exception the exception to handle
   * @param headers the headers to use for the response
   * @param statusCode the status code to use for the response
   * @param request the current request
   * @return a response entity reflecting the current exception
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return handleExceptionInternal(
        exception, buildErrorResponseBody(exception, status, request), EMPTY, status, request);
  }

  private ErrorResponseResource buildErrorResponseBody(
      Throwable throwable, HttpStatus status, WebRequest request) {
    return errorResponseEntityBuilder.buildErrorResponseBody(throwable, status, request);
  }
}
