package org.hawaiiframework.security.sso.service;

import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.DefaultHawaiiSsoUserDetails;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The default implementation of <code>HawaiiSsoUserDetailsService</code>.
 */
public class DefaultHawaiiSsoUserDetailsService implements HawaiiSsoUserDetailsService<DefaultHawaiiSsoUserDetails> {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHawaiiSsoUserDetailsService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultHawaiiSsoUserDetails getUserDetailsFromToken(final AccessToken accessToken, final JSONObject userInfoResponse) {
        LOGGER.debug("Creating new user details for '{}'.", accessToken.getSubject());
        return new DefaultHawaiiSsoUserDetails(accessToken.getSubject());
    }

    /**
     * Not implemented yet.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return null;
    }
}
