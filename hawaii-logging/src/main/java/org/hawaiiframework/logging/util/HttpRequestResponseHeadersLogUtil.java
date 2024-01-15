/*
 * Copyright 2015-2021 the original author or authors.
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

package org.hawaiiframework.logging.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Utility for logging requests / responses.
 *
 * <p>The utility can be used to generate HTTP request / response header log strings. Both for
 * incoming service calls as outgoing calls (i.e. calls to backend systems).
 *
 * @author Rutger Lubbers
 * @since 3.0.0
 */
@SuppressWarnings("PMD.LooseCoupling")
public class HttpRequestResponseHeadersLogUtil {

  /** The configured newline to look for. */
  private static final String NEW_LINE = System.lineSeparator();

  /** Masks passwords in json strings. */
  private final PasswordMaskerUtil passwordMasker;

  /**
   * The constructor for the log utility.
   *
   * @param passwordMasker The password masker utility.
   */
  public HttpRequestResponseHeadersLogUtil(PasswordMaskerUtil passwordMasker) {
    this.passwordMasker = passwordMasker;
  }

  /**
   * Get request headers. With password masking.
   *
   * @param servletRequest The servlet request.
   * @return The headers as string.
   */
  public String getTxRequestHeaders(HttpServletRequest servletRequest) {
    HttpHeaders headers = creatHttpHeaders(servletRequest);
    return createHeadersAsString(headers);
  }

  /**
   * Get response headers. With password masking.
   *
   * @param servletResponse The servlet response.
   * @return The headers as string.
   */
  public String getTxResponseHeaders(HttpServletResponse servletResponse) {
    HttpHeaders headers = creatHttpHeaders(servletResponse);
    return createHeadersAsString(headers);
  }

  /** Create {@link HttpHeaders} for the {@code request}. */
  @SuppressWarnings("PMD.LawOfDemeter")
  private static HttpHeaders creatHttpHeaders(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();

    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      Enumeration<String> values = request.getHeaders(headerName);
      while (values.hasMoreElements()) {
        headers.add(headerName, values.nextElement());
      }
    }

    return headers;
  }

  /** Create {@link HttpHeaders} for the {@code response}. */
  private static HttpHeaders creatHttpHeaders(HttpServletResponse response) {
    HttpHeaders headers = new HttpHeaders();
    for (String headerName : response.getHeaderNames()) {
      for (String headerValue : response.getHeaders(headerName)) {
        headers.add(headerName, headerValue);
      }
    }
    return headers;
  }

  /**
   * Get call request headers. With password masking.
   *
   * @param request The http request.
   * @return The headers as string.
   */
  public String getCallRequestHeaders(HttpRequest request) {
    HttpHeaders headers = request.getHeaders();
    return createHeadersAsString(headers);
  }

  /**
   * Get call response headers. With password masking.
   *
   * @param response The http response.
   * @return The headers as string.
   */
  public String getCallResponseHeaders(ClientHttpResponse response) {
    HttpHeaders headers = response.getHeaders();
    return createHeadersAsString(headers);
  }

  private String createHeadersAsString(HttpHeaders headers) {
    StringBuilder builder = new StringBuilder();
    appendHeaders(builder, headers);
    String value = builder.toString();

    // remove clear text password values and indent the multi line body.
    return passwordMasker.maskPasswordsIn(value);
  }

  private static void appendHeaders(StringBuilder builder, HttpHeaders headers) {
    List<String> headerNames = new ArrayList<>(headers.keySet());
    Collections.sort(headerNames);

    for (String headerName : headerNames) {
      builder
          .append(headerName)
          .append(": ")
          .append(String.join(", ", headers.get(headerName)))
          .append(NEW_LINE);
    }
  }
}
