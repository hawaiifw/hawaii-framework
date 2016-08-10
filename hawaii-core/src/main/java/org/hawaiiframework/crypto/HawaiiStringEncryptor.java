/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.crypto;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.hawaiiframework.exception.HawaiiException;
import org.jasypt.encryption.StringEncryptor;

/**
 * Implementation of Jasypt's {@code StringEncryptor} interface. This class registers the Bouncy
 * Castle JCE Provider.
 *
 * @author Wouter Eerdekens
 * @see org.jasypt.encryption.StringEncryptor
 * @see org.bouncycastle.jce.provider.BouncyCastleProvider
 * @since 2.0.0
 */
public class HawaiiStringEncryptor implements StringEncryptor {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private String key;
    private String initVector;

    /**
     * Creates a new {@code HawaiiStringEncryptor} with the given key and init vector.
     * 
     * @param key the key used for encryption/decryption
     * @param initVector the init vector used for encryption/decryption
     */
    public HawaiiStringEncryptor(String key, String initVector) {
        this.key = Objects.requireNonNull(key);
        this.initVector = Objects.requireNonNull(initVector);
    }

    /**
     * Encrypt the input message.
     *
     * @param message the message to be encrypted
     * @return the result of encryption
     * @throws org.hawaiiframework.exception.HawaiiException when an error occurs.
     */
    @Override
    public String encrypt(String message) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key, initVector);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return Base64.toBase64String(encrypted);
        } catch (GeneralSecurityException e) {
            throw new HawaiiException("Error encrypting message", e);
        }
    }

    /**
     * Decrypt the encrypted input message.
     *
     * @param encryptedMessage the message to be decrypted
     * @return the result of decryption
     * @throws org.hawaiiframework.exception.HawaiiException when an error occurs.
     */
    @Override
    public String decrypt(String encryptedMessage) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key, initVector);
            byte[] decrypted = cipher.doFinal(Base64.decode(encryptedMessage));
            return new String(decrypted);
        } catch (GeneralSecurityException e) {
            throw new HawaiiException("Error decrypting message", e);
        }
    }

    private Cipher initCipher(int mode, String key, String initVector)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        SecretKeySpec secretKeySpec = new SecretKeySpec(hexStringToByteArray(key), "AES");
        IvParameterSpec initVectorSpec = new IvParameterSpec(hexStringToByteArray(initVector));
        cipher.init(mode, secretKeySpec, initVectorSpec);

        return cipher;
    }

    private byte[] hexStringToByteArray(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int high = hexToBin(s.charAt(i));
            int low = hexToBin(s.charAt(i + 1));
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException(
                        "contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (high * 16 + low);
        }

        return out;
    }

    private int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }
}
