package org.hawaiiframework.logging.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class the mask passwords in a string, so log files will not contain plain text (or encrypted) passwords.
 */
public class PasswordMaskerUtil {

    /**
     * The list of password maskers.
     */
    private static final List<PasswordMasker> PASSWORD_MASKERS = new ArrayList<>();

    static {
        PASSWORD_MASKERS.add(new JsonPasswordMasker());
        PASSWORD_MASKERS.add(new XmlAttributePasswordMasker());
        PASSWORD_MASKERS.add(new UriQueryStringPasswordMasker());
    }

    /**
     * Mask the password with {@code ***} in the {@code input}.
     */
    public String maskPasswordsIn(final String input) {
        final MaskedPasswordBuilder builder = new MaskedPasswordBuilder(input);
        if (!builder.findNextPassword()) {
            return input;
        }
        builder.reset();
        return maskPassword(builder);
    }

    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "PMD"})
    private String maskPassword(final MaskedPasswordBuilder builder) {

        while (builder.findNextPassword()) {
            while (builder.hasNext()) {
                boolean fieldMasked = false;
                for (PasswordMasker masker : PASSWORD_MASKERS) {
                    fieldMasked = masker.matches(builder);
                }
                if (fieldMasked) {
                    break;
                }
                if (builder.currentCharIsWhitespace()) {
                    // We've found a whitespace, this means the input is not in one of the expected formats,
                    // break the loop and search again.
                    break;
                }
                builder.next();
            }
        }

        return builder.build();
    }

}
