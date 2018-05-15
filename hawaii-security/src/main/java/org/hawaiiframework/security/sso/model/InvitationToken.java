package org.hawaiiframework.security.sso.model;

/**
 * A wrapper around a JWT to retrieve InvitationToken specific claims.
 */
public class InvitationToken extends TokenWrapper {

    /**
     * The constructor.
     */
    public InvitationToken(final String tokenValue) {
        super(tokenValue);
    }

    /**
     * Get the {@code contact_id}.
     */
    public String getContactId() {
        return getStringClaim("contact_id");
    }

    /**
     * Get the {@code invitation_id}.
     */
    public String getInvitationId() {
        return getStringClaim("invitation_id");
    }

}
