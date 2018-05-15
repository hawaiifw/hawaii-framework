package org.hawaiiframework.logging.util;

/**
 * Class that tries to mask a POST body or URI Query for password fields.
 */
public class UriQueryStringPasswordMasker implements PasswordMasker {

    /**
     * Constant for URI query string field-value separation character.
     */
    private static final Character EQUALS = '=';

    /**
     * Constant for URI query string field delimiter.
     */
    private static final Character AMPERSAND = '&';

    /**
     * Constant for JSON field delimiter.
     */
    private static final Character QUOTE = '"';

    /**
     * Constant for start of XML tag.
     */
    @SuppressWarnings("PMD.ShortVariable")
    private static final Character LT = '<';

    /**
     * Newline.
     */
    private static final Character NEWLINE = '\n';

    /**
     * Carriage return.
     */
    private static final Character CARRIAGE_RETURN = '\r';

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final MaskedPasswordBuilder builder) {
        if (builder.currentCharIs(EQUALS)) {
            // Assumption: start of URI query string (post body).
            final Integer indexOfStartPassword = builder.getCurrentIndex();
            readUntilEndOfQueryParameterValue(builder);

            builder.maskPasswordAt(indexOfStartPassword + 1);
            return true;
        }

        return false;
    }

    /**
     * Returns the index of first character that is not part of the current query parameter.
     * <p>
     * That is, it returns the index of the first '&' following the {@code startIndex},
     * or, it returns {@code input.length()}.
     */
    private void readUntilEndOfQueryParameterValue(final MaskedPasswordBuilder builder) {
        while (builder.hasNext()) {

            if (builder.currentCharIsOneOf(AMPERSAND, QUOTE, LT, NEWLINE, CARRIAGE_RETURN)) {
                break;
            }
            builder.next();
        }
    }

}
