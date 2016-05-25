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

package org.hawaiiframework.util;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link StackedHashMap}.
 *
 * @author Marcel Overdijk
 */
public class StackedHashMapTests {

    private StackedHashMap<Object, Object> map;

    @Test
    public void testDefaultConstructor() {
        map = new StackedHashMap<>();
        assertThat(map.size(), is(0));
        assertThat(map.stackSize(), is(1));

    }

    @Test
    public void testConstructorWithInitialMap() {
        Map initial = new Properties();
        initial.put("foo", "bar");
        map = new StackedHashMap<>(initial);
        assertThat(map, hasEntry("foo", "bar"));
        assertThat(map.size(), is(1));
        assertThat(map.stackSize(), is(1));
    }

    @Test
    public void testPushCopiesMap() {
        map = new StackedHashMap<>();
        map.put("foo", "bar");
        map.push();
        assertThat(map, hasEntry("foo", "bar"));
        assertThat(map.size(), is(1));
        assertThat(map.stackSize(), is(2));
    }

    @Test
    public void testPopRemovesCurrentMapFromStack() {
        map = new StackedHashMap<>();
        map.push();
        map.put("foo", "bar");
        Map<Object, Object> poppedMap = map.pop();
        assertThat(poppedMap, hasEntry("foo", "bar"));
        assertThat(map, not(hasKey("foo")));
        assertThat(map.stackSize(), is(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testPopWithOnlyOneElementThrowsException() {
        map = new StackedHashMap<>();
        map.pop();
    }

    @Test
    public void testPeekReturnsCurrentMap() {
        map = new StackedHashMap<>();
        map.put("foo", "bar");
        Map peekedMap = map.peek();
        assertThat(peekedMap, is(map));
    }

    @Test
    public void testStackSize() {
        map = new StackedHashMap<>();
        assertThat(map.stackSize(), is(1));
        map.push();
        assertThat(map.stackSize(), is(2));
        map.push();
        assertThat(map.stackSize(), is(3));
        map.peek();
        assertThat(map.stackSize(), is(3));
        map.pop();
        assertThat(map.stackSize(), is(2));
        map.pop();
        assertThat(map.stackSize(), is(1));
    }

    @Test
    public void testClearStack() {
        map = new StackedHashMap<>();
        map.put("foo", "bar");
        map.push();
        map.clearStack();
        assertThat(map.size(), is(0));
        assertThat(map.stackSize(), is(1));
    }

    @Test
    public void testPut() {
        map = new StackedHashMap<>();
        map.put("foo", "bar");
        assertThat(map, hasEntry("foo", "bar"));
    }

    @Test
    public void testEntrySet() {
        map = new StackedHashMap<>();
        map.put("foo", "bar");
        assertThat(map.entrySet(), hasSize(1));
        assertThat(map.entrySet(), contains(new SimpleEntry("foo", "bar")));
    }
}
