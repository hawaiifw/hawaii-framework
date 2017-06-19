/*
 * Copyright 2015-2017 the original author or authors.
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

package org.hawaiiframework.exception;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class HawaiiException extends RuntimeException {

    /**
     * Constructs a new {@code HawaiiException}.
     */
    public HawaiiException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Constructs a new {@code HawaiiException} with the supplied message.
     */
    public HawaiiException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code HawaiiException} with the supplied message and {@link Throwable}.
     */
    public HawaiiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code HawaiiException} with the supplied {@link Throwable}.
     */
    public HawaiiException(final Throwable cause) {
        super(cause);
    }

    /**
     * Returns the first {@link HawaiiException} encountered in the chain of exception causes,
     * or the original throwable if no {@link HawaiiException} can be found.
     *
     * @param throwable the Throwable to examine, must not be <code>null</code>
     * @return a HawaiiException, or throwable
     */
    public static Throwable getCausingHawaiiException(final Throwable throwable) {
        Throwable cause = getCause(throwable.getCause());
        if (cause == null) {
            cause = throwable;
        }
        return cause;
    }

    /**
     * Recursive method to find the cause of a Throwable, if that is a {@link HawaiiException}.
     *
     * @param throwable the throwable
     * @return throwable, or null
     */
    private static Throwable getCause(final Throwable throwable) {
        final Throwable cause;
        if (throwable == null) {
            cause = null;
        } else if (throwable instanceof HawaiiException) {
            cause = throwable;
        } else {
            cause = getCause(throwable.getCause());
        }
        return cause;
    }

}
