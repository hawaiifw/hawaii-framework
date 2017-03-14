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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.HawaiiException;
import org.springframework.http.HttpStatus;

import static java.util.Objects.requireNonNull;

/**
 * @author Ivan Melotte
 * @since 2.0.0
 */
public class HttpException extends HawaiiException {

    private final HttpStatus httpStatus;

    /**
     * Constructs a new {@code HttpException} with the supplied {@link HttpStatus}.
     */
    public HttpException(final HttpStatus httpStatus) {
        this.httpStatus = requireNonNull(httpStatus);
    }

    /**
     * Constructs a new {@code HttpException} with the supplied message and {@link HttpStatus}.
     */
    public HttpException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = requireNonNull(httpStatus);
    }

    /**
     * Constructs a new {@code HttpException} with the supplied message, {@link Throwable} and {@link HttpStatus}.
     */
    public HttpException(final String message, final Throwable cause, final HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = requireNonNull(httpStatus);
    }

    /**
     * Constructs a new {@code HttpException} with the supplied {@link Throwable} and {@link HttpStatus}.
     */
    public HttpException(final Throwable cause, final HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = requireNonNull(httpStatus);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
