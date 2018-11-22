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

    @SuppressWarnings("PMD.CommentRequired")
    public Long getDefaultTimeOutInMinutes() {
        return defaultTimeOutInMinutes;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setDefaultTimeOutInMinutes(final Long defaultTimeOutInMinutes) {
        this.defaultTimeOutInMinutes = defaultTimeOutInMinutes;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getClusterMaster() {
        return clusterMaster;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setClusterMaster(final String clusterMaster) {
        this.clusterMaster = clusterMaster;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public List<String> getSentinelHostsAndPorts() {
        return sentinelHostsAndPorts;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setSentinelHostsAndPorts(final List<String> sentinelHostsAndPorts) {
        this.sentinelHostsAndPorts = sentinelHostsAndPorts;
    }


    @SuppressWarnings("PMD.CommentRequired")
    public RedisPoolConfigurationProperties getPoolConfiguration() {
        return poolConfiguration;
    }

    @SuppressWarnings("PMD.CommentRequired")
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
        jedisConnectionFactory.setUsePool(true);
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
