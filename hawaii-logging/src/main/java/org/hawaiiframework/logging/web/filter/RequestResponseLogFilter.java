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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isOriginalRequest;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.markAsAsyncHandling;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.markAsInternalRedirect;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.unmarkAsAsyncHandling;
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
     * The filter voter.
     */
    private final FilterVoter filterVoter;

    /**
     * The constructor.
     *
     * @param hawaiiLogger The request response logger.
     * @param filterVoter  The filter voter.
     */
    public RequestResponseLogFilter(final HawaiiRequestResponseLogger hawaiiLogger, final FilterVoter filterVoter) {
        super();
        this.hawaiiLogger = hawaiiLogger;
        this.filterVoter = filterVoter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    protected void doFilterInternal(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain) throws ServletException, IOException {
        LOGGER.trace("Request dispatcher type is '{}'; is forward is '{}'.", httpServletRequest.getDispatcherType(),
                isOriginalRequest(httpServletRequest));

        if (!filterVoter.enabled(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {

            if (isOriginalRequest(httpServletRequest)) {
                final ContentCachingWrappedResponse wrappedResponse = new ContentCachingWrappedResponse(httpServletResponse);
                final ResettableHttpServletRequest wrappedRequest = new ResettableHttpServletRequest(httpServletRequest, wrappedResponse);
                doFilter(wrappedRequest, wrappedResponse, filterChain);
            } else if (httpServletRequest instanceof ResettableHttpServletRequest resettableHttpServletRequest) {
                doFilter(resettableHttpServletRequest,
                        (ContentCachingWrappedResponse) httpServletResponse,
                        filterChain);
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        }
    }

    private void doFilter(final ResettableHttpServletRequest wrappedRequest, final ContentCachingWrappedResponse wrappedResponse,
            final FilterChain filterChain)
            throws IOException, ServletException {
        hawaiiLogger.logRequest(wrappedRequest);

        // Do filter
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            handleInternalRedirect(wrappedResponse, wrappedRequest);
            handleAsyncRequest(wrappedRequest);

            if (isOriginalRequest(wrappedRequest)) {
                hawaiiLogger.logResponse(wrappedRequest, wrappedResponse);
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    private static void handleAsyncRequest(final ResettableHttpServletRequest httpServletRequest) {
        if (httpServletRequest.isAsyncStarted()) {
            markAsAsyncHandling(httpServletRequest);
        } else {
            unmarkAsAsyncHandling(httpServletRequest);
        }
    }

    private static void handleInternalRedirect(final ContentCachingWrappedResponse wrappedResponse,
            final ResettableHttpServletRequest wrappedRequest) {
        if (wrappedResponse.isRedirect()) {
            markAsInternalRedirect(wrappedRequest);
        } else {
            unmarkAsInternalRedirect(wrappedRequest);
        }
    }

}
