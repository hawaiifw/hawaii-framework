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

import io.opentracing.Tracer;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

/**
 * Factory that creates a {@link OpenTracingAsyncTaskListener}.
 */
public class OpenTracingAsyncTaskListenerFactory implements TaskListenerFactory, ApplicationContextAware,
        ApplicationListener<ApplicationReadyEvent> {

    /**
     * The opentracing Tracer.
     */
    private Tracer tracer;
    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    @Override
    public TaskListener create() {
        return new OpenTracingAsyncTaskListener(tracer, tracer.activeSpan());
    }

    /*
     * The setup of this factory goes in two parts. The reason for this is that the async delegating executor is created
     * quite early in the spring bootstrap, while the tracer is created rather late.
     * Autowiring the tracer does not work, since then a new traces is being created. For Jaeger tracing this leads to errors.
     *
     * Hence the application context that is wired in here and the event listener.
     * So Jaeger can create it's tracer and we can use that tracer here by requesting it just before the application starts.
     */
    @Override
    public void setApplicationContext(@NonNull final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull final ApplicationReadyEvent event) {
        this.tracer = applicationContext.getBean(Tracer.class);
    }
}
