package nl.vodafoneziggo.ponoi.common.sms.exception;

import nl.vodafoneziggo.ponoi.exception.ErrorCode;
import org.hawaiiframework.exception.ApiException;

/**
 * An exception that is thrown when sending the text message fails (for example, due to a MOSA exception).
 */
public class SmsSendException extends ApiException {

    private static final long serialVersionUID = -788891493284004033L;

    /**
     * Constructor for SmsSendException.
     * @param message String describing the exception
     */
    public SmsSendException(final String message) {
        super(ErrorCode.SMS_SEND_ERROR, message);
    }

    /**
     * Constructor for SmsSendException.
     * @param throwable throwable that caused this exception
     */
    public SmsSendException(final Throwable throwable) {
        super(ErrorCode.SMS_SEND_ERROR, throwable);
    }
}
