package org.hawaiiframework.util.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * PasswordMatcher encoder for SHA passwords.
 */
public class Sha1PasswordEncoder implements PasswordEncoder {

    /**
     * the header.
     */
    public static final String HEADER = "{SHA}";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Sha1PasswordEncoder.class);

    /**
     * the base64 encoder.
     */
    private final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(final CharSequence rawPassword) {
        try {
            final MessageDigest alg = MessageDigest.getInstance("SHA-1");
            return encode(alg, rawPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("We do have the 'SHA-1' algorithm available. Encoded string is null.");
            return null;
        }
    }

    private String encode(final MessageDigest alg, final CharSequence rawPassword) {
        final byte[] msg = toByteArray(rawPassword.toString());
        final byte[] hash = alg.digest(msg);
        final byte[] sha1 = encoder.encode(hash);

        final byte[] base64 = Arrays.copyOf(HEADER.getBytes(UTF_8), HEADER.length() + sha1.length);
        System.arraycopy(sha1, 0, base64, HEADER.length(), sha1.length);

        return new String(base64, UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        final String inputPassword = encode(rawPassword);
        final boolean matches = Objects.equals(inputPassword, encodedPassword);
        LOGGER.debug("PasswordMatcher '{}' matches: '{}'", encodedPassword, matches);
        return matches;
    }

    private byte[] toByteArray(final String msg) {
        return msg.getBytes(UTF_8);
    }



}
