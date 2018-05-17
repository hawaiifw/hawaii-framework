package org.hawaiiframework.util.predicate;

import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Predicate to validate a String representing an email address.
 */
public class EmailAddressPredicate implements Predicate<String> {

    /**
     * Regex to validate emailaddress (next to javax.mail validation).
     */
    private static final Pattern PATTERN = Pattern.compile(".+@.+\\..{2,63}");


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public boolean test(final String emailAddress) {
        if (StringUtils.isEmpty(emailAddress)) {
            return false;
        }

        boolean result;
        try {
            final InternetAddress emailAddr = new InternetAddress(emailAddress);
            emailAddr.validate();
            result = PATTERN.matcher(emailAddress).matches();
        } catch (final AddressException ex) {
            result = false;
        }
        return result;
    }
}
