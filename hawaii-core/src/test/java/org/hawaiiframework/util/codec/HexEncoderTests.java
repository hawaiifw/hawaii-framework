package org.hawaiiframework.util.codec;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link HexEncoder}.
 *
 * @author Marcel Overdijk
 */
public class HexEncoderTests {

    private HexEncoder hexEncoder;

    @Before
    public void setUp() {
        hexEncoder = new HexEncoder();
    }

    @Test
    public void testEncodeDecodeHex() {

        final var str = "Hello World!";

        // Encode the string.
        final var encodedString = hexEncoder.encode(str);

        // Encoded string should not equal the original string and should be hex.
        assertThat(encodedString, is(not(equalTo(str))));
        assertThat(encodedString.matches("\\p{XDigit}+"), is(true));

        // Now decode the (encoded) string.
        final var decodedString = hexEncoder.decode(encodedString);

        // Decoded string should equal the original string.
        assertThat(decodedString, is(equalTo(str)));
    }
}
