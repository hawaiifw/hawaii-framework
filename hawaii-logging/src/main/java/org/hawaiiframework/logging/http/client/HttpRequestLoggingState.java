package org.hawaiiframework.logging.http.client;

/**
 * Auto closeable state of http request logging.
 */
public class HttpRequestLoggingState implements AutoCloseable {

    /**
     * Stop suppressing the logging.
     */
    @Override
    public void close() {
        HttpRequestLogging.clear();
    }
}
