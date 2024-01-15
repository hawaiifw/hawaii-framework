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
 * LogUtil to indent data.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public final class IndentUtil {

  /** The default indent to use if none specified. */
  public static final String DEFAULT_INDENTATION = "  ";

  /** The configured newline to look for. */
  private static final String NEW_LINE = System.lineSeparator();

  private IndentUtil() {
    // Utility constructor.
  }

  /**
   * Indent the {@code value} with the default indent. See {@link IndentUtil#DEFAULT_INDENTATION}.
   *
   * @param value The value to indent.
   * @return An indented string.
   */
  public static String indent(String value) {
    return indent(value, DEFAULT_INDENTATION);
  }

  /**
   * Indent the {@code value} with the given {@code indent}.
   *
   * @param value The value to indent.
   * @param indentation The indentation.
   * @return An indented string.
   */
  public static String indent(String value, String indentation) {
    return indentation + value.replace(NEW_LINE, String.format("%n%s", indentation));
  }
}
