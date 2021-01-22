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
import org.hawaiiframework.logging.util.ClientIpResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_REQUEST_IP;
import static org.hawaiiframework.logging.web.filter.ServletFilterUtil.isInternalRedirect;

/**
 * A filter that sets some Kibana Log Fields.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ClientIpLogFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientIpLogFilter.class);

    /**
     * HostResolver for this class.
     */
    private final ClientIpResolver clientIpResolver;

    @Autowired
    public ClientIpLogFilter(final ClientIpResolver clientIpResolver) {
        this.clientIpResolver = clientIpResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        setDefaultLogFields(request);
        filterChain.doFilter(request, response);
    }

    private void setDefaultLogFields(final HttpServletRequest request) {
        if (!isInternalRedirect(request)) {
            final String clientIp = clientIpResolver.getClientIp(request);
            KibanaLogFields.tag(TX_REQUEST_IP, clientIp);
            LOGGER.info("Client ip is '{}'.", clientIp);
        }
    }
}
