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
 * Class that tries to mask a POST body or URI Query for password fields.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class UriQueryStringPasswordMasker implements PasswordMasker {

  /** Constant for URI query string field-value separation character. */
  private static final Character EQUALS = '=';

  /** Constant for URI query string field delimiter. */
  private static final Character AMPERSAND = '&';

  /** Constant for JSON field delimiter. */
  private static final Character QUOTE = '"';

  /** Constant for start of XML tag. */
  @SuppressWarnings({"PMD.ShortMethodName", "PMD.ShortVariable"})
  private static final Character LT = '<';

  /** Newline. */
  private static final Character NEWLINE = '\n';

  /** Carriage return. */
  private static final Character CARRIAGE_RETURN = '\r';

  @Override
  public boolean matches(MaskedPasswordBuilder builder) {
    if (builder.currentCharIs(EQUALS)) {
      // Assumption: start of URI query string (post body).
      int indexOfStartPassword = builder.getCurrentIndex();
      readUntilEndOfQueryParameterValue(builder);

      builder.maskPasswordAt(indexOfStartPassword + 1);
      return true;
    }

    return false;
  }

  /**
   * Returns the index of first character that is not part of the current query parameter.
   *
   * <p>That is, it returns the index of the first '&' following the {@code startIndex}, or, it
   * returns {@code input.length()}.
   */
  private static void readUntilEndOfQueryParameterValue(MaskedPasswordBuilder builder) {
    while (builder.hasNext()) {

      if (builder.currentCharIsOneOf(AMPERSAND, QUOTE, LT, NEWLINE, CARRIAGE_RETURN)) {
        break;
      }
      builder.next();
    }
  }
}
