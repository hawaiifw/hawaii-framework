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

package org.hawaiiframework.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Tests for {@link Validator}.
 *
 * @author Marcel Overdijk
 */
public class ValidatorTests {

  private Object object = new Object();

  @Test
  public void testValidateAndThrowReturnsVoidWhenNoValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // no validation errors
        };
    validator.validateAndThrow(object);
  }

  @Test
  public void testValidateAndThrowWithValidationResultReturnsVoidWhenNoValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // no validation errors
        };
    ValidationResult validationResult = new ValidationResult();
    validator.validateAndThrow(object, validationResult);
  }

  @Test(expected = ValidationException.class)
  public void testValidateAndThrowThrowsExceptionWhenValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // force validation error
          validationResult.reject("error");
        };
    validator.validateAndThrow(object);
  }

  @Test(expected = ValidationException.class)
  public void testValidateAndThrowWithValidationResultThrowsExceptionWhenValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // force validation error
          validationResult.reject("error");
        };
    ValidationResult validationResult = new ValidationResult();
    validator.validateAndThrow(object, validationResult);
  }

  @Test
  public void testIsValidReturnsTrueWhenNoValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // no validation errors
        };
    assertThat(validator.isValid(object), is(true));
  }

  @Test
  public void testIsValidReturnsFalseWhenValidationErrors() {
    Validator<Object> validator =
        (object, validationResult) -> {
          // force validation error
          validationResult.reject("error");
        };
    assertThat(validator.isValid(object), is(false));
  }
}
