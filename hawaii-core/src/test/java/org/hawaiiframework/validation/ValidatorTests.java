/*
 * Copyright 2015-2016 the original author or authors.
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

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Validator}.
 *
 * @author Marcel Overdijk
 */
public class ValidatorTests {

    private Object object = new Object();

    @Test
    public void testValidateAndThrowReturnsVoidWhenNoValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // no validation errors
            }
        };
        validator.validateAndThrow(object);
    }

    @Test
    public void testValidateAndThrowWithValidationResultReturnsVoidWhenNoValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // no validation errors
            }
        };
        ValidationResult validationResult = new ValidationResult();
        validator.validateAndThrow(object, validationResult);
    }

    @Test(expected = ValidationException.class)
    public void testValidateAndThrowThrowsExceptionWhenValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // force validation error
                validationResult.reject("error");
            }
        };
        validator.validateAndThrow(object);
    }

    @Test(expected = ValidationException.class)
    public void testValidateAndThrowWithValidationResultThrowsExceptionWhenValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // force validation error
                validationResult.reject("error");
            }
        };
        ValidationResult validationResult = new ValidationResult();
        validator.validateAndThrow(object, validationResult);
    }

    @Test
    public void testIsValidReturnsTrueWhenNoValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // no validation errors
            }
        };
        assertThat(validator.isValid(object), is(true));
    }

    @Test
    public void testIsValidReturnsFalseWhenValidationErrors() {
        Validator validator = new Validator() {

            @Override
            public void validate(Object object, ValidationResult validationResult) {
                // force validation error
                validationResult.reject("error");
            }
        };
        assertThat(validator.isValid(object), is(false));
    }
}
