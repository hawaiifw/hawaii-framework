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
package org.hawaiiframework.logging.web.util;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * An extension of {@link ContentCachingResponseWrapper} that keeps track whether the response is a
 * redirect.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ContentCachingWrappedResponse extends ContentCachingResponseWrapper {

  private static final Logger LOGGER = getLogger(ContentCachingWrappedResponse.class);

  /** Flag to indicate that the response is a redirect. */
  private boolean redirect;

  /** Flag to indicate that the response is a stream. */
  private boolean streaming;

  /**
   * The constructor.
   *
   * @param response The response to wrap.
   */
  public ContentCachingWrappedResponse(HttpServletResponse response) {
    super(response);
  }

  /** {@inheritDoc} */
  @Override
  public void sendError(int statusCode) throws IOException {
    redirect = true;
    super.sendError(statusCode);
  }

  /** {@inheritDoc} */
  @Override
  public void sendError(int statusCode, String message) throws IOException {
    redirect = true;
    super.sendError(statusCode, message);
  }

  /** {@inheritDoc} */
  @Override
  public void sendRedirect(String location) throws IOException {
    redirect = true;
    super.sendRedirect(location);
  }

  /**
   * Return {@code true} if this is a redirect.
   *
   * @return {@code true} if this is a redirect.
   */
  public boolean isRedirect() {
    return redirect;
  }

  @Override
  public void addHeader(String name, String value) {
    super.addHeader(name, value);
    if (isTextEventStreamHeader(name, value)) {
      LOGGER.debug("Triggered streaming for this content-caching response.");
      this.streaming = true;
    }
  }

  private static boolean isTextEventStreamHeader(String name, String value) {
    return CONTENT_TYPE.equals(name) && TEXT_EVENT_STREAM.equals(MediaType.valueOf(value));
  }

  @Override
  public void flushBuffer() throws IOException {
    if (streaming) {
      copyBodyToResponse(false);
      getResponse().flushBuffer();
    }
  }
}
