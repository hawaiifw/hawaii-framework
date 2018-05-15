package org.hawaiiframework.security.sso.service;

import com.nimbusds.jose.jwk.JWK;
import org.hawaiiframework.security.sso.model.Credentials;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

/**
 * The interface to the Hawaii SSO.
 */
public interface HawaiiSsoService {

    /**
     * Retrieve the JWK with the given key id from the sso server.
     *
     * @param keyId the key id
     * @return the JWK, or null
     */
    CompletableFuture<JWK> findJwk(String keyId);

    /**
     * Call the userInfo endpoint at the SSO with the given credentials.
     * <p>
     * This call can be used to verify that a token (credentials) has not been revoked (while not yet expired).
     *
     * @param credentials the credentials to verify.
     * @return @code{null} if the user is not logged in.
     */
    CompletableFuture<JSONObject> getUserInfo(Credentials credentials);
}
