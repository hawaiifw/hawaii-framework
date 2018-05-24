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

package org.hawaiiframework.validation;

import org.hawaiiframework.exception.HawaiiException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

//import org.junit.Test;


/**
 * The type Validator helper tests.
 *
 * @author Hans Lammers
 */
public class ValidatorHelperTests {

    /**
     * Test validation succeeds.
     */
    @Test
    public void testValidationSucceeds() {
        ValidationResult vr = new ValidationResult();
        TestTarget target = new TestTarget("bla", 1);
        ValidationHelper validationHelper = new ValidationHelper(target, vr);
        validationHelper
                .withMaxLengthField("prop1", 20)
                .validate();
        assertThat(vr.getErrors().size(), is(0));
    }

    /**
     * Test validation fails for non strings.
     */
    @Test(expected = HawaiiException.class)
    public void testValidationFailsForNonStrings() {
        ValidationResult vr = new ValidationResult();
        TestTarget target = new TestTarget("bla", 1);
        ValidationHelper validationHelper = new ValidationHelper(target, vr);
        validationHelper
                .withMaxLengthField("prop1", 20)
                .withMaxLengthField("prop2", 5)
                .validate();
    }

    /**
     * Test validation fails for invalid strings.
     */
    @Test
    public void testValidationFailsForInvalidStrings() {
        ValidationResult vr = new ValidationResult();
        TestTarget target = new TestTarget("blaaaaaaaaaaaad", 1);
        ValidationHelper validationHelper = new ValidationHelper(target, vr);
        validationHelper
                .withMaxLengthField("prop1", 5)
                .validate();
        assertThat(vr.getErrors().size(), is(1));
    }

    /**
     * Test validation fails for null strings.
     */
    @Test
    public void testValidationFailsForNullStrings() {
        ValidationResult vr = new ValidationResult();
        TestTarget target = new TestTarget(null, 1);
        ValidationHelper validationHelper = new ValidationHelper(target, vr);
        validationHelper
                .withMaxLengthField("prop1", 5)
                .validate();
        assertThat(vr.getErrors().size(), is(1));
    }


    /**
     * The type Test target.
     */
    class TestTarget {

        private String prop1;
        private int prop2;

        /**
         * Contructor
         *
         * @param prop1 test property 1
         * @param prop2 test property 2
         */
        public TestTarget(String prop1, int prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        /**
         * Gets prop 1.
         *
         * @return the prop 1
         */
        public String getProp1() {
            return prop1;
        }

        /**
         * Sets prop 1.
         *
         * @param prop1 the prop 1
         */
        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }

        /**
         * Gets prop 2.
         *
         * @return the prop 2
         */
        public int getProp2() {
            return prop2;
        }

        /**
         * Sets prop 2.
         *
         * @param prop2 the prop 2
         */
        public void setProp2(int prop2) {
            this.prop2 = prop2;
        }
    }
}
