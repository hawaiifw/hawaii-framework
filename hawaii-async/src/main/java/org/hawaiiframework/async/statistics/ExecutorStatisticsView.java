package org.hawaiiframework.async.statistics;

/**
 * A view on the {@link ExecutorStatistics}.
 */
public class ExecutorStatisticsView {

    /**
     * The pool size in use.
     */
    private final Integer poolSize;

    /**
     * The configured max pool size.
     */
    private final Integer maxPoolSize;

    /**
     * The queue size.
     */
    private final Integer queueSize;

    /**
     * The number of completed tasks.
     */
    private final Long completedTaskCount;

    /**
     * The number of aborted tasks.
     */
    private final Long abortedTaskCount;

    /**
     * Create a view on {@link ExecutorStatistics}.
     */
    public ExecutorStatisticsView(final ExecutorStatistics statistics) {
        this.poolSize = statistics.getPoolSize();
        this.maxPoolSize = statistics.getMaxPoolSize();
        this.queueSize = statistics.getQueueSize();
        this.completedTaskCount = statistics.getCompletedTaskCount();
        this.abortedTaskCount = statistics.getAbortedTaskCount();
    }

    /**
     * Get the executor's pool size.
     *
     * @return The current number of threads in use by the executor.
     */
    public Integer getPoolSize() {
        return poolSize;
    }

    /**
     * Get the executor's max pool size.
     *
     * @return The executor's max pool size.
     */
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Get the executor's queued task size.
     *
     * @return The number of queued tasks.
     */
    public Integer getQueueSize() {
        return queueSize;
    }

    /**
     * Get the number of completed tasks.
     *
     * @return The executor's completed task count.
     */
    public Long getCompletedTaskCount() {
        return completedTaskCount;
    }

    /**
     * Get the number of aborted tasks.
     *
     * @return The executor's aborted task count.
     */
    public Long getAbortedTaskCount() {
        return abortedTaskCount;
    }

}
