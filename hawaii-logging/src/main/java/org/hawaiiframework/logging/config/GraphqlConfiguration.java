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

package org.hawaiiframework.logging.config;

import static org.hawaiiframework.logging.config.filter.TransactionTypeFilterConfiguration.CONFIG_PREFIX;

import graphql.GraphQL;
import org.hawaiiframework.logging.web.filter.DataFetchExceptionBeanPostProcessor;
import org.hawaiiframework.logging.web.util.GraphQlTransactionTypeSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.GraphQlSource;

/** Configures the {@link GraphQlTransactionTypeSupplier} and the {@link DataFetchExceptionBeanPostProcessor}. */
@Configuration
@ConditionalOnClass({ GraphQL.class, GraphQlSource.class })
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
public class GraphqlConfiguration {

  /** The configuration properties' prefix. */
  public static final String CONFIG_PREFIX = "hawaii.logging.graphql";

  /**
   * Create a data fetch exception bean post processor that wraps
   * {@link DataFetcherExceptionResolverAdapter}s.
   *
   * @return the data fetch exception bean post processor.
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public DataFetchExceptionBeanPostProcessor dataFetchExceptionBeanPostProcessor() {
    return new DataFetchExceptionBeanPostProcessor();
  }

  /**
   * Create the {@link GraphQlTransactionTypeSupplier} bean.
   *
   * @return the {@link GraphQlTransactionTypeSupplier} bean.
   */
  @Bean
  @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", matchIfMissing = true)
  public GraphQlTransactionTypeSupplier graphQlTransactionTypeSupplier() {
    return new GraphQlTransactionTypeSupplier();
  }

}
