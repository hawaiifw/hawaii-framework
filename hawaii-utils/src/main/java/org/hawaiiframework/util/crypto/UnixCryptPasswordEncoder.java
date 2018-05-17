package org.hawaiiframework.util.crypto;

import org.apache.commons.codec.digest.UnixCrypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * PasswordMatcher encoder for Unix Crypt passwords.
 */
public class UnixCryptPasswordEncoder implements PasswordEncoder {

    /**
     * the header.
     */
    public static final String HEADER = "{CRYPT}";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixCryptPasswordEncoder.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(final CharSequence rawPassword) {
        return encode(rawPassword.toString(), RandomStringUtils.randomAlphanumeric(2));
    }

    private String encode(final String rawPassword, final String salt) {
        return HEADER + UnixCrypt.crypt(rawPassword, salt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        final String password = encodedPassword.substring(HEADER.length());
        final String inputPassword = encode(rawPassword.toString(), getSaltFromEncodedPassword(password));
        final boolean matches = Objects.equals(inputPassword, encodedPassword);
        LOGGER.debug("PasswordMatcher '{}' matches: '{}'", encodedPassword, matches);
        return matches;
    }

    private String getSaltFromEncodedPassword(final String encodedPassword) {
        return encodedPassword.substring(0, 2);
    }
}
