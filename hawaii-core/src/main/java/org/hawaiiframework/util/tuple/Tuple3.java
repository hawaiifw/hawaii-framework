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
 * A {@link Tuple} of 3 elements.
 *
 * @param <T1> the type of the 1st element
 * @param <T2> the type of the 2nd element
 * @param <T3> the type of the 3rd element
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@SuppressWarnings({"checkstyle:ClassTypeParameterName", "PMD.GenericsNaming"})
public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

    private static final long serialVersionUID = 1L;

    private T3 element3;

    /**
     * Constructs a new {@code Tuple} with the supplied elements.
     */
    public Tuple3(final T1 element1, final T2 element2, final T3 element3) {
        super(element1, element2);
        this.element3 = element3;
    }

    /**
     * Returns the 3rd element of this tuple.
     *
     * @return the 3rd element of this tuple
     */
    public T3 getElement3() {
        return element3;
    }

    /**
     * Sets the 3rd element of this tuple.
     */
    public void setElement3(final T3 element3) {
        this.element3 = element3;
    }

    @Override
    public int size() {
        return 3;
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
        final Tuple3 other = (Tuple3) o;
        return Objects.equals(this.element3, other.element3);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(element3);
    }
}
