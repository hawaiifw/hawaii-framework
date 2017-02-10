package org.hawaiiframework.validation.field;

import org.hamcrest.Matcher;
import org.hawaiiframework.validation.ValidationResult;

import static org.hamcrest.Matchers.nullValue;

public class FieldRejection<T> {

    private final ValidationResult validationResult;
    private final String field;
    private final T actual;

    private boolean rejected = false;

    public FieldRejection(final ValidationResult validationResult, final String field, final T actual) {
        this.validationResult = validationResult;
        this.field = field;
        this.actual = actual;
    }

    public FieldRejection<T> when(final Matcher<T> matcher) {
        return when(matcher, "invalid");
    }

    public FieldRejection<T> when(final Matcher<T> matcher, final String code) {
        if (!rejected) {
            if (matcher.matches(actual)) {
                rejected = true;
                validationResult.rejectValue(field, code);
            }
        }
        return this;
    }

    public FieldRejection<T> ifNull() {
        return ifNull("required");
    }

    public FieldRejection<T> ifNull(final String code) {
        return when(nullValue(), code);
    }
}
