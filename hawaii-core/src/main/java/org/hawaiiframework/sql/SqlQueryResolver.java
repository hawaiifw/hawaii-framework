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

import org.hawaiiframework.exception.HawaiiException;

/**
 * Interface to be implemented by objects that can resolve sql queries by name.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public interface SqlQueryResolver {


    /**
     * Resolve the given sql query by name.
     * <p>
     * To allow for {@code SqlQueryResolver} chaining, a {@code ViewResolver} should return
     * {@code null} if a sql query with the given name is not defined in it.
     *
     * @param sqlQueryName name of the sql query to resolve
     * @return the sql query, or {@code null} if not found (optional, to allow for {@code
     * SqlQueryResolver} chaining)
     * @throws HawaiiException if the sql query could not be resolved (typically in case of problems
     *         resolving the sql query)
     */
    String resolveSqlQuery(String sqlQueryName) throws HawaiiException;
}
