package org.hawaiiframework.cache;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The HybridMemcachedCacheService.
 */
public class HybridMemcachedCacheService implements Cache {

    private static final String VERSION_SUFFIX = ".version";
    private static final String CACHE_GET = "cache.get";
    private static final String CACHE_PUT = "cache.put";
    private static final String CACHE_DELETE = "cache.delete";
    private static final String CACHE_TIME = "( '%s' ) took '%f' msec.";
    private static final String OBJECT_WITH_KEY = "Object with key '%s' ";
    private static final String OBJECT_NOT_FOUND_IN_CACHES = OBJECT_WITH_KEY
            + "not found in caches";
    private static final String OBJECT_NOT_FOUND_UPDATING_MEMCACHED = OBJECT_WITH_KEY
            + "not found in Memcached but found in internal cache. Updating Memcached";
    private static final String OBJECT_NOT_FOUND_UPDATING_INTERNAL_CACHE = OBJECT_WITH_KEY
            + "not found in internal cache but found in Memcached. Updating internal cache";
    private static final String OBJECT_FOUND_RETURNING_FROM_INTERNAL_CACHE = OBJECT_WITH_KEY
            + "found in caches and version is the same, returning value from internal cache";
    private static final String OBJECT_FOUND_MEMCACHED_IS_NEWER_UPDATING_INTERNAL_CACHE = OBJECT_WITH_KEY
            + "found in caches and version in Memcached is newer, updating internal cache";
    private static final String OBJECT_FOUND_INTERNAL_VERSION_IS_NEWER_UPDATING_MEMCACHED = OBJECT_WITH_KEY
            + "found in caches and internal version is newer than Memcached. Updating Memcached";

    private static final Logger LOGGER = LoggerFactory.getLogger(HybridMemcachedCacheService.class);

    private final Map<String, Object> backingCache;
    private final MemcachedClient memcachedClient;
    private final int defaultExpiration;
    private int maxTries = 10;

    public HybridMemcachedCacheService(final MemcachedClient memcachedClient) {
        this(memcachedClient, ONE_HOUR);
    }

    public HybridMemcachedCacheService(final MemcachedClient memcachedClient, final int defaultExpiration) {
        this(new HashMap<>(), memcachedClient, defaultExpiration);
    }

    public HybridMemcachedCacheService(final Map<String, Object> backingCache,
                                       final MemcachedClient memcachedClient,
                                       final int defaultExpiration) {
        this.backingCache = backingCache;
        this.memcachedClient = memcachedClient;
        this.defaultExpiration = defaultExpiration;
    }

    public void setMaxTries(final int maxTries) {
        this.maxTries = maxTries;
    }

    @Override
    public Object get(final String key) throws CacheServiceException, ClassCastException {

        if (memcachedClient == null) {
            return backingCache.get(key);
        }

        Object element = null;
        final long start = System.nanoTime();
        final String versionKey = getVersionKey(key);

        try {
            CASValue<Object> casValue = memcachedClient.gets(versionKey);
            final Long internalVersion = (Long) backingCache.get(versionKey);

            if (casValue == null) {
                if (internalVersion == null) {
                    LOGGER.debug(String.format(OBJECT_NOT_FOUND_IN_CACHES, key));
                } else {
                    LOGGER.debug(String.format(OBJECT_NOT_FOUND_UPDATING_MEMCACHED, key));
                    final Object value = backingCache.get(key);
                    updateMemcached(key, internalVersion, value);
                    element = value;
                }
            } else {
                final Long casVersion = (Long) casValue.getValue();
                if (internalVersion == null) {
                    LOGGER.debug(String.format(OBJECT_NOT_FOUND_UPDATING_INTERNAL_CACHE, key));
                    casValue = memcachedClient.gets(key);
                    updateBackingCache(key, casVersion, casValue.getValue());
                } else if (casVersion.equals(internalVersion)) {
                    LOGGER.debug(String.format(OBJECT_FOUND_RETURNING_FROM_INTERNAL_CACHE, key));
                } else if (casVersion > internalVersion) {
                    LOGGER.debug(String.format(OBJECT_FOUND_MEMCACHED_IS_NEWER_UPDATING_INTERNAL_CACHE, key));
                    casValue = memcachedClient.gets(key);
                    updateBackingCache(key, casVersion, casValue.getValue());
                } else {
                    LOGGER.debug(String.format(OBJECT_FOUND_INTERNAL_VERSION_IS_NEWER_UPDATING_MEMCACHED, key));
                    updateMemcached(key, internalVersion, backingCache.get(key));
                }
                element = backingCache.get(key);
            }
        } catch (final Exception e) {
            throw new CacheServiceException("Error retrieving object with key '%s' from cache", e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_GET + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }
        return element;
    }

    @Override
    public void put(final String key, final Object value) throws CacheServiceException {

        if (memcachedClient == null) {
            backingCache.put(key, value);
            return;
        }

        final long start = System.nanoTime();
        final String versionKey = getVersionKey(key);

        try {
            CASValue<Object> casValue = memcachedClient.gets(versionKey);
            Long internalVersion = (Long) backingCache.get(versionKey);
            if (internalVersion == null) {
                internalVersion = 0L;
            }
            if (casValue == null) {
                updateBackingCache(key, internalVersion, value);
                updateMemcached(key, internalVersion, value);
            } else {
                final Long casVersion = (Long) casValue.getValue();
                if (internalVersion < casVersion) {
                    throw new CacheServiceException("Memcached was updated by someone else");
                } else {
                    internalVersion++;
                    boolean isMemcachedUpdated = false;
                    for (int i = 0; i < maxTries; i++) {
                        final CASResponse response = memcachedClient.cas(versionKey, casValue.getCas(), internalVersion);
                        if (response == CASResponse.OK) {
                            memcachedClient.set(key, defaultExpiration, value);
                            isMemcachedUpdated = true;
                            break;
                        }
                        casValue = memcachedClient.gets(versionKey);
                    }
                    if (!isMemcachedUpdated) {
                        throw new CacheServiceException("Could not update Memcached");
                    }
                    updateBackingCache(key, internalVersion, value);
                }
            }
        } catch (final Exception e) {
            throw new CacheServiceException("Error storing object with key '" + key + "' to cache", e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_PUT + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }

    }

    @Override
    public void put(final String key, final int expiration, final Object object) throws CacheServiceException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public void delete(final String key) throws CacheServiceException {
        if (memcachedClient == null) {
            backingCache.remove(key);
            return;
        }

        final long start = System.nanoTime();
        final String versionKey = getVersionKey(key);
        try {
            memcachedClient.delete(versionKey);
            memcachedClient.delete(key);
            removeFromBackingCache(key);
        } catch (final Exception e) {
            throw new CacheServiceException("Error deleting object with key '" + key + "' from Memcached", e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_DELETE + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }
    }

    @Override
    public void clear() throws CacheServiceException {
        this.backingCache.clear();
        this.memcachedClient.flush();
    }

    private String getVersionKey(final String key) {
        return key + VERSION_SUFFIX;
    }

    private void updateBackingCache(final String key, final Long version, final Object value) {
        synchronized (backingCache) {
            backingCache.put(getVersionKey(key), version);
            backingCache.put(key, value);
        }
    }

    private Object removeFromBackingCache(final String key) {
        synchronized (backingCache) {
            backingCache.remove(getVersionKey(key));
            return backingCache.remove(key);
        }
    }

    private void updateMemcached(final String key, final Long version, final Object value) {
        memcachedClient.set(getVersionKey(key), defaultExpiration, version);
        memcachedClient.set(key, defaultExpiration, value);
    }

}
