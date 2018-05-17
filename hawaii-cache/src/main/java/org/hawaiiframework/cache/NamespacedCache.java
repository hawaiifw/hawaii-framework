/**
 * Copyright 2015 Q24
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.cache;

/**
 * The NamespacedCache.
 * @param <T> the type of object to be used for the cache.
 */
public class NamespacedCache<T> implements Cache<T> {

    private final Cache<T> cacheService;
    private final String namespace;
    /**
     * The namespace version.
     * Default value for int is 0.
     */
    private int namespaceVersion;
    private final Class<T> type;
    private String prefix;

    public NamespacedCache(final String namespace, final Cache<T> cacheService, final Class<T> type) {
        this.namespace = namespace;
        this.cacheService = cacheService;
        this.type = type;
        makePrefix();
    }

    @Override
    public T get(final String key) throws CacheServiceException, ClassCastException {
        return type.cast(cacheService.get(prefix + key));
    }

    @Override
    public void put(final String key, final T object) throws CacheServiceException {
        cacheService.put(prefix + key, type.cast(object));
    }

    @Override
    public void put(final String key, final int expiration, final Object object) throws CacheServiceException {
        cacheService.put(prefix + key, expiration, type.cast(object));
    }

    @Override
    public void delete(final String key) throws CacheServiceException {
        cacheService.delete(prefix + key);
    }

    @Override
    public void clear() {
        namespaceVersion++;
        makePrefix();
    }

    private void makePrefix() {
        this.prefix = namespace + Integer.toString(namespaceVersion) + ":";
    }

}
