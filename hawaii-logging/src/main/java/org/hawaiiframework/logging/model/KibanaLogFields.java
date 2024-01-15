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

package org.hawaiiframework.logging.model;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * Class that holds the extra fields used for Kibana logging.
 *
 * <p>Log lines for Kibana will contain all fields set, until the log fields are cleared by invoking
 * {@link KibanaLogFields#clear()}.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class KibanaLogFields {

  private KibanaLogFields() {
    // Empty utility class constructor.
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return The field, set.
   */
  public static KibanaLogField tag(KibanaLogField field, Enum<?> value) {
    return tag(field, value.toString());
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return The field, set.
   */
  public static KibanaLogField tag(KibanaLogField field, int value) {
    return tag(field, Integer.toString(value));
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return The field, set.
   */
  public static KibanaLogField tag(KibanaLogField field, String value) {
    if (isBlank(value)) {
      clear(field);
      return field;
    }
    MDC.put(field.getLogName(), value);
    return field;
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}.
   *
   * @param field The field to set.
   * @param values The values to set.
   * @return The field, set.
   */
  public static KibanaLogField tag(KibanaLogField field, Collection<String> values) {
    if (values == null || values.isEmpty()) {
      return tag(field, (String) null);
    }
    String value =
        values.stream().map(item -> String.format("'%s'", item)).collect(joining(", ", "[", "]"));
    return tag(field, value);
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  public static AutoCloseableKibanaLogField tagCloseable(KibanaLogField field, Enum<?> value) {
    return tagCloseable(field, value.toString());
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  public static AutoCloseableKibanaLogField tagCloseable(KibanaLogField field, int value) {
    return tagCloseable(field, Integer.toString(value));
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  public static AutoCloseableKibanaLogField tagCloseable(KibanaLogField field, String value) {
    return new AutoCloseableKibanaLogField(tag(field, value));
  }

  /**
   * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
   *
   * @param field The field to set.
   * @param values The value to set.
   * @return a closable field.
   */
  public static AutoCloseableKibanaLogField tagCloseable(
      KibanaLogField field, Collection<String> values) {
    return new AutoCloseableKibanaLogField(tag(field, values));
  }

  /**
   * Retrieves the value for the {@code field}. It will return {@code null} if no value is set.
   *
   * @param field The field to retrieve the value for.
   * @return The fields value, possibly {@code null}.
   */
  public static String get(KibanaLogField field) {
    return getOrDefault(field, null);
  }

  /**
   * Retrieves the value for the {@code field}. It will return {@code defaultValue} if no value is
   * set.
   *
   * @param field The field to retrieve the value for.
   * @param defaultValue The value if there is no value set.
   * @return The field's value or the default.
   */
  public static String getOrDefault(KibanaLogField field, String defaultValue) {
    return StringUtils.defaultIfEmpty(MDC.get(field.getLogName()), defaultValue);
  }

  /**
   * Removes the value for the field {@code field}.
   *
   * @param field The field to remove.
   */
  public static void clear(KibanaLogField field) {
    MDC.remove(field.getLogName());
  }

  /**
   * Removes the value for the fields {@code fields}.
   *
   * @param fields The fields to remove the values for.
   */
  public static void clear(KibanaLogField... fields) {
    for (KibanaLogField field : fields) {
      MDC.remove(field.getLogName());
    }
  }

  /** Removes all values set for all fields. */
  public static void clear() {
    MDC.clear();
  }

  /**
   * Getter for the log string.
   *
   * @return the log string
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public static String getValuesAsLogString() {
    StringBuilder result = new StringBuilder();
    MDC.getCopyOfContextMap()
        .forEach(
            (key, value) -> {
              if (!key.equals(KibanaLogFieldNames.LOG_TYPE.getLogName())) {
                result.append(' ').append(key).append("=\"").append(value).append('"');
              }
            });

    return result.toString();
  }

  /**
   * Update log fields based on the {@code KibanaLogContext}.
   *
   * <p>See {@link KibanaLogFields#getContext()}.
   *
   * @param logContext The context to copy.
   */
  public static void populateFromContext(KibanaLogContext logContext) {
    if (logContext != null) {
      MDC.setContextMap(logContext.getContextMap());
    }
  }

  /**
   * Create a new log context for the current thread's kibana log fields.
   *
   * <p>See {@link KibanaLogContext#registerKibanaLogFieldsInThisThread()}.
   *
   * @return a log context to use in another thread.
   */
  public static KibanaLogContext getContext() {
    return new KibanaLogContext();
  }
}
