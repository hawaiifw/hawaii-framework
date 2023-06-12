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

import org.hawaiiframework.cache.redis.HawaiiRedisCacheBuilder;
import org.hawaiiframework.time.HawaiiTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redis configuration.
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
@Configuration
@EnableConfigurationProperties(RedisCacheConfigurationProperties.class)
public class RedisConfiguration {

    /**
     * Redis configuration properties.
     */
    private final RedisCacheConfigurationProperties properties;

    /**
     * Hawaii time.
     */
    private final HawaiiTime hawaiiTime;

    /**
     * Constructor.
     *
     * @param properties the properties to create the redis beans.
     * @param hawaiiTime The hawaii time.
     */
    @Autowired
    public RedisConfiguration(final RedisCacheConfigurationProperties properties, final HawaiiTime hawaiiTime) {
        this.properties = properties;
        this.hawaiiTime = hawaiiTime;
    }

    /**
     * Provides a {@link HawaiiRedisCacheBuilder}.
     *
     * @param redisConnectionFactory The redis connection factory.
     * @return an instance of {@link HawaiiRedisCacheBuilder}
     */
    @Bean
    public HawaiiRedisCacheBuilder hawaiiRedisCacheBuilder(final RedisConnectionFactory redisConnectionFactory) {
        return new HawaiiRedisCacheBuilder(properties, redisConnectionFactory, hawaiiTime);
    }
}
