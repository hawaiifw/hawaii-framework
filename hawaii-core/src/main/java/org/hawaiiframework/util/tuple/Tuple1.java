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

package org.hawaiiframework.util.tuple;

import java.io.Serializable;
import java.util.Objects;

/**
 * A {@link Tuple} of 1 element.
 *
 * @param <T1> the type of the 1st element
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@SuppressWarnings({"checkstyle:ClassTypeParameterName", "PMD.GenericsNaming"})
public class Tuple1<T1> implements Tuple, Serializable {

    private static final long serialVersionUID = 1L;

    private T1 element1;

    /**
     * Constructs a new {@code Tuple} with the supplied elements.
     */
    public Tuple1(final T1 element1) {
        this.element1 = element1;
    }

    /**
     * Returns the 1st element of this tuple.
     *
     * @return the 1st element of this tuple
     */
    public T1 getElement1() {
        return element1;
    }

    /**
     * Sets the 1st element of this tuple.
     */
    public void setElement1(final T1 element1) {
        this.element1 = element1;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Tuple1<?> other = (Tuple1) o;
        return Objects.equals(this.element1, other.element1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element1);
    }
}
