package org.hawaiiframework.security.sso.web.input;

/**
 * Input object for user's Credentials.
 */
public class CredentialsInput {

    /**
     * The access token.
     */
    private String accessToken;

    /**
     * The id token.
     */
    private String idToken;

    /**
     * The nonce the ID token is generated with.
     */
    private String nonce;

    @SuppressWarnings("PMD.CommentRequired")
    public String getAccessToken() {
        return accessToken;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getIdToken() {
        return idToken;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getNonce() {
        return nonce;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }
}
