package org.hawaiiframework.util;


import org.apache.commons.lang3.StringUtils;

/**
 * Utility to check postcodes for equality and to format a postcode.
 */
public final class PostcodeUtil {

    /**
     * The constructor.
     */
    private PostcodeUtil() {
        // private constructor for utility classes.
    }

    /**
     * Check two postcodes for equality.
     */
    public static boolean areEqual(final String onePostcode, final String otherPostcode) {
        return StringUtils.equals(normalize(onePostcode), normalize(otherPostcode));
    }

    /**
     * Normalizes a postcode from input.
     *
     * <pre>
     * PostcodeUtil.normalize(null)        = null
     * PostcodeUtil.normalize("")          = ""
     * PostcodeUtil.normalize("8123 AA")   = "8123AA"
     * PostcodeUtil.normalize("8123 aa")   = "8123AA"
     * PostcodeUtil.normalize(" 8123 aa ") = "8123AA"
     * PostcodeUtil.normalize(" 8123 AA ") = "8123AA"
     * </pre>
     */
    public static String normalize(final String postcode) {
        return StringUtils.upperCase(StringUtils.remove(postcode, ' '));
    }
}
