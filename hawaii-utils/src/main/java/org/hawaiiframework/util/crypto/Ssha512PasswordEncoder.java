package org.hawaiiframework.util.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Encoder for salted SHA-512 hashes.
 */
public class Ssha512PasswordEncoder extends SaltedSshaPasswordEncoder implements PasswordEncoder {

    /**
     * The hashing algorithm to use.
     */
    private static final String HASH_ALGORITHM = "SHA-512";

    /**
     * the length of the hashed value.
     */
    private static final int HASH_LENGTH = 64;

    /**
     * The header to set in the ssha hash.
     */
    public static final String HEADER = "{SSHA512}";

    /**
     * Construct an instance with the default salt length
     */
    public Ssha512PasswordEncoder() {
        this(16);
    }

    /**
     * Construct an instance with the given salt length
     */
    public Ssha512PasswordEncoder(final int saltLength) {
        super(saltLength, HASH_ALGORITHM, HASH_LENGTH, HEADER);
    }


}
