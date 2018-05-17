package org.hawaiiframework.util;

import org.hawaiiframework.util.crypto.Sha1PasswordEncoder;

/**
 * Utility for hashing Strings.
 */
public final class HashingUtil {

    /**
     * The Sha1PasswordEncoder.
     */
    private static Sha1PasswordEncoder sha1PasswordEncoder = new Sha1PasswordEncoder();


    /**
     * The constructor.
     */
    private HashingUtil() {
        // private utility constructor.
    }

    /**
     * Encrypts a password using Sha1, as required in various Akana calls.
     */
    public static String generateHashForAkana(final String input) {
        return sha1PasswordEncoder.encode(input);
    }


}
