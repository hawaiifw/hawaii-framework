package org.hawaiiframework.logging.web.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream that can be 'reset', that is, the stream can be reset by supplying the (original) data again.
 */
public class ResettableServletInputStream extends ServletInputStream {

    /**
     * The input stream to use.
     */
    private final InputStream stream;

    /**
     * Flag to indicate that the stream is finished.
     */
    private boolean finished;

    /**
     * The constructor.
     */
    public ResettableServletInputStream(final byte[] rawData) {
        super();
        stream = new ByteArrayInputStream(rawData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        final int read = stream.read();
        if (read == -1) {
            finished = true;
        }
        return read;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReady() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadListener(final ReadListener listener) {
        // ignored
    }

    /**
     * Set the input to use for the stream.
     */
    public void reset() throws IOException {
        // This relies on the stream being a byte array input stream (or a stream that supports reset...)
        stream.reset();
    }
}
