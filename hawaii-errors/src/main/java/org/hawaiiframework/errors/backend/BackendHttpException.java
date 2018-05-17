package org.hawaiiframework.errors.backend;

import org.hawaiiframework.errors.ErrorCode;
import org.hawaiiframework.exception.ApiException;

/**
 * This exception is thrown when an error occurs in a http call executed against some backend.
 */
public class BackendHttpException extends ApiException {

    /**
     * Generated bij IntelliJ Idea.
     */
    private static final long serialVersionUID = 994617360130358652L;

    /**
     * Construct an instance with a response object.
     */
    public BackendHttpException() {
        this(null, null);
    }

    /**
     * Construct an instance with a message.
     *
     * @param message the message
     */
    public BackendHttpException(final String message) {
        this(null, message);
    }

    /**
     * Construct an instance with a cause and a message.
     *
     * @param orig the cause
     * @param message the message
     */
    public BackendHttpException(final Throwable orig, final String message) {
        super(ErrorCode.BACKEND_HTTP_ERROR, orig, message);
    }

}
