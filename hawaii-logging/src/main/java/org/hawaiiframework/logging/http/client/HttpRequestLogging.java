package org.hawaiiframework.logging.http.client;

/** Utility class to suppress http request logging. */
public final class HttpRequestLogging {

  /** The state store per thread. */
  private static final ThreadLocal<HttpRequestLoggingState> THREAD_LOCAL_STORE =
      new ThreadLocal<>();

  /** Utility constructor. */
  private HttpRequestLogging() {
    // Do nothing.
  }

  /**
   * Suppress logging.
   *
   * @return The auto closeable state.
   */
  public static HttpRequestLoggingState suppress() {
    return suppress(true);
  }

  /**
   * Suppress logging. The {@code suppressed} can be used to wire suppression into calling code.
   *
   * @param suppressed Flag to allow configuration of suppression.
   * @return The auto closeable state.
   */
  public static HttpRequestLoggingState suppress(boolean suppressed) {
    HttpRequestLoggingState suppressor = new HttpRequestLoggingState();
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
   * Is logging enabled?
   *
   * @return {@code true} if the logging is enabled for this thread.
   */
  public static boolean isEnabled() {
    return !isSuppressed();
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
