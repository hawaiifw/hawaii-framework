package io.kahu.hawaii.logging.web.filter;

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

import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.REQUEST_DURATION;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.TX_DURATION;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogTypeNames.END;

/**
 * A filter that logs the duration of the request.
 */
@Component
public class RequestDurationFilter extends OncePerRequestFilter {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDurationFilter.class);


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            logEnd(start);
        }
    }

    private void logEnd(final long start) {
        KibanaLogFields.setLogType(END);
        final String duration = String.format("%.2f", (System.nanoTime() - start) / 1E6);
        KibanaLogFields.set(TX_DURATION, duration);
        KibanaLogFields.set(REQUEST_DURATION, duration);
        LOGGER.info("Duration '{}' ms.", duration);
    }
}
