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
 * Interface that allows client projects to use their own log fields.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public interface KibanaLogField {

    /**
     * Get the name with which this field will appear in the log.
     *
     * @return the log name of the field
     */
    String getLogName();

    /**
     * Check if a key matches.
     *
     * @param key the key to match
     * @return true if not null and if the supplied key equals this key.
     */
    default boolean matches(String key) {
        return key != null && key.equalsIgnoreCase(getLogName());
    }
}
