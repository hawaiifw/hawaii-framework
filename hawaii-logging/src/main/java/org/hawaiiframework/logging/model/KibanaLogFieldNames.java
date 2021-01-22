/*
 * Copyright 2015-2019 the original author or authors.
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

    HOST_NAME("host_name"),
    SOFTWARE_VERSION("software_version"),

    REQUEST_ID("req_id"),
    REQUEST_DURATION("req_duration"),


    TX_ID("tx_id"),
    TX_TYPE("tx_type"),
    TX_REQUEST_IP("tx_request_ip"),

    TX_REQUEST_METHOD("tx_request_method"),
    TX_REQUEST_URI("tx_request_uri"),
    TX_REQUEST_SIZE("tx_request_size"),

    TX_REQUEST_HEADERS("tx_request_headers"),
    TX_REQUEST_BODY("tx_request_body"),

    TX_RESPONSE_SIZE("tx_response_size"),
    TX_RESPONSE_HEADERS("tx_response_headers"),
    TX_RESPONSE_BODY("tx_response_body"),

    TX_DURATION("tx_duration"),
    TX_STATUS("tx_status"),

    CALL_ID("call_id"),
    CALL_TYPE("call_type"),

    CALL_REQUEST_METHOD("call_request_method"),
    CALL_REQUEST_URI("call_request_uri"),
    CALL_REQUEST_SIZE("call_request_size"),

    CALL_REQUEST_HEADERS("call_request_headers"),
    CALL_REQUEST_BODY("call_request_body"),

    CALL_RESPONSE_SIZE("call_response_size"),
    CALL_RESPONSE_HEADERS("call_response_headers"),
    CALL_RESPONSE_BODY("call_response_body"),

    CALL_DURATION("call_duration"),
    CALL_STATUS("call_status"),

    TASK_ID("task_id"),


    USER_NAME("user_name"),

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
