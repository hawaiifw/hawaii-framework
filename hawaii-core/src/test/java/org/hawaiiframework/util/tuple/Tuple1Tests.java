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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link Tuple1}.
 *
 * @author Marcel Overdijk
 */
public class Tuple1Tests extends AbstractTupleTests {

    private Tuple1<Object1> tuple = Tuples.of(object1);

    @Test
    public void testSizeIs1() {
        assertThat(tuple.size(), is(1));
    }

    @Test
    public void testSetElement1() {
        tuple.setElement1(other1);
        assertThat(tuple.getElement1(), is(sameInstance(other1)));
    }

    @Test
    public void testGetElement1() {
        assertThat(tuple.getElement1(), is(instanceOf(Object1.class)));
        assertThat(tuple.getElement1(), is(sameInstance(object1)));
    }
}
