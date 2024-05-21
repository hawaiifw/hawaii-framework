package org.hawaiiframework.logging.model;

import static java.util.Objects.requireNonNull;

/**
 * A wrapper around a KibanaLogField where the field is closeable.
 *
 * <p>Closing the field will remove the field (and it's value) from the KibanaLogFields, so further
 * logging will not be marked with the field.
 */
public class CompoundAutocloseableKibanaLogField implements AutoCloseableKibanaLogField {

  private final AutoCloseableKibanaLogField[] fields;

  /**
   * Create an instance that closes the given {@code fields}.
   *
   * @param fields The, never {@code null}, auto-closeable log fields.
   */
  public CompoundAutocloseableKibanaLogField(AutoCloseableKibanaLogField... fields) {
    this.fields = requireNonNull(fields);
  }

  @Override
  @SuppressWarnings("PMD.CloseResource")
  public void close() {
    for (AutoCloseableKibanaLogField field : fields) {
      field.close();
    }
  }

  @Override
  public String getLogName() {
    return "compound[" + fields.length + "]";
  }
}
