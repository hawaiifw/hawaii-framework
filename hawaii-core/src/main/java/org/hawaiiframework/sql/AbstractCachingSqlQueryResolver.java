/*
 * Copyright 2015-2016 the original author or authors.
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

package org.hawaiiframework.sql;

import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Convenient base class for {@link SqlQueryResolver} implementations. Caches sql queries once resolved:
 * This means that sql query resolution won't be a performance problem, no matter how costly initial sql query retrieval is.
 * <p>
 * Subclasses need to implement the {@link #loadSqlQuery} template method to load the sql query.
 * <p>
 * Note this implementation is based on Spring's {@link org.springframework.web.servlet.view.AbstractCachingViewResolver}.
 *
 * @author Marcel Overdijk
 * @author Paul Klos
 * @see #loadSqlQuery
 * @since 2.0.0
 */
public abstract class AbstractCachingSqlQueryResolver implements SqlQueryResolver {

    /**
     * Default maximum number of entries for the sql query cache: 1024
     */
    public static final int DEFAULT_CACHE_LIMIT = 1024;

    /**
     * Dummy marker object for unresolved sql queries in the cache Maps.
     */
    private static final String UNRESOLVED_SQL_QUERY = new String();

    private static Logger logger = LoggerFactory.getLogger(AbstractCachingSqlQueryResolver.class);

    /**
     * Fast access cache for sql queries, returning already cached instances without a global lock.
     */
    private final Map<Object, QueryHolder> sqlQueryAccessCache = new ConcurrentHashMap<>(DEFAULT_CACHE_LIMIT);

    /**
     * The maximum number of entries in the cache.
     */
    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    /**
     * Map from sql query key to sql query instance, synchronized for sql query creation.
     */
    private final Map<Object, QueryHolder> sqlQueryCreationCache =
            new LinkedHashMap<Object, QueryHolder>(DEFAULT_CACHE_LIMIT, 0.75f, true) {

                @Override
                protected boolean removeEldestEntry(Map.Entry<Object, QueryHolder> eldest) {
                    if (size() > getCacheLimit()) {
                        sqlQueryAccessCache.remove(eldest.getKey());
                        return true;
                    } else {
                        return false;
                    }
                }
            };

    /**
     * Whether we should refrain from resolving sql queries again if unresolved once,
     */
    private boolean cacheUnresolved = true;

    private long cacheMillis = -1;

    /**
     * Return the maximum number of entries for the sql query cache.
     */
    public int getCacheLimit() {
        return this.cacheLimit;
    }

    /**
     * Specify the maximum number of entries for the sql query cache. Default is 1024.
     */
    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    /**
     * Return if caching is enabled.
     */
    public boolean isCache() {
        return (this.cacheLimit > 0);
    }

    /**
     * Enable or disable caching.
     * <p>
     * This is equivalent to setting the {@link #setCacheLimit "cacheLimit"} property to the default limit (1024) or to 0, respectively.
     * <p>
     * Default is "true": caching is enabled. Disable this only for debugging and development.
     */
    public void setCache(boolean cache) {
        this.cacheLimit = (cache ? DEFAULT_CACHE_LIMIT : 0);
    }

    /**
     * Whether a sql query name once resolved to {@code null} should be cached and automatically resolved to {@code null} subsequently.
     * <p>
     * Default is "true": unresolved sql query names are being cached. Note that this flag only applies if the general
     * {@link #setCache "cache"} flag is kept at its default of "true" as well.
     */
    public void setCacheUnresolved(boolean cacheUnresolved) {
        this.cacheUnresolved = cacheUnresolved;
    }

    /**
     * Return the cache key for the given sql query name.
     * <p>
     * Default is the sql query name but can be overridden in subclasses.
     */
    protected Object getCacheKey(String sqlQueryName) {
        return sqlQueryName;
    }

