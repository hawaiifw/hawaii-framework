package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.util.HostResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CLIENT_IP;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.METHOD;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.URI;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.START;

/**
 * A filter that sets some Kibana Log Fields.
 */
@Component
public class KibanaLogFilter extends OncePerRequestFilter {

    /**
     * HostResolver for this class.
     */
    private final HostResolver hostResolver = new HostResolver();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        setDefaultLogFields(request);
        filterChain.doFilter(request, response);
    }

    private void setDefaultLogFields(final HttpServletRequest request) {
        KibanaLogFields.setLogType(START);
        KibanaLogFields.set(METHOD, request.getMethod());
        KibanaLogFields.set(URI, request.getRequestURI());
        KibanaLogFields.set(CLIENT_IP, hostResolver.getFrontendIPAddress(request));
    }
}
