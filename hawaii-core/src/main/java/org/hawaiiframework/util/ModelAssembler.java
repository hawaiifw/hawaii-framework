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

package org.hawaiiframework.util;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Interface for components that convert an object into another object (for instance resources or input objects).
 * <p>
 * The object to convert can be any type, but typically this will be a domain object widely used by the application.
 *
 * @param <S> the type of the source object to convert.
 * @param <T> the type of the target object.
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public interface ModelAssembler<S, T> {

    /**
     * Converts the given object into a resource.
     * <p>
     * The default implementation will return {@literal null} for a {@literal null} input.
     *
     * @param source The source to convert.
     * @see #doAssemble(Object)
     */
    default T assemble(S source) {
        if (source == null) {
            return null;
        }

        return doAssemble(source);
    }

    /**
     * This method does the actual conversion.
     *
     * @param source must not be {@literal null}.
     * @return The converted target.
     */
    T doAssemble(S source);

    /**
     * Converts all given objects into resources.
     *
     * @param sources must not be {@literal null}.
     * @see #assemble(Object)
     */
    default List<T> assemble(Iterable<? extends S> sources) {
        requireNonNull(sources, "'sources' must not be null");
        List<T> result = new ArrayList<>();
        for (S source : sources) {
            result.add(assemble(source));
        }

        return result;
    }

}
