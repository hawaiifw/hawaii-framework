package org.hawaiiframework.async.sql;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.ttddyy.dsproxy.proxy.ParameterSetOperation.isSetNullParameterOperation;
import static org.hawaiiframework.logging.util.LogUtil.indent;

/**
 * A listener for logging purposes and creation of a {@link QueryTaskAbortStrategy}.
 */
public class HawaiiQueryExecutionListener implements QueryExecutionListener {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiQueryExecutionListener.class);

    /**
     * The system's line separator.
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
    public void beforeQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        SharedTaskContextHolder.setTaskAbortStrategy(createAbortStrategy(execInfo));

        final QueryInfo queryInfo = queryInfoList.get(0);
        final StringBuilder builder = new StringBuilder();
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
        LOGGER.debug(indent(value, "  "));
    }

    private TaskAbortStrategy createAbortStrategy(final ExecutionInfo execInfo) {
        return new QueryTaskAbortStrategy(execInfo.getStatement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
        LOGGER.debug("Execution of query took '{}' msec.", execInfo.getElapsedTime());
    }
}
