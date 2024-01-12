package org.hawaiiframework.logging.model;

/**
 * A wrapper around a KibanaLogField where the field is closeable.
 *
 * <p>Closing the field will remove the field (and it's value) from the KibanaLogFields, so further
 * logging will not be marked with the field.
 */
public class AutoCloseableKibanaLogField implements KibanaLogField, AutoCloseable {

  /** The delegate log field to close. */
  private final KibanaLogField delegate;

  /**
   * The constructor.
   *
   * @param delegate The delegate log field to close.
   */
  public AutoCloseableKibanaLogField(KibanaLogField delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getLogName() {
    return delegate.getLogName();
  }

  @Override
  public boolean matches(String key) {
    return delegate.matches(key);
  }

  @Override
  public void close() {
    KibanaLogFields.clear(this);
  }
}
