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
import org.hawaiiframework.logging.model.MockMvcFilter;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;

/**
 * A filter that starts content caching.
 *
 * @author Rutger Lubbers
 */
public class ContentCachingRequestResponseFilter extends AbstractGenericFilterBean
    implements MockMvcFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    WrappedHttpRequestResponse wrapped = getWrapped(httpServletRequest, httpServletResponse);
    try {
      filterChain.doFilter(wrapped.request(), wrapped.response());
    } finally {
      copyCachedResponse(wrapped);
    }
  }

  private void copyCachedResponse(WrappedHttpRequestResponse wrapped) throws IOException {
    if (wrapped != null) {
      ResettableHttpServletRequest request = wrapped.request();
      if (!isAsyncStarted(request)) {
        ContentCachingWrappedResponse response = wrapped.response();
        if (response != null) {
          response.copyBodyToResponse();
        }
      }
    }
  }

  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }
}
