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
import java.io.IOException;
import org.hawaiiframework.logging.config.FilterVoter;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.model.MockMvcFilter;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;

/**
 * Filter that logs the input and output of each HTTP request. It also logs the duration of the
 * request.
 *
 * <p>For more inspiration see AbstractRequestLoggingFilter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class RequestResponseLogFilter extends AbstractGenericFilterBean implements MockMvcFilter {

  /** The request response logger to use. */
  private final HawaiiRequestResponseLogger hawaiiLogger;

  /** The filter voter. */
  private final FilterVoter filterVoter;

  /**
   * The constructor.
   *
   * @param hawaiiLogger The request response logger.
   * @param filterVoter The filter voter.
   */
  public RequestResponseLogFilter(
      HawaiiRequestResponseLogger hawaiiLogger, FilterVoter filterVoter) {
    super();
    this.hawaiiLogger = hawaiiLogger;
    this.filterVoter = filterVoter;
  }

  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {

    if (filterVoter.enabled(httpServletRequest)) {
      WrappedHttpRequestResponse wrapped = getWrapped(httpServletRequest, httpServletResponse);
      hawaiiLogger.logRequest(wrapped.request());

      try {
        filterChain.doFilter(wrapped.request(), wrapped.response());
      } finally {
        logResponse(wrapped);
      }
    } else {
      try {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
      } finally {
        logResponse(getWrapped(httpServletRequest));
      }
    }
  }

  private void logResponse(WrappedHttpRequestResponse wrapped) throws IOException {
    if (wrapped != null) {
      ResettableHttpServletRequest request = wrapped.request();
      if (!request.isAsyncStarted() && filterVoter.enabled(request)) {
        ContentCachingWrappedResponse response = wrapped.response();
        if (response != null) {
          hawaiiLogger.logResponse(request, response);
        }
      }
    }
  }

  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }
}
