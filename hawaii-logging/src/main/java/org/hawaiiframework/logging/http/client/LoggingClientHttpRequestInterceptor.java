/*
 * Copyright 2015-2019 the original author or authors.
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
package org.hawaiiframework.logging.http.client;

import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.FAILURE;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.TIMEOUT;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_STATUS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;

import java.io.IOException;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * A logging client http request interceptor.
 *
 * <p>This logs the input and output of each call.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  /** The logger to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

  private final HawaiiRequestResponseLogger hawaiiRequestResponseLogger;

  /**
   * The constructor.
   *
   * @param hawaiiLogger The logger to use.
   */
  public LoggingClientHttpRequestInterceptor(HawaiiRequestResponseLogger hawaiiLogger) {
    this.hawaiiRequestResponseLogger = hawaiiLogger;
  }

  /** {@inheritDoc} */
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    try {
      if (HttpRequestLogging.isEnabled()) {
        hawaiiRequestResponseLogger.logRequest(request, body);
      }
      ClientHttpResponse response = execution.execute(request, body);
      if (HttpRequestLogging.isEnabled()) {
        hawaiiRequestResponseLogger.logResponse(response);
      }
      return response;
    } catch (IOException t) {
      /*
       * We should detect a time-out properly. Most likely this _is_ a timeout, however, this is not certain.
       */
      KibanaLogFields.tag(CALL_STATUS, TIMEOUT);
      KibanaLogFields.tag(LOG_TYPE, CALL_END);
      LOGGER.info("Got IO exception during call, most likely a timeout from backend.", t);
      throw t;
    } catch (Throwable t) {
      KibanaLogFields.tag(CALL_STATUS, FAILURE);
      KibanaLogFields.tag(LOG_TYPE, CALL_END);
      LOGGER.info("Got exception during call, most likely a configuration issue.", t);
      throw t;
    } finally {
      KibanaLogFields.clear(CALL_STATUS, LOG_TYPE);
    }
  }
}
