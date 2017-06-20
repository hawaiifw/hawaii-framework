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

package org.hawaiiframework.sample.web.controller;

import org.hawaiiframework.sample.web.input.CustomerInput;
import org.hawaiiframework.sample.web.input.CustomerInputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Marcel Overdijk
 */
@RestController
@RequestMapping(path = "/api/customers")
public class CustomerController {

    private final CustomerInputValidator customerInputValidator;

    @Autowired
    public CustomerController(final CustomerInputValidator customerInputValidator) {
        this.customerInputValidator = customerInputValidator;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody CustomerInput customerInput) {

        // Validate recipe input; and throw validation exception in case of validation errors
        customerInputValidator.validateAndThrow(customerInput);

        // Do something with the customer input
        // E.g use a org.hawaiiframework.web.input.InputConverter to convert the customer input object to a customer domain object
        // (note in case of a validation error a ValidationException would have been thrown)

        // Return empty 200 to client
        return ResponseEntity.ok().build();
    }
}
