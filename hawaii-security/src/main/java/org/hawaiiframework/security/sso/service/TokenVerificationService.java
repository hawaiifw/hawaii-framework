package org.hawaiiframework.security.sso.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.IdToken;
import org.hawaiiframework.security.sso.model.TokenException;
import org.hawaiiframework.security.sso.model.TokenWrapper;
import org.hawaiiframework.time.HawaiiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.hawaiiframework.async.HawaiiAsyncUtil.get;

/**
 * Service to verify a token.
 *
 * This service offers methods to verify a token cryptographically, check it for validity, and verify that a combination
 * of access token and id token belong together.
 *
 * The actual cryptographic validation is delegated to a {@link TokenSignatureChecker}. To get the correct verifier for the token,
 * the key that was used to sign the token is retrieved from the {@link TokenVerificationService#hawaiiSsoService} first.
 */
@Service
public class TokenVerificationService {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenVerificationService.class);

    /**
     * String constant for the expired token error text.
     */
    private static final String ERROR_TOKEN_IS_EXPIRED = "Token is expired";

    /**
     * Hawaii SSO service. Used to retrieve the signing key from the SSO Server.
     */
    private final HawaiiSsoService hawaiiSsoService;

    /**
     * Token signature checker for cryptographic token verification.
     */
    private final TokenSignatureChecker tokenSignatureChecker;

    /**
     * Constructor.
     *
     * @param hawaiiSsoService the Hawaii SSO Service for retrieving the signing key
     * @param tokenSignatureChecker the token signature checker
     */
    @Autowired
    public TokenVerificationService(final HawaiiSsoService hawaiiSsoService, final TokenSignatureChecker tokenSignatureChecker) {
        this.hawaiiSsoService = hawaiiSsoService;
        this.tokenSignatureChecker = tokenSignatureChecker;
    }

    /**
     * Verify a token.
     *
     * @param token the token to verify
     */
    public void verifyTokenSignature(final TokenWrapper token) {
        final SignedJWT signedJWT = token.getSignedJWT();
        if (signedJWT != null) {
            verifyTokenSignature(signedJWT);
        } else {
            LOGGER.warn("Attempting to verify a token that is not a SignedJWT instance: {}", token);
        }
    }

    /**
     * Verify a token.
     *
     * Delegates to {@link #tokenSignatureChecker}.
     *
     * @param signedJWT the token
     */
    public void verifyTokenSignature(final SignedJWT signedJWT) {
        final JWK jwk = get(hawaiiSsoService.findJwk(getSigningKeyId(signedJWT.getHeader())));
        tokenSignatureChecker.verifyTokenSignature(signedJWT, jwk);
    }

    /**
     * Verify if a token is expired or not.
     *
     * @param token the token
     * @param hawaiiTime the HawaiiTime instance
     */
    public void verifyTokenExpiry(final TokenWrapper token, final HawaiiTime hawaiiTime) {
        final Long expiry = token.getExpiry();
        if (expiry != null && hawaiiTime.millis() >= expiry) {
            throw new TokenException(ERROR_TOKEN_IS_EXPIRED);
        }
    }

    /**
     * Verify if a token is expired or not.
     *
     * @param signedJWT the signed JWT
     * @param hawaiiTime the HawaiiTime instance
     */
    public void verifyTokenExpiry(final SignedJWT signedJWT, final HawaiiTime hawaiiTime) {
        verifyTokenExpiry(new TokenWrapper(signedJWT), hawaiiTime);
    }

    /**
     * Verify that the access token hash in an id token is valid for the given access token.
     *
     * Delegates to {@link TokenHashChecker}.
     *
     * @param accessToken the access token
     * @param idToken the id token
     */
    public void verifyAccessTokenHash(final AccessToken accessToken, final IdToken idToken) {
        final JWSAlgorithm signingAlgorithm = accessToken.getSigningAlgorithm();
        if (signingAlgorithm != null) {
            final TokenHashChecker tokenHashChecker = new TokenHashChecker(signingAlgorithm);
            tokenHashChecker.verifyAccessTokenHash(accessToken, idToken);
        } else {
            LOGGER.warn("Access token is not a signed token, cannot verify access token hash.");
        }
    }

    private String getSigningKeyId(final JWSHeader header) {
        return header.getKeyID();
    }

}
