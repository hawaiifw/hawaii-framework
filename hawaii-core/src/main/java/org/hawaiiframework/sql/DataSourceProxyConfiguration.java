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

import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;

import java.util.List;

/**
 * Configuration for the datasource proxy.
 */
@Configuration
public class DataSourceProxyConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSourceProxyBeanPostProcessor.class)
    @ConditionalOnClass(ProxyDataSource.class)
    public DataSourceProxyBeanPostProcessor datasourceProxyBeanPostProcessor(final DataSourceProxyFactory dataSourceProxyFactory) {
        return new DataSourceProxyBeanPostProcessor(dataSourceProxyFactory);
    }

    @Bean
    @ConditionalOnMissingBean(DataSourceProxyFactory.class)
    @ConditionalOnClass(ProxyDataSource.class)
    public DataSourceProxyFactory proxyDataSourceFactory(final List<OrderedQueryExecutionListener> listeners) {
        listeners.sort(OrderComparator.INSTANCE);
        return new DataSourceProxyFactory(listeners);
    }
}
