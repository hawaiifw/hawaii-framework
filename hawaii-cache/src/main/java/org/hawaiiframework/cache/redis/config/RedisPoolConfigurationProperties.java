package org.hawaiiframework.cache.redis.config;


import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Configuration properties for an Apache Commons Generic Pool used by the redis configuration.
 */
public class RedisPoolConfigurationProperties {

    /**
     * Property to signal that the "borrow object from pool" method should block when the number of
     * free objects is zero.
     *
     * @see GenericKeyedObjectPool#getBlockWhenExhausted()
     */
    private Boolean blockWhenExhausted;

    /**
     * Maximum number of pooled objects.
     */
    private Integer maxTotal;

    /**
     * Maximum number of idle pooled objects.
     */
    private Integer maxIdle;

    /**
     * Minimum number of (spare) idle pooled objects.
     */
    private Integer minIdle;

    /**
     * Maximum wait time for the borrow method.
     *
     * @see GenericKeyedObjectPool#getMaxWaitMillis()
     */
    private Long maxWaitMillis;

    /**
     * Test the object for validity upon borrow from the pool?
     */
    private Boolean testOnBorrow;

    /**
     * Test the object for validity upon returning to the pool?
     */
    private Boolean testOnReturn;

    /**
     * Test the object for validity when it is idle?
     */
    private Boolean testWhenIdle;

    /**
     * Time in milliseconds between idle object eviction runs.
     */
    private Long timeBetweenEvictionRunsMillis;

    /**
     * Number of objects to test per eviction run.
     */
    private Integer numTestsPerEvictionRun;

    /**
     * Time the object must be idle before it will be evicted.
     */
    private Long minEvictableIdleTimeMillis;

    @SuppressWarnings("PMD.CommentRequired")
    public Boolean getBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setBlockWhenExhausted(final Boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Integer getMaxTotal() {
        return maxTotal;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMaxTotal(final Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Integer getMaxIdle() {
        return maxIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMaxIdle(final Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Integer getMinIdle() {
        return minIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMinIdle(final Integer minIdle) {
        this.minIdle = minIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMaxWaitMillis(final Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setTestOnBorrow(final Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setTestOnReturn(final Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Boolean getTestWhenIdle() {
        return testWhenIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setTestWhenIdle(final Boolean testWhenIdle) {
        this.testWhenIdle = testWhenIdle;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setTimeBetweenEvictionRunsMillis(final Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Integer getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setNumTestsPerEvictionRun(final Integer numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMinEvictableIdleTimeMillis(final Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    /**
     * Apply the configuration to a pool configuration.
     *
     * @param genericConfig the configuration to register with the settings from this instance.
     */
    public void applyTo(final GenericObjectPoolConfig genericConfig) {
        genericConfig.setBlockWhenExhausted(getBlockWhenExhausted());
        genericConfig.setMaxIdle(getMaxIdle());
        genericConfig.setMaxTotal(getMaxTotal());
        genericConfig.setMinIdle(getMinIdle());
        genericConfig.setMaxWaitMillis(getMaxWaitMillis());
        genericConfig.setTestOnBorrow(getTestOnBorrow());
        genericConfig.setTestOnReturn(getTestOnReturn());
        genericConfig.setTestWhileIdle(getTestWhenIdle());
        genericConfig.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
        genericConfig.setNumTestsPerEvictionRun(getNumTestsPerEvictionRun());
        genericConfig.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
    }
}
