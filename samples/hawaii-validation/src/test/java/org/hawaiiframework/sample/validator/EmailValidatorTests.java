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

package org.hawaiiframework.sample.validator;

import org.hawaiiframework.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Marcel Overdijk
 */
public class EmailValidatorTests {

    private EmailValidator emailValidator;
    private ValidationResult validationResult;

    @Before
    public void setUp() {
        this.emailValidator = new EmailValidator();
        this.validationResult = new ValidationResult();
        validationResult.pushNestedPath("email");
    }

    @Test
    public void validateWithValidEmailShouldReturnNoErrors() {
        emailValidator.validate("marcel.overdijk@qnh.nl", validationResult);
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void validateWithInvalidEmailShouldReturnInvalidEmailError() {
        emailValidator.validate("marcel", validationResult);
        assertThat(validationResult.hasErrors(), is(true));
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("email")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("invalid_email")));
    }
}
