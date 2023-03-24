package org.hawaiiframework.util;

/**
 * Interface to allow wrapping of a void-returning call.
 */
@FunctionalInterface
public interface Invocable {

    /**
     * The call to invoke.
     *
     * @throws Throwable a generic indication for the actual Exception.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    void invoke() throws Throwable;
}
