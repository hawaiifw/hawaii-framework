package org.hawaiiframework.logging.util;

import static java.util.Objects.requireNonNull;

/**
 * A string visitor that builds the same string, but then with masked passwords.
 */
public class MaskedPasswordBuilder {

    /**
     * The field to search for.
     */
    private static final String PASSWORD = "password";

    /**
     * The length of the search field.
     */
    private static final int PASSWORD_LENGTH = PASSWORD.length();

    /**
     * Constant for the masked password.
     */
    private static final String MASKED_PASSWORD = "***";

    /**
     * The string that contains the string {@link MaskedPasswordBuilder#PASSWORD}.
     */
    private final String stringToMask;

    /**
     * The index of the current character.
     */
    private int currentIndex;

    /**
     * The saved index, set by {@link MaskedPasswordBuilder#mark()} method.
     */
    private int savedCurrentIndex;

    /**
     * The index of the last added character.
     */
    private int lastAddedIndex;

    /**
     * The string builder that contains the masked input string.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder result = new StringBuilder();

    /**
     * The constructor.
     */
    public MaskedPasswordBuilder(final String stringToMask) {
        this.stringToMask = requireNonNull(stringToMask);
    }

    /**
     * Returns {@code true} if the string to mask has more characters.
     */
    public boolean hasNext() {
        return currentIndex < stringToMask.length();
    }

    /**
     * Advances the cursor to the next character.
     */
    public void next() {
        currentIndex++;
    }

    /**
     * Returns {@code true} of the current character is one of the {@code choices}.
     */
    public boolean currentCharIsOneOf(final Character... choices) {
        for (final Character choice : choices) {
            if (currentCharIs(choice)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the current character is {@code character}.
     */
    public boolean currentCharIs(final Character character) {
        return character.equals(getCurrentChar());
    }

    /**
     * Returns {@code true} if the current character is a whitespace.
     */
    public boolean currentCharIsWhitespace() {
        return Character.isWhitespace(getCurrentChar());
    }

    private Character getCurrentChar() {
        return stringToMask.charAt(currentIndex);
    }

    /**
     * Saves the current index, to be used by {@link MaskedPasswordBuilder#reset}.
     */
    public void mark() {
        savedCurrentIndex = currentIndex;
    }

    /**
     * Resets the current character (or index) to the one set by {@link MaskedPasswordBuilder#mark()}.
     */
    public void reset() {
        currentIndex = savedCurrentIndex;
    }

    /**
     * Appends the password mask at the {@code index}.
     */
    public void maskPasswordAt(final Integer index) {
        result.append(stringToMask.substring(lastAddedIndex, index));
        result.append(MASKED_PASSWORD);
        lastAddedIndex = currentIndex;
    }

    /**
     * Returns the current index.
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Returns {@code true} if there is another password to be found in the string to mask.
     */
    public boolean findNextPassword() {
        final int index = stringToMask.indexOf(PASSWORD, currentIndex);
        if (index <= -1) {
            return false;
        }
        currentIndex = index + PASSWORD_LENGTH;
        return true;
    }

    /**
     * Builds the masked string.
     */
    public String build() {
        result.append(stringToMask.substring(lastAddedIndex));
        return result.toString();
    }
}
