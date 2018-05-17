package org.hawaiiframework.logging.util;

/**
 * LogUtil to indent data.
 */
public final class LogUtil {

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    private LogUtil() {
        // Utility constructor.
    }

    /**
     * Indent the @code{value} with the given @code{indent}.
     */
    public static String indent(final String value, final String indent) {
        return value.replace(NEW_LINE, String.format("%n%s", indent));
    }
}
