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

package org.hawaiiframework.util;

import java.util.*;

/**
 * A stacked {@code HashMap} implementation.
 * <p>
 * This implementation is useful if you want to add and/or remove mappings to a map, but also want to return to a previous state of the map
 * easily.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Marcel Overdijk
 * @see HashMap
 * @since 2.0.0
 */
public class StackedHashMap<K, V> extends AbstractMap<K, V> {

    protected Deque<HashMap<K, V>> stack;

    /**
     * Constructs a new {@code StackedHashMap}.
     */
    public StackedHashMap() {
        this.stack = new ArrayDeque<>();
        this.stack.add(new HashMap());
    }

    /**
     * Constructs a new {@code StackedHashMap} with the same mappings as the supplied {@code Map}.
     *
     * @param m the map whose mappings are to be placed in this stacked map
     */
    public StackedHashMap(final Map<? extends K, ? extends V> m) {
        this();
        if (!m.isEmpty()) {
            peek().putAll(m);
        }
    }

    /**
     * Pushes a new element ({@code HashMap}) onto the stack.
     */
    public void push() {
        final HashMap<K, V> m = new HashMap<>(peek());
        this.stack.push(m);
    }

    /**
     * Pops the top element ({@code HashMap}) from the stack. Note that the initial element cannot be popped from the stack.
     *
     * @return the top element of the stack
     * @throws IllegalStateException if there is only {@code 1} element on the stack
     */
    @SuppressWarnings({"PMD.LooseCoupling", "PMD.AvoidLiteralsInIfCondition"})
    public HashMap<K, V> pop() {
        if (stackSize() > 1) {
            return this.stack.pop();
        }
        throw new IllegalStateException("Cannot pop first element from stack");
    }

    /**
     * Retrieves, but does not remove, the top element ({@code HashMap}) from the stack.
     *
     * @return the top element of the stack
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public HashMap<K, V> peek() {
        return this.stack.peek();
    }

    /**
     * Returns the number of elements ({@code HashMap}s) on the stack. There will always be at least {code 1} element on the stack.
     *
     * @return the number of elements on the stack
     */
    public int stackSize() {
        return this.stack.size();
    }

    /**
     * Clears the stack. After clearing the stack there will be {@code 1 } element ({@code HashMap}) on the stack without any mappings.
     */
    public void clearStack() {
        this.stack = new ArrayDeque<>();
        this.stack.add(new HashMap());
    }

    @Override
    public V put(final K key, final V value) {
        return peek().put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return peek().entrySet();
    }
}
