package org.hawaiiframework.logging.http.client;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * HTTP request interceptor to set a business transaction id as a header on an HTTP request.
 *
 * <p>A business transaction id can be any (UUID) value a caller to a service wishes to supply.
 */
public class BusinessTransactionIdSupplierHttpRequestInterceptor
    implements ClientHttpRequestInterceptor {

  /** The header name to set. */
  private final String headerName;

  /** Default constructor with 'X-Hawaii-Business-Tx-Id' as {@code headerName}. */
  public BusinessTransactionIdSupplierHttpRequestInterceptor() {
    this("X-Hawaii-Business-Tx-Id");
  }

  /**
   * Constructor that sets the header name.
   *
   * @param headerName The header name to use.
   */
  public BusinessTransactionIdSupplierHttpRequestInterceptor(String headerName) {
    this.headerName = headerName;
  }

  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    // Read the value from the log fields, since for thread pool style execution, this should be
    // populated,
    // whereas the thread local variable might not be.
    String btxId = KibanaLogFields.get(KibanaLogFieldNames.BUSINESS_TX_ID);
    if (StringUtils.isNotBlank(btxId)) {
      request.getHeaders().add(headerName, btxId);
    }
    return execution.execute(request, body);
  }
}
