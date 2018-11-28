/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.cache;

import org.hawaiiframework.time.HawaiiTime;

import javax.validation.constraints.NotNull;
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
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
public interface Cache<T> {

    /**
     * Put the object in the cache with the given <code>key</code>.
     * <p>
     * The object is stored for the default configured time, depending on the
     * cache implementation. See general remarks about cache evictions.
     *
     * @param key   The (not null) key to store the object under.
     * @param value The (not null) object to store.
     */
    void put(@NotNull String key, @NotNull T value);

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
    void put(@NotNull String key, @NotNull T value, @NotNull Duration duration);

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
    void put(@NotNull String key, @NotNull T value, @NotNull HawaiiTime expiresAt);

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
    default void putEternally(@NotNull String key, @NotNull T value) {
        put(key, value, Duration.ZERO);
    }

    /**
     * Retrieve the object stored under the <code>key</code>.
     *
     * @param key The (never null) key to retrieve the value with.
     * @return The value, or <code>null</code> if the object is not found.
     */
    T get(@NotNull String key);

    /**
     * Retrieve an optional for the object stored under the <code>key</code>.
     *
     * @param key The (never null) key to retrieve the value with.
     * @return The <code>Optional</code> for the value.
     */
    default Optional<T> optional(@NotNull String key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * Remove the value associate with the <code>key</code>.
     *
     * @param key The key to remove.
     */
    void remove(@NotNull String key);

}
