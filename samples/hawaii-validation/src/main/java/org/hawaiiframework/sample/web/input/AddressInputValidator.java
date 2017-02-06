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
import org.hawaiiframework.validation.ValidationResult;
import org.hawaiiframework.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Marcel Overdijk
 */
@Component
public class AddressInputValidator implements Validator<AddressInput> {

    public static final String BE = "BE";
    public static final String NL = "NL";
    public static final String LU = "LU";

    private final Pattern nlPostalCodePattern = Pattern.compile("[0-9]{4}[A-Z]{2}");
    private final List<String> countryCodes = Arrays.asList(BE, NL, LU);

    @Override
    public void validate(AddressInput address, ValidationResult validationResult) {

        // type validation
        if (address.getType() == null) {
            validationResult.rejectValue("type", "required");
        }

        // streetName validation
        String streetName = address.getStreetName();
        if (StringUtils.isBlank(streetName)) {
            validationResult.rejectValue("street_name", "required");
        } else {
            validationResult.rejectValueIf(streetName.length(), greaterThan(25), "street_name", "max_length_exceeded");
        }

        // streetName validation
        String houseNumber = address.getHouseNumber();
        if (StringUtils.isBlank(houseNumber)) {
            validationResult.rejectValue("house_number", "required");
        } else {
            validationResult.rejectValueIf(houseNumber.length(), greaterThan(10), "house_number", "max_length_exceeded");
        }

        // postalCode validation
        String postalCode = address.getPostalCode();
        if (StringUtils.isBlank(postalCode)) {
            validationResult.rejectValue("postal_code", "required");
        } else if (postalCode.length() > 20) {
            validationResult.rejectValue("postal_code", "max_length_exceeded");
        } else {
            if (NL.equals(address.getCountryCode())) {
                if (!nlPostalCodePattern.matcher(postalCode).matches()) {
                    validationResult.rejectValue("postal_code", "invalid_postal_code");
                }
            }
            // For other countries accept any postal code format
        }

        // city validation
        String city = address.getCity();
        if (StringUtils.isBlank(city)) {
            validationResult.rejectValue("city", "required");
        } else {
            validationResult.rejectValueIf(city.length(), greaterThan(25), "city", "max_length_exceeded");
        }

        // countryCode validation
        String countryCode = address.getCountryCode();
        if (StringUtils.isBlank(countryCode)) {
            validationResult.rejectValue("country_code", "required");
        } else {
            if (!countryCodes.contains(countryCode)) {
                validationResult.rejectValue("country_code", "invalid_country_code");
            }
        }
    }
}
