package org.hawaiiframework.cache;

import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Cache} implementation using distributed Memcached system.
 */
public class MemcachedCacheService implements Cache {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedCacheService.class);
    private static final String CACHE_GET = "cache.get";
    private static final String CACHE_PUT = "cache.put";
    private static final String CACHE_DELETE = "cache.delete";
    private static final String CACHE_TIME = "( '%s' ) took '%f' msec.";
    private static final String ERROR_RETRIEVING = "Error retrieving ";
    private static final String ERROR_STORING = "Error storing ";
    private static final String ERROR_DELETING = "Error deleting ";
    private static final String OBJECT_FROM_MEMCACHED = "object with key '%s' from Memcached";

    private final MemcachedClient memcachedClient;
    private final int defaultExpiration;

    public MemcachedCacheService(final MemcachedClient memcachedClient) {
        this(memcachedClient, ONE_HOUR);
    }

    public MemcachedCacheService(final MemcachedClient memcachedClient, final int defaultExpiration) {
        this.memcachedClient = memcachedClient;
        this.defaultExpiration = defaultExpiration;
    }

    @Override
    public Object get(final String key) throws CacheServiceException {
        final long start = System.nanoTime();
        try {
            return memcachedClient.get(key);
        } catch (final Exception e) {
            throw new CacheServiceException(ERROR_RETRIEVING + String.format(OBJECT_FROM_MEMCACHED, key), e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_GET + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }
    }

    @Override
    public void put(final String key, final Object object) throws CacheServiceException {
        put(key, defaultExpiration, object);
    }

    @Override
    public void put(final String key, final int expiration, final Object object) throws CacheServiceException {
        final long start = System.nanoTime();
        try {
            LOGGER.trace("Put '" + key + "', expiry '" + expiration + "': " + object);
            memcachedClient.set(key, expiration, object);
        } catch (final Exception e) {
            throw new CacheServiceException(ERROR_STORING + String.format(OBJECT_FROM_MEMCACHED, key), e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_PUT + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }
    }

    @Override
    public void delete(final String key) throws CacheServiceException {
        final long start = System.nanoTime();
        try {
            LOGGER.trace("Delete '" + key + "'.");
            memcachedClient.delete(key);
        } catch (final Exception e) {
            throw new CacheServiceException(ERROR_DELETING + String.format(OBJECT_FROM_MEMCACHED, key), e);
        } finally {
            final long end = System.nanoTime();
            LOGGER.debug(CACHE_DELETE + String.format(CACHE_TIME, key, (end - start) / 1E6));
        }
    }

    @Override
    public void clear() throws CacheServiceException {
        this.memcachedClient.flush();
    }
}
