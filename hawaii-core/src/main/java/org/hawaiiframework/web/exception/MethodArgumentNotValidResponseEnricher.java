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

import org.hawaiiframework.converter.ModelConverter;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.MethodArgumentNotValidResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Objects;

/**
 * This enricher adds validation errors to the error response resource.
 *
 * @author Paul Klos
 * @author Richard den Adel
 * @since 2.0.0
 */
public class MethodArgumentNotValidResponseEnricher implements ErrorResponseEnricher {

    private final ModelConverter<ObjectError, ValidationErrorResource> objectErrorResourceAssembler;

    public MethodArgumentNotValidResponseEnricher(
            final ModelConverter<ObjectError, ValidationErrorResource> objectErrorResourceAssembler) {
        this.objectErrorResourceAssembler = Objects.requireNonNull(objectErrorResourceAssembler,
                "'objectErrorResourceAssembler' must not be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>NOTE:</strong> This enricher only applies if throwable is a {@link MethodArgumentNotValidException} and
     * #errorResponseResource is a {@link MethodArgumentNotValidResponseResource}.
     */
    @Override
    public void doEnrich(
            final ErrorResponseResource errorResponseResource,
            final Throwable throwable,
            final WebRequest request,
            final HttpStatus httpStatus) {
        if (throwable instanceof MethodArgumentNotValidException validationException
                && errorResponseResource instanceof MethodArgumentNotValidResponseResource resource) {
            final List<ObjectError> errors = getErrors(validationException);
            if (!errors.isEmpty()) {
                resource.setErrors(objectErrorResourceAssembler.convert(errors));
            }
        }
    }

    private List<ObjectError> getErrors(final MethodArgumentNotValidException methodArgumentNotValidException) {
        return getErrors(methodArgumentNotValidException.getBindingResult());
    }

    private List<ObjectError> getErrors(final BindingResult validationResult) {
        return validationResult.getAllErrors();
    }

}
