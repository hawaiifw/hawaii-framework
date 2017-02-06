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

package org.hawaiiframework.validation;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link ValidationResult}.
 *
 * @author Marcel Overdijk
 */
public class ValidationResultTests {

    private ValidationResult validationResult;

    @Before
    public void setUp() {
        this.validationResult = new ValidationResult();
    }

    @Test
    public void testNewValidationResultHasNoErrors() {
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void testNewValidationResultReturnsNoErrors() {
        assertThat(validationResult.getErrors(), is(emptyIterable()));
    }

    @Test
    public void testNewValidationResultHasNoNestedPath() {
        assertThat(validationResult.getNestedPath(), isEmptyString());
    }

    @Test
    public void testPushNestedPath() {
        validationResult.pushNestedPath("name");
        assertThat(validationResult.getNestedPath(), is(equalTo("name")));
    }

    @Test
    public void testPushNestedPathWithIndex() {
        validationResult.pushNestedPath("order_lines", 5);
        assertThat(validationResult.getNestedPath(), is(equalTo("order_lines[5]")));
    }

    @Test
    public void testPopNestedPath() {
        validationResult.pushNestedPath("order_lines", 5);
        validationResult.pushNestedPath("quantity");
        assertThat(validationResult.getNestedPath(), is(equalTo("order_lines[5].quantity")));
        validationResult.popNestedPath();
        assertThat(validationResult.getNestedPath(), is(equalTo("order_lines[5]")));
    }

    @Test
    public void testPopNestedPathThrowsExceptionWhenNoNestedPathOnStack() {
        try {
            validationResult.popNestedPath();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(equalTo("Cannot pop nested path: no nested path on stack")));
        }
    }

    @Test
    public void testReject() {
        validationResult.reject("inactive");
        assertThat(validationResult.getErrors(), hasSize(equalTo(1)));
        assertThat(validationResult.getErrors().get(0).getField(), is(nullValue()));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("inactive")));
    }

    @Test
    public void testRejectValueWithoutNestedValue() {
        validationResult.rejectValue("name", "required");
        assertThat(validationResult.getErrors(), hasSize(equalTo(1)));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("name")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void testRejectValueWithNestedPath() {
        validationResult.pushNestedPath("order_line[5]");
        validationResult.rejectValue("quantity", "required");
        assertThat(validationResult.getErrors(), hasSize(equalTo(1)));
        assertThat(validationResult.getErrors().get(0).getField(), is(equalTo("order_line[5].quantity")));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void testRejectValueWithoutNestedPathAndField() {
        validationResult.rejectValue(null, "required");
        assertThat(validationResult.getErrors(), hasSize(equalTo(1)));
        assertThat(validationResult.getErrors().get(0).getField(), is(nullValue()));
        assertThat(validationResult.getErrors().get(0).getCode(), is(equalTo("required")));
    }

    @Test
    public void testAddError() {
        ValidationError error = new ValidationError("name", "required");
        validationResult.addError(error);
        assertThat(validationResult.getErrors(), hasSize(equalTo(1)));
        assertThat(validationResult.getErrors(), contains(error));
    }

    @Test
    public void testAddAllErrors() {
        ValidationError error1 = new ValidationError("name", "required");
        ValidationError error2 = new ValidationError("order_lines[5].quantity", "required");
        validationResult.addAllErrors(Arrays.asList(error1, error2));
        assertThat(validationResult.getErrors(), hasSize(equalTo(2)));
        assertThat(validationResult.getErrors(), contains(error1, error2));
    }
}
