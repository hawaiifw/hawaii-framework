package org.hawaiiframework.logging.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.hawaiiframework.logging.util.LogUtil.indent;

/**
 * Utility for logging requests / responses.
 * <p>
 * The utility can be used to generate HTTP request / response log strings. Both for incoming service calls as outgoing calls (i.e. calls
 * to backend systems).
 */
@Component
public class HttpRequestResponseLogUtil {

    /**
     * The indentation to use.
     */
    private static final String INDENT = "  ";

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestResponseLogUtil.class);

    /**
     * Masks passwords in json strings.
     */
    private static final PasswordMaskerUtil PASSWORD_MASKER_UTIL = new PasswordMaskerUtil();

    /**
     * Constant for UTF-8 charset.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Create {@link HttpHeaders} for the @code{request}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public HttpHeaders getHeaders(final HttpServletRequest request) {
        final HttpHeaders headers = new HttpHeaders();

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                headers.add(headerName, values.nextElement());
            }
        }

        return headers;
    }

    /**
     * Create {@link HttpHeaders} for the @code{response}.
     */
    public HttpHeaders getHeaders(final HttpServletResponse response) {
        final HttpHeaders headers = new HttpHeaders();
        for (final String headerName : response.getHeaderNames()) {
            for (final String headerValue : response.getHeaders(headerName)) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

    /**
     * Create a log string for the given @code{headers} and @code{body}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public String createLogString(final HttpHeaders headers, final String body) throws IOException {
        return createLogString(headers, body.getBytes(UTF_8));
    }


    /**
     * Create a log string for the given @code{headers} and @code{body}.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public String createLogString(final HttpHeaders headers, final byte[] body) throws IOException {


        final StringBuilder builder = new StringBuilder();
        appendHeaders(INDENT, builder, headers);
        if (body.length > 0) {
            builder.append('\n');
            builder.append(new String(body, UTF_8));
        }
        final String value = builder.toString();
        if (value.lastIndexOf(NEW_LINE) == value.length()) {
            LOGGER.debug("We have trailing newline.");
        }

        // remove clear text password values and indent the multi line body.
        return indent(PASSWORD_MASKER_UTIL.maskPasswordsIn(value), INDENT);
    }

    private void appendHeaders(final String indent, final StringBuilder builder, final HttpHeaders headers) {
        final List<String> headerNames = new ArrayList<>(headers.keySet());
        Collections.sort(headerNames);
        if (!headerNames.isEmpty()) {
            builder.append(indent);
        }
        for (final String headerName : headerNames) {
            builder.append(headerName);
            builder.append(": ");
            builder.append(String.join(", ", headers.get(headerName)));
            builder.append(NEW_LINE);
        }
    }
}
