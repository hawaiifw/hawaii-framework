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
package org.hawaiiframework.logging.web.util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream that can be 'reset', that is, the stream can be reset by supplying the (original) data again.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
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
     *
     * @param rawData A copy of another servlet input stream.
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
     *
     * @throws IOException in case the stream cannot be reset.
     */
    @Override
    public void reset() throws IOException {
        // This relies on the stream being a byte array input stream (or a stream that supports reset...)
        stream.reset();
    }
}
