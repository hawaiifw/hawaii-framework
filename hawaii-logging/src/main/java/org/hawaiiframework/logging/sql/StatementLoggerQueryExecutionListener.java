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

package org.hawaiiframework.logging.sql;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.hawaiiframework.sql.OrderedQueryExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.ttddyy.dsproxy.proxy.ParameterSetOperation.isSetNullParameterOperation;
import static org.apache.commons.lang3.StringUtils.chomp;
import static org.hawaiiframework.logging.util.LogUtil.indent;

/**
 * A listener for logging purposes.
 */
@ConditionalOnClass(QueryExecutionListener.class)
@Component
public class StatementLoggerQueryExecutionListener implements OrderedQueryExecutionListener {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatementLoggerQueryExecutionListener.class);

    /**
     * The system's line separator.
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        if (SqlStatementLogging.isSuppressed()) {
            return;
        }
        final QueryInfo queryInfo = queryInfoList.get(0);
        final StringBuilder builder = new StringBuilder(128);
        builder.append("Executing query:").append(LINE_SEPARATOR).append(queryInfo.getQuery());

        boolean parameterHeaderAppended = false;
        for (final List<ParameterSetOperation> parameterSetOperations : queryInfo.getParametersList()) {
            for (final ParameterSetOperation parameterSetOperation : parameterSetOperations) {
                if (!parameterHeaderAppended) {
                    builder.append(LINE_SEPARATOR).append(LINE_SEPARATOR).append("parameters:").append(LINE_SEPARATOR);
                    parameterHeaderAppended = true;
                }
                if (isSetNullParameterOperation(parameterSetOperation)) {
                    builder.append("null");
                } else {
                    final Object[] args = parameterSetOperation.getArgs();
                    builder.append('\'').append(args[1]).append('\'');
                }
                builder.append(',').append(LINE_SEPARATOR);
            }
        }

        String value = builder.toString();
        if (value.endsWith("," + LINE_SEPARATOR)) {
            value = value.substring(0, value.length() - 1 - LINE_SEPARATOR.length());
        }
        LOGGER.info(indent(value, "  "));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        if (SqlStatementLogging.isSuppressed()) {
            return;
        }
        LOGGER.debug("Execution of query took '{}' msec.", execInfo.getElapsedTime());
        if (!execInfo.isSuccess()) {
            logFailure(execInfo.getThrowable());
        }
    }

    private void logFailure(final Throwable throwable) {
        LOGGER.info("Query execution failed with error '{}'.", chomp(throwable.getMessage()));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
