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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;

import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.SOFTWARE_VERSION;

/**
 * Servlet filter that logs the software version of the application. It adds the version to the Kibana log fields as well.
 *
 * @author Rutger Lubbers
 * @since 3.0.0.M18
 */
public class SoftwareVersionLogFilter extends AbstractGenericFilterBean {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareVersionLogFilter.class);

    /**
     * The build properties.
     */
    private final BuildProperties buildProperties;

    private final FilterVoter filterVoter;

    /**
     * The constructor.
     *
     * @param buildProperties The build properties to get the version from.
     * @param filterVoter     The filter voter.
     */
    public SoftwareVersionLogFilter(final BuildProperties buildProperties, final FilterVoter filterVoter) {
        super();
        this.buildProperties = buildProperties;
        this.filterVoter = filterVoter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        if (filterVoter.enabled(request)) {
            final String version = buildProperties.getVersion();

            LOGGER.info("Software Build version '{}'.", version);
            KibanaLogFields.tag(SOFTWARE_VERSION, version);
        }

        // Do filter
        filterChain.doFilter(request, response);
    }

}
