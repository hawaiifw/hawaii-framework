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

package org.hawaiiframework.sql;

import java.lang.reflect.Method;
import javax.sql.DataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

/** Method interceptor for data sources. */
public class DataSourceMethodInterceptor implements MethodInterceptor {

  private final DataSource dataSource;

  /** Constructor with a {@code dataSource}. */
  public DataSourceMethodInterceptor(DataSource dataSource) {
    super();
    this.dataSource = dataSource;
  }

  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Method proxyMethod =
        ReflectionUtils.findMethod(this.dataSource.getClass(), invocation.getMethod().getName());
    if (proxyMethod != null) {
      return proxyMethod.invoke(this.dataSource, invocation.getArguments());
    }
    return invocation.proceed();
  }
}
