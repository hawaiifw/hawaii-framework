package org.hawaiiframework.security.sso.model;

import com.nimbusds.jwt.JWT;
import org.hawaiiframework.security.sso.token.TokenInput;

/**
 * A wrapper around a JWT to retrieve AccessToken specific claims.
 */
public class AccessToken extends TokenWrapper {

    /**
     * The constructor.
     */
    public AccessToken(final String tokenValue) {
        super(tokenValue);
    }

    /**
     * Construct an access token from an existing JWT.
     *
     * @param jwt the JWT
     */
    public AccessToken(final JWT jwt) {
        super(jwt);
    }

    /**
     * Construct an access token based on a tokenInput object.
     *
     * @param tokenInput the token input.
     */
    public AccessToken(final TokenInput tokenInput) {
        super(tokenInput.getTokenString());
    }

    /**
     * Get the client (authorized party) this token is created for. This is the <code>azp</code> claim.
     */
    public String getAuthorizedParty() {
        return getStringClaim("azp");
    }

}
