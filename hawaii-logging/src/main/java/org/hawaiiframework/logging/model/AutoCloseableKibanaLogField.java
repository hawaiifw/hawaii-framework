package org.hawaiiframework.logging.model;

import static org.hawaiiframework.logging.model.KibanaLogFields.tagCloseable;

import java.util.Collection;

/** A wrapper around a multiKibanaLogField where the field is closeable. */
public interface AutoCloseableKibanaLogField extends KibanaLogField, AutoCloseable {

  /**
   * Chain the closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  default AutoCloseableKibanaLogField and(KibanaLogField field, Enum<?> value) {
    AutoCloseableKibanaLogField other = tagCloseable(field, value);
    return new CompoundAutocloseableKibanaLogField(this, other);
  }

  /**
   * Chain the closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  default AutoCloseableKibanaLogField and(KibanaLogField field, int value) {
    AutoCloseableKibanaLogField other = tagCloseable(field, value);
    return new CompoundAutocloseableKibanaLogField(this, other);
  }

  /**
   * Chain the closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  default AutoCloseableKibanaLogField and(KibanaLogField field, String value) {
    AutoCloseableKibanaLogField other = tagCloseable(field, value);
    return new CompoundAutocloseableKibanaLogField(this, other);
  }

  /**
   * Chain the closeable.
   *
   * @param field The field to set.
   * @param value The value to set.
   * @return a closable field.
   */
  default AutoCloseableKibanaLogField and(KibanaLogField field, Collection<String> value) {
    AutoCloseableKibanaLogField other = tagCloseable(field, value);
    return new CompoundAutocloseableKibanaLogField(this, other);
  }

  @Override
  void close();
}
