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

/**
 * A validator for validating application-specific objects.
 * <p>
 * This interface is inspired on Spring's {@link org.springframework.validation.Validator} mechanism. However Hawaii's validator mechanism
 * uses it's own {@link ValidationResult} instead of Spring's {@link org.springframework.validation.Errors} for returning validation errors.
 * <p>
 * Implementors should typically only implement {@link Validator#validate(Object, ValidationResult)} as other methods are already
 * implemented using the interface's default methods.
 *
 * @param <T> the type of the object to validate
 * @author Marcel Overdijk
 * @see ValidationError
 * @see ValidationException
 * @see ValidationResult
 * @since 2.0.0
 */
public interface Validator<T> {

    /**
     * Validates the supplied object.
     *
     * @param object the object to validate
     * @return the validation result
     */
    default ValidationResult validate(T object) {
        final ValidationResult validationResult = new ValidationResult();
        validate(object, validationResult);
        return validationResult;
    }

    /**
     * Validates the supplied object.
     *
     * @param object           the object to validate
     * @param validationResult the contextual state about the validation process
     */
    void validate(T object, ValidationResult validationResult);

    /**
     * Validates the supplied object.
     *
     * @param object the object to validate
     * @throws ValidationException if the validation fails
     */
    default void validateAndThrow(T object) throws ValidationException {
        final ValidationResult validationResult = new ValidationResult();
        validateAndThrow(object, validationResult);
    }

    /**
     * Validates the supplied object.
     *
     * @param object           the object to validate
     * @param validationResult the contextual state about the validation process
     * @throws ValidationException if the validation fails
     */
    default void validateAndThrow(T object, ValidationResult validationResult)
            throws ValidationException {
        validate(object, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
    }

    /**
     * Returns {@code true} if the validation of the supplied object succeeds.
     *
     * @param object the object to validate
     * @return {@code true} if the validation of the supplied object succeeds
     */
    default boolean isValid(T object) {
        return !validate(object).hasErrors();
    }
}
