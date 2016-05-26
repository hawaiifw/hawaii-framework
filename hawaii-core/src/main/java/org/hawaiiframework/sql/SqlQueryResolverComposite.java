/*
 * Copyright 2015-2016 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hawaiiframework.exception.HawaiiException;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

/**
 * A {@link SqlQueryResolver} that delegates to others.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class SqlQueryResolverComposite implements SqlQueryResolver, Ordered {

    private final List<SqlQueryResolver> sqlQueryResolvers = new ArrayList<>();

    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * Return the list of {@link SqlQueryResolver}s to delegate to.
     */
    public List<SqlQueryResolver> getSqlQueryResolvers() {
        return Collections.unmodifiableList(this.sqlQueryResolvers);
    }

    /**
     * Set the list of {@link SqlQueryResolver}s to delegate to.
     */
    public void setSqlQueryResolvers(List<SqlQueryResolver> sqlQueryResolvers) {
        this.sqlQueryResolvers.clear();
        if (!CollectionUtils.isEmpty(sqlQueryResolvers)) {
            this.sqlQueryResolvers.addAll(sqlQueryResolvers);
        }
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String resolveSqlQuery(String sqlQueryName) throws HawaiiException {
        for (SqlQueryResolver sqlQueryResolver : this.sqlQueryResolvers) {
            String sqlQuery = sqlQueryResolver.resolveSqlQuery(sqlQueryName);
            if (sqlQuery != null) {
                return sqlQuery;
            }
        }
        return null;
    }
}
