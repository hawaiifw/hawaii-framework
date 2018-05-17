package org.hawaiiframework.logging.logback;

/**
 * Constants for new lines and indentation used by the {@link KibanaLogEventEncoder}.
 */
public final class CharacterConstants {

    /**
     * The system's line separator.
     */
    public static final String NEW_LINE = System.lineSeparator();

    /**
     * The indentation for multi line log events.
     */
    public static final String INDENT = "\t";

    /**
     * The constructor.
     */
    private CharacterConstants() {
        // Utility constructor.
    }
}
