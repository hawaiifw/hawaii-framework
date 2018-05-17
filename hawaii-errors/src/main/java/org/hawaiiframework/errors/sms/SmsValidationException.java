package org.hawaiiframework.errors.sms;

/**
 * An exception that is thrown when validation of @code{SmsMessage} fails.
 */
public class SmsValidationException extends Exception {

    private static final long serialVersionUID = -688991492284005033L;

    /**
     * Constructor for SmsValidationException.
     * @param message String describing the exception
     */
    public SmsValidationException(final String message) {
        super(message);
    }

    /**
     * Constructor for SmsValidationException.
     * @param throwable throwable that caused this exception
     */
    public SmsValidationException(final Throwable throwable) {
        super(throwable);
    }
}


