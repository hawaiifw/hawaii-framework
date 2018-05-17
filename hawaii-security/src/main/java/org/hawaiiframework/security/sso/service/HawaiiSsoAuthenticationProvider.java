package org.hawaiiframework.security.sso.service;

import org.hawaiiframework.security.sso.model.AccessToken;
import org.hawaiiframework.security.sso.model.Credentials;
import org.hawaiiframework.security.sso.model.HawaiiSsoAuthenticationToken;
import org.hawaiiframework.security.sso.model.HawaiiSsoUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link AuthenticationProvider}.
 */
@Component
public class HawaiiSsoAuthenticationProvider implements AuthenticationProvider {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiSsoAuthenticationProvider.class);

    /**
     * The validator.
     */
    private final ValidatingUserFactory validatingUserFactory;

    /**
     * The constructor.
     */
    @Autowired
    public HawaiiSsoAuthenticationProvider(final ValidatingUserFactory validatingUserFactory) {
        this.validatingUserFactory = requireNonNull(validatingUserFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        LOGGER.trace("Validating access token, and trying to create a hawaiiSsoUserDetails from the authentication token.");

        final HawaiiSsoUserDetails hawaiiSsoUserDetails = doAuthenticate((HawaiiSsoAuthenticationToken) authentication);

        return new HawaiiSsoAuthenticationToken(hawaiiSsoUserDetails);
    }

    private HawaiiSsoUserDetails doAuthenticate(final HawaiiSsoAuthenticationToken hawaiiSsoAuthenticationToken)
            throws AuthenticationException {
        final Credentials credentials = hawaiiSsoAuthenticationToken.getCredentials();
        final HawaiiSsoUserDetails hawaiiSsoUserDetails = validatingUserFactory.validateUser(credentials);

        if (hawaiiSsoUserDetails == null) {
            throwAuthenticationException(credentials);
        }

        return hawaiiSsoUserDetails;
    }

    private void throwAuthenticationException(final Credentials credentials) {
        final String reason = credentials.getValidationFailureReason();
        if (credentials.isExpired()) {
            throw new CredentialsExpiredException(reason);
        }
        if (reason != null) {
            throw new BadCredentialsException(reason);
        }

        throw new BadCredentialsException(String.format("user with subject '%s' is not found", getSubject(credentials.getAccessToken())));
    }

    private String getSubject(final AccessToken accessToken) {
        return accessToken.getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public boolean supports(final Class<?> authentication) {
        return HawaiiSsoAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
