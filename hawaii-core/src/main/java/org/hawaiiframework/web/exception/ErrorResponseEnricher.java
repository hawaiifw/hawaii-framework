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
import org.springframework.web.context.request.WebRequest;

/**
 * This interface defines a way to enrich the error response with values applicable to the given
 * situation.
 *
 * <p>The situation in this case consists of the exception, the original request and the http
 * status.
 *
 * <p>A {@link HawaiiResponseEntityExceptionHandler} has a list of ErrorResponseEnrichers, which
 * will make sure that all relevant information is captured in the error response.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public interface ErrorResponseEnricher {

  /**
   * Default implementation that first retrieves the original throwable stored in the error response
   * resource, and then calls {@link #doEnrich(ErrorResponseResource, Throwable, WebRequest,
   * HttpStatus)}.
   *
   * <p>Note that the http status is a given, it is assumed to be determined in the exception
   * handler.
   *
   * @param errorResponseResource the error response resource
   * @param request the original web request
   * @param httpStatus the http status that will be returned
   */
  default void enrich(
      ErrorResponseResource errorResponseResource, WebRequest request, HttpStatus httpStatus) {
    doEnrich(errorResponseResource, errorResponseResource.getThrowable(), request, httpStatus);
  }

  /**
   * Performs the enrichment of the error response resource.
   *
   * <p>Note that the http status is a given, it is assumed to be determined in the exception
   * handler.
   *
   * @param errorResponseResource the error response resource
   * @param throwable the exception that was raised
   * @param request the original web request
   * @param httpStatus the http status that will be returned
   */
  void doEnrich(
      ErrorResponseResource errorResponseResource,
      Throwable throwable,
      WebRequest request,
      HttpStatus httpStatus);
}
