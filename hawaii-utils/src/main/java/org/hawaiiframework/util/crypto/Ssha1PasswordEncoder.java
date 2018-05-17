package org.hawaiiframework.util.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder for salted SHA-1 passwords
 */
public class Ssha1PasswordEncoder extends SaltedSshaPasswordEncoder implements PasswordEncoder {

    /**
     * The hashing algorithm to use.
     */
    private static final String HASH_ALGORITHM = "SHA-1";

    /**
     * the length of the hashed value.
     */
    private static final int HASH_LENGTH = 20;

    /**
     * The header to set in the ssha hash.
     */
    static public final String HEADER = "{SSHA}";

    /**
     * Construct an instance with the default salt length.
     */
    public Ssha1PasswordEncoder() {
        this(6);
    }

    /**
     * Construct an instance with the given salt length.
     */
    public Ssha1PasswordEncoder(final int saltLength) {
        super(saltLength, HASH_ALGORITHM, HASH_LENGTH, HEADER);
    }

}
