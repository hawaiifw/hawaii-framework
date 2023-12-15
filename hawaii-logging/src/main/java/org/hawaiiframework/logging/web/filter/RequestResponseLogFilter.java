/*
 * Copyright 2015-2023 the original author or authors.
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

import static org.hawaiiframework.logging.web.util.ServletFilterUtil.isOriginalRequest;
import static org.hawaiiframework.logging.web.util.ServletFilterUtil.markAsAsyncHandling;
import static org.hawaiiframework.logging.web.util.ServletFilterUtil.markAsInternalRedirect;
import static org.hawaiiframework.logging.web.util.ServletFilterUtil.unmarkAsAsyncHandling;
import static org.hawaiiframework.logging.web.util.ServletFilterUtil.unmarkAsInternalRedirect;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter that logs the input and output of each HTTP request. It also logs the duration of the
 * request.
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
    public RequestResponseLogFilter(final HawaiiRequestResponseLogger hawaiiLogger,
        final FilterVoter filterVoter) {
        super();
        this.hawaiiLogger = hawaiiLogger;
        this.filterVoter = filterVoter;
    }

    private static void handleAsyncRequest(final HttpServletRequest httpServletRequest) {
        if (httpServletRequest.isAsyncStarted()) {
            markAsAsyncHandling(httpServletRequest);
        } else {
            unmarkAsAsyncHandling(httpServletRequest);
        }
    }

    private static void handleInternalRedirect(final ContentCachingWrappedResponse wrappedResponse,
        final HttpServletRequest httpServletRequest) {
        if (wrappedResponse.isRedirect()) {
            markAsInternalRedirect(httpServletRequest);
        } else {
            unmarkAsInternalRedirect(httpServletRequest);
        }
    }

    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    protected void doFilterInternal(
        final HttpServletRequest httpServletRequest,
        final HttpServletResponse httpServletResponse,
        final FilterChain filterChain) throws ServletException, IOException {
        LOGGER.trace("Request dispatcher type is '{}'; is forward is '{}'.",
            httpServletRequest.getDispatcherType(),
            isOriginalRequest(httpServletRequest));

        if (!filterVoter.enabled(httpServletRequest) || hasBeenFiltered(httpServletRequest)) {
            try {
                LOGGER.trace("httpServletRequest has already been filtered.");
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } finally {
                logResponse(getWrapped(httpServletRequest));
            }
        } else {
            markHasBeenFiltered(httpServletRequest);
            final WrappedHttpRequestResponse wrapped = getWrapped(httpServletRequest, httpServletResponse);
            hawaiiLogger.logRequest(wrapped.request());

            try {
                filterChain.doFilter(wrapped.request(), wrapped.response());
            } finally {
                logResponse(wrapped);
            }
        }
    }

    private void logResponse(final WrappedHttpRequestResponse wrapped) throws IOException {
        final ResettableHttpServletRequest request = wrapped.request();
        if (!request.isAsyncStarted()) {
            final ContentCachingWrappedResponse response = wrapped.response();
            if (response != null) {
                hawaiiLogger.logResponse(request, response);
                response.copyBodyToResponse();
                handleInternalRedirect(response, request);
            }
        }
        handleAsyncRequest(request);
    }
}
