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
package org.hawaiiframework.logging.logback;

/**
 * Constants for new lines and indentation used by the {@link KibanaLogEventEncoder}.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public final class CharacterConstants {

    /**
     * The system's line separator.
     */
    public static final String NEW_LINE = System.lineSeparator();

    /**
     * The indentation for multi line log events.
     */
    public static final String INDENT = "\t";

    /**
     * The constructor.
     */
    private CharacterConstants() {
        // Utility constructor.
    }
}
