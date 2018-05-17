package org.hawaiiframework.security.sso.web.resource;

/**
 * The response to the verify token "call".
 */
public class TokenVerificationResource {

    /**
     * The SSO's UserSessionId.
     */
    private String userSessionId;

    @SuppressWarnings("PMD.CommentRequired")
    public String getUserSessionId() {
        return userSessionId;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setUserSessionId(final String userSessionId) {
        this.userSessionId = userSessionId;
    }
}
