package org.hawaiiframework.boot.autoconfigure.cache.memcached;

import net.spy.memcached.MemcachedClient;
import org.hawaiiframework.cache.HybridMemcachedCacheService;
import org.hawaiiframework.cache.MemcachedCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis configuration.
 */
@Configuration
@ConditionalOnProperty(name = "hawaii.memcached.enabled")
@ConditionalOnClass(MemcachedCacheService.class)
public class HawaiiMemcachedAutoConfiguration {

    /**
     * The MemcachedCacheService bean creation method.
     *
     * @param memcachedClient the {@link MemcachedClient}
     * @return the {@link MemcachedCacheService} bean.
     */
    @Bean
    public MemcachedCacheService memcachedCacheService(final MemcachedClient memcachedClient) {
        return new MemcachedCacheService(memcachedClient);
    }

    /**
     * The HybridMemcachedCacheService bean creation method.
     *
     * @param memcachedClient the {@link MemcachedClient}.
     * @param expiration      the expiration time.
     * @return the {@link HybridMemcachedCacheService} bean.
     */
    @Bean
    public HybridMemcachedCacheService hybridMemcachedCacheService(final MemcachedClient memcachedClient, final int expiration) {
        return new HybridMemcachedCacheService(memcachedClient, expiration);
    }
}
