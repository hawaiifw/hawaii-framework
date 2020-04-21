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

import org.hawaiiframework.logging.model.AutoCloseableKibanaLogField;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.http.HawaiiRequestResponseLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.TIME_OUT;
import static org.hawaiiframework.logging.model.KibanaLogTypeNames.CALL_END;

/**
 * A logging client http request interceptor.
 * <p>
 * This logs the input and output of each call.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

    private final HawaiiRequestResponseLogger hawaiiRequestResponseLogger;

    /**
     * The constructor.
     */
    public LoggingClientHttpRequestInterceptor(final HawaiiRequestResponseLogger hawaiiLogger) {
        this.hawaiiRequestResponseLogger = hawaiiLogger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        try {
            hawaiiRequestResponseLogger.logRequest(request, body);
            final ClientHttpResponse response = execution.execute(request, body);
            hawaiiRequestResponseLogger.logResponse(response);
            return response;
        } catch (IOException t) {
            KibanaLogFields.callResult(TIME_OUT);
            try (AutoCloseableKibanaLogField kibanaLogField = KibanaLogFields.logType(CALL_END)) {
                LOGGER.info("Got timeout from backend.");
            }
            throw t;
        }
    }



}
