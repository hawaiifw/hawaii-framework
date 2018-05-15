package org.hawaiiframework.logging.model;

import java.util.UUID;

/**
 * Class that holds a request id in a ThreadLocal.
 */
public final class RequestId {

    /**
     * The thread local id.
     */
    @SuppressWarnings("PMD.ShortVariable")
    private static ThreadLocal<UUID> id = new InheritableThreadLocal<>();

    private RequestId() {
        // private constructor for utility class.
    }

    /**
     * Return the id as string.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String get() {
        return id.get().toString();
    }

    /**
     * Set the request id.
     */
    public static void set(final UUID value) {
        id.set(value);
    }

    /**
     * Clear the thread local.
     */
    public static void remove() {
        id.remove();
    }

}
