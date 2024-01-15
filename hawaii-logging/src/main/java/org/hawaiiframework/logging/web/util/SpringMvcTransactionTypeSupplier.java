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

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Spring MVC implementation of the {@link TransactionTypeSupplier}.
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
@Order(1_000)
public class SpringMvcTransactionTypeSupplier implements TransactionTypeSupplier {

  private static final Logger LOGGER = getLogger(SpringMvcTransactionTypeSupplier.class);

  private final ApplicationContext applicationContext;

  /**
   * The constructor.
   *
   * @param applicationContext The application context.
   */
  public SpringMvcTransactionTypeSupplier(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public String getTransactionType(ResettableHttpServletRequest request) {
    for (HandlerMapping handlerMapping : applicationContext.getBeansOfType(HandlerMapping.class)
        .values()) {
      HandlerExecutionChain handlerExecutionChain = null;
      try {
        handlerExecutionChain = handlerMapping.getHandler(request);
      } catch (Exception exception) {
        LOGGER.warn("Exception when fetching the handler");
      }
      String transactionType = getTransactionType(handlerExecutionChain);
      if (transactionType != null) {
        return transactionType;
      }
    }

    LOGGER.debug("No handler found.");
    return null;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  private static String getTransactionType(HandlerExecutionChain handlerExecutionChain) {
    if (handlerExecutionChain != null) {
      var tempHandler = handlerExecutionChain.getHandler();
      if (tempHandler instanceof HandlerMethod handlerMethod) {
        return getTransactionType(handlerMethod);
      }
    }
    return null;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  private static String getTransactionType(HandlerMethod handler) {
    var nameMethod = handler.getMethod().getName();
    var nameController = handler.getBeanType().getSimpleName();
    return nameController + "." + nameMethod;
  }
}
