package org.hawaiiframework.async;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;

/**
 * This class will handle all administrative calls which need to be done for every call.
 *
 * @author Richard Kohlen
 */
public abstract class HawaiiAsyncRunnable implements Runnable {

    /**
     * The abort strategy to set on the executing thread's ThreadLocal {@link SharedTaskContextHolder}.
     */
    protected final SharedTaskContext sharedTaskContext;

    /**
     * Constructor.
     *
     * @param sharedTaskContext the context for the api call thread
     */
    protected HawaiiAsyncRunnable(final SharedTaskContext sharedTaskContext) {
        this.sharedTaskContext = sharedTaskContext;
    }

    @Override
    public void run() {
        try {
            // Also registers KibanaLogFields
            SharedTaskContextHolder.register(sharedTaskContext);
            sharedTaskContext.registerLogFields();

            doRun();
        } finally {
            sharedTaskContext.removeLogFields();
            SharedTaskContextHolder.remove();
        }
    }

    /**
     * This method is executed by {@link Runnable} run. Run executes all administrative calls. Code specified in this
     * method will be executed in between those calls.
     */
    protected abstract void doRun();

}
