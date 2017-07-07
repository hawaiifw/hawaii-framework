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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hamcrest.Matcher;
import org.hawaiiframework.validation.field.FieldRejection;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Stores validation errors for a specific object.
 * <p>
 * This class is heavily inspired on Spring's {@link org.springframework.validation.Errors} interface. The main difference is that Hawaii's
 * {@link ValidationResult} does not bind or require the target object being validated.
 *
 * @author Marcel Overdijk
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ValidationResult {

    /**
     * The separator between path elements in a nested path, for example in "name" or "address.street".
     */
    public static final String NESTED_PATH_SEPARATOR = ".";

    public static final String NESTED_PATH_INDEX_PREFIX = "[";
    public static final String NESTED_PATH_INDEX_SUFFIX = "]";

    private final Deque<String> nestedPathStack = new ArrayDeque<>();
    private final List<ValidationError> errors = new LinkedList<>();

    /**
     * Returns the current nested path of this {@link ValidationResult}.
     *
     * @return the current nested path
     */
    public String getNestedPath() {
        final String nestedPath = this.nestedPathStack.peek();
        return StringUtils.defaultString(nestedPath);
    }

    public void pushNestedPath(final String path) {
        doPushNestedPath(path, null);
    }

    public void pushNestedPath(final String path, final int index) {
        doPushNestedPath(path, index);
    }

    private void doPushNestedPath(final String path, final Integer index) {
        final StringBuilder nestedPathBuilder = new StringBuilder(getNestedPath());
        if (nestedPathBuilder.length() > 0) {
            nestedPathBuilder.append(NESTED_PATH_SEPARATOR);
        }
        nestedPathBuilder.append(path);
        if (index != null) {
            nestedPathBuilder.append(NESTED_PATH_INDEX_PREFIX);
            nestedPathBuilder.append(index);
            nestedPathBuilder.append(NESTED_PATH_INDEX_SUFFIX);
        }
        this.nestedPathStack.push(nestedPathBuilder.toString());
    }

    public void popNestedPath() throws IllegalArgumentException {
        try {
            this.nestedPathStack.pop();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Cannot pop nested path: no nested path on stack", e);
        }
    }

    /**
     * Returns {@code true} if this validation result contains errors.
     *
     * @return {@code true} if this validation result contains errors
     */
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    /**
     * Returns the validation errors.
     *
     * @return the validation errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public void reject(final String code) {
        addError(new ValidationError(code));
    }

    public void rejectIf(final boolean expr, final String code) {
        if (expr) {
            reject(code);
        }
    }

    public <T> void rejectIf(final T actual, final Matcher<? super T> matcher, final String code) {
        rejectIf(matcher.matches(actual), code);
    }

    public void rejectValue(final String code) {
        rejectValue(null, code);
    }

    public void rejectValue(final String field, final String code) {
        final StringBuilder fieldBuilder = new StringBuilder(getNestedPath());
        if (StringUtils.isNotBlank(field)) {
            if (fieldBuilder.length() > 0) {
                fieldBuilder.append(NESTED_PATH_SEPARATOR);
            }
            fieldBuilder.append(field);
        }
        if (fieldBuilder.length() == 0) {
            reject(code);
        } else {
            addError(new ValidationError(fieldBuilder.toString(), code));
        }
    }

    public void rejectValueIf(final boolean expr, final String code) {
        if (expr) {
            rejectValue(code);
        }
    }

    public <T> void rejectValueIf(final T actual, final Matcher<? super T> matcher, final String code) {
        rejectValueIf(matcher.matches(actual), code);
    }

    public void rejectValueIf(final boolean expr, final String field, final String code) {
        if (expr) {
            rejectValue(field, code);
        }
    }

    public <T> void rejectValueIf(final T actual, final Matcher<? super T> matcher, final String field, final String code) {
        rejectValueIf(matcher.matches(actual), field, code);
    }

    /**
     * Reject a <code>field</code> with value <code>actual</code> in a fluent manner.
     *
     * For instance:
     * <blockquote>
     *     validationResult.rejectField("houseNumber", "13-a")
     *                          .whenNull()
     *                          .orWhen(containsString("a"))
     *                          .orWhen(h -> h.length() > 4);
     * </blockquote>
     *
     * @param field The field name to evaluate.
     * @param actual The value to evaluate.
     * @param <T> The type of the value.
     * @return a new field rejection.
     */
    public <T> FieldRejection<T> rejectField(String field, T actual) {
        return new FieldRejection<>(this, field, actual);
    }

    /**
     * Adds the supplied {@link ValidationError} to this {@link ValidationResult}.
     *
     * @param error the validation error
     */
    public void addError(final ValidationError error) {
        this.errors.add(error);
    }

    /**
     * Adds the supplied {@link ValidationError}s to this {@link ValidationResult}.
     *
     * @param errors the validation errors
     */
    public void addAllErrors(final List<ValidationError> errors) {
        this.errors.addAll(errors);
    }

    /**
     * Adds all errors from the supplied {@link ValidationResult} to this {@link ValidationResult}.
     *
     * @param validationResult the validation result to merge in
     */
    public void addAllErrors(final ValidationResult validationResult) {
        this.errors.addAll(validationResult.getErrors());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
