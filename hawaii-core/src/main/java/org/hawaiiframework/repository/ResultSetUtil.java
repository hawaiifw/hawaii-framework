package org.hawaiiframework.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/** Utility to extract null values from a result set. */
public final class ResultSetUtil {

  /** Utility constructor. */
  private ResultSetUtil() {
    // Do nothing.
  }

  /**
   * Returns the Integer value.
   *
   * @param resultSet The result set.
   * @param columnLabel The column to retrieve.
   * @return The integer value, or {@code null}.
   * @throws SQLException in case of an error.
   */
  public static Integer getInteger(ResultSet resultSet, String columnLabel) throws SQLException {
    return getValueOrNull(resultSet, resultSet.getInt(columnLabel));
  }

  /**
   * Returns the Long value.
   *
   * @param resultSet The result set.
   * @param columnLabel The column to retrieve.
   * @return The integer value, or {@code null}.
   * @throws SQLException in case of an error.
   */
  public static Long getLong(ResultSet resultSet, String columnLabel) throws SQLException {
    return getValueOrNull(resultSet, resultSet.getLong(columnLabel));
  }
  /**
   * Returns the Date value.
   *
   * @param resultSet The result set.
   * @param columnLabel The column to retrieve.
   * @return The integer value, or {@code null}.
   * @throws SQLException in case of an error.
   */
  public static Date getDate(ResultSet resultSet, String columnLabel) throws SQLException {
    java.sql.Date sqlDate = getValueOrNull(resultSet, resultSet.getDate(columnLabel));
    return convert(sqlDate);
  }

  private static Date convert(java.sql.Date sqlDate) {
    if (sqlDate == null) {
      return null;
    }
    return new Date(sqlDate.getTime());
  }

  private static <T> T getValueOrNull(ResultSet resultSet, T value) throws SQLException {
    return resultSet.wasNull() ? null : value;
  }
}
