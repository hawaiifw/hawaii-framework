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

import static net.ttddyy.dsproxy.proxy.ParameterSetOperation.isSetNullParameterOperation;
import static org.apache.commons.lang3.StringUtils.chomp;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.FAILURE;
import static org.hawaiiframework.logging.model.KibanaLogCallResultTypes.SUCCESS;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_REQUEST_BODY;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_STATUS;
import static org.hawaiiframework.logging.util.IndentUtil.indent;

import java.util.List;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.sql.OrderedQueryExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/** A listener for logging purposes. */
@ConditionalOnClass(QueryExecutionListener.class)
@Component
public class StatementLoggerQueryExecutionListener implements OrderedQueryExecutionListener {

  /** The logger to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(StatementLoggerQueryExecutionListener.class);

  /** The system's line separator. */
  private static final String LINE_SEPARATOR = System.lineSeparator();

  @Override
  public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    if (SqlStatementLogging.isSuppressed()) {
      return;
    }
    QueryInfo queryInfo = queryInfoList.get(0);
    StringBuilder builder = new StringBuilder(128);
    builder.append(queryInfo.getQuery());

    boolean parameterHeaderAppended = false;
    for (List<ParameterSetOperation> parameterSetOperations : queryInfo.getParametersList()) {
      for (ParameterSetOperation parameterSetOperation : parameterSetOperations) {
        if (!parameterHeaderAppended) {
          builder
              .append(LINE_SEPARATOR)
              .append(LINE_SEPARATOR)
              .append("parameters:")
              .append(LINE_SEPARATOR);
          parameterHeaderAppended = true;
        }
        if (isSetNullParameterOperation(parameterSetOperation)) {
          builder.append("null");
        } else {
          Object[] args = parameterSetOperation.getArgs();
          builder.append('\'').append(args[1]).append('\'');
        }
        builder.append(',').append(LINE_SEPARATOR);
      }
    }

    String value = builder.toString();
    if (value.endsWith("," + LINE_SEPARATOR)) {
      value = value.substring(0, value.length() - 1 - LINE_SEPARATOR.length());
    }
    KibanaLogFields.tag(CALL_REQUEST_BODY, value);
    LOGGER.info("Executing query: {}{}", LINE_SEPARATOR, indent(value));
    KibanaLogFields.clear(CALL_REQUEST_BODY);
  }

  @Override
  public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    if (SqlStatementLogging.isSuppressed()) {
      return;
    }
    /*
     * Note, logging of the results itself stumbles upon the problem that the (returned) result set (execInfo.result) must
     * be able to be reset. This must be done while creating the prepared statement, which is outside the control of this class.
     *
     * There is no generic setting available in Spring (JDBC) that sets the result set type.
     *
     * So logging of the result cannot be done here.
     */
    if (execInfo.isSuccess()) {
      KibanaLogFields.tag(CALL_STATUS, SUCCESS);
    } else {
      KibanaLogFields.tag(CALL_STATUS, FAILURE);
      logFailure(execInfo.getThrowable());
    }
    LOGGER.info("Execution of query took '{}' msec.", execInfo.getElapsedTime());
    KibanaLogFields.clear(CALL_STATUS);
  }

  private static void logFailure(Throwable throwable) {
    LOGGER.info("Query execution failed with error '{}'.", chomp(throwable.getMessage()));
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
