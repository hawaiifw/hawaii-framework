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

package org.hawaiiframework.logging.web.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.hawaiiframework.logging.web.util.DataFetchTypeInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;

/**
 * Data fetch bean post processor. Will create a wrapper around
 * {@link DataFetcherExceptionResolverAdapter}s to extract the graphQl error codes if present.
 *
 * @author Giuseppe Collura
 * @since 6.0.0
 */
public class DataFetchExceptionBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(@Nonnull Object bean,
      @Nullable String ignored) {
    if (bean instanceof DataFetcherExceptionResolverAdapter) {
      ProxyFactory factory = new ProxyFactory(bean);
      factory.setProxyTargetClass(true);
      factory.addAdvice(new DataFetchTypeInterceptor());
      return factory.getProxy();
    }
    return bean;
  }
}
