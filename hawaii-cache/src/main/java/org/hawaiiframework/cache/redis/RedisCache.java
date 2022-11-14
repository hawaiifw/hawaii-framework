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
package org.hawaiiframework.cache.redis;

import org.hawaiiframework.cache.Cache;
import org.hawaiiframework.time.HawaiiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Redis Cache implementation.
 *
 * @param <T> the type to cache.
 * @author Richard Kohlen
 * @version 3.0.0
 */
public class RedisCache<T> implements Cache<T> {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    /**
     * Constant for '_'.
     */
    private static final String UNDERSCORE = "_";

    /**
     * The redis template to use.
     */
    private final RedisTemplate<String, T> redisTemplate;

    /**
     * The default expiration in minutes.
     */
    private final Long defaultExpireInMinutes;

    /**
     * They key's prefix.
     */
    private String keyPrefix;

    /**
     * Hawaii time, used to get the current time.
     */
    private final HawaiiTime hawaiiTime;

    /**
     * Constructor.
     *
     * @param redisTemplate          The redis template to use.
     * @param hawaiiTime             the Hawaii time, used to get the current time.
     * @param defaultExpireInMinutes The default time out in minutes.
     * @param keyPrefix              They key's prefix.
     */
    public RedisCache(final RedisTemplate<String, T> redisTemplate, final HawaiiTime hawaiiTime, final Long defaultExpireInMinutes,
            final String keyPrefix) {
        this.hawaiiTime = hawaiiTime;
        this.redisTemplate = requireNonNull(redisTemplate);
        this.defaultExpireInMinutes = defaultExpireInMinutes;
        requireNonNull(keyPrefix);
        if (keyPrefix.endsWith(UNDERSCORE)) {
            this.keyPrefix = keyPrefix;
        } else {
            this.keyPrefix = keyPrefix + UNDERSCORE;
        }
    }

    private String getKey(final String key) {
        return keyPrefix + key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(@NonNull final String key, @NonNull final T value) {
        requireNonNull(key, "Key should not be null");
        requireNonNull(value);


        final String cacheKey = getKey(key);
        LOGGER.debug("Putting '{}'.", cacheKey);
        redisTemplate.opsForValue().set(cacheKey, value);
        if (defaultExpireInMinutes != null) {
            redisTemplate.expire(cacheKey, defaultExpireInMinutes, TimeUnit.MINUTES);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(@NonNull final String key, @NonNull final T value, @NonNull final Duration duration) {
        requireNonNull(key);
        requireNonNull(value);
        requireNonNull(duration);

        final String cacheKey = getKey(key);
        LOGGER.debug("Putting '{}' with duration '{}'.", cacheKey, duration);
        redisTemplate.opsForValue().set(cacheKey, value);
        redisTemplate.expire(cacheKey, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(@NonNull final String key, @NonNull final T value, @NonNull final LocalDateTime expiresAt) {
        requireNonNull(expiresAt);
        put(key, value, expiresAt.atZone(hawaiiTime.getZone()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(@NonNull final String key, @NonNull final T value, @NonNull final ZonedDateTime expiresAt) {
        requireNonNull(expiresAt);
        putAndSetExpiry(key, value, expiresAt.toInstant());
    }

    private void putAndSetExpiry(@NonNull final String key, @NonNull final T value, final Instant expiry) {
        requireNonNull(key);
        requireNonNull(value);
        final String cacheKey = getKey(key);
        final var exp = hawaiiTime.between(expiry);
        LOGGER.debug("Putting '{}' with expiration of '{}'.", cacheKey, expiry);
        redisTemplate.opsForValue().set(cacheKey, value);
        redisTemplate.expire(cacheKey, exp, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(@NonNull final String key) {
        requireNonNull(key);

        final String cacheKey = getKey(key);
        LOGGER.debug("Get '{}'.", cacheKey);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(@NonNull final String key) {
        requireNonNull(key);

        final String cacheKey = getKey(key);
        LOGGER.debug("Delete '{}'.", cacheKey);
        redisTemplate.delete(cacheKey);
    }
}
