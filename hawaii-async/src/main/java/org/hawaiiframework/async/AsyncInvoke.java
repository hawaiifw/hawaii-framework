package org.hawaiiframework.async;

/**
 * Interface to allow wrapping of an async call.
 */
@FunctionalInterface
public interface AsyncInvoke {

    /**
     * The call to invoke asynchronously.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    void invoke() throws Exception;
}
