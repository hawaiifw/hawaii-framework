package org.hawaiiframework.security.sso.parser;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Parser for a password error response from SSO.
 */
@Component
public class VerifyPasswordErrorDescriptionResponseParser {


    /**
     * Parses the body string to a VerifyPasswordErrorResponse object.
     *
     * @param body the body to parse.
     * @return a VerifyPasswordErrorResponse object. If the body could be parsed to a VerifyPasswordErrorResponse, the
     * error of this object will contain an error value, null in other cases.
     */
    public VerifyPasswordErrorResponse parse(final String body) {
        final VerifyPasswordErrorResponse response = new VerifyPasswordErrorResponse();
        final JSONObject jsonObject = new JSONObject(body);
        final String errorDescription = jsonObject.optString("error_description");
        switch (errorDescription) {
            case "Bad credentials":
                response.setError(VerifyPasswordError.BAD_CREDENTIALS);
                break;
            case "account.almost-locked":
                response.setError(VerifyPasswordError.ALMOST_LOCKED);
                break;
            case "User account is locked":
                response.setError(VerifyPasswordError.LOCKED);
                break;
            default:
                response.setError(null);
                break;
        }
        return response;
    }

}
