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

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.REQUEST_DURATION;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_DURATION;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.END;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.model.AutoCloseableKibanaLogField;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.MockMvcFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that logs the duration of the request.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class RequestDurationFilter extends AbstractGenericFilterBean implements MockMvcFilter {

  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestDurationFilter.class);

  /** The request attribute name for the start timestamp. */
  private static final String START_TIMESTAMP = "start_timestamp";

  /** The filter voter. */
  private final FilterVoter filterVoter;

  /**
   * The constructor.
   *
   * @param filterVoter The filter voter.
   */
  public RequestDurationFilter(FilterVoter filterVoter) {

    super();
    this.filterVoter = filterVoter;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getAttribute(START_TIMESTAMP) == null) {
      request.setAttribute(START_TIMESTAMP, System.nanoTime());
    }
    try {
      filterChain.doFilter(request, response);
    } finally {
      logEnd(request);
    }
  }

  private void logEnd(HttpServletRequest request) {
    if (!request.isAsyncStarted() && filterVoter.enabled(request)) {
      logEnd((Long) request.getAttribute(START_TIMESTAMP));
    }
  }

  @SuppressWarnings({"try", "unused"})
  private static void logEnd(Long start) {
    if (start == null) {
      LOGGER.info("Could not read start timestamp from request!");
      return;
    }

    try (AutoCloseableKibanaLogField endField = KibanaLogFields.tagCloseable(LOG_TYPE, END)) {
      String duration = String.format("%.2f", (System.nanoTime() - start) / 1E6);
      KibanaLogFields.tag(TX_DURATION, duration);
      KibanaLogFields.tag(REQUEST_DURATION, duration);
      LOGGER.info("Duration '{}' ms.", duration);
    }
  }

  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }
}
