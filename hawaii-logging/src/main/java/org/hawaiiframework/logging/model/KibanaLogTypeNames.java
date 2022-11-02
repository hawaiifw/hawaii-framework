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

/**
 * Enumeration for values of the Kibana Log Field LOG_TYPE.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public enum KibanaLogTypeNames {
    /**
     * The start of a transaction.
     */
    START,
    /**
     * The end of a transaction.
     */
    END,
    /**
     * The request body. (incoming)
     */
    REQUEST_BODY,
    /**
     * The response body. (returning)
     */
    RESPONSE_BODY,
    /**
     * The start of a call to another system.
     */
    CALL_START,
    /**
     * The call requests body.
     */
    CALL_REQUEST_BODY,
    /**
     * The call response body.
     */
    CALL_RESPONSE_BODY,
    /**
     * The end of a call to another system.
     */
    CALL_END
}
