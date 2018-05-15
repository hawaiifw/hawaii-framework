package org.hawaiiframework.security.sso.web.controller;

/**
 * Class that holds the paths for the controllers.
 */
public final class Paths {

    /**
     * The base part of all rest API paths.
     */
    private static final String BASE_PATH = "/rest/public";

    /**
     * The path for users. (And creation of an instance.)
     */
    public static final String CHECK_TOKEN = BASE_PATH + "/sso/check-tokens";

    /**
     * Private constructor to prevent instantiation.
     */
    private Paths() {
        // no-op
    }

}
