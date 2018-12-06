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

import org.hawaiiframework.cache.redis.config.RedisConfigurationProperties;
import org.hawaiiframework.time.HawaiiTime;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Builder class for the creation of a {@link RedisCache}.
 * <p>
 * Default values are as followed:
 * <p>
 * Prefix               : Hawaii,
 * Key Serializer       : {@link StringRedisSerializer},
 * Value Serializer     : {@link JdkSerializationRedisSerializer}
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
public class HawaiiRedisCacheBuilder {

    /**
     * Default prefix.
     */
    private static final String DEFAULT_PREFIX = "Hawaii";

    /**
     * The Redis configuration properties.
     */
    private final RedisConfigurationProperties cacheConfiguration;

    /**
     * Jedis connection factory.
     */
    private final JedisConnectionFactory jedisConnectionFactory;

    /**
     * The key prefix which is used by the redis cache to persist items to Redis.
     */
    private final String keyPrefix;

    /**
     * Hawaii time.
     */
    private final HawaiiTime hawaiiTime;

    /**
     * Key serializer which is used to serialize the keys into the Redis store.
     * This is by default a string serializer.
     */
    private final RedisSerializer<String> keySerializer = new StringRedisSerializer();

    /**
     * The value serializer used to serialize values into the Redis store.
     */
    private final RedisSerializer<?> valueSerializer;

    /**
     * Constructor.
     *
     * @param cacheConfiguration     The redis configuration
     * @param jedisConnectionFactory The Jedis connection factory
     */
    public HawaiiRedisCacheBuilder(final RedisConfigurationProperties cacheConfiguration,
            final JedisConnectionFactory jedisConnectionFactory, final HawaiiTime hawaiiTime) {
        this(cacheConfiguration, jedisConnectionFactory, DEFAULT_PREFIX, hawaiiTime, new JdkSerializationRedisSerializer());
    }

    private HawaiiRedisCacheBuilder(final RedisConfigurationProperties cacheConfiguration,
            final JedisConnectionFactory jedisConnectionFactory, final String keyPrefix, final HawaiiTime hawaiiTime,
            final RedisSerializer<?> valueSerializer) {
        this.cacheConfiguration = cacheConfiguration;
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.keyPrefix = keyPrefix;
        this.hawaiiTime = hawaiiTime;
        this.valueSerializer = valueSerializer;
    }

    /**
     * Sets the {@link RedisConfigurationProperties} for this builder.
     *
     * @param cacheConfiguration the redis configuration properties
     * @return new {@link HawaiiRedisCacheBuilder} with the new set values
     */
    public HawaiiRedisCacheBuilder withCacheConfiguration(final RedisConfigurationProperties cacheConfiguration) {
        return new HawaiiRedisCacheBuilder(cacheConfiguration, jedisConnectionFactory, keyPrefix, hawaiiTime, valueSerializer);
    }

    /**
     * Sets the {@link JedisConnectionFactory} for this builder.
     *
     * @param jedisConnectionFactory the connection factory used to build the cache
     * @return new {@link HawaiiRedisCacheBuilder} with the new set values
     */
    public HawaiiRedisCacheBuilder withJedisConnectionFactory(final JedisConnectionFactory jedisConnectionFactory) {
        return new HawaiiRedisCacheBuilder(cacheConfiguration, jedisConnectionFactory, keyPrefix, hawaiiTime, valueSerializer);
    }

    /**
     * Sets the key prefix for this builder.
     *
     * @param keyPrefix the prefix to use when building the redis cache
     * @return new {@link HawaiiRedisCacheBuilder} with the new set values
     */
    public HawaiiRedisCacheBuilder withKeyPrefix(final String keyPrefix) {
        return new HawaiiRedisCacheBuilder(cacheConfiguration, jedisConnectionFactory, keyPrefix, hawaiiTime, valueSerializer);
    }

    /**
     * Sets the {@link HawaiiTime} for this builder.
     *
     * @param hawaiiTime time to use for {@link RedisCache}
     * @return new {@link HawaiiRedisCacheBuilder} with the new set values
     */
    public HawaiiRedisCacheBuilder withHawaiiTime(final HawaiiTime hawaiiTime) {
        return new HawaiiRedisCacheBuilder(cacheConfiguration, jedisConnectionFactory, keyPrefix, hawaiiTime, valueSerializer);
    }

    /**
     * Sets the {@link RedisSerializer} for this builder.
     *
     * @param valueSerializer the value serializer
     * @return new {@link HawaiiRedisCacheBuilder} with the new set values
     */
    public HawaiiRedisCacheBuilder withValueSerializer(final RedisSerializer<?> valueSerializer) {
        return new HawaiiRedisCacheBuilder(cacheConfiguration, jedisConnectionFactory, keyPrefix, hawaiiTime, valueSerializer);
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
     * @param keyPrefix       The prefix which should be used in the {@link RedisTemplate}
     * @param keySerializer   The Key serializer
     * @param valueSerializer The value serializer
     * @param <V>             Type of the value serializer
     * @return the constructed {@link RedisCache}
     */
    private <V> RedisCache<V> generateRedisCache(final String keyPrefix, final HawaiiTime hawaiiTime,
            final RedisSerializer<String> keySerializer,
            final RedisSerializer<?> valueSerializer) {
        return generateRedisCache(generateRedisTemplate(keySerializer, valueSerializer), hawaiiTime, keyPrefix);
    }

    /**
     * Generates a {@link RedisCache} using the given parameters.
     *
     * @param template  The redis template that should be used
     * @param keyPrefix The prefix which should be used in the {@link RedisTemplate}
     * @param <V>       Type of the Redis Cache is inferred from the provided type in the parameter
     * @return the constructed {@link RedisCache}
     */
    private <V> RedisCache<V> generateRedisCache(final RedisTemplate<String, V> template, final HawaiiTime hawaiiTime,
            final String keyPrefix) {
        return new RedisCache<>(template, hawaiiTime, cacheConfiguration.getDefaultTimeOutInMinutes(), keyPrefix);
    }

    /**
     * Generates a {@link RedisTemplate} with the provided serializers.
     *
     * @param keySerializer   The Key serializer
     * @param valueSerializer The value serializer
     * @param <V>             Type of the value serializer
     * @return the constructed {@link RedisTemplate}
     */
    private <V> RedisTemplate<String, V> generateRedisTemplate(final RedisSerializer<String> keySerializer,
            final RedisSerializer<?> valueSerializer) {
        final RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
