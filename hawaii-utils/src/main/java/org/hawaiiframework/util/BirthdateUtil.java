package org.hawaiiframework.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Util for birthdate operations.
 */
public final class BirthdateUtil {

    /**
     * Formatter for date input.
     */
    public static final DateTimeFormatter DATE_OF_BIRTH_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private BirthdateUtil() {
    }

    /**
     * Normalizes a given birthdate String. Removes all whitespace characters and replaces "." and "/" with "-".
     * @param birthdate the birthdate to normalize.
     * @return the normalized birthdate String.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String normalize(final String birthdate) {
        return birthdate
                .replaceAll("\\s+", "")
                .replaceAll("[\\.\\/]", "-");
    }

    /**
     * Converts the given birthdate String to a LocalDate.
     * @param birthdate the birthdate String.
     * @return the LocalDate.
     */
    public static LocalDate convertToLocalDate(final String birthdate) {
        return LocalDate.parse(birthdate, DATE_OF_BIRTH_FORMATTER);
    }
}
