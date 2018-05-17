package org.hawaiiframework.security.sso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Represents an email address in either a confirmed
 * or an unconfirmed state.
 */
public class EmailAddress implements Serializable {

    private static final long serialVersionUID = -543267091090453349L;

    /**
     * The confirmed flag.
     */
    private Boolean confirmed = false;

    /**
     * The email address.
     */
    private String address;

    /**
     * Default constructor.
     */
    public EmailAddress() {
        // Do nothing.
    }

    /**
     * Construct an email address for @code{address} and @code{confirmed}.
     */
    public EmailAddress(final String address, final Boolean confirmed) {
        this.confirmed = confirmed;
        this.address = address;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Boolean isConfirmed() {
        return confirmed;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setConfirmed(final Boolean confirmed) {
        this.confirmed = confirmed;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getAddress() {
        return address;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * Convenience method to return the email address as a String.
     *
     * @return the email address String
     */
    @JsonIgnore
    public String getAddressAsString() {
        if (address == null) {
            return null;
        }
        return address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return address == null ? "" : address + "(" + confirmed + ")";
    }

}
