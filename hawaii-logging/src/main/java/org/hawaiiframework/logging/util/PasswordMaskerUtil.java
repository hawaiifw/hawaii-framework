/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.logging.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class the mask passwords in a string, so log files will not contain plain text (or encrypted) passwords.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
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
     * The list of fields to mask.
     */
    private final Set<String> fieldsToMask = new HashSet<>();

    /**
     * The constructor.
     *
     * @param fieldsToMask The list of fields to mask.
     */
    public PasswordMaskerUtil(final Collection<String> fieldsToMask) {
        if (fieldsToMask != null && !fieldsToMask.isEmpty()) {
            this.fieldsToMask.addAll(fieldsToMask);
        } else {
            this.fieldsToMask.add("password");
        }
    }

    /**
     * Mask the password with {@code ***} in the {@code input}.
     *
     * @param input the input to mask passwords in.
     * @return The masked result.
     */
    public String maskPasswordsIn(final String input) {
        String masked = input;
        for (final String fieldToMask : fieldsToMask) {
            masked = maskPasswords(masked, fieldToMask);
        }
        return masked;
    }

    private String maskPasswords(final String input, final String pattern) {
        final MaskedPasswordBuilder builder = new MaskedPasswordBuilder(input, pattern);
        if (!builder.findNextPassword()) {
            return input;
        }
        builder.reset();
        return maskPasswords(builder);
    }

    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "PMD"})
    private String maskPasswords(final MaskedPasswordBuilder builder) {

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
