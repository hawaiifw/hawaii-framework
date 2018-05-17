package org.hawaiiframework.util.crypto;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * PasswordEncoder for salted digests.
 */
public class SaltedSshaPasswordEncoder implements PasswordEncoder {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaltedSshaPasswordEncoder.class);

    /**
     * the base64 encoder.
     */
    private final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * the base64 decoder.
     */
    private final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * The digest algorithm.
     */
    private final String digestAlgorithm;

    /**
     * the length of the hashed value.
     */
    private final int digestLength;

    /**
     * The header.
     */
    private final String header;

    /**
     * the salt length.
     */
    private final int saltLength;

    /**
     * Construct an instance with the default salt length.
     */
    public SaltedSshaPasswordEncoder(final String digestAlgorithm, final int digestLength, final String header) {
        this(16, digestAlgorithm, digestLength, header);
    }

    /**
     * Construct an instance with the given salt length.
     */
    public SaltedSshaPasswordEncoder(final int saltLength, final String digestAlgorithm, final int digestLength, final String header) {
        this.saltLength = saltLength;
        this.digestAlgorithm = digestAlgorithm;
        this.digestLength = digestLength;
        this.header = header;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        if (StringUtils.isEmpty(rawPassword) || StringUtils.isEmpty(encodedPassword)) {
            return false;
        }
        final byte[] input = encodedPassword.getBytes(UTF_8);
        final byte[] ssha256 = Arrays.copyOfRange(input, header.length(), input.length);
        final byte[] hashAndSalt = decoder.decode(ssha256);
        final byte[] salt = Arrays.copyOfRange(hashAndSalt, digestLength, hashAndSalt.length);

        final boolean matches = encodedPassword.equals(encode(rawPassword.toString(), salt));
        LOGGER.debug("PasswordMatcher '{}' matches: '{}'.", encodedPassword, matches);
        return matches;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private byte[] getSalt() {
        final String salt = RandomStringUtils.random(saltLength);
        return salt.getBytes(UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(final CharSequence rawPassword) {
        final String inputPassword = rawPassword.toString();
        return encode(inputPassword, getSalt());
    }

    private String encode(final String rawPassword, final byte[] salt) {
        try {
            final byte[] hash = digest(rawPassword.getBytes(UTF_8), salt);
            final byte[] hashAndSalt = Arrays.copyOf(hash, digestLength + salt.length);
            System.arraycopy(salt, 0, hashAndSalt, digestLength, salt.length);

            final byte[] saltedDigest = encoder.encode(hashAndSalt);

            final byte[] base64 = Arrays.copyOf(header.getBytes(UTF_8), header.length() + saltedDigest.length);
            System.arraycopy(saltedDigest, 0, base64, header.length(), saltedDigest.length);
            return new String(base64, UTF_8);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("We do have the '{}' algorithm available. Encoded string is null.", digestAlgorithm);
            return null;
        }
    }

    private byte[] digest(final byte[] msg, final byte[] salt) throws NoSuchAlgorithmException {
        final MessageDigest alg = MessageDigest.getInstance(digestAlgorithm);
        return digest(alg, msg, salt);
    }

    private byte[] digest(final MessageDigest alg, final byte[] msg, final byte[] salt) throws NoSuchAlgorithmException {
        final byte[] input = Arrays.copyOf(msg, msg.length + salt.length);
        System.arraycopy(salt, 0, input, msg.length, salt.length);
        return alg.digest(input);
    }

}
