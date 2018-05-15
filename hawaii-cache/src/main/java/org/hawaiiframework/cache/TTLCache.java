package org.hawaiiframework.cache;

import org.hawaiiframework.time.HawaiiTime;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache to store objects.
 * <p>
 * The underlying cache implementation is free to expire objects before the
 * configured (or requested) expiry time. Reasons for this can be memory
 * pressure or (default) cache configuration.
 * <p>
 * In other words, there is no guarantee that a <code>cache.put(key, object)</code>
 * followed by a <code>cache.get(key)</code> will return the object stored.
 * In case the key is not found <code>null</code> will be returned.
 *
 * @param <T> the type of objects to store.
 */
public interface TTLCache<T> {

    /**
     * Put the object in the cache with the given <code>key</code>.
     * <p>
     * The object is stored for the default configured time, depending on the
     * cache implementation. See general remarks about cache evictions.
     *
     * @param key   The (not null) key to store the object under.
     * @param value The (not null) object to store.
     */
    void put(String key, T value);

    /**
     * Put the object in the cache with the given <code>key</code> for the
     * given <code>duration</code>.
     * <p>
     * The object is stored for the requested duration. See general remarks about cache evictions.
     *
     * @param key      The (not null) key to store the object under.
     * @param value    The (not null) object to store.
     * @param duration The (not null) duration to store the object for.
     */
    void put(String key, T value, Duration duration);

    /**
     * Put the object in the cache with the given <code>key</code> for until
     * <code>expiresAt</code> has come.
     * <p>
     * The object is stored and should be removed when <code>expiresAt</code> had come.
     *
     * @param key       The (not null) key to store the object under.
     * @param value     The (not null) object to store.
     * @param expiresAt The (not null) expiry time.
     */
    void put(String key, T value, HawaiiTime expiresAt);

    /**
     * Put the object in the cache with the given <code>key</code> for ever.
     * <p>
     * The object is stored and should never be removed. This should overrule configured default
     * expiry time for objects put in the cache. However, the object may still be evicted from
     * the cache, for instance for memory reasons.
     *
     * @param key   The (not null) key to store the object under.
     * @param value The (not null) object to store.
     */
    default void putEternally(String key, T value) {
        put(key, value, Duration.ofMillis(Long.MAX_VALUE));
    }

    /**
     * Retrieve the object stored under the <code>key</code>.
     *
     * @param key The (never null) key to retrieve the value with.
     * @return The value, or <code>null</code> if the object is not found.
     */
    T get(String key);

    /**
     * Retrieve an optional for the object stored under the <code>key</code>.
     *
     * @param key The (never null) key to retrieve the value with.
     * @return The <code>Optional</code> for the value.
     */
    default Optional<T> optional(String key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * Remove the value associate with the <code>key</code>.
     *
     * @param key The key to remove.
     */
    void remove(String key);
}
