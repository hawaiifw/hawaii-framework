/*
 * Copyright 2015-2020 the original author or authors.
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

import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Validate a bean with jakarta validations (JSR-303).
 *
 * See {@code resources/META-INF/services/jakarta.validation.ConstraintValidator} to add validators for existing constraints.
 *
 * @param <T> The type to validate.
 */
public class JakartaValidationValidator<T> implements Validator<T> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void validate(final T object, final ValidationResult validationResult) {
        final Set<String> requiredFields = new HashSet<>();
        final Set<String> invalidFields = new HashSet<>();

        try (ValidatorFactoryImpl factory = (ValidatorFactoryImpl) Validation.buildDefaultValidatorFactory()) {
            final jakarta.validation.Validator validator = factory.getValidator();

            final Set<ConstraintViolation<T>> violations = validator.validate(object);
            for (final ConstraintViolation<T> constraintViolation : violations) {
                final Path path = constraintViolation.getPropertyPath();
                final String propertyPath = path.toString();
                final Annotation annotation = constraintViolation.getConstraintDescriptor().getAnnotation();
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                final boolean required = isRequired(annotationType);
                if (required) {
                    invalidFields.remove(propertyPath);
                    requiredFields.add(propertyPath);
                } else {
                    if (!requiredFields.contains(propertyPath)) {
                        invalidFields.add(propertyPath);
                    }
                }
            }

            for (final String path : requiredFields) {
                validationResult.rejectValue(path, "REQUIRED");
            }
            for (final String path : invalidFields) {
                validationResult.rejectValue(path, "INVALID");
            }
        }
    }

    private boolean isRequired(final Class<? extends Annotation> annotationType) {
        return annotationType.isAssignableFrom(NotBlank.class)
            || annotationType.isAssignableFrom(NotEmpty.class)
            || annotationType.isAssignableFrom(NotNull.class);
    }
}
