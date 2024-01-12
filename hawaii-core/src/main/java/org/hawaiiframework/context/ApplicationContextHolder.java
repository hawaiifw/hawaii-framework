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

package org.hawaiiframework.context;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public final class ApplicationContextHolder {

  private static final ConcurrentHashMap<ClassLoader, ApplicationContext> CONTEXT_MAP =
      new ConcurrentHashMap<>();

  private ApplicationContextHolder() {}

  public static ApplicationContext getRequiredApplicationContext() {
    ApplicationContext context = getApplicationContext();
    if (context == null) {
      throw new IllegalStateException("No ApplicationContext found");
    }
    return context;
  }

  public static ApplicationContext getApplicationContext() {
    ClassLoader classLoader = getContextClassLoader();
    while (classLoader != null) {
      ApplicationContext applicationContext = CONTEXT_MAP.get(classLoader);
      if (applicationContext != null) {
        return applicationContext;
      }
      classLoader = classLoader.getParent();
    }
    return null;
  }

  /**
   * Binds the {@link ApplicationContext} to the current context class loader.
   *
   * @param context the application context to bind
   */
  public static void bind(ApplicationContext context) {
    Object old = CONTEXT_MAP.putIfAbsent(getContextClassLoader(), context);
    if (old != null) {
      throw new IllegalStateException(
          "ApplicationContext already bound to the class loader of the current thread");
    }
  }

  /** Releases the {@link ApplicationContext} associated with the current context class loader. */
  public static void release() {
    CONTEXT_MAP.remove(getContextClassLoader());
  }

  private static ClassLoader getContextClassLoader() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      throw new IllegalStateException("Unable to get the class loader for the current thread");
    }
    return classLoader;
  }
}
