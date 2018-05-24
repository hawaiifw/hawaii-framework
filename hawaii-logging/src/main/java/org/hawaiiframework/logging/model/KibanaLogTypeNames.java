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
    START,
    END,
    REQUEST_BODY,
    RESPONSE_BODY,
    CALL_START,
    CALL_REQUEST_BODY,
    CALL_RESPONSE_BODY,
    CALL_END
}
