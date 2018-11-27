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
import org.hawaiiframework.cache.redis.HawaiiRedisCacheUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

/**
 * Redis configuration.
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
@Configuration
@EnableConfigurationProperties(RedisConfigurationProperties.class)
public class RedisConfiguration {

    private final RedisConfigurationProperties properties;

    public RedisConfiguration(final RedisConfigurationProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates a new Jedis connection factory.
     *
     * @return a jedis connection factory.
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(final RedisSentinelConfiguration sentinelConfiguration,
            final JedisPoolConfig poolConfig) {
        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    /**
     * Creates a new redis sentinel configuration.
     *
     * @return a redis sentinel config.
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        return new RedisSentinelConfiguration(properties.getClusterMaster(), new HashSet<>(properties.getSentinelHostsAndPorts()));
    }

    /**
     * Creates a Jedis pool configuration.
     *
     * @return a Jedis pool config.
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() throws Exception {
        return applyConfiguration(properties.getPoolConfiguration(), JedisPoolConfig.class);
    }

    /**
     * Provides a {@link HawaiiRedisCacheUtil}.
     *
     * @return an instance of {@link HawaiiRedisCacheUtil}
     */
    @Bean
    public HawaiiRedisCacheUtil hawaiiRedisCacheUtil(final JedisConnectionFactory jedisConnectionFactory) {
        return new HawaiiRedisCacheUtil(properties, jedisConnectionFactory);
    }

    private <T extends GenericObjectPoolConfig> T applyConfiguration(final RedisPoolConfigurationProperties poolConfiguration,
            final Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final T temp = type.getDeclaredConstructor().newInstance();
        poolConfiguration.applyTo(temp);

        return temp;
    }

}
