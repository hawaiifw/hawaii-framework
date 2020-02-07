package org.hawaiiframework.async.http;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


/**
 * HTTP request interceptor to set a transaction id as a header on an HTTP request.
 */
public class TransactionIdSupplierHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The header name to set.
     */
    private final String headerName;

    /**
     * Default constructor with 'X-Hawaii-Tx-Id' as {@code headername}.
     */
    public TransactionIdSupplierHttpRequestInterceptor() {
        this("X-Hawaii-Tx-Id");
    }

    /**
     * Constructor that sets the header name.
     *
     * @param headerName The header name to use.
     */
    public TransactionIdSupplierHttpRequestInterceptor(final String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
        throws IOException {
        final String txId = KibanaLogFields.get(KibanaLogFieldNames.TX_ID);
        if (StringUtils.isNotBlank(txId)) {
            request.getHeaders().add(headerName, txId);
        }
        return execution.execute(request, body);
    }

}
