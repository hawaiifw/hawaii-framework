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
package org.hawaiiframework.logging.opentracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapAdapter;
import org.hawaiiframework.logging.web.filter.AbstractGenericFilterBean;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A filter that adds the trace id to the response header.
 * <p>
 * The filter uses the configured tracer's ability to inject itself into HTTP header fields. So whatever is configured for
 * propagating the tracer's context, is used here as well. This allows switching between, for instance Jaeger and Zipkin format.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M11
 */
public class OpentracingResponseFilter extends AbstractGenericFilterBean
        implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    /**
     * The opentracing Tracer.
     */
    private Tracer tracer;

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final Span activeSpan = tracer.activeSpan();
        if (activeSpan != null) {
            addHeaders(response, activeSpan);
        }

        filterChain.doFilter(request, response);
    }

    private void addHeaders(final HttpServletResponse response, final Span activeSpan) {
        final TextMap headers = new TextMapAdapter(new HashMap<>());
        tracer.inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS, headers);

        headers.forEach(entry -> addHeader(response, entry));
    }

    private void addHeader(final HttpServletResponse response, final Map.Entry<String, String> entry) {
        addHeader(response, entry.getKey(), entry.getValue());
    }

    private void addHeader(final HttpServletResponse response, final String header, final String value) {
        if (!response.containsHeader(header)) {
            response.setHeader(header, value);
        }
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
