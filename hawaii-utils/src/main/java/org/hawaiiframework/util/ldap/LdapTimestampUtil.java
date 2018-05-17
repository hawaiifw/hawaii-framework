package org.hawaiiframework.util.ldap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LDAP utility for creating formatted timestamps.
 */
public final class LdapTimestampUtil {


    private static final String YYYY_MMDD_HHMMSS_Z = "yyyyMMddHHmmss'Z'";

    private LdapTimestampUtil() {
        // private constructor for utility class.
    }

    /**
     * Converts a LocalDateTime (expected to be in UTC) to a date time string for LDAP.
     */
    public static String getDateString(final LocalDateTime date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YYYY_MMDD_HHMMSS_Z);
        return date.format(dtf);
    }

    /**
     * Converts a LocalDateTime (expected to be in UTC) to a date time string for LDAP.
     */
    public static LocalDateTime getDate(final String date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YYYY_MMDD_HHMMSS_Z);
        return LocalDateTime.parse(date, dtf);
    }
}
