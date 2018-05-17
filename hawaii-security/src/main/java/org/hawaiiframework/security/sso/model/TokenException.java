package org.hawaiiframework.security.sso.model;

import org.hawaiiframework.exception.HawaiiException;

/**
 * Exception thrown when handling a token fails.
 */
public class TokenException extends HawaiiException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 740670705944615330L;

    /**
     * Construct a new instance.
     *
     * @param message the error message
     */
    public TokenException(final String message) {
        super(message);
    }

    /**
     * Construct a new instance.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public TokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new instance.
     *
     * @param cause the cause of the exception
     */
    public TokenException(final Throwable cause) {
        super(cause);
    }

}
