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

package org.hawaiiframework.validation.field;

import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link FieldRejection}.
 *
 * @author Rutger Lubbers
 */
public class FieldRejectionTest {
    private static final String FIELD_NAME = "some_name";

    private static final String INVALID = "invalid";
    private static final String REQUIRED = "required";

    private static final String ERR_CODE = "some_error_code";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ValidationResult validationResult;

    @Before
    public void setUp() {
        validationResult = new ValidationResult();
    }

    @Test
    public void testRejectNull() {
        String actual = null;
        validationResult
                .rejectField(FIELD_NAME, actual)
                .whenNull();

        assureErrorHas(FIELD_NAME, REQUIRED);
    }

    @Test
    public void testRejectNullWithErrorCode() {
        String actual = null;
        validationResult
                .rejectField(FIELD_NAME, actual)
                .whenNull(ERR_CODE);

        assureErrorHas(FIELD_NAME, ERR_CODE);
    }

    @Test
    public void assureThatEvaluationStopsAfterFirstMatch() {
        String actual = null;
        validationResult
                .rejectField(FIELD_NAME, actual)
                .whenNull()
                .orWhen(h -> h==null, ERR_CODE);

        assureErrorHas(FIELD_NAME, REQUIRED);
    }

    @Test
    public void testThatOrderDoesMatter() {
        thrown.expect(NullPointerException.class);

        String actual = null;
        validationResult
                .rejectField(FIELD_NAME, actual)
                .orWhen(String::length, is(greaterThan(10)), ERR_CODE)
                .or()
                .whenNull();
    }

    @Test
    public void assureThatEvaluationStopsAfterFirstMatchWithExplicitOr() {
        String actual = null;
        validationResult
                .rejectField(FIELD_NAME, actual)
                .whenNull()
                .or()
                .when(h -> h==null, ERR_CODE);

        assureErrorHas(FIELD_NAME, REQUIRED);
    }

    @Test
    public void testMatchersWork() {
        String actual = "Some string containing 'foo'.";
        validationResult
                .rejectField(FIELD_NAME, actual)
                .when(containsString("'bar'"), "error")
                .or()
                .when(containsString("'foo'"), ERR_CODE);

        assureErrorHas(FIELD_NAME, ERR_CODE);
    }

    @Test
    public void testMatchersWorkWithoutExplicitErrorCode() {
        String actual = "Some string containing 'foo'.";
        validationResult
                .rejectField(FIELD_NAME, actual)
                .when(containsString("'bar'"), "error")
                .or()
                .when(containsString("'foo'"));

        assureErrorHas(FIELD_NAME, INVALID);
    }

    @Test
    public void testFunction() {
        String actual = "Some string containing 'foo'.";
        validationResult
                .rejectField(FIELD_NAME, actual)
                .or(String::length, is(greaterThan(10)), "length");

        assureErrorHas(FIELD_NAME, "length");

    }

    private void assureErrorHas(String field, String code) {
        ValidationError validationError = validationResult.getErrors().get(0);
        assertThat(validationError.getField(), is(field));
        assertThat(validationError.getCode(), is(code));
    }
}
