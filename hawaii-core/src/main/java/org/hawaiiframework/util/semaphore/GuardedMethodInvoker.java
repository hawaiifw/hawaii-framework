package org.hawaiiframework.util.semaphore;

/**
 * Guard that only one {@link GuardedMethod} is active at one point in time.
 */
public interface GuardedMethodInvoker {

    /**
     * Invoke the invocation if it's not active.
     *
     * @param method The method to invoke.
     * @return {@code true} if the invocation was attempted, {@code false} if another invocation was active.
     */
    boolean invokeIfNotActive(GuardedMethod method);
}
