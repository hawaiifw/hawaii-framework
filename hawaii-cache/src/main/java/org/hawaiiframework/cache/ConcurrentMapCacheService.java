/**
 * Copyright 2015 Q24
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link Cache} implementation using an in-memory concurrent map. Just
 * as in Memcache, a 0 value as expiration date is used to express unlimited
 * expiration.
 */
public class ConcurrentMapCacheService implements Cache<Object> {

    private static final String CACHE_RETRIEVE_EXCEPTION_MESSAGE = "Error retrieving object with key '%s' from ConcurrentMap";
    private static final String CACHE_STORE_EXCEPTION_MESSAGE = "Error storing object with key '%s' in ConcurrentMap";
    private static final String CACHE_DELETE_EXCEPTION_MESSAGE = "Error deleting object with key '%s' from ConcurrentMap";

    private static final int UNLIMITED_EXPIRATION = 0;

    private final ConcurrentMap<String, Object> store;
    private final ConcurrentMap<String, Long> expirations;
    private final int defaultExpiration;


    /**
     * The default constructor.
     */
    public ConcurrentMapCacheService() {
        this(ONE_HOUR);
    }

    /**
     * The constructor with timeout.
     *
     * @param defaultExpiration the default expected expiration.
     */
    public ConcurrentMapCacheService(final int defaultExpiration) {
        this.store = new ConcurrentHashMap<String, Object>();
        this.expirations = new ConcurrentHashMap<String, Long>();
        this.defaultExpiration = defaultExpiration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final String key) throws CacheServiceException, ClassCastException {
        try {
            final Long expiration = expirations.get(key);
            if (expiration != null && expiration != UNLIMITED_EXPIRATION && expiration < System.currentTimeMillis()) {
                delete(key);
                return null;
            }
            return store.get(key);
        } catch (Exception e) {
            throw new CacheServiceException(String.format(CACHE_RETRIEVE_EXCEPTION_MESSAGE, key), e);
        }
    }

    public Long getExpiration(final String key) throws CacheServiceException {
        try {
            return expirations.get(key);
        } catch (Exception e) {
            throw new CacheServiceException(String.format(CACHE_RETRIEVE_EXCEPTION_MESSAGE, key), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final Object object) throws CacheServiceException {
        put(key, defaultExpiration, object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final int expiration, final Object object) throws CacheServiceException {
        try {
            store.put(key, object);
            // see
            // http://dustin.sallings.org/java-memcached-client/apidocs/net/spy/memcached/MemcachedClient.html#set(java.lang.String,
            // int, java.lang.Object) for logic behind expiration
            final long calculatedExpiration;
            if (expiration == UNLIMITED_EXPIRATION || expiration > THIRTY_DAYS) {
                calculatedExpiration = expiration;
            } else {
                calculatedExpiration = System.currentTimeMillis() + (expiration * 1000);
            }
            expirations.put(key, calculatedExpiration);
        } catch (Exception e) {
            throw new CacheServiceException(String.format(CACHE_STORE_EXCEPTION_MESSAGE, key), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final String key) throws CacheServiceException {
        try {
            expirations.remove(key);
            store.remove(key);
        } catch (Exception e) {
            throw new CacheServiceException(String.format(CACHE_DELETE_EXCEPTION_MESSAGE, key), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() throws CacheServiceException {
        try {
            this.store.clear();
            this.expirations.clear();
        } catch (Exception e) {
            throw new CacheServiceException("Error clearing ConcurrentMap", e);
        }
    }
}
