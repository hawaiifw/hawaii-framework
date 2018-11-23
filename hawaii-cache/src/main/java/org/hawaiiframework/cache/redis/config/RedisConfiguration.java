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
package org.hawaiiframework.cache.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

/**
 * Redis configuration.
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */

public class RedisConfiguration {


    private final RedisConfigurationProperties properties;

    public RedisConfiguration(RedisConfigurationProperties properties) {
        this.properties = properties;
    }

    /**
     * Create a new Jedis connection factory.
     *
     * @return a jedis connection factory.
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisSentinelConfiguration sentinelConfiguration, JedisPoolConfig poolConfig) {
        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);

        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    /**
     * Create a new redis sentinel configuration.
     *
     * @return a redis sentinel config.
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        return new RedisSentinelConfiguration(properties.getClusterMaster(), new HashSet<>(properties.getSentinelHostsAndPorts()));
    }

    /**
     * Create a Jedis pool configuration.
     *
     * @return a Jedis pool config.
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() throws Exception {
        return applyConfiguration(JedisPoolConfig.class, properties.getPoolConfiguration());
    }

    private <T extends GenericObjectPoolConfig> T applyConfiguration(Class<T> type,
            final RedisPoolConfigurationProperties poolConfiguration)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T temp = type.getDeclaredConstructor().newInstance();
        poolConfiguration.applyTo(temp);

        return temp;
    }

//
//    /**
//     * Create a user cache in redis.
//     *
//     * @param template           the redis template.
//     * @param cacheConfiguration the configuration.
//     * @return a cache for {@code HawaiiSsoUserDetails}
//     */
//    @Bean
//    public Cache<U> userPrincipalCache(final RedisTemplate<String, U> template, final RedisConfiguration cacheConfiguration) {
//        return new RedisCache<>(template, cacheConfiguration.getDefaultTimeOutInMinutes(), "jti");
//    }
//
//
//    /**
//     * Create a new redis template to retrieve / store users.
//     *
//     * @param jedisConnectionFactory the connection factory.
//     * @return a redis template for {@code HawaiiSsoUserDetails}
//     */
//    @Bean
//    public RedisTemplate<String, U> userRedisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
//        final RedisTemplate<String, U> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(redisSerializer());
//
//        template.afterPropertiesSet();
//        return template;
//    }


}
