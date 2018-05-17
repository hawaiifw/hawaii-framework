package org.hawaiiframework.security.sso.model;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.hawaiiframework.security.sso.token.TokenResponse;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class for various token types.
 * <p>
 * We basically handle tokens using the <a href="https://connect2id.com/products/nimbus-jose-jwt">Nimbus JOSE and JWT</a> library.
 * Our token classes to represent {@link AccessToken} and {@link IdToken} simply wrap a {@link JWT} object. This is the
 * base class that provides common functionality.
 */
public class TokenWrapper {

    /**
     * constant for scope string.
     */
    public static final String SCOPE = "scope";

    /**
     * The token.
     */
    private final JWT jwt;

    /**
     * The JWT claims set, extracted for convenience.
     */
    private final JWTClaimsSet jwtClaimsSet;

    /**
     * The constructor.
     * <p>
     * Parses the tokenValue to a {@link JWT} and extracts the {@link JWTClaimsSet}.
     *
     * @param tokenValue the token string
     * @throws TokenException if a {@link ParseException} occurs
     */
    public TokenWrapper(final String tokenValue) {
        try {
            jwt = JWTParser.parse(tokenValue);
            jwtClaimsSet = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new TokenException(e);
        }
    }

    /**
     * Construct an instance from an existing JWT.
     *
     * @param jwt the JWT
     * @throws TokenException if a {@link ParseException} occurs
     */
    public TokenWrapper(final JWT jwt) {
        this.jwt = jwt;
        try {
            this.jwtClaimsSet = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new TokenException(e);
        }
    }

    /**
     * Construct a tokenWrapper instance from a tokenResponse.
     *
     * @param tokenResponse The response.
     */
    public TokenWrapper(final TokenResponse tokenResponse) {
        this(tokenResponse.getAccessToken());
    }

    /**
     * Get the JWT id. This is the <code>jti</code> claim.
     *
     * @return the JWT id
     */
    public String getId() {
        return getStringClaim("jti");
    }

    /**
     * Get the token expiry. This is the the <code>exp</code> claim.
     *
     * @return the expiry
     */
    public Long getExpiry() {
        return getTimestamp(jwtClaimsSet.getExpirationTime());
    }

    private Long getTimestamp(final Date date) {
        return date.getTime();
    }

    /**
     * Get the {@link SignedJWT}.
     *
     * @return the SignedJWT if it is a signed JWT, null otherwise
     */
    public SignedJWT getSignedJWT() {
        if (jwt instanceof SignedJWT) {
            return (SignedJWT) jwt;
        }
        return null;
    }

    /**
     * Get the signing algorithm.
     *
     * @return the algorithm used to sign the token, null if not a signed token
     */
    public JWSAlgorithm getSigningAlgorithm() {
        if (jwt instanceof SignedJWT) {
            final JWSHeader jwsHeader = getJWSHeader((SignedJWT) jwt);
            return getJWSAlgorithm(jwsHeader);
        }
        return null;
    }

    private JWSHeader getJWSHeader(final SignedJWT signedJWT) {
        return signedJWT.getHeader();
    }

    private JWSAlgorithm getJWSAlgorithm(final JWSHeader jwsHeader) {
        return jwsHeader.getAlgorithm();
    }

    /**
     * Get the subject, i.e. the SSO's unique user id. This is the <code>sub</code> claim.
     *
     * @return the subject
     */
    public String getSubject() {
        return getStringClaim("sub");
    }

    /**
     * Extract a string claim from the claims set.
     *
     * @param name the claim name
     * @return the claim value
     * @throws TokenException is a {@link ParseException} occurs
     */
    public String getStringClaim(final String name) {
        try {
            return jwtClaimsSet.getStringClaim(name);
        } catch (ParseException e) {
            throw new TokenException(e);
        }
    }

    /**
     * Extract a long claim from the claims set.
     *
     * @param name the claim name
     * @return the claim value
     * @throws TokenException is a {@link ParseException} occurs
     */
    public Long getLongClaim(final String name) {
        try {
            return jwtClaimsSet.getLongClaim(name);
        } catch (ParseException e) {
            throw new TokenException(e);
        }
    }

    /**
     * Get the scopes. This is the <code>scope</code> claim.
     *
     * @return the scopes
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public Set<String> getScopes() {
        try {
            return Arrays.stream(jwtClaimsSet.getStringArrayClaim(SCOPE)).collect(Collectors.toSet());
        } catch (ParseException e) {
            throw new TokenException(e);
        }
    }

    /**
     * Check if the token contains all requested scopes.
     *
     * @param requiredScopes the required scopes
     * @return true if the token contains all scopes
     */
    public boolean hasAllScopes(final Set<String> requiredScopes) {
        final Set<String> tokenScopes = getScopes();
        return tokenScopes != null && allScopesPresent(tokenScopes, requiredScopes);
    }

    private boolean allScopesPresent(final Set<String> tokenScopes, final Set<String> requiredScopes) {
        return tokenScopes.containsAll(requiredScopes);
    }

    /**
     * Check if the token contains at least one of the any scopes.
     *
     * @param anyScopes the required scopes
     * @return true if the token contains all scopes
     */
    public boolean hasAnyScopes(final Set<String> anyScopes) {
        final Set<String> tokenScopes = getScopes();
        return tokenScopes != null && anyScopePresent(tokenScopes, anyScopes);
    }

    private boolean anyScopePresent(final Set<String> tokenScopes, final Set<String> anyScopes) {
        return !Collections.disjoint(tokenScopes, anyScopes);
    }

    /**
     * Get the String representation of the token.
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        return jwt.serialize();
    }

}
