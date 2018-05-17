package org.hawaiiframework.boot.autoconfigure.cache.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis configuration.
 */
@Configuration
@ConditionalOnProperty(name = "hawaii.redis.enabled")
@ConditionalOnClass(JedisPoolConfig.class)
@EnableConfigurationProperties(HawaiiRedisProperties.class)
public class HawaiiRedisAutoConfiguration {

}
