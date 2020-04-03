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

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.util.List;

/**
 * Factory to create a datasource proxy with listeners.
 * <p>
 * See {@link OrderedQueryExecutionListener}s.
 */
public class DataSourceProxyFactory {

    /**
     * The listeners to use in the data source proxy.
     */
    private final List<OrderedQueryExecutionListener> listeners;

    /**
     * The constructor.
     *
     * @param listeners The listeners to use in the data source proxy.
     */
    public DataSourceProxyFactory(final List<OrderedQueryExecutionListener> listeners) {
        this.listeners = listeners;
    }

    private boolean isEmpty() {
        return listeners.isEmpty();
    }

    /**
     * Creates a data source proxy for the {@code target} iff there are listeners.
     *
     * Otherwise it will return the target itself.
     *
     * @param target The data source to create a proxy for.
     * @return The data source to use.
     */
    public DataSource proxy(final DataSource target) {
        if (isEmpty()) {
            return target;
        }

        final ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create(target);
        for (final OrderedQueryExecutionListener listener : listeners) {
            builder.listener(listener);
        }
        return builder.build();
    }
}
