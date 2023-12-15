package org.hawaiiframework.logging.web.filter;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.web.util.ResettableHttpServletRequest;
import org.hawaiiframework.logging.web.util.TransactionTypeSupplier;
import org.hawaiiframework.logging.web.util.WrappedHttpRequestResponse;
import org.slf4j.Logger;


/**
 * A filter that assigns the transaction's name (class and method name) to the Kibana logger for each request.
 *
 * @author Richard Kohlen
 */
public class TransactionTypeFilter extends AbstractGenericFilterBean {

    private static final Logger LOGGER = getLogger(TransactionTypeFilter.class);
    private final List<TransactionTypeSupplier> suppliers;

    /**
     * The constructor.
     *
     * @param suppliers The transaction type suppliers.
     */
    public TransactionTypeFilter(final List<TransactionTypeSupplier> suppliers) {
        this.suppliers = suppliers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest httpRequest,
        final HttpServletResponse httpResponse,
        final FilterChain filterChain) throws ServletException, IOException {

        if (hasBeenFiltered(httpRequest)) {
            filterChain.doFilter(httpRequest, httpResponse);
        } else {
            try {
                markHasBeenFiltered(httpRequest);
                final WrappedHttpRequestResponse wrapped = getWrapped(httpRequest, httpResponse);

                final String type = getTransactionType(wrapped.request());
                KibanaLogFields.tag(TX_TYPE, type);
                LOGGER.debug("Set '{}' with value '{}'.", TX_TYPE.getLogName(), type);

                filterChain.doFilter(wrapped.request(), wrapped.response());
            } catch (IOException ignored) {
                LOGGER.warn("Could not determine the transaction type.", ignored);
            }
        }
    }

    private String getTransactionType(final ResettableHttpServletRequest wrappedRequest)
        throws IOException {
        for (TransactionTypeSupplier supplier : suppliers) {
            final String type = supplier.getTransactionType(wrappedRequest);
            if (type != null) {
                return type;
            }
        }
        return null;
    }
}
