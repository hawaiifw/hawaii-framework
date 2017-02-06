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

package org.hawaiiframework.sample.web.input;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.sample.validator.EmailValidator;
import org.hawaiiframework.validation.ValidationResult;
import org.hawaiiframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Marcel Overdijk
 */
@Component
public class CustomerInputValidator implements Validator<CustomerInput> {

    private final EmailValidator emailValidator;
    private final AddressInputValidator addressInputValidator;

    @Autowired
    public CustomerInputValidator(final EmailValidator emailValidator, final AddressInputValidator addressInputValidator) {
        this.emailValidator = emailValidator;
        this.addressInputValidator = addressInputValidator;
    }

    @Override
    public void validate(CustomerInput customer, ValidationResult validationResult) {

        // firstName validation
        String firstName = customer.getFirstName();
        if (StringUtils.isBlank(firstName)) {
            validationResult.rejectValue("first_name", "required");
        } else {
            validationResult.rejectValueIf(firstName.length(), greaterThan(25), "first_name", "max_length_exceeded");
        }

        // lastName validation
        String lastName = customer.getLastName();
        if (StringUtils.isBlank(lastName)) {
            validationResult.rejectValue("last_name", "required");
        } else {
            validationResult.rejectValueIf(lastName.length(), greaterThan(25), "last_name", "max_length_exceeded");
        }

        // email validation
        String email = customer.getEmail();
        if (StringUtils.isBlank(email)) {
            validationResult.rejectValue("email", "required");
        } else if (email.length() > 100) {
            validationResult.rejectValue("email", "max_length_exceeded");
        } else {
            validationResult.pushNestedPath("email");
            emailValidator.validate(email, validationResult);
            validationResult.popNestedPath();
        }

        // adresses validation
        List<AddressInput> addresses = customer.getAddresses();
        if (addresses == null || addresses.size() == 0) {
            validationResult.rejectValue("addresses", "required");
        } else {
            // addresses array validations
            long primaries = addresses.stream()
                    .filter(address -> address.getType() == AddressType.PRIMARY)
                    .count();
            if (primaries == 0) {
                validationResult.rejectValue("addresses", "primary_address_required");
            } else if (primaries > 1) {
                validationResult.rejectValue("addresses", "only_1_primary_address_allowed");
            }
            if (addresses.size() > 3) {
                validationResult.rejectValue("addresses", "max_array_length_exceeded");
            }
            // address validations
            for (int i = 0; i < addresses.size(); i++) {
                validationResult.pushNestedPath("addresses", i);
                addressInputValidator.validate(addresses.get(i), validationResult);
                validationResult.popNestedPath();
            }
        }
    }
}
