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
 * Class that tries to mask a password in a string.
 *
 * <p>The implementation tries to mask a field for a specific format, for instance JSON or XML.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public interface PasswordMasker {

  /**
   * Did the masked find a match and could it be masked?
   *
   * @param builder a "string builder" that builds a string without the password fields.
   * @return whether some masking has been done.
   */
  boolean matches(MaskedPasswordBuilder builder);
}
