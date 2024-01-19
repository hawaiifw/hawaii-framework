/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging.config.filter;

import static org.hawaiiframework.logging.config.filter.FilterRegistrationBeanUtil.createFilterRegistrationBean;
import static org.hawaiiframework.logging.config.filter.TransactionTypeFilterConfiguration.CONFIG_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import org.hawaiiframework.logging.web.filter.TransactionTypeFilter;
import org.hawaiiframework.logging.web.util.SpringMvcTransactionTypeSupplier;
import org.hawaiiframework.logging.web.util.TransactionTypeSupplier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configures the {@link TransactionTypeFilter}. */
@Configuration
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class TransactionTypeFilterConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX = "hawaii.logging.filters.transaction-type";

  private static final Logger LOGGER = getLogger(TransactionTypeFilterConfiguration.class);

  @Value("${" + CONFIG_PREFIX + ".order:-1000}")
  private int filterOrder;

  /**
   * Create the {@link TransactionTypeFilter} bean.
   *
   * @param transactionTypeSuppliers The transaction type suppliers.
   * @return the {@link TransactionTypeFilter} bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public TransactionTypeFilter transactionTypeFilter(
      List<TransactionTypeSupplier> transactionTypeSuppliers) {
    LOGGER.trace("Configuration: order '{}'.", filterOrder);
    return new TransactionTypeFilter(transactionTypeSuppliers);
  }

  /**
   * Create the {@link SpringMvcTransactionTypeSupplier} bean.
   *
   * @param applicationContext The application context of the Spring Boot Application.
   * @return the {@link SpringMvcTransactionTypeSupplier} bean.
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public SpringMvcTransactionTypeSupplier springMvcTransactionTypeSupplier(
      ApplicationContext applicationContext) {
    return new SpringMvcTransactionTypeSupplier(applicationContext);
  }

  /**
   * Register the {@link TransactionTypeFilter} bean.
   *
   * @param transactionNameFilter the transaction name filter
   * @return the {@link TransactionTypeFilter} bean, wrapped in a {@link FilterRegistrationBean}
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public FilterRegistrationBean<TransactionTypeFilter> transactionTypeFilterRegistration(
      TransactionTypeFilter transactionNameFilter) {
    return createFilterRegistrationBean(transactionNameFilter, filterOrder);
  }
}
