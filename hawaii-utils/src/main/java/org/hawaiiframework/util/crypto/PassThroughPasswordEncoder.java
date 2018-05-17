package org.hawaiiframework.util.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A special password encoder that accepts an already encoded password as input and returns it as is.
 * <p>
 * The {@link PasswordEncoder#matches(CharSequence, String)} method will always throw an exception!
 */
public class PassThroughPasswordEncoder implements PasswordEncoder {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(final CharSequence rawPassword) {
        return rawPassword.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        throw new IllegalArgumentException("This method is not implemented for pass through passwords.");
    }

}
