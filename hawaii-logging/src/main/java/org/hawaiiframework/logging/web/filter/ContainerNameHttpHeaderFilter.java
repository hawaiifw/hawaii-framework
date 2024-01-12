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

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.HOST_NAME;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter class that will be added in the servlet filter chain to add a http response header to
 * every response. This response header will be the value of the $HOSTNAME environment variable, to
 * show in the frontend in which container this application is running.
 */
public class ContainerNameHttpHeaderFilter extends AbstractGenericFilterBean {

  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ContainerNameHttpHeaderFilter.class);

  /** The header name to set. */
  private final String headerName;

  /** The hostname to set in the header. */
  private final String hostname;

  /**
   * Constructor.
   *
   * @param headerName The header to set.
   * @param hostName The value to set.
   */
  public ContainerNameHttpHeaderFilter(String headerName, String hostName) {
    super();
    this.headerName = headerName;
    this.hostname = hostName;
  }

  /** {@inheritDoc} */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!hasBeenFiltered(request)) {
      markHasBeenFiltered(request);

      KibanaLogFields.tag(HOST_NAME, hostname);
      LOGGER.debug("Set '{}' with value '{}'.", headerName, hostname);

      if (!response.containsHeader(headerName)) {
        response.addHeader(headerName, hostname);
      }
    }

    filterChain.doFilter(request, response);
  }
}
