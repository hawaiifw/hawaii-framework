package org.hawaiiframework.security.sso.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The user of the application.
 * <p>
 * Warning the boolean flags as defined by UserDetails are set to a (sort of) sensible defaults.
 */
public class DefaultHawaiiSsoUserDetails extends AbstractUserDetails implements HawaiiSsoUserDetails {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 3386510006849237419L;

    /**
     * The user's SSO unique id (subject id in openid speak).
     */
    private final String subjectId;

    /**
     * The user's roles.
     */
    private final List<String> roles = new ArrayList<>();

    /**
     * Construct a user details object.
     *
     * @param subjectId the subject id
     * @param roles the roles (optional)
     */
    public DefaultHawaiiSsoUserDetails(final String subjectId, final String... roles) {
        this.subjectId = requireNonNull(subjectId);
        if (roles != null) {
            this.roles.addAll(Arrays.asList(roles));
        }
    }

    @Override @SuppressWarnings("PMD.CommentRequired")
    public String getSubjectId() {
        return subjectId;
    }

    @Override @SuppressWarnings("PMD.CommentRequired")
    public List<String> getRoles() {
        return roles;
    }

}
