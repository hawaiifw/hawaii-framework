/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging.opentracing;

import org.hawaiiframework.logging.model.KibanaLogField;

import java.util.Arrays;

/**
 * Kibana Fields for OpenTracing.
 * All enumeration field names should come from the spec as defined by opentracing.
 *
 * Some fields are invented, {@code span.id} for instance. These fields should be marked as 'invented'.
 *
 * @see <a href="https://github.com/opentracing/specification/blob/master/semantic_conventions.md">
 *     https://github.com/opentracing/specification/blob/master/semantic_conventions.md</a>
 */
public enum OpenTracingKibanaLogField implements KibanaLogField {
    // Invented, not part of the spec.
    SPAN_ID("span.id"),
    // Invented, not part of the spec.
    TRACE_ID("trace.id");

    /**
     * The logging key for this MDC entry.
     */
    private final String fieldName;

    /**
     * @param fieldName the kibana log's field name..
     */
    OpenTracingKibanaLogField(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the Kibana log's field name.
     */
    @Override
    public String getLogName() {
        return fieldName;
    }

    /**
     * Lookup method that does not throw an exception if the specified
     * key is not found.
     *
     * @param key the key to look for
     * @return the MdcKey with the given name, or null
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static OpenTracingKibanaLogField fromKey(final String key) {
        OpenTracingKibanaLogField result = null;
        if (key != null) {
            result = Arrays.stream(values()).filter(fieldName -> fieldName.matches(key)).findAny().orElse(null);
        }
        return result;
    }
}
