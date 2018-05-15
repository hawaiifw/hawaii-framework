package org.hawaiiframework.cache;

import org.hawaiiframework.time.HawaiiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Redis Cache implementation.
 *
 * @param <T> the type to cache.
 */
public class RedisCache<T> implements TTLCache<T> {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    /**
     * Constant for '_'.
     */
    private static final String UNDERSCORE = "_";

    /**
     * The redis template to use.
     */
    private final RedisTemplate<String, T> redisTemplate;

    /**
     * The default time out in minutes.
     */
    private final Long defaultTimeOutInMinutes;

    /**
     * They key's prefix.
     */
    private String keyPrefix;

    /**
     * The constructor.
     */
    public RedisCache(final RedisTemplate<String, T> redisTemplate, final Long defaultTimeOutInMinutes, final String keyPrefix) {
        this.redisTemplate = requireNonNull(redisTemplate);
        this.defaultTimeOutInMinutes = defaultTimeOutInMinutes;
        requireNonNull(keyPrefix);
        if (keyPrefix.endsWith(UNDERSCORE)) {
            this.keyPrefix = keyPrefix;
        } else {
            this.keyPrefix = keyPrefix + UNDERSCORE;
        }
    }

    private String getKey(final String key) {
        return keyPrefix + key;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void put(@NotNull final String key, @NotNull final T value) {
        final String cacheKey = getKey(key);
        LOGGER.debug("Putting '{}'.", cacheKey);
        redisTemplate.opsForValue().set(cacheKey, value);
        if (defaultTimeOutInMinutes != null) {
            redisTemplate.expire(cacheKey, defaultTimeOutInMinutes, TimeUnit.MINUTES);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void put(@NotNull final String key, @NotNull final T value, @NotNull final Duration duration) {
        final String cacheKey = getKey(key);
        LOGGER.debug("Putting '{}' with duration '{}'.", cacheKey, duration);
        redisTemplate.opsForValue().set(cacheKey, value);
        redisTemplate.expire(cacheKey, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void put(@NotNull final String key, @NotNull final T value, @NotNull final HawaiiTime expiresAt) {
        final String cacheKey = getKey(key);
        LOGGER.debug("Putting '{}' with expiration of '{}'.", cacheKey, expiresAt);
        redisTemplate.opsForValue().set(cacheKey, value);
        final long expiry = expiresAt.millis() - new HawaiiTime().millis();
        redisTemplate.expire(cacheKey, expiry, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public T get(@NotNull final String key) {
        final String cacheKey = getKey(key);
        LOGGER.debug("Get '{}'.", cacheKey);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final String key) {
        final String cacheKey = getKey(key);
        LOGGER.debug("Delete '{}'.", cacheKey);
        redisTemplate.delete(cacheKey);
    }
}
