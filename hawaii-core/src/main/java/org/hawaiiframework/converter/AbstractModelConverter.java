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

package org.hawaiiframework.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import static java.util.Objects.requireNonNull;

/**
 * Abstract {@link ModelConverter} implementation.
 *
 * @param <S> the type of the input object
 * @param <T> the type of the domain object
 * @author Wouter Eerdekens
 * @author Marcel Overdijk
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public abstract class AbstractModelConverter<S, T> implements ModelConverter<S, T> {

    /**
     * The domain type.
     */
    private final Class<T> targetType;

    /**
     * Constructs a {@link AbstractModelConverter}.
     *
     * @param targetType the target type
     */
    public AbstractModelConverter(final Class<T> targetType) {
        this.targetType = requireNonNull(targetType, "'targetType' must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T convert(final S source) {
        if (source == null) {
            return null;
        }
        final T target = instantiateTargetObject(source);
        convert(source, target);
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> convert(final Iterable<? extends S> objects) {
        requireNonNull(objects, "'objects' must not be null");
        final List<T> result = new ArrayList<>();
        for (final S object : objects) {
            result.add(convert(object));
        }
        return result;
    }

    /**
     * Instantiates the domain object.
     *
     * @param source the source
     * @return the target object
     */
    protected T instantiateTargetObject(final S source) {
        return BeanUtils.instantiateClass(targetType);
    }
}
