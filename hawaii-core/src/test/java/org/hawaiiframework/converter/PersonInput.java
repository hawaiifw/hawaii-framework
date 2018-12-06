package org.hawaiiframework.converter;

import java.util.List;

/**
 * Sample bean to convert.
 */
public class PersonInput {

    private String name;

    @SuppressWarnings("PMD.CommentRequired")
    public String getName() {
        return name;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setName(final String name) {
        this.name = name;
    }

}
