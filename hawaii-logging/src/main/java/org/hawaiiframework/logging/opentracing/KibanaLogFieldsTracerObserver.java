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
import io.opentracing.contrib.api.TracerObserver;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A TracerObserver that registers some of the span data in KibanaLogFields.
 */
public class KibanaLogFieldsTracerObserver implements TracerObserver {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KibanaLogFieldsTracerObserver.class);

    /**
     * The singleton span observer.
     */
    private final KibanaLogFieldsSpanObserver spanObserver;

    /**
     * The constructor.
     */
    public KibanaLogFieldsTracerObserver() {
        this.spanObserver = new KibanaLogFieldsSpanObserver();
    }

    @Override
    public SpanObserver onStart(final SpanData spanData) {
        KibanaLogFields.set(OpenTracingKibanaLogField.SPAN_ID, spanData.getSpanId());
        KibanaLogFields.set(OpenTracingKibanaLogField.TRACE_ID, spanData.getTraceId());
        LOGGER.debug("Start of span '{}'.", spanData.getSpanId());
        return spanObserver;
    }
}
