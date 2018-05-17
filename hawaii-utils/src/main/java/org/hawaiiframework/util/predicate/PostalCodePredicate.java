package org.hawaiiframework.util.predicate;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Predicate to validate a String representing a Dutch postal code.
 */
public class PostalCodePredicate implements Predicate<String> {


    /**
     * Regex to match Dutch postal code.
     * 4 digits, optional space, 2 letters.
     */
    private static final Pattern PATTERN = Pattern.compile("\\s?\\d{4} ?[a-zA-Z]{2}\\s?");

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public boolean test(final String postalCode) {
        if (StringUtils.isEmpty(postalCode)) {
            return false;
        }
        return PATTERN.matcher(postalCode).matches();
    }

}
