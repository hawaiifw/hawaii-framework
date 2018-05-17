package org.hawaiiframework.security.sso.config;

import java.util.List;
import java.util.Optional;

/**
 * Class to hold the SSO server configuration properties.
 */
@SuppressWarnings("PMD.TooManyFields")
public class SsoConfiguration implements BackChannelSsoConfiguration {

    /**
     * The clientId for accessing SSO rest APIs.
     */
    private String clientId;

    /**
     * The clientSecret for accessing SSO rest APIs.
     */
    private String clientSecret;

    /**
     * The SSO base URL.
     */
    private String baseUrl;

    /**
     * The URL to retrieve the JWK (JSON Web Key) from.
     */
    private String encryptionKeysEndpoint;

    /**
     * The URL to obtain CSRF tokens from.
     */
    private String csrfEndpoint;

    /**
     * The URL the SSO's login form posts to.
     */
    private String loginFormPostEndpoint;

    /**
     * The URL a client uses to obtain authorization (from the user).
     */
    private String authorizeEndpoint;

    /**
     * The URL to retrieve the user info from.
     */
    private String userInfoEndpoint;

    /**
     * The URL to retrieve an upgrade session token from.
     */
    private String sessionUpgradeTokenEndpoint;

    /**
     * The URL to request tokens directly.
     */
    private String tokenEndpoint;

    /**
     * The URL to retrieve a token by its jti.
     */
    private String confirmableTokenEndpoint;

    /**
     * The URL to upgrade a session.
     */
    private String upgradeSessionEndpoint;

    /**
     * The URL to check if a session is alive.
     */
    private String userSessionEndpoint;

    /**
     * The URL for retrieving or deleting user sessions..
     */
    private String userSessionsEndpoint;

    /**
     * The URL to retrieve the subject for merge.
     */
    private String sessionUpgradeTokenByJTIEndpoint;

    /**
     * The list of allowed client ids.
     */
    private List<String> allowedClientIds;

    @Override @SuppressWarnings("PMD.CommentRequired")
    public String getClientId() {
        return clientId;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    @Override @SuppressWarnings("PMD.CommentRequired")
    public String getClientSecret() {
        return clientSecret;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @SuppressWarnings({"PMD.CommentRequired", "PMD.LawOfDemeter"})
    public String getBaseUrl() {
        return Optional.ofNullable(baseUrl).orElse("");
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getEncryptionKeysEndpoint() {
        return getBaseUrl() + encryptionKeysEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setEncryptionKeysEndpoint(final String encryptionKeysEndpoint) {
        this.encryptionKeysEndpoint = encryptionKeysEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getUserInfoEndpoint() {
        return getBaseUrl() + userInfoEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setUserInfoEndpoint(final String userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getSessionUpgradeTokenEndpoint() {
        return getBaseUrl() + sessionUpgradeTokenEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setSessionUpgradeTokenEndpoint(final String sessionUpgradeTokenEndpoint) {
        this.sessionUpgradeTokenEndpoint = sessionUpgradeTokenEndpoint;
    }

    /**
     * Getter for token endpoint.
     *
     * @return the token endpoint
     */
    public String getTokenEndpoint() {
        return getBaseUrl() + tokenEndpoint;
    }

    /**
     * Getter for token endpoint.
     *
     * @param tokenEndpoint the token endpoint
     */
    public void setTokenEndpoint(final String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    /**
     * Getter for the confirmable token endpoint.
     *
     * @return the confirmable token endpoint
     */
    public String getConfirmableTokenEndpoint() {
        return getBaseUrl() + confirmableTokenEndpoint;
    }

    /**
     * Setter for the confirmable token endpoint.
     *
     * @param confirmableTokenEndpoint the confirmable token endpoint
     */
    public void setConfirmableTokenEndpoint(final String confirmableTokenEndpoint) {
        this.confirmableTokenEndpoint = confirmableTokenEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getUpgradeSessionEndpoint() {
        return getBaseUrl() + upgradeSessionEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setUpgradeSessionEndpoint(final String upgradeSessionEndpoint) {
        this.upgradeSessionEndpoint = upgradeSessionEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getCsrfEndpoint() {
        return getBaseUrl() + csrfEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setCsrfEndpoint(final String csrfEndpoint) {
        this.csrfEndpoint = csrfEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getLoginFormPostEndpoint() {
        return getBaseUrl() + loginFormPostEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setLoginFormPostEndpoint(final String loginFormPostEndpoint) {
        this.loginFormPostEndpoint = loginFormPostEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getAuthorizeEndpoint() {
        return getBaseUrl() + authorizeEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setAuthorizeEndpoint(final String authorizeEndpoint) {
        this.authorizeEndpoint = authorizeEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public List<String> getAllowedClientIds() {
        return allowedClientIds;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setAllowedClientIds(final List<String> allowedClientIds) {
        this.allowedClientIds = allowedClientIds;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getUserSessionEndpoint() {
        return getBaseUrl() + userSessionEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setUserSessionEndpoint(final String userSessionEndpoint) {
        this.userSessionEndpoint = userSessionEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getUserSessionsEndpoint() {
        return getBaseUrl() + userSessionsEndpoint;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setUserSessionsEndpoint(final String userSessionsEndpoint) {
        this.userSessionsEndpoint = userSessionsEndpoint;
    }

    /**
     * Get the upgradeSessionSubjectEndpoint.
     *
     * @return The upgradeSessionSubjectEndpoint.
     **/
    public String getSessionUpgradeTokenByJTIEndpoint() {
        return getBaseUrl() + sessionUpgradeTokenByJTIEndpoint;
    }

    /**
     * Set the upgradeSessionSubjectEndpoint property.
     *
     * @param sessionUpgradeTokenByJTIEndpoint The upgradeSessionSubjectEndpoint property.
     **/
    public void setSessionUpgradeTokenByJTIEndpoint(final String sessionUpgradeTokenByJTIEndpoint) {
        this.sessionUpgradeTokenByJTIEndpoint = sessionUpgradeTokenByJTIEndpoint;
    }
}
