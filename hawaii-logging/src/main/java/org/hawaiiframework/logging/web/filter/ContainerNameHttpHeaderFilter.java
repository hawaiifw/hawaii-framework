/*
 * Copyright 2015-2020 the original author or authors.
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

import org.hawaiiframework.logging.config.filter.ContainerNameHttpHeaderFilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter class that will be added in the Tomcat filter chain to add a http response header to every response.
 * This response header will be the value of the $HOSTNAME environment variable, to show in the frontend in which container this
 * application is running.
 */
public class ContainerNameHttpHeaderFilter extends AbstractGenericFilterBean {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerNameHttpHeaderFilter.class);

    /**
     * The header name to set.
     */
    private final String headerName;

    /**
     * The hostname to set in the header.
     */
    private final String hostname;

    /**
     * Constructor.
     *
     * @param config The configuration.
     */
    public ContainerNameHttpHeaderFilter(final ContainerNameHttpHeaderFilterProperties config) {
        super();
        this.headerName = config.getHttpHeader();
        this.hostname = config.getHostname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
        throws ServletException, IOException {

        LOGGER.debug("Set '{}' with value '{}'.", headerName, hostname);

        if (!response.containsHeader(headerName)) {
            response.addHeader(headerName, hostname);
        }

        filterChain.doFilter(request, response);
    }
}
