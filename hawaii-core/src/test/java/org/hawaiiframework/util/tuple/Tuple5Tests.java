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

package org.hawaiiframework.util.tuple;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Tuple5}.
 *
 * @author Marcel Overdijk
 */
public class Tuple5Tests extends AbstractTupleTests {

    private Tuple5<Object1, Object2, Object3, Object4, Object5> tuple =
            Tuples.of(object1, object2, object3, object4, object5);

    @Test
    public void testSizeIs5() {
        assertThat(tuple.size(), is(5));
    }

    @Test
    public void testSetElement5() {
        tuple.setElement5(other5);
        assertThat(tuple.getElement5(), is(sameInstance(other5)));
    }

    @Test
    public void testGetElement1() {
        assertThat(tuple.getElement1(), is(instanceOf(Object1.class)));
        assertThat(tuple.getElement1(), is(sameInstance(object1)));
    }

    @Test
    public void testGetElement2() {
        assertThat(tuple.getElement2(), is(instanceOf(Object2.class)));
        assertThat(tuple.getElement2(), is(sameInstance(object2)));
    }

    @Test
    public void testGetElement3() {
        assertThat(tuple.getElement3(), is(instanceOf(Object3.class)));
        assertThat(tuple.getElement3(), is(sameInstance(object3)));
    }

    @Test
    public void testGetElement4() {
        assertThat(tuple.getElement4(), is(instanceOf(Object4.class)));
        assertThat(tuple.getElement4(), is(sameInstance(object4)));
    }

    @Test
    public void testGetElement5() {
        assertThat(tuple.getElement5(), is(instanceOf(Object5.class)));
        assertThat(tuple.getElement5(), is(sameInstance(object5)));
    }
}
