package org.hawaiiframework.util.semaphore;

/**
 * Guard that only one {@link GuardedMethod} is active at one point in time.
 */
public interface GuardedMethodInvoker {

    /**
     * Invoke the invocation if it's not active.
     *
     * @param method The method to invoke.
     */
    void invokeIfNotActive(GuardedMethod method);
}
