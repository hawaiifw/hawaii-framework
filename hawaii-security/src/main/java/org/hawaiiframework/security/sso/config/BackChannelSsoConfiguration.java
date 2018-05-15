package org.hawaiiframework.security.sso.config;

/**
 * Interface that provides just the client id / shared secret parts of the SSO configuration properties.
 *
 */
public interface BackChannelSsoConfiguration {

    /**
     * Get the client id.
     *
     * @return the client id
     */
    String getClientId();

    /**
     * Get the client secret.
     *
     * @return the client secret
     */
    String getClientSecret();
}
