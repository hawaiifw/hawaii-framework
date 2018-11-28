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
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * Helper class for the creation of a {@link RedisCache}.
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
@Component
public class HawaiiRedisCacheUtil {

    /**
     * The Redis configuration properties.
     */
    private final RedisConfigurationProperties cacheConfiguration;

    /**
     * Jedis connection factory.
     */
    private final JedisConnectionFactory jedisConnectionFactory;

    /**
     * Constructor.
     *
     * @param cacheConfiguration     The redis configuration
     * @param jedisConnectionFactory The Jedis connection factory
     */
    public HawaiiRedisCacheUtil(final RedisConfigurationProperties cacheConfiguration,
            final JedisConnectionFactory jedisConnectionFactory) {
        this.cacheConfiguration = cacheConfiguration;
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    /**
     * Generates a Redis cache using the provided parameters.
     *
     * @param keyPrefix       The prefix which should be used in the {@link RedisTemplate}
     * @param keySerializer   The Key serializer
     * @param valueSerializer The value serializer
     * @param <T>             Type of the Redis Cache is inferred from the provided type in the parameter
     * @param <U>             Type of the key serializer
     * @param <V>             Type of the value serializer
     * @return the constructed {@link RedisCache}
     */
    public <T, U, V> RedisCache<T> generateRedisCache(final String keyPrefix, final RedisSerializer<U> keySerializer,
            final RedisSerializer<V> valueSerializer) {
        return generateRedisCache(generateRedisTemplate(keySerializer, valueSerializer), keyPrefix);
    }

    /**
     * Generates a {@link RedisCache} using the given parameters.
     *
     * @param template  The redis template that should be used
     * @param keyPrefix The prefix which should be used in the {@link RedisTemplate}
     * @param <T>       Type of the Redis Cache is innferred from the provided type in the parameter
     * @return the constructed {@link RedisCache}
     */
    public <T> RedisCache<T> generateRedisCache(final RedisTemplate<String, T> template, final String keyPrefix) {
        return new RedisCache<>(template, cacheConfiguration.getDefaultTimeOutInMinutes(), keyPrefix);
    }

    /**
     * Generates a {@link RedisTemplate} with the provided serializers.
     *
     * @param keySerializer   The Key serializer
     * @param valueSerializer The value serializer
     * @param <T>             Type of the Redis Cache is inferred from the provided type in the parameter
     * @param <U>             Type of the key serializer
     * @param <V>             Type of the value serializer
     * @return the constructed {@link RedisTemplate}
     */
    public <T, U, V> RedisTemplate<String, T> generateRedisTemplate(final RedisSerializer<U> keySerializer,
            final RedisSerializer<V> valueSerializer) {
        final RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
