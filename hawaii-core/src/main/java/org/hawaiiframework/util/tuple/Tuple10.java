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
 * A {@link Tuple} of 10 elements.
 *
 * @param <T1>  the type of the 1st element
 * @param <T2>  the type of the 2nd element
 * @param <T3>  the type of the 3rd element
 * @param <T4>  the type of the 4th element
 * @param <T5>  the type of the 5th element
 * @param <T6>  the type of the 6th element
 * @param <T7>  the type of the 7th element
 * @param <T8>  the type of the 8th element
 * @param <T9>  the type of the 9th element
 * @param <T10> the type of the 10th element
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@SuppressWarnings({"checkstyle:ClassTypeParameterName", "checkstyle:ParameterNumber", "PMD.GenericsNaming", "PMD.ExcessiveParameterList"})
public class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
        extends Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    private static final long serialVersionUID = 1L;

    private T10 element10;

    /**
     * Constructs a new {@code Tuple} with the supplied elements.
     */
    public Tuple10(final T1 element1, final T2 element2, final T3 element3, final T4 element4, final T5 element5, final T6 element6,
            final T7 element7, final T8 element8, final T9 element9, final T10 element10) {
        super(element1, element2, element3, element4, element5, element6, element7, element8, element9);
        this.element10 = element10;
    }

    /**
     * Returns the 10th element of this tuple.
     *
     * @return the 10th element of this tuple
     */
    public T10 getElement10() {
        return element10;
    }

    /**
     * Sets the 10th element of this tuple.
     */
    public void setElement10(final T10 element10) {
        this.element10 = element10;
    }

    @Override
    public int size() {
        return 10;
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
        final Tuple10 other = (Tuple10) o;
        return Objects.equals(this.element10, other.element10);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(element10);
    }
}
