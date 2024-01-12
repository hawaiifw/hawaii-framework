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

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import org.hawaiiframework.exception.HawaiiException;
import org.springframework.http.HttpStatus;

/**
 * @author Ivan Melotte
 * @since 2.0.0
 */
public class HttpException extends HawaiiException {

  /** The serial version UID. */
  @Serial private static final long serialVersionUID = -5505430727908889048L;

  private final HttpStatus httpStatus;

  /** Constructs a new {@code HttpException} with the supplied {@link HttpStatus}. */
  public HttpException(HttpStatus httpStatus) {
    this.httpStatus = requireNonNull(httpStatus);
  }

  /** Constructs a new {@code HttpException} with the supplied message and {@link HttpStatus}. */
  public HttpException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = requireNonNull(httpStatus);
  }

  /**
   * Constructs a new {@code HttpException} with the supplied message, {@link Throwable} and {@link
   * HttpStatus}.
   */
  public HttpException(String message, Throwable cause, HttpStatus httpStatus) {
    super(message, cause);
    this.httpStatus = requireNonNull(httpStatus);
  }

  /**
   * Constructs a new {@code HttpException} with the supplied {@link Throwable} and {@link
   * HttpStatus}.
   */
  public HttpException(Throwable cause, HttpStatus httpStatus) {
    super(cause);
    this.httpStatus = requireNonNull(httpStatus);
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
