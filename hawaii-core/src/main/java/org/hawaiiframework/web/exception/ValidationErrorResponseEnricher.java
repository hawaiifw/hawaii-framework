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

import java.util.List;
import org.hawaiiframework.converter.ModelConverter;
import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.validation.ValidationResult;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResource;
import org.hawaiiframework.web.resource.ValidationErrorResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This enricher adds validation errors to the error response resource.
 *
 * @author Paul Klos
 * @author Richard den Adel
 * @since 2.0.0
 */
public class ValidationErrorResponseEnricher implements ErrorResponseEnricher {

  private final ModelConverter<ValidationError, ValidationErrorResource>
      validationErrorResourceAssembler;

  /**
   * Constructor with a {@code validationErrorResourceAssembler}.
   */
  public ValidationErrorResponseEnricher(
      ModelConverter<ValidationError, ValidationErrorResource> validationErrorResourceAssembler) {
    super();
    this.validationErrorResourceAssembler =
        requireNonNull(
            validationErrorResourceAssembler,
            "'validationErrorResourceAssembler' must not be null");
  }

  /**
   * {@inheritDoc}
   *
   * <p><strong>NOTE:</strong> This enricher only applies if throwable is a {@link
   * ValidationException} and #errorResponseResource is a {@link ValidationErrorResponseResource}.
   */
  @Override
  public void doEnrich(
      ErrorResponseResource errorResponseResource,
      Throwable throwable,
      WebRequest request,
      HttpStatus httpStatus) {
    if (throwable instanceof ValidationException validationException
        && errorResponseResource instanceof ValidationErrorResponseResource resource) {
      List<ValidationError> errors = getErrors(validationException);
      if (errors != null && !errors.isEmpty()) {
        resource.setErrors(validationErrorResourceAssembler.convert(errors));
      }
    }
  }

  private static List<ValidationError> getErrors(ValidationException validationException) {
    return getErrors(validationException.getValidationResult());
  }

  private static List<ValidationError> getErrors(ValidationResult validationResult) {
    return validationResult.getErrors();
  }
}
