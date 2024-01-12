package org.hawaiiframework.async.sql;

import java.sql.SQLException;
import java.sql.Statement;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A query abort strategy.
 *
 * <p>The {@link QueryTaskAbortStrategy} will call {@link Statement#cancel()} in order to stop a
 * query. It depends on the JDBC driver and database whether the cancel request is honoured.
 */
public class QueryTaskAbortStrategy implements TaskAbortStrategy {

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(QueryTaskAbortStrategy.class);

  /** The statement to abort. */
  private final Statement statement;

  /**
   * Create a new instance for the given {@code statement}.
   *
   * @param statement The statement to abort.
   */
  public QueryTaskAbortStrategy(Statement statement) {
    this.statement = statement;
  }

  /** {@inheritDoc} */
  @Override
  public boolean invoke() {
    try {
      LOGGER.trace("Invoking Statement#cancel().");
      statement.cancel();
      return true;
    } catch (SQLException exception) {
      LOGGER.warn("Could not abort statement.", exception);
    }
    return false;
  }
}
