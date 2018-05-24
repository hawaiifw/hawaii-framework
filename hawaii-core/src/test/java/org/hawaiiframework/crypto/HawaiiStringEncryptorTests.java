/*
 * Copyright 2015-2018 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link HawaiiStringEncryptor}.
 *
 * @author Wouter Eerdekens
 * @since 2.0.0
 */
public class HawaiiStringEncryptorTests {

    private HawaiiStringEncryptor hawaiiStringEncryptor;

    /**
     * The key and init vector used in this test were obtained by running the following openssl command:
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
