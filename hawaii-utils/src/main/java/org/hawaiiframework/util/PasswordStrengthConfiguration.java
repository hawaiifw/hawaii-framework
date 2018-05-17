package org.hawaiiframework.util;

import org.springframework.stereotype.Component;

/**
 * Configuration for password checking.
 * <p>
 * The default for @code{minimumPasswordStrength} is @code{'1'} and @code{tooWeakErrorCode} is @code{'weak_password'}.
 */
@Component
public class PasswordStrengthConfiguration {

    /**
     * The minimum password strength score.
     */
    private int minimumPasswordStrength = 1;

    /**
     * The error code to use when rejecting a password.
     */
    private String tooWeakErrorCode = "weak_password";
    /**
     * Name of the file containing the dutch dictionary.
     * Should be in resources folder.
     */
    private String dutchDictionaryFileName;

    /**
     * The minimum password length.
     */
    private int minimumPasswordLength = 8;

    @SuppressWarnings("PMD.CommentRequired")
    public int getMinimumPasswordStrength() {
        return minimumPasswordStrength;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMinimumPasswordStrength(final int minimumPasswordStrength) {
        this.minimumPasswordStrength = minimumPasswordStrength;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getTooWeakErrorCode() {
        return tooWeakErrorCode;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setTooWeakErrorCode(final String tooWeakErrorCode) {
        this.tooWeakErrorCode = tooWeakErrorCode;
    }

    /**
     * Get the dutchDictionaryFileName.
     *
     * @return The dutchDictionaryFileName.
     **/
    public String getDutchDictionaryFileName() {
        return dutchDictionaryFileName;
    }

    /**
     * Set the dutchDictionaryFileName property.
     *
     * @param dutchDictionaryFileName The dutchDictionaryFileName property.
     **/
    public void setDutchDictionaryFileName(final String dutchDictionaryFileName) {
        this.dutchDictionaryFileName = dutchDictionaryFileName;
    }

    /**
     * Gets minimumPasswordLength .
     *
     * @return value of minimumPasswordLength .
     */
    public int getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    /**
     * Sets minimumPasswordLength to the given value.
     *
     * @param minimumPasswordLength .
     */
    public void setMinimumPasswordLength(final int minimumPasswordLength) {
        this.minimumPasswordLength = minimumPasswordLength;
    }
}
