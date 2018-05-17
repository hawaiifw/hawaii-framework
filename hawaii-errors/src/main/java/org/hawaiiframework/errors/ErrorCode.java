package org.hawaiiframework.errors;

import org.hawaiiframework.exception.ApiError;

import java.util.Objects;

/**
 * Defines API error codes.
 */
public enum ErrorCode implements ApiError {

    GENERAL_ERROR(1000, "Unspecified, unexpected error."),
    INVALID_PIN(1001, "An incorrect pin was supplied."),
    MAX_TRIES_EXCEEDED(1002, "The maximum number of attempts to verify an SMS challenge was exceeded."),
    INVALID_TOKEN(1003, "The supplied token was invalid."),
    USERNAME_NOT_AVAILABLE(1007, "A user with this username already exists."),
    BACKEND_HTTP_ERROR(1009, "Error executing backend http call."),
    USER_NOT_FOUND(1011, "User not found."),
    SINGLE_TOKEN_REQUIRED(1012, "A single token is required (got zero, or multiple)."),
    USER_ID_NOT_EMAIL_ADDRESS(1016, "UserId is not an email address."),
    INVALID_REQUEST_PASSWORD_RESET_OPTION(1017, "Invalid request password reset option."),
    NO_SEARCH_CRITERIA_PROVIDED(1021, "No search criteria provided. Searching without criteria is not supported."),
    SMS_SEND_ERROR(1027, "Error sending sms message."),
    INVITATION_NOT_FOUND(1029, "The invitation you are trying to use cannot be found."),
    WRONG_PASSWORD(1030, "Wrong password."),
    WRONG_PASSWORD_ONE_ATTEMPT_REMAINING(1031, "Wrong password, one attempt remaining."),
    WRONG_PASSWORD_NOW_LOCKED(1032, "Account is locked, you can try again in 15 minutes."),
    USER_ALREADY_CONFIRMED(1034, "The user is already confirmed."),
    WRONG_SUBJECT_FOR_CHANGE_USERNAME(1036, "The subject in the given token doesn't match the authenticated user."),
    USER_SESSION_NOT_FOUND(1043, "The user session was not found.");

    /**
     * The error code.
     */
    private final Integer errorCode;

    /**
     * The reason.
     */
    private final String reason;

    /**
     * Construct an instance with error code and reason.
     *
     * @param errorCode the error code
     * @param reason    the reason
     */
    ErrorCode(final Integer errorCode, final String reason) {
        this.errorCode = Objects.requireNonNull(errorCode);
        this.reason = reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorCode() {
        return errorCode.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReason() {
        return reason;
    }

}
