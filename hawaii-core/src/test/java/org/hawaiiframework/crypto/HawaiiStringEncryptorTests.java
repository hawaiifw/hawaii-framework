package org.hawaiiframework.crypto;

import java.security.SecureRandom;

/**
 * Unit tests for HawaiiStringEncryptor.
 *
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
public class HawaiiStringEncryptorTest {

    private HawaiiStringEncryptor hawaiiStringEncryptor;

    /**
     * The key and init vector used in this test were obtained by
     * running the following openssl command:
     *
     *  <pre>
     *      openssl enc -aes-128-cbc -k supersecret -P -md sha1
     *
     *      
     *  </pre>
     */
    public void setUp() {
        String key ="supersecret";
        String initVector = "";
    }

}
