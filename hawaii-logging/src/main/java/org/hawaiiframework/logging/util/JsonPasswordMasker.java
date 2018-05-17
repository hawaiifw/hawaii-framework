package org.hawaiiframework.logging.util;

/**
 * Masks passwords in a json key value.
 */
public class JsonPasswordMasker implements PasswordMasker {

    /**
     * Constant for JSON escape character.
     */
    private static final Character JSON_ESCAPE = '\\';

    /**
     * Constant for JSON field-value separation character.
     */
    private static final Character COLON = ':';

    /**
     * Constant for JSON field delimiter.
     */
    private static final Character QUOTE = '"';

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final MaskedPasswordBuilder builder) {
        return builder.currentCharIs(QUOTE) && matchesJsonValue(builder);
    }

    private boolean matchesJsonValue(final MaskedPasswordBuilder builder) {
        // Assumption: start of json.
        builder.mark();
        builder.next();
        if (readUntilStartOfJsonValue(builder)) {
            final Integer indexOfStartPassword = builder.getCurrentIndex();
            if (readUntilEndOfJsonValue(builder)) {
                builder.maskPasswordAt(indexOfStartPassword + 1);
                return true;
            }
        }

        builder.reset();
        return false;
    }

    /**
     * Returns the index of the QUOTE that starts the JSON value.
     * <p>
     * Will return {@code null} if there is no quote found.
     */
    private boolean readUntilStartOfJsonValue(final MaskedPasswordBuilder builder) {
        readWhiteSpaces(builder);
        if (builder.currentCharIs(COLON)) {
            builder.next();
            readWhiteSpaces(builder);

            return builder.currentCharIs(QUOTE);
        }
        return false;
    }

    /**
     * Returns the index of the QUOTE that ends the JSON value.
     * <p>
     * Will return {@code null} if there is no quote found.
     */
    private boolean readUntilEndOfJsonValue(final MaskedPasswordBuilder builder) {
        boolean escape = false;
        while (builder.hasNext()) {
            builder.next();
            if (!escape && builder.currentCharIs(QUOTE)) {
                return true;
            }
            escape = builder.currentCharIs(JSON_ESCAPE);
        }
        return false;
    }

    private void readWhiteSpaces(final MaskedPasswordBuilder builder) {
        while (builder.currentCharIsWhitespace() && builder.hasNext()) {
            builder.next();
        }
    }
}
