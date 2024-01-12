package org.hawaiiframework.logging.sql;

/** Utility class to suppress sql logging. */
public final class SqlStatementLogging {

  /** The state store per thread. */
  private static final ThreadLocal<SqlLoggingState> THREAD_LOCAL_STORE = new ThreadLocal<>();

  /** Utility constructor. */
  private SqlStatementLogging() {
    // Do nothing.
  }

  /**
   * Suppress logging.
   *
   * @return The auto closeable state.
   */
  public static SqlLoggingState suppress() {
    return suppress(true);
  }

  /**
   * Suppress logging. The {@code suppressed} can be used to wire suppression into calling code.
   *
   * @param suppressed Flag to allow configuration of suppression.
   * @return The auto closeable state.
   */
  public static SqlLoggingState suppress(boolean suppressed) {
    SqlLoggingState suppressor = new SqlLoggingState();
    if (suppressed) {
      THREAD_LOCAL_STORE.set(suppressor);
    }
    return suppressor;
  }

  /** Enable logging. */
  public static void enable() {
    THREAD_LOCAL_STORE.remove();
  }

  /**
   * Is logging suppressed?
   *
   * @return {@code true} if the logging is suppressed for this thread.
   */
  public static boolean isSuppressed() {
    return THREAD_LOCAL_STORE.get() != null;
  }

  /** Continue with normal logging. */
  public static void clear() {
    THREAD_LOCAL_STORE.remove();
  }
}
