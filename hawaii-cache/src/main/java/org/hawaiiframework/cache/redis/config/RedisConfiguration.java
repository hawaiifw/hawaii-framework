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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.List;

/**
 * Redis configuration.
 *
 * @author Richard Kohlen
 * @version 3.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfiguration {

    /**
     * The default timeout in minutes.
     */
    private Long defaultTimeOutInMinutes;

    /**
     * The redis cluster's master name.
     */
    private String clusterMaster;

    /**
     * The set of sentinels.
     */
    private List<String> sentinelHostsAndPorts;

    /**
     * The connection pool configuration.
     */
    private RedisPoolConfigurationProperties poolConfiguration;

    public Long getDefaultTimeOutInMinutes() {
        return defaultTimeOutInMinutes;
    }

    public void setDefaultTimeOutInMinutes(final Long defaultTimeOutInMinutes) {
        this.defaultTimeOutInMinutes = defaultTimeOutInMinutes;
    }

    public String getClusterMaster() {
        return clusterMaster;
    }

    public void setClusterMaster(final String clusterMaster) {
        this.clusterMaster = clusterMaster;
    }

    public List<String> getSentinelHostsAndPorts() {
        return sentinelHostsAndPorts;
    }

    public void setSentinelHostsAndPorts(final List<String> sentinelHostsAndPorts) {
        this.sentinelHostsAndPorts = sentinelHostsAndPorts;
    }


    public RedisPoolConfigurationProperties getPoolConfiguration() {
        return poolConfiguration;
    }

    public void setPoolConfiguration(final RedisPoolConfigurationProperties poolConfiguration) {
        this.poolConfiguration = poolConfiguration;
    }

    /**
     * Create a new jedis connection factory.
     *
     * @return a jedis connection factory.
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());

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
        return new RedisSentinelConfiguration(getClusterMaster(), new HashSet<>(getSentinelHostsAndPorts()));
    }

    /**
     * Create a jedis pool configuration.
     *
     * @return a jedis pool config.
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        applyConfiguration(getPoolConfiguration(), jedisPoolConfig);
        return jedisPoolConfig;
    }

    private void applyConfiguration(final RedisPoolConfigurationProperties poolConfiguration, final JedisPoolConfig jedisPoolConfig) {
        poolConfiguration.applyTo(jedisPoolConfig);
    }
}
