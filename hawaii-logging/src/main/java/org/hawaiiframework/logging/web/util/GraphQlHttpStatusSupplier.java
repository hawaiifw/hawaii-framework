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

import graphql.ErrorClassification;
import org.springframework.http.HttpStatus;

/**
 * GraphQl error classification to HTTP status.
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
public interface GraphQlHttpStatusSupplier {

  /**
   * Get the HTTP status from the graphQl error classification.
   *
   * @param error The error classification.
   * @return The HTTP status or {@code null}.
   */
  HttpStatus getStatus(ErrorClassification error);
}
