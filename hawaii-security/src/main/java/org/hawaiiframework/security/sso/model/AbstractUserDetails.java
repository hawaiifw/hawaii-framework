package org.hawaiiframework.security.sso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A basic implementation of {@link HawaiiSsoUserDetails}.
 */
public abstract class AbstractUserDetails implements HawaiiSsoUserDetails {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -32246385373644452L;

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return getSubjectId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
