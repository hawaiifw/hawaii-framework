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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;

/**
 * Validate a bean with jakarta validations (JSR-303).
 *
 * <p>See {@code resources/META-INF/services/jakarta.validation.ConstraintValidator} to add
 * validators for existing constraints.
 *
 * @param <T> The type to validate.
 */
public class JakartaValidationValidator<T> implements Validator<T> {

    @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public void validate(T object, ValidationResult validationResult) {
    Set<String> requiredFields = new HashSet<>();
    Set<String> invalidFields = new HashSet<>();

    try (ValidatorFactoryImpl factory =
        (ValidatorFactoryImpl) Validation.buildDefaultValidatorFactory()) {
      jakarta.validation.Validator validator = factory.getValidator();

      Set<ConstraintViolation<T>> violations = validator.validate(object);
      for (ConstraintViolation<T> constraintViolation : violations) {
        Path path = constraintViolation.getPropertyPath();
        String propertyPath = path.toString();
        Annotation annotation = constraintViolation.getConstraintDescriptor().getAnnotation();
        Class<? extends Annotation> annotationType = annotation.annotationType();
        boolean required = isRequired(annotationType);
        if (required) {
          invalidFields.remove(propertyPath);
          requiredFields.add(propertyPath);
        } else {
          if (!requiredFields.contains(propertyPath)) {
            invalidFields.add(propertyPath);
          }
        }
      }

      for (String path : requiredFields) {
        validationResult.rejectValue(path, "REQUIRED");
      }
      for (String path : invalidFields) {
        validationResult.rejectValue(path, "INVALID");
      }
    }
  }

  private static boolean isRequired(Class<? extends Annotation> annotationType) {
    return annotationType.isAssignableFrom(NotBlank.class)
        || annotationType.isAssignableFrom(NotEmpty.class)
        || annotationType.isAssignableFrom(NotNull.class);
  }
}
