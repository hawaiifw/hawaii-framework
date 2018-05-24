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
package org.hawaiiframework.logging.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * LoggingEvent encoder for Kibana.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class KibanaLogEventEncoder extends EncoderBase<LoggingEvent> {

    /**
     * Flag that indicates if a log entry is flushed immediately.
     * Not flushing immediately gives better performance, but
     * implies the risk of losing logging if the server crashes.
     */
    private static final boolean IMMEDIATE_FLUSH = true;

    /**
     * The logging event converter.
     */
    private final LoggingEventConverter loggingEventConverter;

    /**
     * Constructor used by logging (e.g. logback config).
     */
    public KibanaLogEventEncoder() {
        super();
        this.loggingEventConverter = new LoggingEventConverter();
    }

    /**
     * Constructor used unit testing.
     */
    public KibanaLogEventEncoder(final LoggingEventConverter loggingEventConverter) {
        super();
        this.loggingEventConverter = loggingEventConverter;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Override
    public void doEncode(final LoggingEvent event) throws IOException {
        final String line = loggingEventConverter.convert(event);
        outputStream.write(convertToBytes(line));

        if (IMMEDIATE_FLUSH) {
            outputStream.flush();
        }
    }

    private byte[] convertToBytes(final String msg) {
        return msg.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // do nothing;
    }


}
