/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.security.oauth2.provider.token;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

import java.time.Duration;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * {@link ResourceServerTokenServices} implementation that combines the logic of
 * the {@link DefaultTokenServices} and {@link UserInfoTokenServices} implementations.
 *
 * @author Wouter Eerdekens
 * @author Jules Houben
 * @since 3.0.0
 */
public class HawaiiTokenServices implements ResourceServerTokenServices {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiTokenServices.class);

    /**
     * {@link DefaultTokenServices} that will perform token validation. Make sure
     * it uses a {@link JwkTokenStore} to perform the actual validation.
     */
    private final DefaultTokenServices defaultTokenServices;

    /**
     * {@link DefaultTokenServices} that uses a user-info REST service.
     */
    private final UserInfoTokenServices userInfoTokenServices;

    /**
     * {@link Cache} to store the user info Authentication.
     */
    private final Cache<Authentication> cache;

    /**
     * Constructor.
     *
     * @param defaultTokenServices the defaultTokenServices instance
     * @param userInfoTokenServices the userInfoTokenServices instance
     */
    public HawaiiTokenServices(final DefaultTokenServices defaultTokenServices,
            final UserInfoTokenServices userInfoTokenServices, final Cache<Authentication> cache) {
        this.defaultTokenServices = defaultTokenServices;
        this.userInfoTokenServices = userInfoTokenServices;
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2Authentication loadAuthentication(final String accessToken) throws AuthenticationException, InvalidTokenException {
        // check access token
        final OAuth2Authentication jwkAuthentication = defaultTokenServices.loadAuthentication(accessToken);
        final OAuth2Request request = extractOAuth2Request(jwkAuthentication);
        final OAuth2AccessToken token = readAccessToken(accessToken);
        final String jti = getJti(token);

        // call user info endpoint
        Authentication userAuthentication = cache.get(jti);
        if (userAuthentication == null) {
            LOGGER.info("UserAuthentication not found in cache, retrieving it from SSO");
            final OAuth2Authentication userInfoAuthentication = userInfoTokenServices.loadAuthentication(accessToken);
            userAuthentication = extractUserAuthentication(userInfoAuthentication);
            cache.put(jti, userAuthentication, Duration.of(token.getExpiresIn(), SECONDS));
        } else {
            LOGGER.info("UserAuthentication found in cache");
        }

        return new OAuth2Authentication(request, userAuthentication);
    }

    private OAuth2Request extractOAuth2Request(final OAuth2Authentication authentication) {
        return authentication.getOAuth2Request();
    }

    private Authentication extractUserAuthentication(final OAuth2Authentication authentication) {
        return authentication.getUserAuthentication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2AccessToken readAccessToken(final String accessToken) {
        return defaultTokenServices.readAccessToken(accessToken);
    }

    private String getJti(final OAuth2AccessToken accessToken) {
        final Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        return getJti(additionalInformation);
    }

    private String getJti(final Map<String, Object> additionalInformation) {
        final String jti = (String) additionalInformation.get("jti");
        if (StringUtils.isBlank(jti)) {
            throw new InvalidTokenException("Access token has no jti");
        }
        return jti;
    }

}
