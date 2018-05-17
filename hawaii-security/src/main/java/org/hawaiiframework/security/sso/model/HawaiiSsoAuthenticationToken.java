package org.hawaiiframework.security.sso.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The Hawaii SSO authentication token.
 * <p>
 * This class is used by the Hawaii SSO as a Spring Security <code>Authentication</code>.
 */
public class HawaiiSsoAuthenticationToken implements Authentication, Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -1837782359774176937L;

    /**
     * The credentials passed during the authentication.
     */
    private final transient Credentials credentials;

    /**
     * The hawaiiSsoUserDetails, optionally <code>null</code>.
     */
    private final HawaiiSsoUserDetails hawaiiSsoUserDetails;

    /**
     * Flag to indicate whether the hawaiiSsoUserDetails is authenticated.
     */
    private final boolean authenticated;

    /**
     * Constructor that accepts an access token.
     */
    @SuppressWarnings("PMD.NullAssignment")
    public HawaiiSsoAuthenticationToken(final String accessToken) {
        credentials = new Credentials();
        credentials.setAccessToken(requireNonNull(accessToken));
        this.hawaiiSsoUserDetails = null;
        this.authenticated = false;
    }

    /**
     * Constructor that accepts a hawaiiSsoUserDetails with it's roles.
     */
    @SuppressWarnings("PMD.NullAssignment")
    public HawaiiSsoAuthenticationToken(final HawaiiSsoUserDetails hawaiiSsoUserDetails) {
        this.credentials = null;
        this.hawaiiSsoUserDetails = requireNonNull(hawaiiSsoUserDetails);
        this.authenticated = true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (hawaiiSsoUserDetails == null) {
            return Collections.emptyList();
        }
        return hawaiiSsoUserDetails.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HawaiiSsoUserDetails getDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HawaiiSsoUserDetails getPrincipal() {
        return hawaiiSsoUserDetails;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
        // not possible to set.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (hawaiiSsoUserDetails == null) {
            return "";
        }
        return hawaiiSsoUserDetails.getUsername();
    }

}
