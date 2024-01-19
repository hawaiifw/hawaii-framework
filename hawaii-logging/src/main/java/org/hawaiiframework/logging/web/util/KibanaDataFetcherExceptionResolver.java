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

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

/**
 * The kibana data fetch exception resolver adapter. Will translate the graphQl errors to error codes and add this to
 * the logging as {@code TX_STATUS}.
 *
 * <p>This wont intercept the default graphQl schema validation errors because these are caught early on in
 * the request.</p>
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
public class KibanaDataFetcherExceptionResolver implements DataFetcherExceptionResolver {

  private final DataFetcherExceptionResolver delegate;

  /**
   * The constructor.
   */
  public KibanaDataFetcherExceptionResolver(DataFetcherExceptionResolver delegate) {
    this.delegate = delegate;
  }

  /**
   * Convert {@link ErrorClassification} into an error code.
   *
   * @param errorClassification The error classification.
   * @return the, possibly {@code null}, error code.
   */
  private static Integer convertToErrorCode(ErrorClassification errorClassification) {
    if (errorClassification instanceof ErrorType errorType) {
      return switch (errorType) {
        case NOT_FOUND -> 404;
        case INTERNAL_ERROR -> 500;
        case BAD_REQUEST -> 400;
        case FORBIDDEN -> 403;
        case UNAUTHORIZED -> 401;
      };
    }

    // There is an error, but we can't identify the error type.
    return 400;
  }

  private static void setTransactionStatus(Integer statusCode) {
    if (statusCode != null) {
      KibanaLogFields.tag(TX_STATUS, statusCode);
    }
  }

  @Override
  public Mono<List<GraphQLError>> resolveException(Throwable exception,
      DataFetchingEnvironment environment) {
    Mono<List<GraphQLError>> monoErrors = delegate.resolveException(exception, environment);
    List<GraphQLError> errors = monoErrors.block();

    if (errors != null && !errors.isEmpty()) {
      ErrorClassification errorType = errors.get(0).getErrorType();
      Integer errorCode = convertToErrorCode(errorType);
      setTransactionStatus(errorCode);
    }

    return monoErrors;
  }
}
