package org.hawaiiframework.crypto;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for HawaiiStringEncryptor.
 *
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
public class HawaiiStringEncryptorTests {

    private HawaiiStringEncryptor hawaiiStringEncryptor;

    /**
     * The key and init vector used in this test were obtained by
     * running the following openssl command:
     * <p>
     * <pre>
     *      openssl enc -aes-128-cbc -k supersecret -P -md sha1
     *
     *      salt=17E0643BA7B6B022
     *      key=513D515613B7354770285477900F12A5
     *      iv =C4E1972E3F882415767C2E1E32A51D93
     *  </pre>
     */
    @Before
    public void setUp() {
        String key = "513D515613B7354770285477900F12A5";
        String initVector = "C4E1972E3F882415767C2E1E32A51D93";
        hawaiiStringEncryptor = new HawaiiStringEncryptor(key, initVector);
    }

    @Test
    public void testSuccesfulEncryptDecryptCycle() throws Exception {
        String message = "message to be encrypted";
        String encrypted = hawaiiStringEncryptor.encrypt(message);
        String decrypted = hawaiiStringEncryptor.decrypt(encrypted);
        assertThat(decrypted, is(equalTo(message)));
    }

}
