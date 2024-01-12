package org.hawaiiframework.util;

/**
 * Interface to allow wrapping of a returning call.
 *
 * @param <T> The type to return.
 */
@FunctionalInterface
public interface Returnable<T> {

  /**
   * The call to invoke.
   *
   * @return the return value of the wrapped call.
   */
  T invoke() throws Throwable;
}
