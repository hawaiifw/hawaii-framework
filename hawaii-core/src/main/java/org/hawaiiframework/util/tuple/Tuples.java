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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, elementither express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.util.tuple;

/**
 * This class consists exclusively of static methods that operate on or return {@link Tuple}s.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class Tuples {

    /**
     * Creates a {@code Tuple} of 1 element.
     */
    public static <T1> Tuple1<T1> of(T1 element1) {
        return new Tuple1<>(element1);
    }

    /**
     * Creates a {@code Tuple} of 2 elements.
     */
    public static <T1, T2> Tuple2<T1, T2> of(T1 element1, T2 element2) {
        return new Tuple2<>(element1, element2);
    }

    /**
     * Creates a {@code Tuple} of 3 elements.
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 element1, T2 element2, T3 element3) {
        return new Tuple3<>(element1, element2, element3);
    }

    /**
     * Creates a {@code Tuple} of 4 elements.
     */
    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 element1, T2 element2, T3 element3,
            T4 element4) {
        return new Tuple4<>(element1, element2, element3, element4);
    }

    /**
     * Creates a {@code Tuple} of 5 elements.
     */
    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 element1, T2 element2,
            T3 element3, T4 element4, T5 element5) {
        return new Tuple5<>(element1, element2, element3, element4, element5);
    }

    /**
     * Creates a {@code Tuple} of 6 elements.
     */
    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 element1,
            T2 element2, T3 element3, T4 element4, T5 element5, T6 element6) {
        return new Tuple6<>(element1, element2, element3, element4, element5, element6);
    }

    /**
     * Creates a {@code Tuple} of 7 elements.
     */
    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 element1,
            T2 element2, T3 element3, T4 element4, T5 element5, T6 element6, T7 element7) {
        return new Tuple7<>(element1, element2, element3, element4, element5, element6, element7);
    }

    /**
     * Creates a {@code Tuple} of 8 elements.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(
            T1 element1, T2 element2, T3 element3, T4 element4, T5 element5, T6 element6,
            T7 element7, T8 element8) {
        return new Tuple8<>(element1, element2, element3, element4, element5, element6, element7,
                element8);
    }

    /**
     * Creates a {@code Tuple} of 9 elements.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
            T1 element1, T2 element2, T3 element3, T4 element4, T5 element5, T6 element6,
            T7 element7, T8 element8, T9 element9) {
        return new Tuple9<>(element1, element2, element3, element4, element5, element6, element7,
                element8, element9);
    }

    /**
     * Creates a {@code Tuple} of 10 elements.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> of(
            T1 element1, T2 element2, T3 element3, T4 element4, T5 element5, T6 element6,
            T7 element7, T8 element8, T9 element9, T10 element10) {
        return new Tuple10<>(element1, element2, element3, element4, element5, element6, element7,
                element8, element9, element10);
    }
}
