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

package org.hawaiiframework.logging.model;

import java.util.UUID;

/** Class that holds a request id in a ThreadLocal. */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class RequestId {

  /** The thread local id. */
  private static final ThreadLocal<UUID> UUID_THREAD_LOCAL = new InheritableThreadLocal<>();

  private RequestId() {
    // private constructor for utility class.
  }

  /**
   * Return the id as string.
   *
   * @return The ID as string, or {@code null} if not set.
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public static String get() {
    if (UUID_THREAD_LOCAL.get() == null) {
      return null;
    }
    return UUID_THREAD_LOCAL.get().toString();
  }

  /**
   * Set the request id.
   *
   * @param value The UUID to set.
   */
  public static void set(UUID value) {
    UUID_THREAD_LOCAL.set(value);
  }

  /** Clear the thread local. */
  public static void remove() {
    UUID_THREAD_LOCAL.remove();
  }
}
