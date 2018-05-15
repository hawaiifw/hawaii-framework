package org.hawaiiframework.logging.http.client;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.HttpRequestResponseLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogCallResultTypes.BACKEND_FAILURE;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogCallResultTypes.SUCCESS;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogCallResultTypes.TIME_OUT;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.CALL_END;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.CALL_REQUEST_BODY;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.CALL_RESPONSE_BODY;

/**
 * A logging client http request interceptor.
 * <p>
 * This logs the input and output of each call.
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

    /**
     * Constant for UTF-8 charset.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * The request/response log util to use for generating log statements.
     */
    private final HttpRequestResponseLogUtil httpRequestResponseLogUtil;

    /**
     * The constructor.
     */
    public LoggingClientHttpRequestInterceptor(final HttpRequestResponseLogUtil httpRequestResponseLogUtil) {
        this.httpRequestResponseLogUtil = httpRequestResponseLogUtil;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        try {
            logRequest(request, body);
            final ClientHttpResponse response = execution.execute(request, body);
            logResponse(response);
            return response;
        } catch (IOException t) {
            KibanaLogFields.setLogType(CALL_END);
            KibanaLogFields.setCallResult(TIME_OUT);
            LOGGER.info("Got timeout from backend.");
            throw t;
        }
    }

    private void logRequest(final HttpRequest request, final byte[] body) throws IOException {
        KibanaLogFields.setLogType(CALL_REQUEST_BODY);
        LOGGER.debug("Called '{} {}':\n{}", request.getMethod(), request.getURI(),
                httpRequestResponseLogUtil.createLogString(request.getHeaders(), body));
        KibanaLogFields.unsetLogType();
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        final HttpStatus statusCode = response.getStatusCode();
        final String statusText = response.getStatusText();
        final String body = readResponseBody(response);

        logResponse(statusCode, statusText, response.getHeaders(), body);
    }

    private void logResponse(final HttpStatus statusCode, final String statusText, final HttpHeaders headers, final String body)
            throws IOException {
        KibanaLogFields.setLogType(CALL_RESPONSE_BODY);
        if (statusCode.is2xxSuccessful() || statusCode.is3xxRedirection()) {
            KibanaLogFields.setCallResult(SUCCESS);
        } else {
            KibanaLogFields.setCallResult(BACKEND_FAILURE);
        }

        LOGGER.debug("Got response '{} {}':\n{}", statusCode, statusText, httpRequestResponseLogUtil.createLogString(headers, body));
        KibanaLogFields.unsetLogType();
    }

    private String readResponseBody(final ClientHttpResponse response) throws IOException {
        final StringBuilder inputStringBuilder = new StringBuilder();

        return readResponseBody(inputStringBuilder, response);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private String readResponseBody(final StringBuilder inputStringBuilder, final ClientHttpResponse response) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(response.getBody(), UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append(NEW_LINE);
                line = bufferedReader.readLine();
            }
        }

        return inputStringBuilder.toString();
    }

}
