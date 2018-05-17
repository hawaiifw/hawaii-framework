package org.hawaiiframework.security.sso.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jwt.SignedJWT;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.security.sso.model.TokenException;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * This class performs cryptographic verification of a signed JWT.
 */
@Component
public class TokenSignatureChecker {
    /**
     * The JWS Verifier Factory.
     */
    private final JWSVerifierFactory jwsVerifierFactory;

    /**
     * Constructor.
     *
     * @param jwsVerifierFactory the JWS verifier factory
     */
    public TokenSignatureChecker(final JWSVerifierFactory jwsVerifierFactory) {
        this.jwsVerifierFactory = jwsVerifierFactory;
    }

    /**
     * Verify a token.
     *
     * @param signedJWT the signed JWT
     * @param jwk the signing key
     * @throws TokenException if the verification fails
     */
    public void verifyTokenSignature(final SignedJWT signedJWT, final JWK jwk) {
        final JWSVerifier jwsVerifier = createJWSVerifier(signedJWT.getHeader(), jwk);
        try {
            signedJWT.verify(jwsVerifier);
        } catch (JOSEException e) {
            throw new TokenException(e);
        }
    }

    private JWSVerifier createJWSVerifier(final JWSHeader jwsHeader, final JWK jwk) {
        JWSVerifier jwsVerifier = null;
        try {
            if (jwk instanceof RSAKey) {
                jwsVerifier = jwsVerifierFactory.createJWSVerifier(jwsHeader, getPublicKey((RSAKey) jwk));
            } else if (jwk instanceof ECKey) {
                jwsVerifier = jwsVerifierFactory.createJWSVerifier(jwsHeader, getPublicKey((ECKey) jwk));
            } else if (jwk instanceof OctetSequenceKey) {
                jwsVerifier = jwsVerifierFactory.createJWSVerifier(jwsHeader, getPublicKey((OctetSequenceKey) jwk));
            }
        } catch (final JOSEException e) {
            throw new HawaiiException("Error creating public key", e);
        }
        if (jwsVerifier == null) {
            throw new HawaiiException("Can't create verifier for key " + jwk.getKeyID());
        }
        return jwsVerifier;
    }

    private Key getPublicKey(final RSAKey key) throws JOSEException {
        return key.toPublicKey();
    }

    private Key getPublicKey(final ECKey key) throws JOSEException {
        return key.toPublicKey();
    }

    private Key getPublicKey(final OctetSequenceKey key) throws JOSEException {
        return key.toSecretKey();
    }

}
