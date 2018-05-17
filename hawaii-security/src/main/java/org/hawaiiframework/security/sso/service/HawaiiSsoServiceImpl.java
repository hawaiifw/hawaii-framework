package org.hawaiiframework.security.sso.service;

import com.nimbusds.jose.jwk.JWK;
import org.hawaiiframework.cache.TTLCache;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.security.sso.config.SsoConfiguration;
import org.hawaiiframework.security.sso.model.Credentials;
import org.hawaiiframework.security.sso.model.TokenException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This is the default implementation of the HawaiiSsoService.
 * <p>
 * {@inheritDoc}
 */
@Service("securitySsoService")
public class HawaiiSsoServiceImpl implements HawaiiSsoService {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiSsoServiceImpl.class);

    /**
     * The configuration for accessing the SSO server.
     */
    private final SsoConfiguration ssoConfiguration;

    /**
     * The rest template to use for SSO communication.
     */
    private final RestTemplate ssoBackChannelRestTemplate;

    /**
     * The rest template to use for SSO communication.
     */
    private final RestTemplate ssoRestTemplate;

    /**
     * The cache for JWKs.
     */
    private final TTLCache<JWK> jwkCache;

    /**
     * The constructor.
     */
    @Autowired
    public HawaiiSsoServiceImpl(
            @Qualifier("backchannel.rest.template") final RestTemplate ssoBackChannelRestTemplate,
            @Qualifier("hawaii.sso.rest.template") final RestTemplate ssoRestTemplate,
            final SsoConfiguration ssoConfiguration,
            final TTLCache<JWK> jwkCache) {
        this.ssoBackChannelRestTemplate = requireNonNull(ssoBackChannelRestTemplate);
        this.ssoRestTemplate = requireNonNull(ssoRestTemplate);
        this.ssoConfiguration = requireNonNull(ssoConfiguration);
        this.jwkCache = requireNonNull(jwkCache);
    }


    /**
     * {@inheritDoc}
     */
    @Async("hawaii_sso.find_jwk")
    @Override
    public CompletableFuture<JWK> findJwk(final String keyId) {
        requireNonNull(keyId);
        LOGGER.trace("Finding the JWK with key '{}'.", keyId);

        JWK jwk = jwkCache.get(keyId);
        if (jwk == null) {
            @SuppressWarnings("PMD.LawOfDemeter") final String response =
                    ssoBackChannelRestTemplate.getForObject(ssoConfiguration.getEncryptionKeysEndpoint(), String.class);

            final JSONObject jwkSet = new JSONObject(response);
            jwk = findJwk(jwkSet, keyId);

            if (jwk == null) {
                throw new HawaiiException(String.format("Could not find JWK for key '%s'.", keyId));
            } else {
                jwkCache.put(keyId, jwk, Duration.ofHours(1));
            }
        }

        return CompletableFuture.completedFuture(jwk);
    }

    private JWK findJwk(final JSONObject jwkSet, final String keyId) {
        return findJwk(jwkSet.getJSONArray("keys"), keyId);
    }

    private JWK findJwk(final JSONArray array, final String keyId) {
        JWK jwk = null;
        for (int i = 0; i < array.length(); i++) {
            final JSONObject json = array.getJSONObject(i);

            jwk = createJWKIfKeyIdMatches(json, keyId);
            if (jwk != null) {
                break;
            }
        }

        return jwk;
    }

    private JWK createJWKIfKeyIdMatches(final JSONObject json, final String keyId) {
        final String kid = json.getString("kid");

        if (keyId.equals(kid)) {
            try {
                return JWK.parse(json.toString());
            } catch (ParseException e) {
                throw new TokenException(e);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Async("hawaii_sso.is_logged_in")
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public CompletableFuture<JSONObject> getUserInfo(final Credentials credentials) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, "Bearer " + credentials.getAccessToken());
        final HttpEntity<JSONObject> httpEntity = new HttpEntity<>(httpHeaders);
        final ResponseEntity<JSONObject> response =
                ssoRestTemplate.exchange(
                        ssoConfiguration.getUserInfoEndpoint(), HttpMethod.GET, httpEntity, JSONObject.class);

        return CompletableFuture.completedFuture(response.getBody());
    }
}
