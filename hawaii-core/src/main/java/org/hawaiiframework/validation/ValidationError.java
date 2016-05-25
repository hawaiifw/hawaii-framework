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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.util.Assert;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Encapsulates a validation error.
 *
 * @author Marcel Overdijk
 * @see Validator
 * @see ValidationResult
 * @since 2.0.0
 */
public class ValidationError {

    private final String field;
    private final String code;

    /**
     * Constructs a new {@link ValidationError} with the supplied error code.
     *
     * @param code the error code
     */
    public ValidationError(String code) {
        this(null, code);
    }

    /**
     * Constructs a new {@link ValidationError} with the supplied field name and error code.
     *
     * @param field the field name
     * @param code  the error code
     */
    public ValidationError(String field, String code) {
        Assert.notNull(code, "Code must not be null");
        this.field = field;
        this.code = code;
    }

    /**
     * Returns the field name or {@code null}.
     *
     * @return the field name or {@code null}
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
