package org.hawaiiframework.util.predicate;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.util.MsisdnUtil;

import java.util.function.Predicate;

/**
 * Predicate to validate a MSISDN.
 * If the predicate is true, the given MSISDN is of the form 31[6|97]\d{8} (e.g. either 316 or 3197, followed by 8 numbers) after
 * standardising it with {@link MsisdnUtil#toInternationalFormat(String)}.
 */
public class MsisdnPredicate implements Predicate<String> {


    /**
     * Prefix for standard MSISDN.
     */
    public static final String STANDARD_MSISDN_PREFIX = "316";
    /**
     * Prefix for data only number.
     */
    public static final String DATA_ONLY_MSISDN_PREFIX = "3197";
    /**
     * Minimum length of a data only (3197) number.
     */
    private static final int DATA_ONLY_MSISDN_MIN_LENGTH = 11;
    /**
     * Maximum length of a data only (3197) number.
     */
    private static final int DATA_ONLY_MSISDN_MAX_LENGTH = 16;
    /**
     * Default length of standard (316) msisdn.
     */
    private static final int STANDARD_MSISDN_LENGTH = 11;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public boolean test(final String msisdn) {
        boolean result = true;
        final String internationalizedMsisdn = MsisdnUtil.toInternationalFormat(msisdn);
        if (StringUtils.isBlank(internationalizedMsisdn) || !StringUtils.isNumeric(internationalizedMsisdn)) {
            result = false;
        } else if (!(internationalizedMsisdn.startsWith(STANDARD_MSISDN_PREFIX) || internationalizedMsisdn
                .startsWith(DATA_ONLY_MSISDN_PREFIX))) {
            result = false;
        } else if (internationalizedMsisdn.startsWith(STANDARD_MSISDN_PREFIX)
                && internationalizedMsisdn.length() != STANDARD_MSISDN_LENGTH) {
            result = false;
        } else if (internationalizedMsisdn.startsWith(DATA_ONLY_MSISDN_PREFIX)
                && (internationalizedMsisdn.length() < DATA_ONLY_MSISDN_MIN_LENGTH
                || internationalizedMsisdn.length() > DATA_ONLY_MSISDN_MAX_LENGTH)) {
            result = false;
        }

        return result;
    }
}
