package org.hawaiiframework.security.sso.parser;

import org.springframework.stereotype.Component;

/**
 * Parser for a password error response from SSO.
 */
@Component
public class VerifyPasswordErrorResponseParser extends BaseUnauthorizedResponseParser<VerifyPasswordErrorResponse> {

    /**
     * Construct an instance for {@link VerifyPasswordErrorResponse}.
     */
    public VerifyPasswordErrorResponseParser() {
        super(VerifyPasswordErrorResponse.class);
    }

}
