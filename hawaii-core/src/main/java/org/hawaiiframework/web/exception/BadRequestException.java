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

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.Serial;

/**
 * A Bad Request exception.
 *
 * @author Ivan Melotte
 * @since 2.0.0
 */
public class BadRequestException extends HttpException {

  /** The serial version UID. */
  @Serial private static final long serialVersionUID = -4395628375914269570L;

  /** Constructs a new {@code BadRequestException}. */
  public BadRequestException() {
    super(BAD_REQUEST);
  }

  /** Constructs a new {@code BadRequestException} with the supplied message. */
  public BadRequestException(String message) {
    super(message, BAD_REQUEST);
  }

  /**
   * Constructs a new {@code BadRequestException} with the supplied message and {@link Throwable}.
   */
  public BadRequestException(String message, Throwable cause) {
    super(message, cause, BAD_REQUEST);
  }

  /** Constructs a new {@code BadRequestException} with the supplied {@link Throwable}. */
  public BadRequestException(Throwable cause) {
    super(cause, BAD_REQUEST);
  }
}
