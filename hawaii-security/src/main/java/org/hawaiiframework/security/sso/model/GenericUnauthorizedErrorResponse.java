package org.hawaiiframework.security.sso.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class represents an error resource as returned from the SSO server if a generic unauthorized exception is thrown.
 *
 * The generic error information contains just an error ('unauthorized') and a description.
 */
@Component
public class GenericUnauthorizedErrorResponse {

    /**
     * String constant for the no attempts left error.
     */
    private static final String NO_ATTEMPTS_LEFT = "No attempts left.";

    /**
     * The error.
     */
    private String error;

    /**
     * The error description.
     */
    private String errorDescription;

    /**
     * Getter for the error.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Setter for the error.
     *
     * @param error the error
     */
    public void setError(final String error) {
        this.error = error;
    }

    /**
     * Getter for the description.
     *
     * @return the description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Setter for the description.
     *
     * @param errorDescription the description
     */
    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * Check if this is a max tries exceeded error.
     *
     * @return true if it is
     */
    public boolean isMaxTriesExceeded() {
        return NO_ATTEMPTS_LEFT.equals(errorDescription);
    }

    /**
     * Check if this object is valid.
     *
     * Valid means that this object correctly represents the response that was parsed.
     *
     * @return true if {@link #error} is not blank
     */
    public boolean isValid() {
        return StringUtils.isNotBlank(error);
    }
}
