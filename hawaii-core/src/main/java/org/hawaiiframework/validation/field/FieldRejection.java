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

import static org.hamcrest.Matchers.nullValue;

import java.util.function.Function;
import java.util.function.Predicate;
import org.hamcrest.Matcher;
import org.hawaiiframework.validation.ValidationResult;

/**
 * Reject a field based on a few conditions.
 *
 * <p>For instance:
 *
 * <pre>
 * new FieldRejection(validationResult, "houseNumber", "13-a")
 *         .whenNull()
 *         .orWhen(h -&gt; h.contains("a"))
 *         .orWhen(h -&gt; h.length() &gt; 10);
 * </pre>
 *
 * <p>If used with the ValidationResult class this will look like:
 *
 * <pre>
 * validationResult.rejectField("houseNumber", "13-a")
 *         .whenNull()
 *         .orWhen(h -&gt; h.contains("a'))
 *         .orWhen(h -&gt; h.length() &gt; 10);
 * </pre>
 *
 * <p>The rejections without <code>code</code> parameters have the value <code>invalid</code>,
 * except the <code>whenNull()</code>, this has the <code>required</code> code value.
 *
 * <p>The chain will stop evaluating the rejection clauses after the first matching clause. In the
 * examples above the chain will not evaluate the length of the house number.
 *
 * @param <T> The type of the value to evaluate.
 * @author Rutger Lubbers
 */
@SuppressWarnings("PMD.TooManyMethods")
public class FieldRejection<T> {

  public static final String REQUIRED = "required";
  public static final String INVALID = "invalid";
  private final ValidationResult validationResult;
  private final String field;
  private final T actual;

  private boolean mustEvaluate = true;

  /**
   * Construct a new field rejection.
   *
   * @param validationResult The validation result.
   * @param field The field name (property name).
   * @param actual The field's value.
   */
  public FieldRejection(ValidationResult validationResult, String field, T actual) {
    this.validationResult = validationResult;
    this.field = field;
    this.actual = actual;
  }

  private FieldRejection<T> evaluate(Matcher<?> matcher, String code) {
    if (mustEvaluate && matcher.matches(actual)) {
      mustEvaluate = false;
      validationResult.rejectValue(field, code);
    }

    return this;
  }

  /**
   * Syntactic sugar, allows the use of <code>or().when(...).or().when(...)</code> syntaxis.
   *
   * @return The current field rejection.
   */
  public FieldRejection<T> or() {
    return this;
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with the error code <code>invalid</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> or(Matcher<T> matcher) {
    return when(matcher);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with supplied the error code <code>code</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> or(Matcher<T> matcher, String code) {
    return when(matcher, code);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with the error code <code>invalid</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> or(Predicate<T> predicate) {
    return when(predicate);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with supplied the error code <code>code</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> or(Predicate<T> predicate, String code) {
    return when(predicate, code);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with the error code <code>invalid</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> or(Function<T, R> function, Matcher<R> matcher) {
    return when(function, matcher, INVALID);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with supplied the error code <code>code</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> or(Function<T, R> function, Matcher<R> matcher, String code) {
    return when(function, matcher, code);
  }

  /**
   * Rejects the field when the <code>actual</code> is <code>null</code>. This will reject the field
   * with the error code <code>required</code>.
   *
   * @return The current field rejection.
   */
  public FieldRejection<T> whenNull() {
    return whenNull(REQUIRED);
  }

  /**
   * Rejects the field when the <code>actual</code> is <code>null</code>. This will reject the field
   * with supplied the error code <code>code</code>.
   *
   * @param code The error code to set if the actual value is <code>null</code>.
   * @return The current field rejection.
   */
  public FieldRejection<T> whenNull(String code) {
    return evaluate(nullValue(), code);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with the error code <code>invalid</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> when(Matcher<T> matcher) {
    return when(matcher, INVALID);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with supplied the error code <code>code</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> when(Matcher<T> matcher, String code) {
    return evaluate(matcher, code);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with the error code <code>invalid</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> when(Predicate<T> predicate) {
    return when(predicate, INVALID);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with the error code <code>invalid</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> when(Function<T, R> function, Matcher<R> matcher) {
    return when(function, matcher, INVALID);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with supplied the error code <code>code</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> when(Function<T, R> function, Matcher<R> matcher, String code) {
    return when(value -> matcher.matches(function.apply(value)), code);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with supplied the error code <code>code</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> when(Predicate<T> predicate, String code) {
    if (mustEvaluate && predicate.test(actual)) {
      mustEvaluate = false;
      validationResult.rejectValue(field, code);
    }

    return this;
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with the error code <code>invalid</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> orWhen(Matcher<T> matcher) {
    return when(matcher);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the <code>actual</code> value. This
   * will reject the field with supplied the error code <code>code</code>.
   *
   * @param matcher The matcher to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> orWhen(Matcher<T> matcher, String code) {
    return when(matcher, code);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with the error code <code>invalid</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> orWhen(Predicate<T> predicate) {
    return when(predicate);
  }

  /**
   * Rejects the field when the <code>predicate</code> evaluates to <code>true</code>. This will
   * reject the field with supplied the error code <code>code</code>.
   *
   * @param predicate The predicate to use.
   * @return The current field rejection.
   */
  public FieldRejection<T> orWhen(Predicate<T> predicate, String code) {
    return when(predicate, code);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with the error code <code>invalid</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> orWhen(Function<T, R> function, Matcher<R> matcher) {
    return when(function, matcher, INVALID);
  }

  /**
   * Rejects the field when the <code>matcher</code> matches the result of the <code>function</code>
   * . This will reject the field with supplied the error code <code>code</code>.
   *
   * @param function The function to apply to the <code>actual</code> value.
   * @param matcher The matcher to use against the return value of the <code>function</code>.
   * @return The current field rejection.
   */
  public <R> FieldRejection<T> orWhen(Function<T, R> function, Matcher<R> matcher, String code) {
    return when(function, matcher, code);
  }
}
