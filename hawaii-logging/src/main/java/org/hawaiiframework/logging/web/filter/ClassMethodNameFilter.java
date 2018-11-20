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
package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_TYPE;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isInternalRedirect;

/**
 * A filter that assigns the class name and method name to the Kibana logger for each request.
 *
 * @author Richard Kohlen
 */
public class ClassMethodNameFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassMethodNameFilter.class);

    /**
     * Application context (ac), ac is the context of this Spring Boot Application.
     * Ac is needed to get the appropriate handler for each request.
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructor, the application context should be provided when constructing this class. This class cannot be a bean
     * because it inhered from AbstractGenericFilterBean.
     *
     * @param applicationContext the application context of the Spring Boot Application
     */
    public ClassMethodNameFilter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        if (!isInternalRedirect(request)) {
            logRequest(request);
        }

        filterChain.doFilter(request, response);
    }

    private void logRequest(final HttpServletRequest request) {
        HandlerMethod handler = null;

        for (HandlerMapping handlerMapping : applicationContext.getBeansOfType(HandlerMapping.class).values()) {
            HandlerExecutionChain handlerExecutionChain = null;
            try {
                handlerExecutionChain = handlerMapping.getHandler(request);
            } catch (Exception e) {
                LOGGER.warn("Exception when fetching the handler");
            }
            if (handlerExecutionChain != null) {
                final var tempHandler = handlerExecutionChain.getHandler();
                handler = tempHandler instanceof HandlerMethod ? (HandlerMethod) tempHandler : null;
                break;
            }
        }

        if (handler == null) {
            LOGGER.debug("HANDLER NOT FOUND");
        } else {

            final var nameMethod = handler.getMethod().getName();
            final var nameController = handler.getBeanType().getSimpleName();
            final var value = nameController + "." + nameMethod;

            KibanaLogFields.set(TX_TYPE, value);
            LOGGER.debug("Set '{}' with value '{};", TX_TYPE.getLogName(), value);
        }
    }

}
