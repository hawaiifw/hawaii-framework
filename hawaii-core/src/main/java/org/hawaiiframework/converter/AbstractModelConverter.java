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

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.converter.DefaultNullListConversionStrategies.returnEmptyList;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

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

  /** The domain type. */
  private final Class<T> targetType;

  /** The conversion strategy for null lists values. */
  private final NullListConversionStrategy<T> nullListConversionStrategy;

  /**
   * Constructs a {@link AbstractModelConverter}.
   *
   * @param targetType the target type
   */
  public AbstractModelConverter(Class<T> targetType) {
    this(targetType, returnEmptyList(targetType));
  }

  /**
   * Constructs a {@link AbstractModelConverter}.
   *
   * <p>The null list conversion strategy to be used can be supplied, or one of the supplied default
   * methods can be used. See:
   *
   * <ul>
   *   <li>{@link DefaultNullListConversionStrategies#raiseError(Class)}
   *   <li>{@link DefaultNullListConversionStrategies#returnNull(Class)}
   *   <li>{@link DefaultNullListConversionStrategies#returnEmptyList(Class)}
   * </ul>
   *
   * @param targetType the target type.
   * @param nullListConversionStrategy the strategy how to handle null lists.
   */
  public AbstractModelConverter(
      Class<T> targetType, NullListConversionStrategy<T> nullListConversionStrategy) {
    this.targetType = requireNonNull(targetType, "'targetType' must not be null");
    this.nullListConversionStrategy =
        requireNonNull(nullListConversionStrategy, "'nullListConversionStrategy' must not be null");
  }

  @Override
  public T convert(S source) {
    if (source == null) {
      return null;
    }
    T target = instantiateTargetObject(source);
    convert(source, target);
    return target;
  }

  @Override
  public List<T> convert(Iterable<? extends S> objects) {
    if (objects == null) {
      return nullListConversionStrategy.apply();
    }
    List<T> result = new ArrayList<>();
    for (S object : objects) {
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
  protected T instantiateTargetObject(S source) {
    return BeanUtils.instantiateClass(targetType);
  }
}
