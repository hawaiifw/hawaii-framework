package org.hawaiiframework.security.sso.parser;

/**
 * Verify password response.
 */
public class VerifyPasswordErrorResponse {

    /**
    *   The error.
    **/
    private VerifyPasswordError error;

    /**
     * Default constructor.
     */
    public VerifyPasswordErrorResponse() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Constructor.
     * @param error The error property.
     */
    public VerifyPasswordErrorResponse(final VerifyPasswordError error) {
        this.error = error;
    }

    /**
     * Get the error.
     *
     * @return The error.
     **/
    public VerifyPasswordError getError() {
        return error;
    }

    /**
     * Set the error property.
     *
     * @param error The error property.
     **/
    public void setError(final VerifyPasswordError error) {
        this.error = error;
    }
}
