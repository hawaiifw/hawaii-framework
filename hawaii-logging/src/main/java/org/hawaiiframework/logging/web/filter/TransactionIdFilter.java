package org.hawaiiframework.logging.web.filter;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.TransactionId;
import org.hawaiiframework.logging.util.UuidResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames.TX_ID;

/**
 * A filter that assigns each request a unique transaction id and output the transaction id to the response header.
 */
@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    /**
     * String constant for incoming Hawaii transaction id header name.
     */
    public static final String X_HAWAII_TRANSACTION_ID_HEADER = "X-Hawaii-Tx-Id";

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    /**
     * The UUID Resolver.
     */
    private final UuidResolver uuidResolver = new UuidResolver();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final UUID uuid = uuidResolver.resolve(request, X_HAWAII_TRANSACTION_ID_HEADER);

        TransactionId.set(uuid);
        KibanaLogFields.set(TX_ID, TransactionId.get());

        LOGGER.debug("Set '{}' with value '{};.", CALL_ID.getLogName(), uuid);

        try {
            if (!response.containsHeader(X_HAWAII_TRANSACTION_ID_HEADER)) {
                response.addHeader(X_HAWAII_TRANSACTION_ID_HEADER, TransactionId.get());
            }
            filterChain.doFilter(request, response);
        } finally {
            TransactionId.remove();
        }
    }
}
