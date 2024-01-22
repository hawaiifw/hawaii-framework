/*
 * Copyright 2015-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.logging.web.util;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_STATUS;
import static org.slf4j.LoggerFactory.getLogger;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.slf4j.Logger;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

/**
 * The kibana data fetch exception resolver adapter. Will translate the graphQl errors to error
 * codes and add this to the logging as {@code TX_STATUS}.
 *
 * <p>This wont intercept the default graphQl schema validation errors because these are caught
 * early on in the request.
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
public class KibanaDataFetcherExceptionResolver implements DataFetcherExceptionResolver {

  private static final Logger LOGGER = getLogger(KibanaDataFetcherExceptionResolver.class);

  private final DataFetcherExceptionResolver delegate;

  private final List<GraphQlHttpStatusSupplier> suppliers;

  /** The constructor. */
  public KibanaDataFetcherExceptionResolver(
      DataFetcherExceptionResolver delegate, List<GraphQlHttpStatusSupplier> suppliers) {
    this.delegate = delegate;
    this.suppliers = suppliers;
  }

  /**
   * Convert {@link ErrorClassification} into an error code.
   *
   * @param errorClassification The error classification.
   * @return the, possibly {@code null}, error code.
   */
  private HttpStatus convertToErrorCode(ErrorClassification errorClassification) {
    for (GraphQlHttpStatusSupplier supplier : suppliers) {
      HttpStatus status = supplier.getStatus(errorClassification);
      if (status != null) {
        return status;
      }
    }

    LOGGER.info(
        "Could not identify the HTTP status based on the graphQl error `{}`.", errorClassification);
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public Mono<List<GraphQLError>> resolveException(
      Throwable exception, DataFetchingEnvironment environment) {
    Mono<List<GraphQLError>> monoErrors = delegate.resolveException(exception, environment);
    List<GraphQLError> errors = monoErrors.block();

    if (errors != null && !errors.isEmpty()) {
      ErrorClassification errorType = errors.get(0).getErrorType();
      HttpStatus errorCode = convertToErrorCode(errorType);
      KibanaLogFields.tag(TX_STATUS, errorCode);
    }

    return monoErrors;
  }
}
