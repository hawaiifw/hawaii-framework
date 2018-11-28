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

import java.util.Arrays;

/**
 * This enum represents keys for data that is stored in the logging MDC.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public enum KibanaLogFieldNames implements KibanaLogField {
    SESSION_ID("session_id"),

    TX_ID("tx_id"),
    TX_TYPE("tx_type"),
    // Deprecated, left in place for current usage of kibana.
    TX_DURATION("tx_duration"),

    REQUEST_ID("req_id"),
    REQUEST_DURATION("req_duration"),

    CALL_ID("call_id"),
    CALL_TYPE("call_type"),
    CALL_DURATION("call_duration"),
    CALL_RESULT("call_result"),

    TASK_ID("task_id"),

    URI("uri"),
    METHOD("method"),
    USER("user_name"),

    HTTP_STATUS("tx_status"),

    CLIENT_IP("client_ip"),

    LOG_TYPE("log_type"),

    THREAD("thread"),
    LEVEL("level"),
    TIMESTAMP("timestamp"),
    LOG_LOCATION("log_loc"),

    MESSAGE("message");

    /**
     * The logging key for this MDC entry.
     */
    private final String fieldName;

    /**
     * @param fieldName the kibana log's field name..
     */
    KibanaLogFieldNames(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the kibana log's field name.
     */
    @Override public String getLogName() {
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
    public static KibanaLogFieldNames fromKey(final String key) {
        KibanaLogFieldNames result = null;
        if (key != null) {
            result = Arrays.stream(values()).filter(fieldName -> fieldName.matches(key)).findAny().orElse(null);
        }
        return result;
    }

}
