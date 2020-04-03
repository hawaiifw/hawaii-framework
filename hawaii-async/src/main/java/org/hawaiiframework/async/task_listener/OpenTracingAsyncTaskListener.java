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

package org.hawaiiframework.async.task_listener;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.hawaiiframework.async.timeout.SharedTaskContext;

/**
 * Task listener for open tracing's Tracer. Should be executed first upon start and last upon finish.
 * <p>
 * The task listener creates a span for each async invocation.
 * <p>
 * Ordered at -200.
 */
public class OpenTracingAsyncTaskListener implements TaskListener {

    /**
     * Opentracing's tracer to use.
     */
    private final Tracer tracer;

    /**
     * The parent span to create a child span in.
     */
    private final Span parent;

    /**
     * The shared task context.
     */
    private SharedTaskContext sharedTaskContext;

    /**
     * The created child span.
     */
    private Span span;

    /**
     * The created scope.
     */
    private Scope scope;

    public OpenTracingAsyncTaskListener(final Tracer tracer, final Span parent) {
        this.tracer = tracer;
        this.parent = parent;
    }

    @Override
    public int getOrder() {
        return -200;
    }

    @Override
    public void setSharedTaskContext(final SharedTaskContext sharedTaskContext) {
        this.sharedTaskContext = sharedTaskContext;
    }

    @Override
    public void startExecution() {
        this.span = tracer
                .buildSpan(sharedTaskContext.getTaskName())
                .withTag(Tags.COMPONENT, "async")
                .asChildOf(parent)
                .start();
        this.scope = tracer.activateSpan(span);
    }

    @Override
    public void finish() {
        scope.close();
        span.finish();
    }
}
