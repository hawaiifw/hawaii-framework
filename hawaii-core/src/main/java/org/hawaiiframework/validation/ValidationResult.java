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

package org.hawaiiframework.validation;

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hamcrest.Matcher;
import org.hawaiiframework.validation.field.FieldRejection;

/**
 * Stores validation errors for a specific object.
 *
 * <p>This class is heavily inspired on Spring's {@link org.springframework.validation.Errors}
 * interface. The main difference is that Hawaii's {@link ValidationResult} does not bind or require
 * the target object being validated.
 *
 * @author Marcel Overdijk
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ValidationResult implements Serializable {

  /** The serial version UID. */
  private static final long serialVersionUID = 6587801940281589895L;

  /**
   * The separator between path elements in a nested path, for example in "name" or
   * "address.street".
   */
  private static final String NESTED_PATH_SEPARATOR = ".";

  private static final String NESTED_PATH_INDEX_PREFIX = "[";
  private static final String NESTED_PATH_INDEX_SUFFIX = "]";

  private final transient Deque<String> nestedPathStack = new ArrayDeque<>();
  private final transient List<ValidationError> errors = new LinkedList<>();

  /**
   * Returns the current nested path of this {@link ValidationResult}.
   *
   * @return the current nested path
   */
  public String getNestedPath() {
    String nestedPath = this.nestedPathStack.peek();
    return StringUtils.defaultString(nestedPath);
  }

  /** Push the nested path. */
  public void pushNestedPath(String path) {
    doPushNestedPath(path, null);
  }

  /** Push the nested path. */
  public void pushNestedPath(String path, int index) {
    doPushNestedPath(path, index);
  }

  private void doPushNestedPath(String path, Integer index) {
    StringBuilder nestedPathBuilder = new StringBuilder(getNestedPath());
    if (!nestedPathBuilder.isEmpty()) {
      nestedPathBuilder.append(NESTED_PATH_SEPARATOR);
    }
    nestedPathBuilder.append(path);
    if (index != null) {
      nestedPathBuilder
          .append(NESTED_PATH_INDEX_PREFIX)
          .append(index)
          .append(NESTED_PATH_INDEX_SUFFIX);
    }
    this.nestedPathStack.push(nestedPathBuilder.toString());
  }

  /** Remove the nested path. */
  public void popNestedPath() {
    try {
      this.nestedPathStack.pop();
    } catch (NoSuchElementException exception) {
      throw new IllegalStateException("Cannot pop nested path: no nested path on stack", exception);
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
    return unmodifiableList(this.errors);
  }

  /** Reject the value. */
  public void reject(String code) {
    addError(new ValidationError(code));
  }

  /** Reject the value {@code expr} is {@code true}. */
  public void rejectIf(boolean expr, String code) {
    if (expr) {
      reject(code);
    }
  }

  /** Reject the value {@code actual} if the {@code matcher} matches. */
  public <T> void rejectIf(T actual, Matcher<? super T> matcher, String code) {
    rejectIf(matcher.matches(actual), code);
  }

  /** Reject the value with {@code code} error. */
  public void rejectValue(String code) {
    rejectValue(null, code);
  }

  /** Reject the value for {@code field} with {@code code} error. */
  public void rejectValue(String field, String code) {
    StringBuilder fieldBuilder = new StringBuilder(getNestedPath());
    if (StringUtils.isNotBlank(field)) {
      if (!fieldBuilder.isEmpty()) {
        fieldBuilder.append(NESTED_PATH_SEPARATOR);
      }
      fieldBuilder.append(field);
    }
    if (fieldBuilder.isEmpty()) {
      reject(code);
    } else {
      addError(new ValidationError(fieldBuilder.toString(), code));
    }
  }

  /** Reject the value if {@code expr} is {@code true}. Will return the {@code code} error. */
  public void rejectValueIf(boolean expr, String code) {
    if (expr) {
      rejectValue(code);
    }
  }

  /**
   * Reject the value if {@code matcher} matches the value {@code actual}. Will return the {@code
   * code} error on the {@code field}.
   */
  public <T> void rejectValueIf(T actual, Matcher<? super T> matcher, String code) {
    rejectValueIf(matcher.matches(actual), code);
  }

  /**
   * Reject the value if {@code expr} is {@code true}. Will return the {@code code} error on the
   * {@code field}.
   */
  public void rejectValueIf(boolean expr, String field, String code) {
    if (expr) {
      rejectValue(field, code);
    }
  }

  /**
   * Reject the value if the value matches the {@code matcher}. Will return the {@code code} error
   * on the {@code field}.
   */
  public <T> void rejectValueIf(T actual, Matcher<? super T> matcher, String field, String code) {
    rejectValueIf(matcher.matches(actual), field, code);
  }

  /**
   * Reject a <code>field</code> with value <code>actual</code> in a fluent manner.
   *
   * <p>For instance:
   *
   * <pre>
   * validationResult.rejectField("houseNumber", "13-a")
   *         .whenNull()
   *         .orWhen(containsString("a"))
   *         .orWhen(h -&gt; h.length() &gt; 4);
   * </pre>
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
  public void addError(ValidationError error) {
    this.errors.add(error);
  }

  /**
   * Adds the supplied {@link ValidationError}s to this {@link ValidationResult}.
   *
   * @param errors the validation errors
   */
  public void addAllErrors(List<ValidationError> errors) {
    this.errors.addAll(errors);
  }

  /**
   * Adds all errors from the supplied {@link ValidationResult} to this {@link ValidationResult}.
   *
   * @param validationResult the validation result to merge in
   */
  public void addAllErrors(ValidationResult validationResult) {
    this.errors.addAll(validationResult.getErrors());
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
  }
}
