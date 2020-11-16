package org.hawaiiframework.util.semaphore;

/**
 * Functional interface to allow shielding of the method to invoke using the {@link GuardedMethodInvoker}.
 */
@FunctionalInterface
public interface GuardedMethod {

    /**
     * Perform the method.
     */
    void invoke();
}
