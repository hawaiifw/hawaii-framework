package org.hawaiiframework.util.predicate;

import java.util.function.Predicate;

/**
 * Simple predicate util.
 */
public final class PredicateUtils {

    /**
     * Private constructor.
     */
    private PredicateUtils() { }

    /**
     * Simple predicate method to negate a verification.
     *
     * @param predicate the predicate to invert.
     * @param <T> the predicate type.
     * @return the inverted predicate.
     */
    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return predicate.negate();
    }
}
