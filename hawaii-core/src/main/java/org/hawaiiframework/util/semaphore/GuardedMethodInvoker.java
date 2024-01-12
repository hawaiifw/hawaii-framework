package org.hawaiiframework.util.semaphore;

import org.hawaiiframework.util.Invocable;

/** Guard that only one {@link Invocable} is active at one point in time. */
public interface GuardedMethodInvoker {

  /**
   * Invoke the invocation if it's not active.
   *
   * @param method The method to invoke.
   * @return {@code true} if the invocation was attempted, {@code false} if another invocation was
   *     active.
   */
  boolean invokeIfNotActive(Invocable method);
}
