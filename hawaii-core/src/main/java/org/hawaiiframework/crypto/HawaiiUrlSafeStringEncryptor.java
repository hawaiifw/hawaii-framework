package org.hawaiiframework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.UrlBase64;
import org.hawaiiframework.exception.HawaiiException;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Security;


/**
 * Hawaii String Encryptor with Url safe base64 encoding.
 */
public class HawaiiUrlSafeStringEncryptor extends HawaiiStringEncryptor {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final String key;
    private final String initVector;

    /**
     * Creates a new {@code HawaiiUrlSafeStringEncryptor} with the given key and init vector.
     *
     * @param key        the key used for encryption/decryption
     * @param initVector the init vector used for encryption/decryption
     */
    public HawaiiUrlSafeStringEncryptor(final String key, final String initVector) {
        super(key, initVector);
        this.key = key;
        this.initVector = initVector;
    }

    /**
     * Encrypt the input message.
     *
     * @param message the message to be encrypted
     * @return the result of encryption
     */
    @Override
    public String encrypt(final String message) {
        try {
            final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key, initVector);
            final byte[] encrypted = cipher.doFinal(message.getBytes(Charset.defaultCharset()));

            final byte[] encoded = UrlBase64.encode(encrypted);
            return Strings.fromByteArray(encoded);
        } catch (GeneralSecurityException e) {
            throw new HawaiiException("Error encrypting message", e);
        }
    }

    /**
     * Decrypt the encrypted input message.
     *
     * @param encryptedMessage the message to be decrypted
     * @return the result of decryption
     */
    @Override
    public String decrypt(final String encryptedMessage) {
        try {
            final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key, initVector);
            final byte[] decrypted = cipher.doFinal(UrlBase64.decode(encryptedMessage));
            return new String(decrypted, Charset.defaultCharset());
        } catch (GeneralSecurityException e) {
            throw new HawaiiException("Error decrypting message", e);
        }
    }

}
