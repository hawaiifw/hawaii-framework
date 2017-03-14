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

package org.hawaiiframework.validation;

import org.hawaiiframework.exception.HawaiiException;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Marcel Overdijk
 * @see ValidationResult
 * @since 2.0.0
 */
public class ValidationException extends HawaiiException {

    private final ValidationResult validationResult;

    /**
     * Constructs a new {@link ValidationException} with an empty {@link ValidationResult}.
     */
    public ValidationException() {
        this.validationResult = new ValidationResult();
    }

    /**
     * Constructs a new {@link ValidationException} with the supplied {@link ValidationResult}.
     *
     * @param validationResult the validation result, not null
     */
    public ValidationException(final ValidationResult validationResult) {
        requireNonNull(validationResult, "'validationResult' must not be null");
        this.validationResult = validationResult;
    }

    /**
     * Constructs a new {@link ValidationException} with the supplied {@link ValidationError}.
     *
     * @param validationError the validation error, not null
     */
    public ValidationException(final ValidationError validationError) {
        this();
        requireNonNull(validationError, "'validationError' must not be null");
        validationResult.addError(validationError);
    }

    /**
     * Constructs a new {@link ValidationException} with the supplied {@link ValidationError}s.
     *
     * @param validationErrors the validation errors, not null
     */
    public ValidationException(final List<ValidationError> validationErrors) {
        this();
        requireNonNull(validationErrors, "'validationErrors' must not be null");
        validationResult.addAllErrors(validationErrors);
    }

    /**
     * Returns the validation result containing the validation errors.
     *
     * @return the validation result containing the validation errors
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
