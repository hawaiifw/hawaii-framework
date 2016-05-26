/*
 * Copyright 2015-2016 the original author or authors.
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
 * A {@link Tuple} of 8 elements.
 *
 * @param <T1> the type of the 1st element
 * @param <T2> the type of the 2nd element
 * @param <T3> the type of the 3rd element
 * @param <T4> the type of the 4th element
 * @param <T5> the type of the 5th element
 * @param <T6> the type of the 6th element
 * @param <T7> the type of the 7th element
 * @param <T8> the type of the 8th element
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple7<T1, T2, T3, T4, T5, T6, T7> {

    private static final long serialVersionUID = 1L;

    private T8 element8;

    /**
     * Constructs a new {@code Tuple} with the supplied elements.
     */
    public Tuple8(T1 element1, T2 element2, T3 element3, T4 element4, T5 element5, T6 element6,
            T7 element7, T8 element8) {
        super(element1, element2, element3, element4, element5, element6, element7);
        this.element8 = element8;
    }

    /**
     * Returns the 8th element of this tuple.
     *
     * @return the 8th element of this tuple
     */
    public T8 getElement8() {
        return element8;
    }

    /**
     * Sets the 8th element of this tuple.
     */
    public void setElement8(T8 element8) {
        this.element8 = element8;
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Tuple8 other = (Tuple8) o;
        return Objects.equals(this.element8, other.element8);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(element8);
    }
}
