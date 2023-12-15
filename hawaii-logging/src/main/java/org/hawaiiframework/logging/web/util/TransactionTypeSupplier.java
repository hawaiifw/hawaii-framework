package org.hawaiiframework.logging.web.util;

import java.io.IOException;

/**
 * The Transaction type supplier.
 *
 * @author Giuseppe Collura
 * @since 6.0.0.m4
 */
public interface TransactionTypeSupplier {


    /**
    * Get the transaction type for the {@code request}.
    *
    * @param request The request.
    * @return Optional, {@code null} if the type is unknown;
    */
    String getTransactionType(ResettableHttpServletRequest request) throws IOException;
}
