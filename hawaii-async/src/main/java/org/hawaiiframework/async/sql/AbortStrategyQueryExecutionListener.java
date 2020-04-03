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

package org.hawaiiframework.async.sql;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.hawaiiframework.sql.OrderedQueryExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Query execution listener that registers an abort strategy for a task.
 */
@ConditionalOnClass(QueryExecutionListener.class)
@Component
public class AbortStrategyQueryExecutionListener implements OrderedQueryExecutionListener {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbortStrategyQueryExecutionListener.class);

    @Override
    public void beforeQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        LOGGER.trace("Registering abort strategy.");
        SharedTaskContextHolder.setTaskAbortStrategy(createAbortStrategy(execInfo));
    }

    @Override
    public void afterQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        // Do nothing.
    }

    private TaskAbortStrategy createAbortStrategy(final ExecutionInfo execInfo) {
        return new QueryTaskAbortStrategy(execInfo.getStatement());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
