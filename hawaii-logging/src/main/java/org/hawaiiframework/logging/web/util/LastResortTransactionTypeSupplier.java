/*
 * Copyright 2015-2023 the original author or authors.
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

import org.springframework.core.annotation.Order;

/**
 * Default, last resort, URL based implementation of the {@link TransactionTypeSupplier}.
 *
 * @author Rutger Lubbers
 * @since 6.0.0
 */
@Order(20_000)
public class LastResortTransactionTypeSupplier implements TransactionTypeSupplier {

  @Override
  public String getTransactionType(ResettableHttpServletRequest request) {
    return "static-content";
  }
}
