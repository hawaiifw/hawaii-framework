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
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.RequestId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.REQUEST_ID;

/**
 * A filter that assigns each request a unique request id and output the request id to the response header.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class RequestIdFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdFilter.class);

    /**
     * The incoming Hawaii request id header name.
     */
    private final String headerName;

    /**
     * Constructor.
     * @param headerName the name of the header to store the Hawaii request id.
     */
    public RequestIdFilter(final String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final UUID uuid = UUID.randomUUID();

        RequestId.set(uuid);
        KibanaLogFields.tag(REQUEST_ID, RequestId.get());

        LOGGER.debug("Set '{}' with value '{};.", REQUEST_ID.getLogName(), uuid);

        if (!response.containsHeader(headerName)) {
            response.addHeader(headerName, RequestId.get());
        }

        filterChain.doFilter(request, response);
    }
}
