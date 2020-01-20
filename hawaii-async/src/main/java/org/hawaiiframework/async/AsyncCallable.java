package org.hawaiiframework.async;

/**
 * Interface to allow wrapping of an async call.
 *
 * @param <T> The return type.
 */
@FunctionalInterface
public interface AsyncCallable<T> {

    /**
     * The call to invoke asynchronously.
     *
     * @return T the return type.
     * @throws Exception a generic indication for the actual Exception.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    T invoke() throws Exception;
}
