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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Class that holds the extra fields used for Kibana logging.
 * <p>
 * Log lines for Kibana will contain all fields set, until the log fields are cleared by invoking {@link KibanaLogFields#clear()}.
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
     */
    public static KibanaLogField tag(final KibanaLogField field, final Enum<?> value) {
        return tag(field, value.toString());
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}.
     */
    public static KibanaLogField tag(final KibanaLogField field, final int value) {
        return tag(field, Integer.toString(value));
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}.
     */
    public static KibanaLogField tag(final KibanaLogField field, final String value) {
        if (isBlank(value)) {
            clear(field);
            return field;
        }
        MDC.put(field.getLogName(), value);
        return field;
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}.
     */
    public static KibanaLogField tag(final KibanaLogField field, final Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return tag(field, (String) null);
        }
        final String value = values.stream().map(s -> String.format("'%s'", s)).collect(Collectors.joining(", ", "[", "]"));
        return tag(field, value);
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
     */
    public static AutoCloseableKibanaLogField tagCloseable(final KibanaLogField field, final Enum<?> value) {
        return tagCloseable(field, value.toString());
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
     */
    public static AutoCloseableKibanaLogField tagCloseable(final KibanaLogField field, final int value) {
        return tagCloseable(field, Integer.toString(value));
    }

    /**
     * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
     */
    public static AutoCloseableKibanaLogField tagCloseable(final KibanaLogField field, final String value) {
        return new AutoCloseableKibanaLogField(tag(field, value));
    }
    /**
     * Sets the Kibana log field {@code field} to the {@code value}, returns an auto closeable.
     */
    public static AutoCloseableKibanaLogField tagCloseable(final KibanaLogField field, final Collection<String> values) {
        return new AutoCloseableKibanaLogField(tag(field, values));
    }
    /**
     * Retrieves the value for the {@code field}. It will return {@code null} if no value is set.
     */
    public static String get(final KibanaLogField field) {
        return getOrDefault(field, null);
    }

    /**
     * Retrieves the value for the {@code field}. It will return {@code defaultValue} if no value is set.
     */
    public static String getOrDefault(final KibanaLogField field, final String defaultValue) {
        return StringUtils.defaultIfEmpty(MDC.get(field.getLogName()), defaultValue);
    }

    /**
     * Removes the value for the field {@code field}.
     */
    public static void clear(final KibanaLogField field) {
        MDC.remove(field.getLogName());
    }

    /**
     * Removes the value for the fields {@code fields}.
     */
    public static void clear(final KibanaLogField... fields) {
        for (final KibanaLogField field : fields) {
            MDC.remove(field.getLogName());
        }
    }

    /**
     * Removes all values set for all fields.
     */
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
        final StringBuilder result = new StringBuilder();
        MDC.getCopyOfContextMap().forEach((key, value) -> {
            if (!key.equals(KibanaLogFieldNames.LOG_TYPE.getLogName())) {
                result.append(' ');
                result.append(key);
                result.append("=\"");
                result.append(value);
                result.append('"');
            }
        });

        return result.toString();
    }

    /**
     * Update log fields based on the {@code KibanaLogContext}.
     * <p>
     * See {@link KibanaLogFields#getContext()}.
     */
    public static void populateFromContext(final KibanaLogContext logContext) {
        if (logContext != null) {
            MDC.setContextMap(logContext.getContextMap());
        }
    }

    /**
     * Create a new log context for the current thread's kibana log fields.
     * <p>
     * See {@link KibanaLogContext#registerKibanaLogFieldsInThisThread()}.
     */
    public static KibanaLogContext getContext() {
        return new KibanaLogContext();
    }

}
