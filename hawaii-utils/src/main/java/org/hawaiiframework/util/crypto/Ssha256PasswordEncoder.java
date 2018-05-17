package org.hawaiiframework.util.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder for salted SHA-256 passwords
 */
public class Ssha256PasswordEncoder extends SaltedSshaPasswordEncoder implements PasswordEncoder {

    /**
     * The hashing algorithm to use.
     */
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * the length of the hashed value.
     */
    private static final int HASH_LENGTH = 32;

    /**
     * The header to set in the ssha hash.
     */
    public static final String HEADER = "{SSHA256}";

    /**
     * Construct an instance with the default salt length
     */
    public Ssha256PasswordEncoder() {
        this(4);
    }

    /**
     * Construct an instance with the given salt length
     */
    public Ssha256PasswordEncoder(final int saltLength) {
        super(saltLength, HASH_ALGORITHM, HASH_LENGTH, HEADER);
    }

}
