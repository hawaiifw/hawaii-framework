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

package org.hawaiiframework.web.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.Serial;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class ResourceNotFoundException extends HttpException {

  /** The serial version UID. */
  @Serial private static final long serialVersionUID = 7464957757015625481L;

  /** Constructs a new {@code ResourceNotFoundException}. */
  public ResourceNotFoundException() {
    super(NOT_FOUND);
  }

  /**
   * Constructs a new {@code ResourceNotFoundException} with the supplied message.
   *
   * @param message The message to set.
   */
  public ResourceNotFoundException(String message) {
    super(message, NOT_FOUND);
  }

  /**
   * Constructs a new {@code ResourceNotFoundException} with the supplied message and {@link
   * Throwable}.
   *
   * @param message The message to set.
   * @param cause The cause.
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause, NOT_FOUND);
  }

  /**
   * Constructs a new {@code ResourceNotFoundException} with the supplied {@link Throwable}.
   *
   * @param cause The cause.
   */
  public ResourceNotFoundException(Throwable cause) {
    super(cause, NOT_FOUND);
  }
}
