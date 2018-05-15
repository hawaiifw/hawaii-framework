package org.hawaiiframework.security.sso.model;

import com.nimbusds.jwt.JWT;
import org.hawaiiframework.security.sso.token.TokenResponse;

/**
 * A wrapper around a JWT to retrieve AccessToken specific claims.
 */
public class EmailToken extends TokenWrapper {

    /**
     * Constructor.
     */
    public EmailToken(final TokenResponse response) {
        super(response.getAccessToken());
    }

    /**
     * The constructor.
     */
    public EmailToken(final String tokenValue) {
        super(tokenValue);
    }

    /**
     * Construct an access token from an existing JWT.
     *
     * @param jwt the JWT
     */
    public EmailToken(final JWT jwt) {
        super(jwt);
    }

    /**
     * Get the client (authorized party) this token is created for. This is the <code>azp</code> claim.
     */
    public String getAuthorizedParty() {
        return getStringClaim("azp");
    }


    /**
     * Get the new username. This is the <code>email</code> claim.
     */
    public String getEmail() {
        return getStringClaim("email");
    }

}
