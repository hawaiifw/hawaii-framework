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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.web.util.ContentCachingWrappedResponse;
import org.slf4j.Logger;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Utility for logging requests / responses.
 *
 * <p>The utility can be used to generate HTTP request / response log strings. Both for incoming
 * service calls as outgoing calls (i.e. calls to backend systems).
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@SuppressWarnings("PMD.LooseCoupling")
public class HttpRequestResponseBodyLogUtil {

  private static final Logger LOGGER = getLogger(HttpRequestResponseBodyLogUtil.class);

  /** The configured newline to look for. */
  private static final String NEW_LINE = System.lineSeparator();

  /** Masks passwords in json, xml and query strings. */
  private final PasswordMaskerUtil passwordMasker;

  /**
   * The constructor for the log utility.
   *
   * @param passwordMasker The password masker utility.
   */
  public HttpRequestResponseBodyLogUtil(PasswordMaskerUtil passwordMasker) {
    this.passwordMasker = passwordMasker;
  }

  /**
   * Get the request body. With password masking.
   *
   * @param servletRequest The servlet request.
   * @return The body.
   * @throws IOException In case the body could not be read.
   */
  public String getTxRequestBody(HttpServletRequest servletRequest) throws IOException {
    return maskPasswords(getPostBody(servletRequest));
  }

  /**
   * Get the response body. With password masking.
   *
   * @param servletResponse The servlet response.
   * @return The body.
   */
  public String getTxResponseBody(ContentCachingWrappedResponse servletResponse) {
    String characterEncoding = servletResponse.getCharacterEncoding();
    if (APPLICATION_JSON_VALUE.equals(servletResponse.getContentType())) {
      characterEncoding = "UTF-8";
    }
    if (characterEncoding == null || characterEncoding.isEmpty()) {
      characterEncoding = Charset.defaultCharset().name();
    }
    return maskPasswords(toString(servletResponse.getContentAsByteArray(), characterEncoding));
  }

  /**
   * Get the call request body. With password masking.
   *
   * @param body The http request body.
   * @return The body.
   */
  public String getCallRequestBody(byte[] body) {
    return maskPasswords(toString(body, Charset.defaultCharset()));
  }

  /**
   * Get the call response body. With password masking.
   *
   * @param response The http response.
   * @return The body.
   * @throws IOException in case something went wrong getting the payload from the response.
   */
  public String getCallResponseBody(ClientHttpResponse response) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();

    return maskPasswords(getResponseBody(inputStringBuilder, response));
  }

  private static String toString(byte[] body, String charset) {
    return toString(body, Charset.forName(charset));
  }

  private static String toString(byte[] body, Charset charset) {
    return new String(body, charset);
  }

  private static String getPostBody(HttpServletRequest servletRequest) throws IOException {
    String body =
        IOUtils.toString(servletRequest.getInputStream(), servletRequest.getCharacterEncoding());
    if (StringUtils.isNotBlank(body)) {
      return body;
    }

    return getPostParametersBody(servletRequest);
  }

  private static String getPostParametersBody(HttpServletRequest request) {
    Map<String, String[]> parameters = request.getParameterMap();
    return getPostParametersBody(request, parameters);
  }

  private static String getPostParametersBody(
      HttpServletRequest request, Map<String, String[]> parameters) {
    if (parameters == null || parameters.isEmpty()) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    List<String> parameterNames = new ArrayList<>(parameters.keySet());
    Collections.sort(parameterNames);
    for (String parameterName : parameterNames) {
      String[] parameterValues = request.getParameterValues(parameterName);
      if (parameterValues != null) {
        for (String value : parameterValues) {
          stringBuilder.append(parameterName).append('=').append(value).append('\n');
        }
      }
    }
    return stringBuilder.toString();
  }

  private static String getResponseBody(
      StringBuilder inputStringBuilder, ClientHttpResponse response) throws IOException {
    try (InputStreamReader inputStreamReader = new InputStreamReader(response.getBody(), UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
      String line = bufferedReader.readLine();
      while (line != null) {
        inputStringBuilder.append(line)
            .append(NEW_LINE);
        line = bufferedReader.readLine();
      }
    } catch (HttpRetryException exception) {
      LOGGER.warn("Got retry exception.");
      LOGGER.trace("Stacktrace is: ", exception);
    } catch (IOException exception) {
      LOGGER.warn("Could not get response body.", exception);
    }

    return inputStringBuilder.toString();
  }

  private String maskPasswords(String input) {
    return passwordMasker.maskPasswordsIn(input);
  }
}
