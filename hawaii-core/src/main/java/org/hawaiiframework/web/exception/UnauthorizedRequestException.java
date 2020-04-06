/*
 * Copyright 2015-2020 the original author or authors.
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

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the requested resource is not available for the user performing the request.
 *
 * @author Rutger Lubbers
 */
public class UnauthorizedRequestException extends HttpException {

    /**
     * The serial version uuid.
     */
    private static final long serialVersionUID = -2865532032411939533L;

    /**
     * Default constructor.
     */
    public UnauthorizedRequestException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructor with a message.
     *
     * @param message The message to set.
     */
    public UnauthorizedRequestException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructor with a message and an underlying cause.
     *
     * @param message The message to set.
     * @param cause   The cause.
     */
    public UnauthorizedRequestException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructor with an underlying cause.
     *
     * @param cause The cause.
     */
    public UnauthorizedRequestException(final Throwable cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}
