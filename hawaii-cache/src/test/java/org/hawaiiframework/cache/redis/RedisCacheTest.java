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
package org.hawaiiframework.cache.redis;

import org.hawaiiframework.time.HawaiiTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RedisCacheTest {

    private final String keyPrefix = "redisCacheTest_";
    private final Duration duration = Duration.ofMillis(1000);
    private final HawaiiTime hawaiiTime;
    private final String fullKey;
    private Foo testObject;

    @Mock
    private RedisTemplate<String, Foo> mockTemplate;

    @Mock
    private ValueOperations<String, Foo> mockOperations;

    /**
     * Object to be tested.
     */
    private RedisCache<Foo> redisCache;

    public RedisCacheTest() {
        this.hawaiiTime = new HawaiiTime();
        testObject = new Foo("test-foo", "test-hello", "test-world");
        fullKey = constructKey(testObject.bar);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockTemplate.opsForValue()).thenReturn(mockOperations);
        final Long defaultExpiry = 1L;
        this.redisCache = new RedisCache<>(mockTemplate, defaultExpiry, keyPrefix);
    }

    @Test
    public void put() {
        //Test 2 parameter put
        redisCache.put(testObject.bar, testObject);
        //Test 3 parameter put Duration
        redisCache.put(testObject.bar, testObject, duration);
        //Test 3 parameter put HawaiiTime
        redisCache.put(testObject.bar, testObject, hawaiiTime);
        //Test eternity put
        redisCache.putEternally(testObject.bar, testObject);

        //Test if the template is called with the right values.
        verify(mockTemplate.opsForValue(), times(4)).set(fullKey, testObject);
        verify(mockTemplate, times(3)).expire(eq(fullKey), any(Long.class), any(TimeUnit.class));
    }

    @Test
    public void putProducesNullPointerOnPassingNull() {
        //Test if put produces an error
        List<Runnable> tests = new ArrayList<>();
        tests.add(() -> redisCache.put(null, testObject));
        tests.add(() -> redisCache.put(testObject.bar, null));
        //Test put duration
        Duration d = null;
        tests.add(() -> redisCache.put(testObject.bar, testObject, d));
        tests.add(() -> redisCache.put(testObject.bar, null, duration));
        tests.add(() -> redisCache.put(null, testObject, duration));
        //Test hawaiiTime
        HawaiiTime ht = null;
        tests.add(() -> redisCache.put(testObject.bar, testObject, ht));
        tests.add(() -> redisCache.put(testObject.bar, null, hawaiiTime));
        tests.add(() -> redisCache.put(null, testObject, hawaiiTime));
        //Test eternity put
        tests.add(() -> redisCache.putEternally(testObject.bar, null));
        tests.add(() -> redisCache.putEternally(null, testObject));

        //Passing calls
        redisCache.put(testObject.bar, testObject);
        redisCache.put(testObject.bar, testObject, duration);
        redisCache.put(testObject.bar, testObject, hawaiiTime);
        redisCache.putEternally(testObject.bar, testObject);

        for (Runnable item : tests) {
            assertThrows(NullPointerException.class, item);
        }

        //Verify if redis template is called appropriately
        verify(mockTemplate.opsForValue(), times(4)).set(eq(fullKey), any(Foo.class));
    }


    @Test
    public void get() {
        redisCache.get(testObject.bar);
        redisCache.get(testObject.bar);

        //Verify if redis template is called x times
        verify(mockTemplate.opsForValue(), times(2)).get(eq(fullKey));
    }

    @Test
    public void getProducesNullPointerOnPassingNull() {
        //Test if get produces an error
        List<Runnable> tests = new ArrayList<>();
        tests.add(() -> redisCache.get(null));

        //Passing calls
        redisCache.get(testObject.bar);

        for (Runnable item : tests) {
            assertThrows(NullPointerException.class, item);
        }

        //Verify if redis template is called appropriately
        verify(mockTemplate.opsForValue(), times(1)).get(eq(fullKey));
    }


    @Test
    public void remove() {
        redisCache.remove(testObject.bar);
        redisCache.remove(testObject.bar);

        //Verify if redis template is called x times
        verify(mockTemplate, times(2)).delete(eq(fullKey));

    }

    @Test
    public void removeProducesNullPointerOnPassingNull() {
        //Test if get produces an error
        List<Runnable> tests = new ArrayList<>();
        tests.add(() -> redisCache.remove(null));

        //Passing calls
        redisCache.remove(testObject.bar);

        for (Runnable item : tests) {
            assertThrows(NullPointerException.class, item);
        }

        //Verify if redis template is called appropriately
        verify(mockTemplate, times(1)).delete(eq(fullKey));
    }

    public static void assertThrows(Class<? extends Exception> expectedException, Runnable actionThatShouldThrow) {
        try {
            actionThatShouldThrow.run();
            fail("expected action to throw " + expectedException.getSimpleName() + " but it did not.");
        } catch (Exception e) {
            if (!expectedException.isInstance(e)) {
                fail("Expected action should have thrown: " + expectedException.getSimpleName() + ", but was: " + e.getClass()
                        .getSimpleName());
            }
        }
    }

    private String constructKey(String key) {
        return keyPrefix + key;
    }

    private class Foo {

        private final String bar;
        private final String hello;
        private final String world;

        private Foo(String bar, String hello, String world) {
            this.bar = bar;
            this.hello = hello;
            this.world = world;
        }

    }
}
