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

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.BUSINESS_TX_ID;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isInternalRedirect;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hawaiiframework.logging.model.BusinessTransactionId;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.UuidResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that assigns each request a unique transaction id and output the transaction id to the response header.
 */
public class BusinessTransactionIdFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessTransactionIdFilter.class);

    /**
     * The incoming Hawaii transaction id header name.
     */
    private final String headerName;

    /**
     * The UUID Resolver.
     */
    private final UuidResolver uuidResolver = new UuidResolver();

    /**
     * Constructor.
     * @param headerName the headerName to use for the Hawaii transaction id.
     */
    public BusinessTransactionIdFilter(final String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        if (!isInternalRedirect(request)) {
            final UUID uuid = uuidResolver.resolve(request, headerName);

            BusinessTransactionId.set(uuid);
            KibanaLogFields.tag(BUSINESS_TX_ID, BusinessTransactionId.get());

            LOGGER.debug("Set '{}' with value '{};.", BUSINESS_TX_ID.getLogName(), uuid);
        }

        try {
            if (!response.containsHeader(headerName)) {
                response.addHeader(headerName, BusinessTransactionId.get());
            }
            filterChain.doFilter(request, response);
        } finally {
            if (!isInternalRedirect(request)) {
                BusinessTransactionId.remove();
            }
        }
    }
}
