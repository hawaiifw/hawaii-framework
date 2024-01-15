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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This class creates proper HTTP response bodies for exceptions.
 *
 * @since 6.0.0
 */
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class ErrorResponseEntityBuilder {
  private final ExceptionResponseFactory exceptionResponseFactory;
  private final Set<ErrorResponseEnricher> errorResponseEnrichers = new HashSet<>();

  /** The constructor. */
  public ErrorResponseEntityBuilder(
      ExceptionResponseFactory exceptionResponseFactory,
      List<ErrorResponseEnricher> errorResponseEnrichers) {
    this.exceptionResponseFactory =
        requireNonNull(exceptionResponseFactory, "'exceptionResponseFactory' must not be null");
    if (errorResponseEnrichers != null) {
      this.errorResponseEnrichers.addAll(errorResponseEnrichers);
    }
  }

  /**
   * Builds a meaningful response body for the given throwable, HTTP status and request.
   *
   * <p>This method constructs an {@link ErrorResponseResource} using {@link
   * #exceptionResponseFactory} and then applies the error response enrichers returned from {@link
   * #getResponseEnrichers()} to complete the response.
   *
   * @param throwable the exception
   * @param status the HTTP status
   * @param request the current request
   * @return an error response
   */
  public ErrorResponseResource buildErrorResponseBody(
      Throwable throwable, HttpStatus status, WebRequest request) {
    ErrorResponseResource resource = exceptionResponseFactory.create(throwable);
    getResponseEnrichers().forEach(enricher -> enricher.enrich(resource, request, status));
    return resource;
  }

  /**
   * Registers a {@link ErrorResponseEnricher}.
   *
   * @param errorResponseEnricher the error response enricher
   */
  public void addResponseEnricher(ErrorResponseEnricher errorResponseEnricher) {
    errorResponseEnrichers.add(errorResponseEnricher);
  }

  /**
   * De-registers a {@link ErrorResponseEnricher}.
   *
   * @param errorResponseEnricher the error response enricher
   */
  public void removeResponseEnricher(ErrorResponseEnricher errorResponseEnricher) {
    errorResponseEnrichers.remove(errorResponseEnricher);
  }

  /**
   * Returns a collection of registered response enrichers.
   *
   * @return the response enrichers
   */
  public Collection<ErrorResponseEnricher> getResponseEnrichers() {
    return new HashSet<>(errorResponseEnrichers);
  }
}
