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

package org.hawaiiframework.converter;

import java.util.ArrayList;

/**
 * A few default implementations for the {@link NullListConversionStrategy}.
 */
public final class DefaultNullListConversionStrategies {

    /**
     * The constructor.
     */
    private DefaultNullListConversionStrategies() {
        // Utility constructor.
    }

    /**
     * Raise an {@link IllegalArgumentException} that the list is null.
     *
     * @param ignored The class, ignored, used for type casting.
     * @param <T>     The element type of the list to be returned.
     * @throws IllegalArgumentException always
     */
    public static <T> NullListConversionStrategy<T> raiseError(final Class<T> ignored) {
        return () -> {
            throw new IllegalArgumentException("'objects' cannot be null");
        };
    }

    /**
     * Return null.
     *
     * @param ignored The class, ignored, used for type casting.
     * @param <T>     The element type of the list to be returned.
     * @return {@code null}, always.
     */
    public static <T> NullListConversionStrategy<T> returnNull(final Class<T> ignored) {
        return () -> null;
    }

    /**
     * Return an empty list.
     *
     * @param ignored The class, ignored, used for type casting.
     * @param <T>     The element type of the list to be returned.
     * @return An empty list.
     */
    public static <T> NullListConversionStrategy<T> returnEmptyList(final Class<T> ignored) {
        return ArrayList::new;
    }
}
