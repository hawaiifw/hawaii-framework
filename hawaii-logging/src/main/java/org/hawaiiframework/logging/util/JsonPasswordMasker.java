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

package org.hawaiiframework.logging.util;

/**
 * Masks passwords in a json key value.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class JsonPasswordMasker implements PasswordMasker {

  /** Constant for JSON escape character. */
  private static final Character JSON_ESCAPE = '\\';

  /** Constant for JSON field-value separation character. */
  private static final Character COLON = ':';

  /** Constant for JSON field delimiter. */
  private static final Character QUOTE = '"';

  /** {@inheritDoc} */
  @Override
  public boolean matches(MaskedPasswordBuilder builder) {
    if (builder.currentCharIs(QUOTE)) {
      // Assumption: start of json.
      builder.mark();
      builder.next();
      if (readUntilStartOfJsonValue(builder)) {
        Integer indexOfStartPassword = builder.getCurrentIndex();
        if (readUntilEndOfJsonValue(builder)) {
          builder.maskPasswordAt(indexOfStartPassword + 1);
          return true;
        }
      }

      builder.reset();
    }
    return false;
  }

  /**
   * Returns the index of the QUOTE that starts the JSON value.
   *
   * <p>Will return {@code null} if there is no quote found.
   */
  private static boolean readUntilStartOfJsonValue(MaskedPasswordBuilder builder) {
    readWhiteSpaces(builder);
    if (builder.currentCharIs(COLON)) {
      builder.next();
      readWhiteSpaces(builder);

      return builder.currentCharIs(QUOTE);
    }
    return false;
  }

  /**
   * Returns the index of the QUOTE that ends the JSON value.
   *
   * <p>Will return {@code null} if there is no quote found.
   */
  private static boolean readUntilEndOfJsonValue(MaskedPasswordBuilder builder) {
    boolean escape = false;
    while (builder.hasNext()) {
      builder.next();
      if (!escape && builder.currentCharIs(QUOTE)) {
        return true;
      }
      escape = builder.currentCharIs(JSON_ESCAPE);
    }
    return false;
  }

  private static void readWhiteSpaces(MaskedPasswordBuilder builder) {
    while (builder.currentCharIsWhitespace() && builder.hasNext()) {
      builder.next();
    }
  }
}
