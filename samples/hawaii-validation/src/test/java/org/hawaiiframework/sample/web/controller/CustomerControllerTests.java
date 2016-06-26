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

package org.hawaiiframework.sample.web.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hawaiiframework.sample.Application;
import org.hawaiiframework.sample.web.input.AddressInput;
import org.hawaiiframework.sample.web.input.AddressType;
import org.hawaiiframework.sample.web.input.CustomerInput;
import org.hawaiiframework.test.mockmvc.AbstractMockMvcTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Marcel Overdijk
 */
@SpringApplicationConfiguration(classes = Application.class)
public class CustomerControllerTests extends AbstractMockMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createWithValidCustomerInputShouldReturn200()
            throws Exception {
        CustomerInput customerInput = createBaseCustomerInput();
        String json = objectMapper.writeValueAsString(customerInput);
        mockMvc.perform(post("/rest/customers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void createWithInvalidCustomerInputShouldReturn400WithErrors()
            throws Exception {
        CustomerInput customerInput = new CustomerInput();
        AddressInput addressInput = new AddressInput();
        customerInput.getAddresses().add(addressInput);
        String json = objectMapper.writeValueAsString(customerInput);
        mockMvc.perform(post("/rest/customers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.method", is(equalTo("POST"))))
                .andExpect(jsonPath("$.uri", is(equalTo("/rest/customers"))))
                .andExpect(jsonPath("$.query", is(nullValue())))
                .andExpect(
                        jsonPath("$.content_type", is(equalTo(MediaType.APPLICATION_JSON_VALUE))))
                .andExpect(jsonPath("$.status_code", is(equalTo(HttpStatus.BAD_REQUEST.value()))))
                .andExpect(jsonPath("$.status_message",
                        is(equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))))
                .andExpect(jsonPath("$.error_message", is(nullValue())))
                .andExpect(jsonPath("$.errors", hasSize(10)))
                .andExpect(jsonPath("$.errors[0].field", is("first_name")))
                .andExpect(jsonPath("$.errors[0].code", is("required")))
                .andExpect(jsonPath("$.errors[1].field", is("last_name")))
                .andExpect(jsonPath("$.errors[1].code", is("required")))
                .andExpect(jsonPath("$.errors[2].field", is("email")))
                .andExpect(jsonPath("$.errors[2].code", is("required")))
                .andExpect(jsonPath("$.errors[3].field", is("addresses")))
                .andExpect(jsonPath("$.errors[3].code", is("primary_address_required")))
                .andExpect(jsonPath("$.errors[4].field", is("addresses[0].type")))
                .andExpect(jsonPath("$.errors[4].code", is("required")))
                .andExpect(jsonPath("$.errors[5].field", is("addresses[0].street_name")))
                .andExpect(jsonPath("$.errors[5].code", is("required")))
                .andExpect(jsonPath("$.errors[6].field", is("addresses[0].house_number")))
                .andExpect(jsonPath("$.errors[6].code", is("required")))
                .andExpect(jsonPath("$.errors[7].field", is("addresses[0].postal_code")))
                .andExpect(jsonPath("$.errors[7].code", is("required")))
                .andExpect(jsonPath("$.errors[8].field", is("addresses[0].city")))
                .andExpect(jsonPath("$.errors[8].code", is("required")))
                .andExpect(jsonPath("$.errors[9].field", is("addresses[0].country_code")))
                .andExpect(jsonPath("$.errors[9].code", is("required")));
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
