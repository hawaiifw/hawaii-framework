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

package org.hawaiiframework.web.exception;

import java.io.Serial;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author Ivan Melotte
 * @since 2.0.0
 */
public class InternalServerErrorException extends HttpException {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = -7187613746044972447L;

    /**
     * Constructs a new {@code InternalServerErrorException}.
     */
    public InternalServerErrorException() {
        super(INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs a new {@code InternalServerErrorException} with the supplied message.
     *
     * @param message The message to set.
     */
    public InternalServerErrorException(final String message) {
        super(message, INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs a new {@code InternalServerErrorException} with the supplied message and {@link Throwable}.
     *
     * @param message The message to set.
     * @param cause   The cause.
     */
    public InternalServerErrorException(final String message, final Throwable cause) {
        super(message, cause, INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs a new {@code InternalServerErrorException} with the supplied {@link Throwable}.
     *
     * @param cause The cause.
     */
    public InternalServerErrorException(final Throwable cause) {
        super(cause, INTERNAL_SERVER_ERROR);
    }
}
