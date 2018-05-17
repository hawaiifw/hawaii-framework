package org.hawaiiframework.security.sso.model;

import org.hawaiiframework.security.sso.service.TokenVerificationService;
import org.hawaiiframework.time.HawaiiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Class to hold the Id Token, Access Token, Refresh Token.
 */
public class Credentials {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Credentials.class);

    /**
     * The ID TOKEN parameter.
     */
    private static final String ID_TOKEN = "id_token";

    /**
     * The ACCESS_TOKEN parameter.
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * The Id Token.
     */
    private String idToken;

    /**
     * The Access Token.
     */
    private String accessToken;

    /**
     * The nonce.
     */
    private String nonce;

    /**
     * The validation failure reason.
     */
    private String validationFailureReason;

    /**
     * Flag to indicate that the token is expired.
     */
    private boolean expired;

    /**
     * Basic accessor.
     *
     * @return The IdToken as JWT, or null.
     */
    public IdToken getIdToken() {
        if (idToken == null) {
            return null;
        }
        return new IdToken(idToken);
    }

    /**
     * Return ID Token's bare string value.
     */
    public String getIdTokenAsString() {
        return idToken;
    }

    /**
     * Basic accessor.
     *
     * @param idToken The token to set, can be null.
     */
    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }

    /**
     * Basic accessor.
     *
     * @return The AccessToken as JWT, or null.
     */
    public AccessToken getAccessToken() {
        if (accessToken == null) {
            return null;
        }
        return new AccessToken(accessToken);
    }

    /**
     * Get the access token's bare string value.
     */
    public String getAccessTokenAsString() {
        return accessToken;
    }

    /**
     * Basic accessor.
     *
     * @param accessToken The token to set, can be null.
     */
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getNonce() {
        return nonce;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    /**
     * Validate the token's signature, .
     *
     * @param tokenVerificationService The token verification service.
     * @param currentTime              The current time.
     * @param allowedClientIds         The client ids the token, one of which must be created for.
     * @return true if the signature matches, exception otherwise.
     */
    public boolean validate(final TokenVerificationService tokenVerificationService,
            final HawaiiTime currentTime,
            final List<String> allowedClientIds) {
        requireNonNull(tokenVerificationService);
        requireNonNull(allowedClientIds);
        requireNonNull(currentTime);

        final AccessToken accessToken = getAccessToken();
        final IdToken idToken = getIdToken();

        return validate(idToken, accessToken, tokenVerificationService, currentTime, allowedClientIds);
    }

    private boolean validate(final IdToken idToken, final AccessToken accessToken, final TokenVerificationService tokenVerificationService,
                             final HawaiiTime currentTime,
                             final List<String> allowedClientIds) {

        boolean valid = verifyTokenSignature(accessToken, tokenVerificationService, ACCESS_TOKEN);
        valid = valid && verifyTokenNotExpired(accessToken, tokenVerificationService, currentTime, ACCESS_TOKEN);
        valid = valid && tokenIsForOneOfTheClients(accessToken, allowedClientIds, ACCESS_TOKEN);

        if (idToken != null) {
            valid = valid && verifyTokenSignature(idToken, tokenVerificationService, ID_TOKEN);
            valid = valid && verifyTokenNotExpired(accessToken, tokenVerificationService, currentTime, ID_TOKEN);
            valid = valid && verifyAtHash(accessToken, idToken, tokenVerificationService);
            valid = valid && verifyNonce(idToken);
        }

        if (!valid) {
            LOGGER.debug(validationFailureReason);
        }
        return valid;
    }

    private boolean verifyNonce(final IdToken idToken) {
        if (Objects.equals(nonce, idToken.getNonce())) {
            return true;
        }

        validationFailureReason = "nonce does not match";
        return false;
    }

    private boolean verifyAtHash(
            final AccessToken accessToken,
            final IdToken idToken,
            final TokenVerificationService tokenVerificationService) {
        try {
            tokenVerificationService.verifyAccessTokenHash(accessToken, idToken);
            return true;
        } catch (TokenException e) {
            validationFailureReason = "at_hash does not match";
            return false;
        }
    }

    private boolean verifyTokenSignature(
            final TokenWrapper token,
            final TokenVerificationService tokenVerificationService,
            final String type) {
        try {
            tokenVerificationService.verifyTokenSignature(token);
            return true;
        } catch (TokenException e) {
            validationFailureReason = String.format("%s has invalid signature", type);
            return false;
        }
    }

    private boolean verifyTokenNotExpired(
            final TokenWrapper token,
            final TokenVerificationService tokenVerificationService,
            final HawaiiTime currentTime,
            final String type) {
        try {
            tokenVerificationService.verifyTokenExpiry(token, currentTime);
            return true;
        } catch (TokenException e) {
            expired = true;
            validationFailureReason = String.format("%s is expired", type);
            return false;
        }
    }

    private boolean tokenIsForOneOfTheClients(
            final AccessToken token,
            final List<String> allowedClientIds,
            final String type) {
        final String authorizedParty = token.getAuthorizedParty();
        if (allowedClientIds.contains(authorizedParty)) {
            return true;
        }

        validationFailureReason = String.format("%s is created for '%s' but is not accepted (configured)", type, authorizedParty);
        return false;
    }

    /**
     * Are the credentials expired.
     *
     * @return true if either the access token or the id token is expired
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Get the reason text in case a token validation error happened.
     *
     * @return the validation failure reason
     */
    public String getValidationFailureReason() {
        return validationFailureReason;
    }
}
