package org.hawaiiframework.util.codec;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hawaiiframework.exception.HawaiiException;

/**
 * Utility class to encode and decode hex strings.
 *
 * @since 3.0.0.M6
 */
public class HexEncoder {

  /**
   * Encodes the provided {@link String}.
   *
   * @param str the string to encode
   * @return the encoded string
   */
  public String encode(String str) {
    return Hex.encodeHexString(str.getBytes(UTF_8));
  }

  /**
   * Decodes the provided hex {@link String}.
   *
   * @param str the hex string to decode
   * @return the decoded string
   */
  public String decode(String str) {
    try {
      return new String(Hex.decodeHex(str), UTF_8);
    } catch (DecoderException e) {
      throw new HawaiiException("Error decoding hex string: " + str, e);
    }
  }
}
