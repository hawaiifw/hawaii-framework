package org.hawaiiframework.security.sso.model;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Interface for user details.
 */
public interface HawaiiSsoUserDetails extends UserDetails {

    @SuppressWarnings("PMD.CommentRequired") String getSubjectId();

    @SuppressWarnings("PMD.CommentRequired") Collection<String> getRoles();
}
