package org.hawaiiframework.errors.web;

import org.hawaiiframework.web.exception.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the requested resource is not available for the user performing the request.
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
