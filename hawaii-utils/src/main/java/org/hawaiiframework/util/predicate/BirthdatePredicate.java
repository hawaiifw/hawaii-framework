package org.hawaiiframework.util.predicate;

import org.hawaiiframework.util.BirthdateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Predicate to validate a String representing a birthdate.
 */
public class BirthdatePredicate implements Predicate<String> {

    /**
     * The dateformat used by birthdatePredicate.
     */
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("nl", "NL"));

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(final String birthDate) {
        boolean valid;
        if (birthDate != null) {
            final String normalized = BirthdateUtil.normalize(birthDate);
            try {
                final Date parsedDate = dateFormat.parse(normalized);
                valid = stringCorrectlyParsed(normalized, parsedDate) && !isFutureDate(parsedDate);
            } catch (ParseException e) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    private boolean isFutureDate(final Date date) {
        final Date currentDate = new Date();
        return date.after(currentDate);
    }

    private boolean stringCorrectlyParsed(final String stringDate, final Date parsedDate) {
        return stringDate.equals(dateFormat.format(parsedDate));
    }

}
