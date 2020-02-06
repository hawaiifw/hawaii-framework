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
import java.util.HashMap;
import java.util.Map;

/**
 * Kibana Fields for OpenTracing.
 * All enumeration field names should come from the spec as defined by opentracing.
 * <p>
 * Some fields are invented, {@code span.id} for instance. These fields should be marked as 'invented'.
 *
 * @see <a href="https://github.com/opentracing/specification/blob/master/semantic_conventions.md">
 * https://github.com/opentracing/specification/blob/master/semantic_conventions.md</a>
 */
public enum OpenTracingKibanaLogField implements KibanaLogField {
    // Invented, not part of the spec.
    SPAN_ID("span.id"),
    // Invented, not part of the pec
    SPAN_OPERATION_NAME("span.operation_name"),
    // Invented, not part of the spec.
    TRACE_ID("trace.id"),
    // http method
    HTTP_METHOD("http.method"),
    // http status_code
    HTTP_STATUS_CODE("http.status_code"),
    // http url
    HTTP_URL("http.url"),
    // peer.address
    PEER_ADDRESS("peer.address"),
    // peer.hostname
    PEER_HOSTNAME("peer.hostname"),
    // peer.ipv4
    PEER_IPV4("peer.ipv4"),
    // peer.ipv6
    PEER_IPV6("peer.ipv6"),
    // peer.port
    PEER_PORT("peer.port"),
    //peer.service
    PEER_SERVICE("peer.service");

    private static final Map<String, OpenTracingKibanaLogField> LOOKUP = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(field -> LOOKUP.put(field.fieldName, field));
    }

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
     * Lookup method that does not throw an exception if the specified key is not found.
     *
     * @param key the key to look for.
     * @return the LogField with the given name, or {@code null}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static OpenTracingKibanaLogField fromKey(final String key) {
        return LOOKUP.getOrDefault(key, null);
    }
}
