package org.hawaiiframework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility to obfuscate email addresses.
 */
public final class AddressUtil {

    /**
     * Private constructor.
     */
    private AddressUtil() {

    }

    /**
     * Obfuscate an address.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String obfuscate(final String address) {
        if (address != null) {
            final String obfuscated = StringUtils.repeat('*', address.length() - 4);
            return StringUtils.overlay(address, obfuscated, 2, address.length() - 2);
        } else {
            return StringUtils.EMPTY;
        }
    }
}
