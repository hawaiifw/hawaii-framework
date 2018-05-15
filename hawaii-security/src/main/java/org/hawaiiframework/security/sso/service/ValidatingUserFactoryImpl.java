package org.hawaiiframework.security.sso.service;

import org.hawaiiframework.cache.Cache;
import org.hawaiiframework.cache.CacheServiceException;
import org.hawaiiframework.errors.web.UnauthorizedRequestException;
import org.hawaiiframework.security.sso.config.SsoConfiguration;
import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.Credentials;
import org.hawaiiframework.security.sso.model.HawaiiSsoUserDetails;
import org.hawaiiframework.security.sso.model.IdToken;
import org.hawaiiframework.time.HawaiiTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of the <code>ValidatingUserFactory</code>.
 */
@Component
public class ValidatingUserFactoryImpl implements ValidatingUserFactory {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatingUserFactoryImpl.class);

    /**
     * The sso service.
     */
    private final HawaiiSsoService hawaiiSsoService;

    /**
     * The user management service.
     */
    private final HawaiiSsoUserDetailsService<?> userDetailsService;

    /**
     * The sso configuration.
     */
    private final SsoConfiguration ssoConfiguration;

    /**
     * The token verification service.
     */
    private final TokenVerificationService tokenVerificationService;

    /**
     * The user cache.
     */
    private final Cache<HawaiiSsoUserDetails> userCache;

    /**
     * The constructor.
     */
    @Autowired
    public ValidatingUserFactoryImpl(
            final HawaiiSsoService hawaiiSsoService,
            final HawaiiSsoUserDetailsService<?> userDetailsService,
            final SsoConfiguration ssoConfiguration,
            final TokenVerificationService tokenVerificationService,
            final Cache<HawaiiSsoUserDetails> userCache) {
        this.hawaiiSsoService = requireNonNull(hawaiiSsoService);
        this.userDetailsService = requireNonNull(userDetailsService);
        this.ssoConfiguration = requireNonNull(ssoConfiguration);
        this.tokenVerificationService = requireNonNull(tokenVerificationService);
        this.userCache = requireNonNull(userCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HawaiiSsoUserDetails validateUser(final Credentials credentials) {
        requireNonNull(credentials);

        HawaiiSsoUserDetails hawaiiSsoUserDetails = null;


        final IdToken idToken = credentials.getIdToken();
        if (idToken == null) {
            try {
                hawaiiSsoUserDetails = getFromCache(credentials.getAccessToken());
            } catch (CacheServiceException e) {
                LOGGER.error("Could not retrieve accessToken UserDetails for " + credentials.getAccessTokenAsString() + " from cache.");
            }
        }

        if (hawaiiSsoUserDetails == null) {
            hawaiiSsoUserDetails = createUserDetails(credentials);
        }

        return hawaiiSsoUserDetails;
    }


    private HawaiiSsoUserDetails createUserDetails(final Credentials credentials) {
        final boolean valid = credentials.validate(tokenVerificationService, new HawaiiTime(), ssoConfiguration.getAllowedClientIds());

        if (!valid) {
            LOGGER.trace("Credentials are invalid.");
            return null;
        }

        HawaiiSsoUserDetails hawaiiSsoUserDetails = null;
        /*
         * The getUserInfo call returns an unauthorized response for a non valid access token.
         * This is translated into a null JSONObject.
         *
         * If we have a non-null response we can use this to populate the user details.
         */
        final JSONObject userInfo = validateUserIsLoggedInAtSso(credentials);
        if (userInfo != null) {
            hawaiiSsoUserDetails = createUserDetails(credentials, userInfo);
        } else {
            LOGGER.trace("Token is invalid at SSO.");
            // fall back to outer return to return null;
        }
        return hawaiiSsoUserDetails;
    }

    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private HawaiiSsoUserDetails createUserDetails(final Credentials credentials, final JSONObject userInfo) {
        try {
            return createUser(credentials, userInfo);
        } catch (Throwable cause) {
            // As a last resort we catch everything here, otherwise this will cause failures in the Spring Security Filter Chain.
            LOGGER.error("Cannot create a user details.", cause);
        }
        return null;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private JSONObject validateUserIsLoggedInAtSso(final Credentials credentials) {
        LOGGER.trace("Must validate that the access token is still valid, asking the SSO server.");

        JSONObject userInfo = null;
        try {
            userInfo = hawaiiSsoService.getUserInfo(credentials).get();
        } catch (InterruptedException ignored) {
            //
        } catch (ExecutionException executionException) {
            final Throwable cause = executionException.getCause();
            if (cause != null && !(cause instanceof UnauthorizedRequestException)) {
                LOGGER.error("Cannot verify whether the user is logged in.", cause);
            }
        }

        return userInfo;
    }

    /**
     * Create a user or null.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private HawaiiSsoUserDetails createUser(final Credentials credentials, final JSONObject userInfo) {
        final AccessToken accessToken = credentials.getAccessToken();

        final String subjectId = accessToken.getSubject();
        final HawaiiSsoUserDetails hawaiiSsoUserDetails = userDetailsService.getUserDetailsFromToken(accessToken, userInfo);
        if (hawaiiSsoUserDetails == null) {
            LOGGER.trace("Could not create user details for subject '{}'.", subjectId);
        } else {
            try {
                addToCache(accessToken, hawaiiSsoUserDetails);
            } catch (CacheServiceException e) {
                LOGGER.error("Could not register accessToken for " + hawaiiSsoUserDetails.getSubjectId() + " to cache.");
            }
        }
        return hawaiiSsoUserDetails;
    }

    private void addToCache(final AccessToken accessToken, final HawaiiSsoUserDetails userDetails) throws CacheServiceException {
        final String key = getKey(accessToken);
        LOGGER.trace("Adding user details to cache.");
        userCache.put(key, userDetails);
    }

    private HawaiiSsoUserDetails getFromCache(final AccessToken accessToken) throws CacheServiceException {
        final String key = getKey(accessToken);
        final HawaiiSsoUserDetails userDetails = userCache.get(key);
        if (userDetails != null) {
            LOGGER.trace("Found cached user details for '{}'.", key);
        }
        return userDetails;
    }

    private String getKey(final AccessToken accessToken) {
        return accessToken.getId();
    }

}
