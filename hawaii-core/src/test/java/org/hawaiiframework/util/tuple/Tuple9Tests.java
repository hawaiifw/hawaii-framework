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

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Tuple9}.
 *
 * @author Marcel Overdijk
 */
public class Tuple9Tests extends AbstractTupleTests {

    private Tuple9<Object1, Object2, Object3, Object4, Object5, Object6, Object7, Object8, Object9>
            tuple =
            Tuples.of(object1, object2, object3, object4, object5, object6, object7, object8,
                    object9);

    @Test
    public void testSizeIs9() {
        assertThat(tuple.size(), is(9));
    }

    @Test
    public void testSetElement9() {
        tuple.setElement9(other9);
        assertThat(tuple.getElement9(), is(sameInstance(other9)));
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

    @Test
    public void testGetElement6() {
        assertThat(tuple.getElement6(), is(instanceOf(Object6.class)));
        assertThat(tuple.getElement6(), is(sameInstance(object6)));
    }

    @Test
    public void testGetElement7() {
        assertThat(tuple.getElement7(), is(instanceOf(Object7.class)));
        assertThat(tuple.getElement7(), is(sameInstance(object7)));
    }

    @Test
    public void testGetElement8() {
        assertThat(tuple.getElement8(), is(instanceOf(Object8.class)));
        assertThat(tuple.getElement8(), is(sameInstance(object8)));
    }

    @Test
    public void testGetElement9() {
        assertThat(tuple.getElement9(), is(instanceOf(Object9.class)));
        assertThat(tuple.getElement9(), is(sameInstance(object9)));
    }
}
