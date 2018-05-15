package org.hawaiiframework.security.sso.token;

/**
 * Functional interface from which a token string can be retrieved.
 *
 * This interface serves to decouple token validation, so that token validation can be done without any knowledge of the (input) object
 * containing the token.
 */
public interface TokenInput {

    /**
     * Get the token.
     *
     * @return the token string
     */
    String getTokenString();

}
