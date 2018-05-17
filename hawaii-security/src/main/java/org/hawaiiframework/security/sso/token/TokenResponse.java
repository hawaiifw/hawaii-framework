package org.hawaiiframework.security.sso.token;

/**
 * This class represents the output from the SSO /token endpoint.
 */
public class TokenResponse {

    /**
     * The access token that was issued.
     */
    private String accessToken;

    /**
     * The token type, e.g. <code>Bearer</code>.
     */
    private String tokenType;

    /**
     * The id token that was issued.
     */
    private String idToken;

    /**
     * The number of seconds in which the token will expire.
     */
    private Long expiresIn;

    /**
     * The scopes that were granted, separated by spaces.
     */
    private String scope;

    /**
     * Getter for the access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Setter for the access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Getter for the token type.
     *
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Setter for the token type.
     *
     * @param tokenType the token type
     */
    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Getter for the id token.
     *
     * @return the id token
     */
    public String getIdToken() {
        return idToken;
    }

    /**
     * Setter for the id token.
     *
     * @param idToken the id token
     */
    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }

    /**
     * Getter for the token expiry time.
     *
     * @return the token expiry time
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Setter for the token expiry time.
     *
     * @param expiresIn the token expiry time
     */
    public void setExpiresIn(final Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Getter for the scopes.
     *
     * @return the scopes
     */
    public String getScope() {
        return scope;
    }

    /**
     * Setter for the scopes.
     *
     * @param scope the scopes
     */
    public void setScope(final String scope) {
        this.scope = scope;
    }

}
