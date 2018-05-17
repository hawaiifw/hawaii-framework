package org.hawaiiframework.security.sso.model;

/**
 * A wrapper around a JWT to retrieve IdToken specific claims.
 */
public class IdToken extends TokenWrapper {

    /**
     * The constructor.
     */
    public IdToken(final String tokenValue) {
        super(tokenValue);
    }

    /**
     * Get the hash of the access token (<code>at_hash</code>).
     */
    public String getAccessTokenHash() {
        return getStringClaim("at_hash");
    }

    /**
     * Get the <code>nonce</code>.
     */
    public String getNonce() {
        return getStringClaim("nonce");
    }

    /**
     * Get the <code>user_session_id</code>.
     */
    public String getUserSessionId() {
        return getStringClaim("user_session_id");
    }

}
