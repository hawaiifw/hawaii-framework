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

package org.hawaiiframework.web.resource;

import java.util.List;
import org.hawaiiframework.validation.ValidationException;

/**
 * Response resource for validation errors.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public class ValidationErrorResponseResource extends ErrorResponseResource {

  /** The validation errors. */
  private List<ValidationErrorResource> errors;

  /**
   * Default constructor.
   */
  public ValidationErrorResponseResource() {
    this(null);
  }

  /**
   * Constructor with a {@code validationException}.
   */
  public ValidationErrorResponseResource(ValidationException validationException) {
    super(validationException);
  }

  /**
   * Returns the validation errors.
   *
   * @return the validation errors
   */
  public List<ValidationErrorResource> getErrors() {
    return errors;
  }

  /**
   * Sets the validation errors.
   *
   * @param errors the validation errors
   */
  public void setErrors(List<ValidationErrorResource> errors) {
    this.errors = errors;
  }
}
