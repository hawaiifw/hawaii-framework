package org.hawaiiframework.logging.util;

/**
 * Class that tries to mask a password in a string.
 *
 * The implementation tries to mask a field for a specific format, for instance JSON or XML.
 */
public interface PasswordMasker {

    /**
     * Did the masked find a match and could it be masked?
     */
    boolean matches(MaskedPasswordBuilder builder);
}
