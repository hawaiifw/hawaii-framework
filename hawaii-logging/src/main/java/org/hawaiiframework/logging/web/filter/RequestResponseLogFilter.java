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

import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isInternalRedirect;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.markAsInternalRedirect;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.unmarkAsInternalRedirect;

/**
 * Filter that logs the input and output of each HTTP request. It also logs the duration of the request.
 * <p>
 * For more inspiration see AbstractRequestLoggingFilter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class RequestResponseLogFilter extends AbstractGenericFilterBean {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    /**
     * The request response logger to use.
     */
    private final HawaiiRequestResponseLogger hawaiiLogger;

    /**
     * The constructor.
     */
    public RequestResponseLogFilter(final HawaiiRequestResponseLogger hawaiiLogger) {
        super();
        this.hawaiiLogger = hawaiiLogger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    protected void doFilterInternal(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        LOGGER.trace("Request dispatcher type is '{}'; is forward is '{}'.", httpServletRequest.getDispatcherType(),
                isInternalRedirect(httpServletRequest));


        // Create a new wrapped request, which we can use to get the body from.
        final ContentCachingWrappedResponse wrappedResponse = new ContentCachingWrappedResponse(response);
        final ResettableHttpServletRequest wrappedRequest = new ResettableHttpServletRequest(httpServletRequest, wrappedResponse);

        if (!isInternalRedirect(httpServletRequest)) {
            hawaiiLogger.logRequest(wrappedRequest);
        }

        // Do filter
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            if (wrappedResponse.isRedirect()) {
                markAsInternalRedirect(wrappedRequest);
            } else {
                unmarkAsInternalRedirect(wrappedRequest);
                hawaiiLogger.logResponse(wrappedRequest, wrappedResponse);
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

}
