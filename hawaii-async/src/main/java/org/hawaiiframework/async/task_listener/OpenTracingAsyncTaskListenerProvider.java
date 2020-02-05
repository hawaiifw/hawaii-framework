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
 * Task Context provider for open tracing's Tracer.
 */
public class OpenTracingAsyncTaskListenerProvider implements TaskListenerProvider, ApplicationContextAware,
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
    public TaskListener provide() {
        return new OpenTracingAsyncTaskListener(tracer, tracer.activeSpan());
    }

    @Override
    public void setApplicationContext(@NonNull final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull final ApplicationReadyEvent event) {
        this.tracer = applicationContext.getBean(Tracer.class);
    }
}
