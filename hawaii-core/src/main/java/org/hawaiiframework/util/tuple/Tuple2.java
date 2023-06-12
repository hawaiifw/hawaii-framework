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

import java.util.Objects;

/**
 * A {@link Tuple} of 2 elements.
 *
 * @param <T1> the type of the 1st element
 * @param <T2> the type of the 2nd element
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@SuppressWarnings({"checkstyle:ClassTypeParameterName", "PMD.GenericsNaming"})
public class Tuple2<T1, T2> extends Tuple1<T1> {

    private static final long serialVersionUID = 1L;

    private T2 element2;

    /**
     * Constructs a new {@code Tuple} with the supplied elements.
     */
    public Tuple2(final T1 element1, final T2 element2) {
        super(element1);
        this.element2 = element2;
    }

    /**
     * Returns the 2nd element of this tuple.
     *
     * @return the 2nd element of this tuple
     */
    public T2 getElement2() {
        return element2;
    }

    /**
     * Sets the 2nd element of this tuple.
     */
    public void setElement2(final T2 element2) {
        this.element2 = element2;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Tuple2<?, ?> other = (Tuple2) o;
        return Objects.equals(this.element2, other.element2);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(element2);
    }
}
