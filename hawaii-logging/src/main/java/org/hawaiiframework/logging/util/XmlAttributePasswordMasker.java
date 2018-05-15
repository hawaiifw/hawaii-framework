package org.hawaiiframework.logging.util;

/**
 * A password masker for XML tags.
 */
public class XmlAttributePasswordMasker implements PasswordMasker {

    /**
     * Constant for start of XML tag.
     */
    @SuppressWarnings("PMD.ShortVariable")
    private static final Character LT = '<';

    /**
     * Constant for end of XML tag.
     */
    @SuppressWarnings("PMD.ShortVariable")
    private static final Character GT = '>';

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final MaskedPasswordBuilder builder) {
        if (builder.currentCharIs(GT)) {
            // Assumption: end of XML tag.
            final Integer indexOfStartPassword = builder.getCurrentIndex();
            if (readUntilEndOfXmlValue(builder)) {
                builder.maskPasswordAt(indexOfStartPassword + 1);
                readUntilEndOfXmlTag(builder);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the index of the LT ('<') that ends the XML value.
     * <p>
     * Will return {@code null} if there is no '<' found.
     */
    private boolean readUntilEndOfXmlValue(final MaskedPasswordBuilder builder) {
        while (builder.hasNext()) {
            builder.next();
            if (builder.currentCharIs(LT)) {
                return true;
            }
        }
        return false;
    }


    private void readUntilEndOfXmlTag(final MaskedPasswordBuilder builder) {
        while (builder.hasNext()) {
            builder.next();
            if (builder.currentCharIs(GT)) {
                break;
            }
        }
    }
}
