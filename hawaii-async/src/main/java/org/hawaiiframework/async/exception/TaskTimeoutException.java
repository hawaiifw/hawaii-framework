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

package org.hawaiiframework.async.exception;

import org.hawaiiframework.exception.HawaiiException;

import java.io.Serial;


/**
 * Exception thrown when a task has been timed-out.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class TaskTimeoutException extends HawaiiException {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 7124314347993711382L;

    /**
     * Constructs a new {@code TaskTimeoutException}.
     */
    public TaskTimeoutException() {
        super();
    }

    /**
     * Constructs a new {@code TaskTimeoutException} with the supplied {@code message}.
     *
     * @param message The exception message.
     */
    public TaskTimeoutException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code TaskTimeoutException} with the supplied {@code message} and {@code cause}.
     *
     * @param message The exception message.
     * @param cause   The exception cause.
     */
    public TaskTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code TaskTimeoutException} with the supplied {@code cause}.
     *
     * @param cause The cause.
     */
    public TaskTimeoutException(final Throwable cause) {
        super(cause);
    }
}
