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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * A filter that starts content caching.
 *
 * @author Rutger Lubbers
 */
public class ContentCachingRequestResponseFilter extends AbstractGenericFilterBean {

    private static final Logger LOGGER = getLogger(ContentCachingRequestResponseFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain) throws ServletException, IOException {

        if (hasBeenFiltered(httpServletRequest)) {
            try {
                LOGGER.trace("httpServletRequest has already been filtered.");
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } finally {
                copyCachedResponse(getWrapped(httpServletRequest));
            }
        } else {
            markHasBeenFiltered(httpServletRequest);
            final WrappedHttpRequestResponse wrapped = getWrapped(httpServletRequest, httpServletResponse);
            try {
                filterChain.doFilter(wrapped.request(), wrapped.response());
            } finally {
                copyCachedResponse(wrapped);
            }
        }
    }

    private void copyCachedResponse(final WrappedHttpRequestResponse wrapped) throws IOException {
        if (wrapped != null) {
            final ResettableHttpServletRequest request = wrapped.request();
            if (!request.isAsyncStarted()) {
                final ContentCachingWrappedResponse response = wrapped.response();
                if (response != null) {
                    response.copyBodyToResponse();
                }
            }
        }
    }

}
