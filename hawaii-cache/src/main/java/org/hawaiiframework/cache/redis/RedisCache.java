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

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.hawaiiframework.cache.Cache;
import org.hawaiiframework.time.HawaiiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

/**
 * Redis Cache implementation.
 *
 * @param <T> the type to cache.
 * @author Richard Kohlen
 * @version 3.0.0
 */
public class RedisCache<T> implements Cache<T> {

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

  /** Constant for '_'. */
  private static final String UNDERSCORE = "_";

  /** The redis template to use. */
  private final RedisTemplate<String, T> redisTemplate;

  /** The default expiration in minutes. */
  private final Duration defaultExpiration;

  /** They key's prefix. */
  private final String keyPrefix;

  /** Hawaii time, used to get the current time. */
  private final HawaiiTime hawaiiTime;

  /**
   * Constructor.
   *
   * @param redisTemplate The redis template to use.
   * @param hawaiiTime the Hawaii time, used to get the current time.
   * @param defaultExpiration The default time out/expiration.
   * @param keyPrefix They key's prefix.
   */
  public RedisCache(
      RedisTemplate<String, T> redisTemplate,
      HawaiiTime hawaiiTime,
      Duration defaultExpiration,
      String keyPrefix) {
    this.hawaiiTime = hawaiiTime;
    this.redisTemplate = requireNonNull(redisTemplate);
    this.defaultExpiration = defaultExpiration;
    requireNonNull(keyPrefix);
    if (keyPrefix.endsWith(UNDERSCORE)) {
      this.keyPrefix = keyPrefix;
    } else {
      this.keyPrefix = keyPrefix + UNDERSCORE;
    }
  }

  private String getKey(String key) {
    return keyPrefix + key;
  }

  @Override
  public void put(@NonNull String key, @NonNull T value) {
    requireNonNull(key, "Key should not be null");
    requireNonNull(value);

    String cacheKey = getKey(key);
    LOGGER.debug("Putting '{}'.", cacheKey);
    redisTemplate.opsForValue().set(cacheKey, value);
    if (defaultExpiration != null) {
      redisTemplate.expire(cacheKey, defaultExpiration);
    }
  }

  @Override
  public void put(@NonNull String key, @NonNull T value, @NonNull Duration duration) {
    requireNonNull(key);
    requireNonNull(value);
    requireNonNull(duration);

    String cacheKey = getKey(key);
    LOGGER.debug("Putting '{}' with duration '{}'.", cacheKey, duration);
    redisTemplate.opsForValue().set(cacheKey, value);
    redisTemplate.expire(cacheKey, duration);
  }

  @Override
  public void put(@NonNull String key, @NonNull T value, @NonNull LocalDateTime expiresAt) {
    requireNonNull(expiresAt);
    put(key, value, expiresAt.atZone(hawaiiTime.getZone()));
  }

  @Override
  public void put(@NonNull String key, @NonNull T value, @NonNull ZonedDateTime expiresAt) {
    requireNonNull(expiresAt);
    putAndSetExpiry(key, value, expiresAt.toInstant());
  }

  private void putAndSetExpiry(@NonNull String key, @NonNull T value, Instant expiry) {
    requireNonNull(key);
    requireNonNull(value);
    String cacheKey = getKey(key);
    Long exp = hawaiiTime.between(expiry);
    LOGGER.debug("Putting '{}' with expiration of '{}'.", cacheKey, expiry);
    redisTemplate.opsForValue().set(cacheKey, value);
    redisTemplate.expire(cacheKey, exp, TimeUnit.MILLISECONDS);
  }

  @Override
  public T get(@NonNull String key) {
    requireNonNull(key);

    String cacheKey = getKey(key);
    LOGGER.debug("Get '{}'.", cacheKey);
    return redisTemplate.opsForValue().get(cacheKey);
  }

  @Override
  public void remove(@NonNull String key) {
    requireNonNull(key);

    String cacheKey = getKey(key);
    LOGGER.debug("Delete '{}'.", cacheKey);
    redisTemplate.delete(cacheKey);
  }
}
