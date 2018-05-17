package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that cleans up the Kibana Log Fields.
 */
@Component
public class KibanaLogCleanupFilter extends OncePerRequestFilter {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KibanaLogCleanupFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            LOGGER.trace("Clearing Kibana log fields.");
            KibanaLogFields.clear();
        }
    }
}
