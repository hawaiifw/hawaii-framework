package org.hawaiiframework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility to convert MSISDNs into different formats.
 */
public final class MsisdnUtil {

    /**
     * The international country code for the Netherlands.
     */
    private static final String COUNTRY_CODE_FOR_NL = "31";

    /**
     * The constructor.
     */
    private MsisdnUtil() {
        // private utility constructor.
    }

    /**
     * Normalizes a postcode from input.
     * <p>
     * <pre>
     * MsisdnUtil.toInternationalFormat(null)              = null
     * MsisdnUtil.toInternationalFormat("")                = ""
     * MsisdnUtil.toInternationalFormat("  06-12345678")   = "31612345678"
     * MsisdnUtil.toInternationalFormat("06 123 456 78")   = "31612345678"
     * MsisdnUtil.toInternationalFormat("06.12345678")     = "31612345678"
     * MsisdnUtil.toInternationalFormat("31 6 12345678")   = "31612345678"
     * MsisdnUtil.toInternationalFormat("+31 6 12345678")  = "31612345678"
     * </pre>
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String toInternationalFormat(final String msisdn) {
        if (StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }

        String internationalFormat = normalize(msisdn);
        if (internationalFormat.startsWith("0031")) {
            internationalFormat = internationalFormat.substring(2);
        }
        if (internationalFormat.startsWith("06") || internationalFormat.startsWith("097")) {
            internationalFormat = COUNTRY_CODE_FOR_NL + internationalFormat.substring(1);
        }
        return internationalFormat;
    }

    /**
     * Normalizes a postcode from input.
     * <p>
     * <pre>
     * MsisdnUtil.toDutchFormat(null)              = null
     * MsisdnUtil.toDutchFormat("")                = ""
     * MsisdnUtil.toDutchFormat("  06-12345678")   = "0612345678"
     * MsisdnUtil.toDutchFormat("06 123 456 78")   = "0612345678"
     * MsisdnUtil.toDutchFormat("06.12345678")     = "0612345678"
     * MsisdnUtil.toDutchFormat("31 6 12345678")   = "0612345678"
     * MsisdnUtil.toDutchFormat("+31 6 12345678")  = "0612345678"
     * </pre>
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String toDutchFormat(final String msisdn) {
        if (StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }

        String dutchFormat = normalize(msisdn);
        if (dutchFormat.startsWith(COUNTRY_CODE_FOR_NL)) {
            dutchFormat = "0" + dutchFormat.substring(COUNTRY_CODE_FOR_NL.length());
        }
        return dutchFormat;
    }

    /**
     * The same rules apply for {@link #toDutchFormat(String)}.
     * However, after '06', the first 6 characters are replaced by a '*'.
     */
    public static String obfuscateDutchFormat(final String msisdn) {
        if (StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }

        final String obfuscateDutchFormat = toDutchFormat(msisdn);
        return StringUtils.overlay(obfuscateDutchFormat, "******", 2, 8);

    }
    private static String normalize(final String input) {
        return StringUtils.replaceChars(input, " -+.", null);
    }


}
