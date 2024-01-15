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
 * A password masker for XML tags.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class XmlAttributePasswordMasker implements PasswordMasker {

  /** Constant for start of XML tag. */
  @SuppressWarnings({"PMD.ShortMethodName", "PMD.ShortVariable"})
  private static final Character LT = '<';

  /** Constant for end of XML tag. */
  @SuppressWarnings({"PMD.ShortMethodName", "PMD.ShortVariable"})
  private static final Character GT = '>';

  /** {@inheritDoc} */
  @Override
  public boolean matches(MaskedPasswordBuilder builder) {
    if (builder.currentCharIs(GT)) {
      // Assumption: end of XML tag.
      builder.mark();
      int indexOfStartPassword = builder.getCurrentIndex();
      if (readUntilEndOfXmlValue(builder)) {
        builder.maskPasswordAt(indexOfStartPassword + 1);
        readUntilEndOfXmlTag(builder);
        return true;
      } else {
        builder.reset();
      }
    }
    return false;
  }

  /**
   * Returns the index of the LT ('<') that ends the XML value.
   *
   * <p>Will return {@code null} if there is no '<' found.
   */
  private static boolean readUntilEndOfXmlValue(MaskedPasswordBuilder builder) {
    while (builder.hasNext()) {
      if (builder.currentCharIs(LT)) {
        return true;
      }
      builder.next();
    }
    return false;
  }

  private static void readUntilEndOfXmlTag(MaskedPasswordBuilder builder) {
    while (builder.hasNext()) {
      if (builder.currentCharIs(GT)) {
        break;
      }
      builder.next();
    }
  }
}
