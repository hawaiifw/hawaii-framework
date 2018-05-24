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

import java.util.List;

/**
 * Converter interface for converting between two types.
 *
 * @param <S> the source type
 * @param <T> the target type
 * @author Wouter Eerdekens
 * @author Marcel Overdijk
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public interface ModelConverter<S, T> {

    /**
     * Converts the given source object into a new instance of target object.
     *
     * @param source the source object
     * @return the target object
     */
    T convert(S source);

    /**
     * Converts the given source object into the target object.
     *
     * @param source the source object
     * @param target the target object
     */
    void convert(S source, T target);

    /**
     * Converts all given source objects into target objects.
     *
     * @param objects the object, must not be {@literal null}.
     * @return the target objects
     */
    List<T> convert(Iterable<? extends S> objects);
}
