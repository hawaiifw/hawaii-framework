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
  /** The session id. */
  SESSION_ID("session_id"),

  /** The host name. */
  HOST_NAME("host_name"),

  /** The software version. */
  SOFTWARE_VERSION("software_version"),

  /** The request id. */
  REQUEST_ID("req_id"),
  /** The request duration. */
  REQUEST_DURATION("req_duration"),

  /** The business transaction id. */
  BUSINESS_TX_ID("business_tx_id"),

  /** The transaction id. */
  TX_ID("tx_id"),
  /** The transaction type. */
  TX_TYPE("tx_type"),
  /** The transaction request ip address. */
  TX_REQUEST_IP("tx_request_ip"),

  /** The transaction request method. */
  TX_REQUEST_METHOD("tx_request_method"),
  /** The transaction request uri. */
  TX_REQUEST_URI("tx_request_uri"),
  /** The transaction request size. */
  TX_REQUEST_SIZE("tx_request_size"),

  /** The transaction request headers. */
  TX_REQUEST_HEADERS("tx_request_headers"),
  /** The transaction request body. */
  TX_REQUEST_BODY("tx_request_body"),

  /** The transaction response size. */
  TX_RESPONSE_SIZE("tx_response_size"),
  /** The transaction response headers. */
  TX_RESPONSE_HEADERS("tx_response_headers"),
  /** The transaction response body. */
  TX_RESPONSE_BODY("tx_response_body"),

  /** The transaction duration. */
  TX_DURATION("tx_duration"),
  /** The transaction status. */
  TX_STATUS("tx_status"),

  /** The HTTP status. */
  HTTP_STATUS("http_status"),

  /** The call id. */
  CALL_ID("call_id"),
  /** The call type. */
  CALL_TYPE("call_type"),

  /** The call request method. */
  CALL_REQUEST_METHOD("call_request_method"),
  /** The call request uri. */
  CALL_REQUEST_URI("call_request_uri"),
  /** The call request size. */
  CALL_REQUEST_SIZE("call_request_size"),

  /** The call request headers. */
  CALL_REQUEST_HEADERS("call_request_headers"),
  /** The call request body. */
  CALL_REQUEST_BODY("call_request_body"),

  /** The call response size. */
  CALL_RESPONSE_SIZE("call_response_size"),
  /** The call response headers. */
  CALL_RESPONSE_HEADERS("call_response_headers"),
  /** The call response body. */
  CALL_RESPONSE_BODY("call_response_body"),

  /** The call duration. */
  CALL_DURATION("call_duration"),
  /** The call status. */
  CALL_STATUS("call_status"),

  /** The task id. */
  TASK_ID("task_id"),

  /** The username. */
  USER_NAME("user_name"),

  /** THe log type. */
  LOG_TYPE("log_type"),

  /** The thread. */
  THREAD("thread"),
  /** The level. */
  LEVEL("level"),
  /** The timestamp. */
  TIMESTAMP("timestamp"),
  /** The log location. */
  LOG_LOCATION("log_loc"),

  /** The log message. */
  MESSAGE("message");

  /** The logging key for this MDC entry. */
  private final String fieldName;

  /**
   * The constructor.
   *
   * @param fieldName the kibana log's field name.
   */
  KibanaLogFieldNames(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public String getLogName() {
    return fieldName;
  }

  /**
   * Lookup method that does not throw an exception if the specified key is not found.
   *
   * @param key the key to look for
   * @return the MdcKey with the given name, or null
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public static KibanaLogFieldNames fromKey(String key) {
    KibanaLogFieldNames result = null;
    if (key != null) {
      result =
          Arrays.stream(values())
              .filter(fieldName -> fieldName.matches(key))
              .findAny()
              .orElse(null);
    }
    return result;
  }
}
