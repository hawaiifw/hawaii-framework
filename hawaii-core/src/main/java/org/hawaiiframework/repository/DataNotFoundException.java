package org.hawaiiframework.repository;

import org.hawaiiframework.exception.ApiError;
import org.hawaiiframework.exception.ApiException;

/**
 * Placeholder exception for data not found. These exceptions translate to 404 errors in the
 * controller.
 */
public class DataNotFoundException extends ApiException {

  /** The serial version UID. */
  private static final long serialVersionUID = -111268009416090869L;

  /**
   * Construct a new instance.
   *
   * @param apiError the error code
   */
  public DataNotFoundException(ApiError apiError) {
    this(apiError, null, null);
  }

  /**
   * Construct a new instance with a message.
   *
   * @param apiError the error code
   * @param message the message
   */
  public DataNotFoundException(ApiError apiError, String message) {
    this(apiError, null, message);
  }

  /**
   * Construct a new instance with a cause.
   *
   * @param orig the cause
   */
  public DataNotFoundException(ApiError apiError, Throwable orig) {
    this(apiError, orig, null);
  }

  /**
   * Construct a new instance with a cause and a message.
   *
   * @param orig the cause
   */
  public DataNotFoundException(ApiError apiError, Throwable orig, String message) {
    super(apiError, orig, message);
  }
}
