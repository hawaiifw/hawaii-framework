package org.hawaiiframework.cache;

/**
 * The base Cache interface.
 * @param <T> The type of the interface elements.
 */
public interface Cache<T> {

    /**
     * number of seconds in 30 days.
     */
    int THIRTY_DAYS = 60 * 60 * 24 * 30;

    /**
     * number of seconds in one hour.
     */
    int ONE_HOUR = 60 * 60;

    /**
     * Returns a previously-stored object, or null if key not found.
     *
     * @param key the key of the cache entry
     * @return the previously-stored object, or null if key not found
     * @throws CacheServiceException in case of any error
     * @throws ClassCastException    in case object cannot be cast to type T
     */
    T get(String key) throws CacheServiceException, ClassCastException;

    /**
     * Stores an object into the cache using the given key. Uses default
     * expiration time.
     *
     * @param key   the key for the new cache entry
     * @param value the object to be stored
     * @throws CacheServiceException in case of any error
     */
    void put(String key, T value) throws CacheServiceException;

    /**
     * Stores an object into the cache using the given key.
     * <p>
     * The expiration may either be Unix time (number of seconds since January
     * 1, 1970, as a 32-bit value), or a number of seconds starting from current
     * time. In the latter case, this number of seconds may not exceed
     * 60*60*24*30 (number of seconds in 30 days); if the number sent by a
     * client is larger than that, the server will consider it to be real Unix
     * time value rather than an offset from current time.
     *
     * @param key        the key for the new cache entry
     * @param expiration the expiration of the object
     * @param object     the object to be stored
     * @throws CacheServiceException in case of any error
     */
    void put(String key, int expiration, T object) throws CacheServiceException;

    /**
     * Removes the key from the cache.
     *
     * @param key the key of the cache entry
     * @throws CacheServiceException in case of any error
     */
    void delete(String key) throws CacheServiceException;

    /**
     * Clears the cache (removing all items).
     *
     * @throws CacheServiceException in case of any error
     */
    void clear() throws CacheServiceException;

}
