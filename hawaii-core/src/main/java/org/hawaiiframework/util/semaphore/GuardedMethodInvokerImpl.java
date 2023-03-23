package org.hawaiiframework.util.semaphore;

import java.util.concurrent.Semaphore;

/**
 * Default implementation of {@link GuardedMethodInvoker}.
 */
public class GuardedMethodInvokerImpl implements GuardedMethodInvoker {

    /**
     * The semaphore to guard the check with.
     * <p>
     * The use is to allow only one call to the check for activation method, without blocking callers.
     */
    private final Semaphore semaphore;

    /**
     * The constructor.
     */
    public GuardedMethodInvokerImpl() {
        this.semaphore = new Semaphore(1);
    }

    /**
     * Constructor an instance with a given semaphore.
     */
    public GuardedMethodInvokerImpl(final Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public boolean invokeIfNotActive(final GuardedMethod invocation) {
        if (semaphore.tryAcquire()) {
            try {
                invocation.invoke();
            } finally {
                semaphore.release();
            }
            return true;
        }
        return false;
    }

}
