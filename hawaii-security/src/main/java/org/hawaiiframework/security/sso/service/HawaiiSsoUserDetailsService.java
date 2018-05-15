package org.hawaiiframework.security.sso.service;

import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.HawaiiSsoUserDetails;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * This is the Hawaii SSO implementation of Spring Security's UserDetailsService.
 *
 * @param <T> The type of user details the service creates.
 */
public interface HawaiiSsoUserDetailsService<T extends HawaiiSsoUserDetails> extends UserDetailsService {

    /**
     * Retrieve the user details for the given @code{accessToken} and @code{userInfoResponse}.
     */
    T getUserDetailsFromToken(AccessToken accessToken, JSONObject userInfoResponse);
}
