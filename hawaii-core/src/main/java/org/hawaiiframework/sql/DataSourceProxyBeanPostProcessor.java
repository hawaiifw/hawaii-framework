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

import javax.sql.DataSource;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

/** Bean post processor. */
public class DataSourceProxyBeanPostProcessor implements BeanPostProcessor {

  private final DataSourceProxyFactory dataSourceProxyFactory;

  public DataSourceProxyBeanPostProcessor(DataSourceProxyFactory dataSourceProxyFactory) {
    this.dataSourceProxyFactory = dataSourceProxyFactory;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String ignored) {
    return createDataSourceProxy(bean);
  }

  private Object createDataSourceProxy(Object bean) {
    if (!(bean instanceof DataSource) || bean instanceof ProxyDataSource) {
      return bean;
    }

    DataSource proxyDataSource = dataSourceProxyFactory.proxy((DataSource) bean);
    // Instead of directly returning a less specific datasource bean
    // (e.g.: HikariDataSource -> DataSource), return a proxy object.
    // See following links for why:
    //
    // https://stackoverflow.com/questions/44237787/how-to-use-user-defined-database-proxy-in-datajpatest
    //   https://gitter.im/spring-projects/spring-boot?at=5983602d2723db8d5e70a904
    //   http://blog.arnoldgalovics.com/2017/06/26/configuring-a-datasource-proxy-in-spring-boot/
    ProxyFactory factory = new ProxyFactory(bean);
    factory.setProxyTargetClass(true);
    factory.addAdvice(new DataSourceMethodInterceptor(proxyDataSource));
    return factory.getProxy();
  }
}
