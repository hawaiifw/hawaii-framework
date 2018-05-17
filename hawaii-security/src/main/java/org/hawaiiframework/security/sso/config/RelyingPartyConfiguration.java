package org.hawaiiframework.security.sso.config;

import java.util.List;

/**
 * Class to hold the configuration properties for user management.
 */
public class RelyingPartyConfiguration {

    /**
     * The clientId for accessing SSO rest API's.
     */
    private String clientId;

    /**
     * The clientSecret for accessing SSO rest API's.
     */
    private String clientSecret;

    /**
     * The SSO base URL.
     */
    private List<String> redirectUrls;

    @SuppressWarnings("PMD.CommentRequired")
    public String getClientId() {
        return clientId;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getClientSecret() {
        return clientSecret;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public List<String> getRedirectUrls() {
        return redirectUrls;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setRedirectUrls(final List<String> redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    /**
     * Return the first configured redirect url.
     */
    public String getFirstRedirectUrl() {
        return redirectUrls.get(0);
    }
}
