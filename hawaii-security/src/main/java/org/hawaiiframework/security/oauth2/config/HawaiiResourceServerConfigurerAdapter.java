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

package org.hawaiiframework.security.oauth2.config;

import org.hawaiiframework.cache.Cache;
import org.hawaiiframework.security.oauth2.provider.token.HawaiiTokenServices;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

/**
 * Configuration class for the {@link HawaiiTokenServices}.
 *
 * @author Wouter Eerdekens
 * @author Jules Houben
 * @since 3.0.0
 */
@EnableWebSecurity
@EnableResourceServer
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class HawaiiResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    /**
     * Bean configuration of a {@link JwkTokenStore} which does the actual Jwk checks.
     * @param properties the ResourceServerProperties
     * @return the bean
     */
    @Bean
    public TokenStore jwkTokenStore(final ResourceServerProperties properties) {
        final DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new DefaultUserAuthenticationConverter());
        return new JwkTokenStore(resolveJwkSetUri(properties.getJwk()), accessTokenConverter);
    }

    private String resolveJwkSetUri(final ResourceServerProperties.Jwk jwk) {
        return jwk.getKeySetUri();
    }

    /**
     * Bean configuration of the {@link OAuth2RestTemplate} to use to access the user info endpoint.
     * @param resource the protected resource details
     * @param clientContext the client context
     * @return the bean
     */
    @Bean
    @Primary
    public OAuth2RestOperations oAuth2RestOperations(
            final OAuth2ProtectedResourceDetails resource, final OAuth2ClientContext clientContext) {
        return new OAuth2RestTemplate(resource, clientContext);
    }

    /**
     * Bean configuration of the {@link HawaiiTokenServices} to use.
     * @param jwkTokenStore the jwk token store
     * @param restTemplate the rest template
     * @param properties the resource server properties
     * @param principalExtractor the principal extractor
     * @param authenticationCache the cache to store the user info authentication
     * @return the bean
     */
    @Bean
    public ResourceServerTokenServices resourceServerTokenServices(
            final TokenStore jwkTokenStore, final OAuth2RestOperations restTemplate, final ResourceServerProperties properties,
            final PrincipalExtractor principalExtractor, final Cache<Authentication> authenticationCache) {
        // Create a DefaultTokenServices with a JwkTokeStore that will check if the access token is valid or not.
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwkTokenStore);

        // Create a UserInfoTokenServices that will call the SSO server's userinfo endpoint.
        final UserInfoTokenServices userInfoTokenServices = new UserInfoTokenServices(
                properties.getUserInfoUri(), properties.getClientId());
        userInfoTokenServices.setRestTemplate(restTemplate);
        userInfoTokenServices.setTokenType(properties.getTokenType());
        userInfoTokenServices.setPrincipalExtractor(principalExtractor);

        // Create a CombinedTokenServices that will both check the validity of the token and fetch the userinfo.
        return new HawaiiTokenServices(defaultTokenServices, userInfoTokenServices, authenticationCache);
    }

}