    /**
     * Set the number of seconds to cache a loaded query.
     * <p>
     * After the cache seconds have expired, {@link #doRefreshQueryHolder(String, QueryHolder)} will be called, so even if refreshing a once
     * loaded query is enabled, it is up to the subclass to define the refresh mechanism.
     * <p>
     * Note, setting this to anything other than -1 only makes sense if the queries are loaded from a source other than the classpath.
     * <p>
     * <ul>
     * <li>Default is "-1", indicating to cache forever.
     * <li>A positive number will cache a query for the given number of seconds. This is essentially the interval between refresh checks.
     * <li>A value of "0" will check for expiry on each query access!</b>
     * </ul>
     */
    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = (1000 * cacheSeconds);
    }

    /**
     * Provides functionality to clear the cache for a certain sql query.
     *
     * @param sqlQueryName the sql query name for which the cached sql query (if any) needs to be removed
     */
    public void removeFromCache(String sqlQueryName) {
        if (!isCache()) {
            logger.warn("Sql query caching is SWITCHED OFF -- removal not necessary");
        } else {
            Object cacheKey = getCacheKey(sqlQueryName);
            Object cachedSqlQuery;
            synchronized (this.sqlQueryCreationCache) {
                this.sqlQueryAccessCache.remove(cacheKey);
                cachedSqlQuery = this.sqlQueryCreationCache.remove(cacheKey);
            }
            if (logger.isDebugEnabled()) {
                if (cachedSqlQuery == null) {
                    logger.debug("No cached instance for sql query '" + cacheKey + "' was found");
                } else {
                    logger.debug("Cache for sql query " + cacheKey + " has been cleared");
                }
            }
        }
    }

    /**
     * Clear the entire sql query cache, removing all cached sql queries. Subsequent resolve calls will lead to loading of demanded sql
     * queries.
     */
    public void clearCache() {
        logger.debug("Clearing entire sql query cache");
        synchronized (this.sqlQueryCreationCache) {
            this.sqlQueryAccessCache.clear();
            this.sqlQueryCreationCache.clear();
        }
    }

    @Override
    public String resolveSqlQuery(String sqlQueryName) throws HawaiiException {
        if (!isCache()) {
            return loadSqlQuery(sqlQueryName, null);
        } else {
            Object cacheKey = getCacheKey(sqlQueryName);
            QueryHolder queryHolder = this.sqlQueryAccessCache.get(cacheKey);
            if (queryHolder != null) {
                long originalTimestamp = queryHolder.getRefreshTimestamp();
                if (originalTimestamp > System.currentTimeMillis() - this.cacheMillis) {
                    // Up-to-date
                    if (logger.isTraceEnabled()) {
                        logger.trace("Query {} within cache seconds, not refreshing", sqlQueryName);
                    }
                    return queryHolder.getSqlQuery();
                }
                refreshQueryHolder(sqlQueryName, queryHolder);
            } else {
                synchronized (this.sqlQueryCreationCache) {
                    queryHolder = this.sqlQueryCreationCache.get(cacheKey);
                    if (queryHolder == null) {
                        queryHolder = new QueryHolder();
                        queryHolder.setRefreshTimestamp(System.currentTimeMillis());
                        loadSqlQuery(sqlQueryName, queryHolder);
                        if (this.cacheUnresolved || queryHolder.getSqlQuery() != null) {
                            this.sqlQueryAccessCache.put(cacheKey, queryHolder);
                            this.sqlQueryCreationCache.put(cacheKey, queryHolder);
                            if (logger.isTraceEnabled()) {
                                logger.trace("Cached sql query [" + cacheKey + "]");
                            }
                        }
                    }
                }
            }
            return queryHolder.getSqlQuery();
        }
    }

    /**
     * If a lock can be obtained on the QueryHolder, calls {@link #doRefreshQueryHolder(String, QueryHolder)} to actually refresh the
     * QueryHolder. If not, i.e. while another thread is refreshing the QueryHolder, this method simply returns, which means that the
     * currently cached version is still used.
     *
     * @param sqlQueryName
     * @param queryHolder
     */
    private void refreshQueryHolder(String sqlQueryName, QueryHolder queryHolder) {
        if (logger.isTraceEnabled()) {
            logger.trace("Attempting to refresh QueryHolder for query {}", sqlQueryName);
        }
        if (!queryHolder.refreshLock.tryLock()) {
            if (logger.isTraceEnabled()) {
                logger.trace("QueryHolder is locked");
            }
            return;
        }
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Refreshing query");
            }
            doRefreshQueryHolder(sqlQueryName, queryHolder);
        } finally {
            queryHolder.refreshLock.unlock();
        }
    }

    /**
     * Subclasses may override this method to implement their own expiry mechanism. The default implementation does nothing,
     * i.e. once a query is cached it will never be updated.
     * <p>
     * This method is only called when the current thread has a lock on the QueryHolder, so subclasses need not deal with thread-safety.
     *
     * @param sqlQueryName the name of the query to refresh
     * @param queryHolder  the cached QueryHolder to check
     */
    protected void doRefreshQueryHolder(String sqlQueryName, QueryHolder queryHolder) {
        // do nothing
    }

    /**
     * Subclasses must implement this method to load the sql query. The returned sql queries will be cached by this {@code SqlQueryResolver}
     * base class.
     *
     * @param sqlQueryName the name of the sql query to retrieve
     * @param queryHolder  the QueryHolder to populate
     * @return the sql query, or {@code null} if not found (optional, to allow for {@code
     * SqlQueryResolver} chaining)
     * @throws HawaiiException if the sql query could not be resolved (typically in case of problems resolving the sql query)
     * @see #resolveSqlQuery
     */
    protected abstract String loadSqlQuery(String sqlQueryName, QueryHolder queryHolder)
            throws HawaiiException;

    /**
     * QueryHolder for caching. Stores the timestamp of the last refresh (updated every time the query is refreshed) as well as the query
     * timestamp. Whether or not the latter is only meaningful depends on the way the query is loaded. For example, loading a query as a
     * resource from the classpath will not result in any meaningful timestamp, whereas loading it from the file system will.
     */
    protected class QueryHolder {

        private String sqlQuery;
        private volatile long refreshTimestamp = -2;
        private long queryTimestamp = -1;
        private final ReentrantLock refreshLock = new ReentrantLock();

        public String getSqlQuery() {
            return sqlQuery;
        }

        public void setSqlQuery(String sqlQuery) {
            this.sqlQuery = sqlQuery;
        }

        public long getRefreshTimestamp() {
            return refreshTimestamp;
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getQueryTimestamp() {
            return queryTimestamp;
        }

        public void setQueryTimestamp(long queryTimestamp) {
            this.queryTimestamp = queryTimestamp;
        }

    }
}
