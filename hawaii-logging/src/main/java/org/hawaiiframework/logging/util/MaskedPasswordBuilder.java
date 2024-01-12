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

import static java.util.Objects.requireNonNull;

/**
 * A string visitor that builds the same string, but then with masked passwords.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class MaskedPasswordBuilder {

  /** Constant for the masked password. */
  private static final String MASKED_PASSWORD = "***";

  /** The pattern to search for. */
  private final String pattern;

  /** The length of the search pattern. */
  private final int patternLength;

  /** The string that contains the {@link MaskedPasswordBuilder#pattern} string. */
  private final String stringToMask;

  /** The index of the current character. */
  private int currentIndex;

  /** The saved index, set by {@link MaskedPasswordBuilder#mark()} method. */
  private int savedCurrentIndex;

  /** The index of the last added character. */
  private int lastAddedIndex;

  /** The string builder that contains the masked input string. */
  @SuppressWarnings("PMD.AvoidStringBufferField")
  private final StringBuilder result = new StringBuilder();

  /**
   * The constructor.
   *
   * @param stringToMask The string to mask.
   * @param pattern The pattern to mask.
   */
  public MaskedPasswordBuilder(String stringToMask, String pattern) {
    this.stringToMask = requireNonNull(stringToMask);
    this.pattern = pattern;
    this.patternLength = pattern.length();
  }

  /**
   * Returns {@code true} if the string to mask has more characters.
   *
   * @return {@code true} if the string to mask has more characters.
   */
  public boolean hasNext() {
    return currentIndex < stringToMask.length();
  }

  /** Advances the cursor to the next character. */
  public void next() {
    currentIndex++;
  }

  /**
   * Returns {@code true} of the current character is one of the {@code choices}.
   *
   * @param choices Check whether the current char is one of the choices.
   * @return {@code true} of the current character is one of the {@code choices}.
   */
  public boolean currentCharIsOneOf(Character... choices) {
    for (Character choice : choices) {
      if (currentCharIs(choice)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if the current character is {@code character}.
   *
   * @param character The character to check.
   * @return {@code true} if the current character is {@code character}.
   */
  public boolean currentCharIs(Character character) {
    return character.equals(getCurrentChar());
  }

  /**
   * Returns {@code true} if the current character is a whitespace.
   *
   * @return {@code true} if the current character is a whitespace.
   */
  public boolean currentCharIsWhitespace() {
    return Character.isWhitespace(getCurrentChar());
  }

  private Character getCurrentChar() {
    return stringToMask.charAt(currentIndex);
  }

  /** Saves the current index, to be used by {@link MaskedPasswordBuilder#reset}. */
  public void mark() {
    savedCurrentIndex = currentIndex;
  }

  /**
   * Resets the current character (or index) to the one set by {@link MaskedPasswordBuilder#mark()}.
   */
  public void reset() {
    currentIndex = savedCurrentIndex;
  }

  /**
   * Appends the password mask at the {@code index}.
   *
   * @param index the index in the current buffer to append the masked password at.
   */
  public void maskPasswordAt(Integer index) {
    result.append(stringToMask, lastAddedIndex, index);
    result.append(MASKED_PASSWORD);
    lastAddedIndex = currentIndex;
  }

  /**
   * Returns the current index.
   *
   * @return the current index.
   */
  public int getCurrentIndex() {
    return currentIndex;
  }

  /**
   * Find the next password in the input.
   *
   * @return {@code true} if there is another password to be found in the string to mask.
   */
  public boolean findNextPassword() {
    int index = stringToMask.indexOf(pattern, currentIndex);
    if (index <= -1) {
      return false;
    }
    currentIndex = index + patternLength;
    return true;
  }

  /**
   * Builds the masked string.
   *
   * @return the masked string.
   */
  public String build() {
    result.append(stringToMask.substring(lastAddedIndex));
    return result.toString();
  }
}
