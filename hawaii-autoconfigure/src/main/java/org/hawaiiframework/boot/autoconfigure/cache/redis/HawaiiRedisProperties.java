package org.hawaiiframework.boot.autoconfigure.cache.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.List;

/**
 * The HawaiiRedisProperties placeholder.
 */
@ConfigurationProperties(prefix = "redis")
public class HawaiiRedisProperties {

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
    private HawaiiRedisPoolProperties poolConfiguration;

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

    public HawaiiRedisPoolProperties getPoolConfiguration() {
        return poolConfiguration;
    }

    public void setPoolConfiguration(final HawaiiRedisPoolProperties poolConfiguration) {
        this.poolConfiguration = poolConfiguration;
    }

    /**
     * Create a new jedis connection factory.
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
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        return new RedisSentinelConfiguration(getClusterMaster(), new HashSet<>(getSentinelHostsAndPorts()));
    }

    /**
     * Create a jedis pool configuration.
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        applyConfiguration(getPoolConfiguration(), jedisPoolConfig);
        return jedisPoolConfig;
    }

    /**
     * Create the redis serializer.
     *
     * @return the serializer
     */
    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    private void applyConfiguration(final HawaiiRedisPoolProperties poolConfiguration, final JedisPoolConfig jedisPoolConfig) {
        poolConfiguration.applyTo(jedisPoolConfig);
    }
}
