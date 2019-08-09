package org.hawaiiframework.async;

/**
 * Interface to allow wrapping of an async call.
 */
@FunctionalInterface
public interface AsyncInvoke {

    /**
     * The call to invoke asynchronously.
     *
     * @throws Exception a generic indication for the actual Exception.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    void invoke() throws Exception;
}
