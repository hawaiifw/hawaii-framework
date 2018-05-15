package org.hawaiiframework.security.sso.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import org.hawaiiframework.exception.HawaiiException;
import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.IdToken;
import org.hawaiiframework.security.sso.model.TokenException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Helper class to calculate the hash code of an access token in the same way as it is stored in an id token, for validation purposes.
 */
public class TokenHashChecker {

    /**
     * The JWS signing algorithm.
     */
    private final JWSAlgorithm signingAlg;

    /**
     * Construct a new instance.
     *
     * @param signingAlg the JWS signing algorithm
     */
    public TokenHashChecker(final JWSAlgorithm signingAlg) {
        this.signingAlg = signingAlg;
    }

    /**
     * Verify that the id token is for the access token.
     *
     * The claim <code>at_hash</code> in the id token is the left half of the hashed value of the access token.
     * This method computes the access token hash and compares it to the claim in the id token.
     *
     * @param accessToken the access token
     * @param idToken the id token
     * @throws TokenException if the verification fails
     */
    public void verifyAccessTokenHash(final AccessToken accessToken, final IdToken idToken) {
        verifyAccessTokenHash(accessToken.getSignedJWT(), idToken.getAccessTokenHash());
    }

    private void verifyAccessTokenHash(final JWT accessToken, final String accessTokenHash) {
        final Base64URL computedHash = getAccessTokenHash(accessToken);
        if (!verifyAccessTokenHash(accessTokenHash, computedHash)) {
            throw new TokenException("Id token has invalid access token hash");
        }
    }

    private boolean verifyAccessTokenHash(final String hash, final Base64URL computedHash) {
        return hash.equals(computedHash.toString());
    }

    /**
     * Compute the base64 encoded SHA hash of a token.
     *
     * @param token the token
     * @return the hash
     */
    private Base64URL getAccessTokenHash(final JWT token) {
        final byte[] tokenBytes = getTokenBytes(token.serialize());
        return getHash(signingAlg, tokenBytes);
    }

    /**
     * Get the token as a byte array, using the UTF-8 charset.
     *
     * @param tokenString the token
     * @return the bytes
     */
    private byte[] getTokenBytes(final String tokenString) {
        return tokenString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Helper method to calculate the access token hash value that is stored in an id token as the <code>at_hash</code> claim.
     *
     * @param signingAlg the signing algorithm
     * @param bytes the access token bytes
     * @return the leftmost half of the access token hash, base64 encoded
     */
    private Base64URL getHash(final JWSAlgorithm signingAlg, final byte[] bytes) {
        Base64URL encodedHash = null;

        final MessageDigest hasher = getHasherForAlgorithm(signingAlg);

        if (hasher != null) {
            final byte[] hashBytes = hashTheBytes(hasher, bytes);
            final byte[] hashBytesLeftHalf = Arrays.copyOf(hashBytes, hashBytes.length / 2);
            encodedHash = Base64URL.encode(hashBytesLeftHalf);
        }

        return encodedHash;
    }

    /**
     * Get the hasher to use, given a signing algorithm.
     *
     * The hashing algorithm is SHA with the same number of bytes as the signing algorithm. For example,
     * if a token was signed using the {@link JWSAlgorithm#RS256} algorithm, the hasher will be SHA-256.
     *
     * @param signingAlg the signing algorithm
     * @return a MessageDigest for the determined hashing algorithm
     * @throws HawaiiException if a {@link NoSuchAlgorithmException} occurs
     */
    private MessageDigest getHasherForAlgorithm(final JWSAlgorithm signingAlg) {
        MessageDigest hasher = null;
        // First determine the hashing algorithm
        String hashAlg = null;
        if (is256(signingAlg)) {
            hashAlg = "SHA-256";
        } else if (is384(signingAlg)) {
            hashAlg = "SHA-384";
        } else if (is512(signingAlg)) {
            hashAlg = "SHA-512";
        }
        if (hashAlg != null) {
            try {
                hasher = MessageDigest.getInstance(hashAlg);
            } catch (NoSuchAlgorithmException e) {
                throw new HawaiiException(e);
            }
        }
        return hasher;
    }

    private byte[] hashTheBytes(final MessageDigest hasher, final byte[] bytes) {
        hasher.reset();
        hasher.update(bytes);

        return hasher.digest();
    }

    /**
     * Check if an algorithm is 256 bytes.
     *
     * @param signingAlg the signing algorithm
     * @return true if this is a 256 bit algorithm
     */
    private boolean is256(final JWSAlgorithm signingAlg) {
        return signingAlg.equals(JWSAlgorithm.HS256) || signingAlg.equals(JWSAlgorithm.ES256)
                || signingAlg.equals(JWSAlgorithm.RS256) || signingAlg.equals(JWSAlgorithm.PS256);
    }

    /**
     * Check if an algorithm is 384 bytes.
     *
     * @param signingAlg the signing algorithm
     * @return true if this is a 384 bit algorithm
     */
    private boolean is384(final JWSAlgorithm signingAlg) {
        return signingAlg.equals(JWSAlgorithm.ES384) || signingAlg.equals(JWSAlgorithm.HS384)
                || signingAlg.equals(JWSAlgorithm.RS384) || signingAlg.equals(JWSAlgorithm.PS384);
    }

    /**
     * Check if an algorithm is 512 bytes.
     *
     * @param signingAlg the signing algorithm
     * @return true if this is a 512 bit algorithm
     */
    private boolean is512(final JWSAlgorithm signingAlg) {
        return signingAlg.equals(JWSAlgorithm.ES512) || signingAlg.equals(JWSAlgorithm.HS512)
                || signingAlg.equals(JWSAlgorithm.RS512) || signingAlg.equals(JWSAlgorithm.PS512);
    }

}
