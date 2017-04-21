/*
 * Copyright 2015-2017 the original author or authors.
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

package org.hawaiiframework.web.input;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Abstract {@link InputConverter} implementation.
 *
 * @param <S> the type of the input object
 * @param <T> the type of the domain object
 * @author Wouter Eerdekens
 * @author Marcel Overdijk
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public abstract class AbstractInputConverter<S, T> implements InputConverter<S, T> {

    /**
     * This method does the actual conversion.
     *
     * @param input the input, must not be {@literal null}.
     * @return the domain object.
     */
    protected abstract T doConvert(S input);

    /**
     * {@inheritDoc}
     */
    @Override
    public T convert(final S input) {
        if (input == null) {
            return null;
        }
        return doConvert(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> convert(final Iterable<? extends S> objects) {
        requireNonNull(objects, "'objects' must not be null");
        final List<T> result = new ArrayList<T>();
        for (final S object : objects) {
            result.add(convert(object));
        }
        return result;
    }
}
