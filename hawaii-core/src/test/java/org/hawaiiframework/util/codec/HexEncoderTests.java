package org.hawaiiframework.util.codec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;
import org.junit.Test;

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

    var str = "Hello World!";

    // Encode the string.
    var encodedString = hexEncoder.encode(str);

    // Encoded string should not equal the original string and should be hex.
    assertThat(encodedString, is(not(equalTo(str))));
    assertThat(encodedString.matches("\\p{XDigit}+"), is(true));

    // Now decode the (encoded) string.
    var decodedString = hexEncoder.decode(encodedString);

    // Decoded string should equal the original string.
    assertThat(decodedString, is(equalTo(str)));
  }
}
