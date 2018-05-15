package org.hawaiiframework.boot.autoconfigure.cache.redis;


import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Configuration properties for an Apache Commons Generic Pool used by the redis configuration.
 */
public class HawaiiRedisPoolProperties {

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


    public Boolean getBlockWhenExhausted() {
        return blockWhenExhausted;
    }


    public void setBlockWhenExhausted(final Boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }


    public Integer getMaxTotal() {
        return maxTotal;
    }


    public void setMaxTotal(final Integer maxTotal) {
        this.maxTotal = maxTotal;
    }


    public Integer getMaxIdle() {
        return maxIdle;
    }


    public void setMaxIdle(final Integer maxIdle) {
        this.maxIdle = maxIdle;
    }


    public Integer getMinIdle() {
        return minIdle;
    }


    public void setMinIdle(final Integer minIdle) {
        this.minIdle = minIdle;
    }


    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }


    public void setMaxWaitMillis(final Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }


    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }


    public void setTestOnBorrow(final Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }


    public Boolean getTestOnReturn() {
        return testOnReturn;
    }


    public void setTestOnReturn(final Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }


    public Boolean getTestWhenIdle() {
        return testWhenIdle;
    }


    public void setTestWhenIdle(final Boolean testWhenIdle) {
        this.testWhenIdle = testWhenIdle;
    }


    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }


    public void setTimeBetweenEvictionRunsMillis(final Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }


    public Integer getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }


    public void setNumTestsPerEvictionRun(final Integer numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }


    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }


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
