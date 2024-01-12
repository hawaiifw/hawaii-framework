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

import java.time.Duration;
import org.hawaiiframework.cache.redis.config.RedisCacheConfigurationProperties;
import org.hawaiiframework.time.HawaiiTime;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Builder class for the creation of a {@link RedisCache}.
 *
 * <p>Default values are as followed:
 *
 * <p>Prefix : Hawaii, Key Serializer : {@link StringRedisSerializer}, Value Serializer : {@link
 * JdkSerializationRedisSerializer}
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
public class HawaiiRedisCacheBuilder {

  /** Default prefix. */
  private static final String DEFAULT_PREFIX = "Hawaii";

  /** The Redis configuration properties. */
  private final RedisCacheConfigurationProperties cacheConfiguration;

  /** Redis connection factory. */
  private final RedisConnectionFactory redisConnectionFactory;

  /** The key prefix which is used by the redis cache to persist items to Redis. */
  private final String keyPrefix;

  /** Hawaii time. */
  private final HawaiiTime hawaiiTime;

  /**
   * Key serializer which is used to serialize the keys into the Redis store. This is by default a
   * string serializer.
   */
  private final RedisSerializer<String> keySerializer = new StringRedisSerializer();

  /** The value serializer used to serialize values into the Redis store. */
  private final RedisSerializer<?> valueSerializer;

  /** The default expiration. */
  private Duration defaultExpiration;

  /**
   * Constructor.
   *
   * @param cacheConfiguration The redis configuration
   * @param redisConnectionFactory The redis connection factory
   * @param hawaiiTime The clock to use.
   */
  public HawaiiRedisCacheBuilder(
      RedisCacheConfigurationProperties cacheConfiguration,
      RedisConnectionFactory redisConnectionFactory,
      HawaiiTime hawaiiTime) {
    this(
        cacheConfiguration,
        redisConnectionFactory,
        DEFAULT_PREFIX,
        hawaiiTime,
        new JdkSerializationRedisSerializer(),
        null);
  }

  private HawaiiRedisCacheBuilder(
      RedisCacheConfigurationProperties cacheConfiguration,
      RedisConnectionFactory redisConnectionFactory,
      String keyPrefix,
      HawaiiTime hawaiiTime,
      RedisSerializer<?> valueSerializer,
      Duration defaultExpiration) {
    this.cacheConfiguration = cacheConfiguration;
    this.redisConnectionFactory = redisConnectionFactory;
    this.keyPrefix = keyPrefix;
    this.hawaiiTime = hawaiiTime;
    this.valueSerializer = valueSerializer;
    this.defaultExpiration = defaultExpiration;
  }

  /**
   * Sets the {@link RedisCacheConfigurationProperties} for this builder.
   *
   * @param cacheConfiguration the redis configuration properties
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withCacheConfiguration(
      RedisCacheConfigurationProperties cacheConfiguration) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        defaultExpiration);
  }

  /**
   * Sets the {@link RedisConnectionFactory} for this builder.
   *
   * @param redisConnectionFactory the connection factory used to build the cache
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withRedisConnectionFactory(
      RedisConnectionFactory redisConnectionFactory) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        defaultExpiration);
  }

  /**
   * Sets the timeout for the redis cache. When this is not set the redis' server default will be
   * used.
   *
   * @param expiration the timeout in minutes to set.
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withTimeOut(Duration expiration) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        expiration);
  }

  /**
   * Sets the key prefix for this builder.
   *
   * @param keyPrefix the prefix to use when building the redis cache
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withKeyPrefix(String keyPrefix) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        defaultExpiration);
  }

  /**
   * Sets the {@link HawaiiTime} for this builder.
   *
   * @param hawaiiTime time to use for {@link RedisCache}
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withHawaiiTime(HawaiiTime hawaiiTime) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        defaultExpiration);
  }

  /**
   * Sets the {@link RedisSerializer} for this builder.
   *
   * @param valueSerializer the value serializer
   * @return new {@link HawaiiRedisCacheBuilder} with the new set values
   */
  public HawaiiRedisCacheBuilder withValueSerializer(RedisSerializer<?> valueSerializer) {
    return new HawaiiRedisCacheBuilder(
        cacheConfiguration,
        redisConnectionFactory,
        keyPrefix,
        hawaiiTime,
        valueSerializer,
        defaultExpiration);
  }

  /**
   * Builds a {@link RedisCache} with the set values.
   *
   * @param <V> Type of the {@link RedisCache}
   * @return a new {@link RedisCache}
   */
  public <V> RedisCache<V> build() {
    return generateRedisCache(keyPrefix, hawaiiTime, keySerializer, valueSerializer);
  }

  /**
   * Generates a Redis cache using the provided parameters.
   *
   * @param keyPrefix The prefix which should be used in the {@link RedisTemplate}
   * @param keySerializer The Key serializer
   * @param valueSerializer The value serializer
   * @param <V> Type of the value serializer
   * @return the constructed {@link RedisCache}
   */
  private <V> RedisCache<V> generateRedisCache(
      String keyPrefix,
      HawaiiTime hawaiiTime,
      RedisSerializer<String> keySerializer,
      RedisSerializer<?> valueSerializer) {
    return generateRedisCache(
        generateRedisTemplate(keySerializer, valueSerializer), hawaiiTime, keyPrefix);
  }

  /**
   * Generates a {@link RedisCache} using the given parameters.
   *
   * @param template The redis template that should be used
   * @param keyPrefix The prefix which should be used in the {@link RedisTemplate}
   * @param <V> Type of the Redis Cache is inferred from the provided type in the parameter
   * @return the constructed {@link RedisCache}
   */
  private <V> RedisCache<V> generateRedisCache(
      RedisTemplate<String, V> template, HawaiiTime hawaiiTime, String keyPrefix) {
    var expiration =
        defaultExpiration != null ? defaultExpiration : cacheConfiguration.getDefaultExpiration();
    return new RedisCache<>(template, hawaiiTime, expiration, keyPrefix);
  }

  /**
   * Generates a {@link RedisTemplate} with the provided serializers.
   *
   * @param keySerializer The Key serializer
   * @param valueSerializer The value serializer
   * @param <V> Type of the value serializer
   * @return the constructed {@link RedisTemplate}
   */
  private <V> RedisTemplate<String, V> generateRedisTemplate(
      RedisSerializer<String> keySerializer, RedisSerializer<?> valueSerializer) {
    RedisTemplate<String, V> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(keySerializer);
    template.setValueSerializer(valueSerializer);

    template.afterPropertiesSet();
    return template;
  }
}
