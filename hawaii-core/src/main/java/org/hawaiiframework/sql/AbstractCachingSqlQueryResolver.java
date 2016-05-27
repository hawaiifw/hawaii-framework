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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenient base class for {@link SqlQueryResolver} implementations. Caches sql queries once
 * resolved: This means that sql query resolution won't be a performance problem, no matter how
 * costly initial sql query retrieval is.
 * <p>
 * Subclasses need to implement the {@link #loadSqlQuery} template method to load the sql query.
 * <p>
 * Note this implementation is based on Spring's
 * {@link org.springframework.web.servlet.view.AbstractCachingViewResolver}.
 *
 * @author Marcel Overdijk
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
    private final Map<Object, String> sqlQueryAccessCache =
            new ConcurrentHashMap<>(DEFAULT_CACHE_LIMIT);

    /**
     * The maximum number of entries in the cache.
     */
    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    /**
     * Map from sql query key to sql query instance, synchronized for sql query creation.
     */
    private final Map<Object, String> sqlQueryCreationCache =
            new LinkedHashMap<Object, String>(DEFAULT_CACHE_LIMIT, 0.75f, true) {

                @Override
                protected boolean removeEldestEntry(Map.Entry<Object, String> eldest) {
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
     * This is equivalent to setting the {@link #setCacheLimit "cacheLimit"} property to the default
     * limit (1024) or to 0, respectively.
     * <p>
     * Default is "true": caching is enabled. Disable this only for debugging and development.
     */
    public void setCache(boolean cache) {
        this.cacheLimit = (cache ? DEFAULT_CACHE_LIMIT : 0);
    }

    /**
     * Whether a sql query name once resolved to {@code null} should be cached and automatically
     * resolved to {@code null} subsequently.
     * <p>
     * Default is "true": unresolved sql query names are being cached. Note that this flag only
     * applies if the general {@link #setCache "cache"} flag is kept at its default of "true" as
     * well.
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
     * Provides functionality to clear the cache for a certain sql query.
     *
     * @param sqlQueryName the sql query name for which the cached sql query (if any) needs to be
     *        removed
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
     * Clear the entire sql query cache, removing all cached sql queries. Subsequent resolve calls
     * will lead to loading of demanded sql queries.
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
            return loadSqlQuery(sqlQueryName);
        } else {
            Object cacheKey = getCacheKey(sqlQueryName);
            String sqlQuery = this.sqlQueryAccessCache.get(cacheKey);
            if (sqlQuery == null) {
                synchronized (this.sqlQueryCreationCache) {
                    sqlQuery = this.sqlQueryCreationCache.get(cacheKey);
                    if (sqlQuery == null) {
                        sqlQuery = loadSqlQuery(sqlQueryName);
                        if (sqlQuery == null && this.cacheUnresolved) {
                            sqlQuery = UNRESOLVED_SQL_QUERY;
                        }
                        if (sqlQuery != null) {
                            this.sqlQueryAccessCache.put(cacheKey, sqlQuery);
                            this.sqlQueryCreationCache.put(cacheKey, sqlQuery);
                            if (logger.isTraceEnabled()) {
                                logger.trace("Cached sql query [" + cacheKey + "]");
                            }
                        }
                    }
                }
            }
            return (sqlQuery != UNRESOLVED_SQL_QUERY ? sqlQuery : null);
        }
    }

    /**
     * Subclasses must implement this method to load the sql query. The returned sql queries will be
     * cached by this {@code SqlQueryResolver} base class.
     *
     * @param sqlQueryName the name of the sql query to retrieve
     * @return the sql query, or {@code null} if not found (optional, to allow for {@code
     * SqlQueryResolver} chaining)
     * @throws HawaiiException if the sql query could not be resolved (typically in case of problems
     *         resolving the sql query)
     * @see #resolveSqlQuery
     */
    protected abstract String loadSqlQuery(String sqlQueryName) throws HawaiiException;
}
