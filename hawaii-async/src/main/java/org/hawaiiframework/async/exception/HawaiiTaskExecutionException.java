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

package org.hawaiiframework.async.exception;

import java.io.Serial;
import org.hawaiiframework.exception.HawaiiException;

/**
 * Exception thrown when a task's {@link java.util.concurrent.CompletableFuture#get()} throws an
 * error.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class HawaiiTaskExecutionException extends HawaiiException {

  /** The serial version UID. */
  @Serial private static final long serialVersionUID = -3946536802580483516L;

  /** Constructs a new {@code HawaiiTaskExecutionException}. */
  public HawaiiTaskExecutionException() {
    super();
  }

  /**
   * Constructs a new {@code HawaiiTaskExecutionException} with the supplied {@code message}.
   *
   * @param message the message.
   */
  public HawaiiTaskExecutionException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code HawaiiTaskExecutionException} with the supplied {@code message} and
   * {@code cause}.
   *
   * @param message the message.
   * @param cause The cause.
   */
  public HawaiiTaskExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new {@code HawaiiTaskExecutionException} with the supplied {@code cause}.
   *
   * @param cause The cause.
   */
  public HawaiiTaskExecutionException(Throwable cause) {
    super(cause);
  }
}
