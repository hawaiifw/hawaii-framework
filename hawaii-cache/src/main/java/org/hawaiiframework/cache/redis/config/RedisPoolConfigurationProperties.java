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


import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Configuration properties for an Apache Commons Generic Pool used by the redis configuration.
 *
 * @author Richard Kohlen
 * @version 3.0.0
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
    public void applyTo(final GenericObjectPoolConfig<?> genericConfig) {
        genericConfig.setBlockWhenExhausted(getBlockWhenExhausted());
        genericConfig.setMaxIdle(getMaxIdle());
        genericConfig.setMaxTotal(getMaxTotal());
        genericConfig.setMinIdle(getMinIdle());
        genericConfig.setMaxWait(Duration.of(getMaxWaitMillis(), MILLIS));
        genericConfig.setTestOnBorrow(getTestOnBorrow());
        genericConfig.setTestOnReturn(getTestOnReturn());
        genericConfig.setTestWhileIdle(getTestWhenIdle());
        genericConfig.setTimeBetweenEvictionRuns(Duration.of(getTimeBetweenEvictionRunsMillis(), MILLIS));
        genericConfig.setNumTestsPerEvictionRun(getNumTestsPerEvictionRun());
        genericConfig.setMinEvictableIdleTime(Duration.of(getMinEvictableIdleTimeMillis(), MILLIS));
    }
}
