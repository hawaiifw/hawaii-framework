package org.hawaiiframework.logging.http;

import org.hawaiiframework.logging.web.filter.ContentCachingWrappedResponse;
import org.hawaiiframework.logging.web.filter.ResettableHttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Responsible for logging Http requests and responses.
 */
public interface HawaiiRequestResponseLogger {

    void logRequest(HttpRequest request, byte[] body);

    void logRequest(ResettableHttpServletRequest wrappedRequest) throws IOException;

    void logResponse(ClientHttpResponse response) throws IOException;

    void logResponse(HttpServletRequest servletRequest, ContentCachingWrappedResponse wrappedResponse) throws IOException;

}
