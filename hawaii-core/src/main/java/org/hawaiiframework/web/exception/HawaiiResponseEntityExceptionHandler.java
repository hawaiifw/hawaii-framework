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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ValidationErrorResource;
import org.hawaiiframework.web.resource.ValidationErrorResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@ControllerAdvice
public class HawaiiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ValidationErrorResourceAssembler validationErrorResourceAssembler;

    public HawaiiResponseEntityExceptionHandler(ValidationErrorResourceAssembler validationErrorResourceAssembler) {
        Assert.notNull(validationErrorResourceAssembler, "Validation error resource assembler must not be null");
        this.validationErrorResourceAssembler = validationErrorResourceAssembler;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity handleValidationException(ValidationException e) {
        List<ValidationError> validationErrors = e.getValidationResult().getErrors();
        List<ValidationErrorResource> resources = validationErrorResourceAssembler.toResources(validationErrors);
        return ResponseEntity.badRequest().body(resources);
    }
}
