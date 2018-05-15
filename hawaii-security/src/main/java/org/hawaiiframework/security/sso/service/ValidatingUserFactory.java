package org.hawaiiframework.security.sso.service;

import org.hawaiiframework.security.sso.model.Credentials;
import org.hawaiiframework.security.sso.model.HawaiiSsoUserDetails;

/**
 * Factory to create a user object from the ID and AT tokens.
 */
public interface ValidatingUserFactory {

    /**
     * Validate the credentials and create a user from the credentials.
     *
     * @param credentials the ID and AT credentials.
     * @return the user or <code>null</code> if the user cannot be created.
     */
    HawaiiSsoUserDetails validateUser(Credentials credentials);
}
