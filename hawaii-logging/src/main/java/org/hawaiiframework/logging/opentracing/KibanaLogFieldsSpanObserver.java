/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging.opentracing;

import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A SpanObserver that adds some field data in KibanaLogFields.
 */
public class KibanaLogFieldsSpanObserver implements SpanObserver {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KibanaLogFieldsSpanObserver.class);

    @Override
    public void onSetOperationName(final SpanData spanData, final String operationName) {
        LOGGER.debug("onSetOperationName(spanData, '{}')", operationName);
    }

    @Override
    public void onSetTag(final SpanData spanData, final String key, final Object value) {
        LOGGER.debug("onSetTag(spanData, '{}', '{}')", key, value);
    }

    @Override
    public void onSetBaggageItem(final SpanData spanData, final String key, final String value) {
        LOGGER.debug("onSetBaggageItem");
    }

    @Override
    public void onLog(final SpanData spanData, final long timestampMicroseconds, final Map<String, ?> fields) {
        logOnLogEvent(timestampMicroseconds, fields);
    }

    @Override
    public void onLog(final SpanData spanData, final long timestampMicroseconds, final String event) {
        logOnLogEvent(timestampMicroseconds, event);
    }

    @Override
    public void onFinish(final SpanData spanData, final long finishMicros) {
        LOGGER.debug("onFinish");
    }

    private void logOnLogEvent(final long timestampMicroseconds, final Object payload) {
        LOGGER.debug("onLog(spanData, '{}', '{}')", timestampMicroseconds, payload);
    }
}
