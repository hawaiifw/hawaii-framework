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

import org.hawaiiframework.sample.validator.EmailValidator;
import org.hawaiiframework.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Marcel Overdijk
 */
public class CustomerInputValidatorTests {

    private CustomerInputValidator customerInputValidator;
    private EmailValidator emailValidator;
    private AddressInputValidator addressInputValidator;
    private ValidationResult validationResult;

    @Before
    public void setUp() {
        this.emailValidator = new EmailValidator();
        this.addressInputValidator = new AddressInputValidator();
        this.customerInputValidator = new CustomerInputValidator(emailValidator, addressInputValidator);
        this.validationResult = new ValidationResult();
    }

    @Test
    public void validateWithValidCustomerInputShouldReturnNoErrors() {
        CustomerInput customerInput = createBaseCustomerInput();
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void validateWithBlankFirstNameShouldReturnRequiredError() {
        CustomerInput customerInput = createBaseCustomerInput();
        customerInput.setFirstName("");
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("first_name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void validateWithBlankLastNameShouldReturnRequiredError() {
        CustomerInput customerInput = createBaseCustomerInput();
        customerInput.setLastName("");
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("last_name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void validateWithBlankEmailShouldReturnRequiredError() {
        CustomerInput customerInput = createBaseCustomerInput();
        customerInput.setEmail("");
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("email")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void validateWithInvalidEmailShouldReturnInvalidEmailError() {
        CustomerInput customerInput = createBaseCustomerInput();
        customerInput.setEmail("marcel");
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("email")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("invalid_email")));
    }

    @Test
    public void validateWithBlankStreetNameShouldReturnRequiredError() {
        CustomerInput customerInput = createBaseCustomerInput();
        AddressInput addressInput2 = createBaseAddressInput();
        addressInput2.setType(AddressType.ALTERNATIVE);
        addressInput2.setStreetName("");
        customerInput.getAddresses().add(addressInput2);
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("addresses[1].street_name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void validateWith2PrimaryAddressesShouldReturnOnly1PrimaryAddressAllowedError() {
        CustomerInput customerInput = createBaseCustomerInput();
        AddressInput addressInput2 = createBaseAddressInput();
        customerInput.getAddresses().add(addressInput2);
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("addresses")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("only_1_primary_address_allowed")));
    }

    @Test
    public void validateWith4PrimaryAddressesShouldReturnError() {
        CustomerInput customerInput = createBaseCustomerInput();
        AddressInput addressInput2 = createBaseAddressInput();
        addressInput2.setType(AddressType.ALTERNATIVE);
        AddressInput addressInput3 = createBaseAddressInput();
        addressInput3.setType(AddressType.ALTERNATIVE);
        AddressInput addressInput4 = createBaseAddressInput();
        addressInput4.setType(AddressType.ALTERNATIVE);
        customerInput.getAddresses().addAll(Arrays.asList(addressInput2, addressInput3, addressInput4));
        customerInputValidator.validate(customerInput, validationResult);
        assertThat(validationResult.getErrors(), hasSize(1));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("addresses")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("max_array_length_exceeded")));
    }

    private CustomerInput createBaseCustomerInput() {
        CustomerInput customerInput = new CustomerInput();
        customerInput.setFirstName("Marcel");
        customerInput.setLastName("Overdijk");
        customerInput.setEmail("marcel.overdijk@qnh.nl");
        List<AddressInput> addresses = new ArrayList<>();
        AddressInput addressInput1 = createBaseAddressInput();
        addresses.add(addressInput1);
        customerInput.setAddresses(addresses);
        return customerInput;
    }

    private AddressInput createBaseAddressInput() {
        AddressInput addressInput = new AddressInput();
        addressInput.setType(AddressType.PRIMARY);
        addressInput.setStreetName("Rennemigstraat");
        addressInput.setHouseNumber("1");
        addressInput.setPostalCode("6413BR");
        addressInput.setCity("Heerlen");
        addressInput.setCountryCode("NL");
        return addressInput;
    }
}
