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
 * No operation {@link Cache} implementation suitable for disabling the
 * cache.
 */
public class FakeCacheService implements Cache<Object> {

    @Override
    public Object get(final String key) throws CacheServiceException, ClassCastException {
        // do nothing
        return null;
    }

    @Override
    public void put(final String key, final Object object) throws CacheServiceException {
        // do nothing
    }

    @Override
    public void put(final String key, final int expiration, final Object object) throws CacheServiceException {
        // do nothing
    }

    @Override
    public void delete(final String key) throws CacheServiceException {
        // do nothing
    }

    @Override
    public void clear() throws CacheServiceException {
        // do nothing
    }
}
