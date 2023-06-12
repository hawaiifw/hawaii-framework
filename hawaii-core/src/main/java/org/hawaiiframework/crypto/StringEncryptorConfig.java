package org.hawaiiframework.crypto;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * Config class to set up the {@link StringEncryptor} for encrypted properties.
 * <p>
 * This is moved to a separate class to make sure the StringEncryptor, which we're
 * overriding here to use the {@link HawaiiStringEncryptor} is created before any encrypted
 * properties are accessed. Otherwise, Jasypt will fall back to its default {@link StringEncryptor}.
 */
@Configuration
public class StringEncryptorConfig {

    /**
     * The number of parts the encryption initialisation string should consist of.
     */
    private static final Integer NUM_PARTS = 2;

    /**
     * Name of the environment variable with which the encryption is initialized.
     */
    @Value("${hawaii.crypto.init:}")
    private String encryptionInitProperty;

    /**
     * Creates a {@link StringEncryptor} to decrypt encrypted property values.
     * <p>
     * The environment variable identified by encryptionInit contains the
     * key and the init vector.
     *
     * @return a new {@link HawaiiStringEncryptor}
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        Assert.hasLength(
                encryptionInitProperty,
                "Encryption cannot be initialized without 'hawaii.crypto.init' configuration. "
                        + "Or disable via 'hawaii.crypto.enabled: false'.");

        final String encryptionInit = System.getenv(encryptionInitProperty);
        Assert.hasLength(
            encryptionInit,
            String.format("Encryption cannot be initialized without '%s' environment variable.", encryptionInitProperty));

        final String[] parts = splitEncryptionInitializationString(encryptionInit);
        return createStringEncryptor(parts);
    }

    private StringEncryptor createStringEncryptor(final String... parts) {
        return new HawaiiStringEncryptor(parts[0], parts[1]);
    }

    private String[] splitEncryptionInitializationString(final String encryptionInit) {
        final String[] parts = encryptionInit.split(":");
        if (parts.length != NUM_PARTS) {
            throw new IllegalArgumentException(
                String.format(
                    "The environment variable '%s' must consist of two parts, separated by a colon.", encryptionInit));
        }
        return parts;
    }

    public void setEncryptionInitProperty(final String encryptionInitProperty) {
        this.encryptionInitProperty = encryptionInitProperty;
    }
}
