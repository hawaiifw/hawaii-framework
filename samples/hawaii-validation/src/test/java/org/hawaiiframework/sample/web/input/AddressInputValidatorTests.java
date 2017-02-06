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

package org.hawaiiframework.sample.web.input;

import org.hawaiiframework.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Marcel Overdijk
 */
public class AddressInputValidatorTests {

    private AddressInputValidator addressInputValidator;
    private ValidationResult validationResult;

    @Before
    public void setUp() {
        this.addressInputValidator = new AddressInputValidator();
        this.validationResult = new ValidationResult();
    }

    @Test
    public void validateWithValidAddressInputShouldReturnNoErrors() {
        AddressInput addressInput = createBaseAddressInput();
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void validateWithBlankStreetNameShouldReturnRequiredError() {
        AddressInput addressInput = createBaseAddressInput();
        addressInput.setStreetName("");
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("street_name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void validateWithVeryLongStreetNameShouldReturnMaxLengthExceededError() {
        AddressInput addressInput = createBaseAddressInput();
        addressInput.setStreetName("Ir. Mr. Dr. van Waterschoot van der Grachtstraat");
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("street_name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("max_length_exceeded")));
    }

    @Test
    public void validateWithNonNlPostalCodeAndNlCountryCodeShouldReturnInvalidPostalCodeError() {
        AddressInput addressInput = createBaseAddressInput();
        addressInput.setPostalCode("12AB");
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("postal_code")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("invalid_postal_code")));
    }

    @Test
    public void validateWithNonNlPostalCodeAndNonNlCountryCodeShouldReturnNoErrors() {
        AddressInput addressInput = createBaseAddressInput();
        addressInput.setPostalCode("12AB");
        addressInput.setCountryCode("BE");
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void validateWithNonBeOrNlOrLuCountryCodeShouldReturnInvalidCountryCodeError() {
        AddressInput addressInput = createBaseAddressInput();
        addressInput.setCountryCode("DE");
        addressInputValidator.validate(addressInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("country_code")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("invalid_country_code")));
    }

    private AddressInput createBaseAddressInput() {
        AddressInput addressInput = new AddressInput();
        addressInput.setType(AddressType.PRIMARY);
        addressInput.setStreetName("Litscherveldweg");
        addressInput.setHouseNumber("1");
        addressInput.setPostalCode("6413BA");
        addressInput.setCity("Heerlen");
        addressInput.setCountryCode("NL");
        return addressInput;
    }
}
