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

import org.hawaiiframework.logging.model.KibanaLogField;
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

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CLIENT_IP;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.METHOD;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.URI;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.START;

/**
 * A filter that sets some Kibana Log Fields.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class KibanaLogFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KibanaLogFilter.class);

    /**
     * HostResolver for this class.
     */
    private final ClientIpResolver clientIpResolver;

    @Autowired
    public KibanaLogFilter(final ClientIpResolver clientIpResolver) {
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
        try (KibanaLogField kibanaLogField = KibanaLogFields.logType(START)) {
            KibanaLogFields.set(METHOD, request.getMethod());
            KibanaLogFields.set(URI, request.getRequestURI());
            KibanaLogFields.set(CLIENT_IP, clientIpResolver.getClientIp(request));
            LOGGER.info("Start request '{}', '{}'", request.getMethod(), request.getRequestURI());
        }
    }
}
