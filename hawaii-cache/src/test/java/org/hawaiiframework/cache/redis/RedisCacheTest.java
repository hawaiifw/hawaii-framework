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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RedisCacheTest {

    private final String keyPrefix = "redisCacheTest_";
    private final Duration duration = Duration.ofMillis(1000);
    private final String fullKey;
    private final Long defaultExpiry = 1L;
    private final Foo testObject;

    private HawaiiTime hawaiiTime;

    @Mock
    private RedisTemplate<String, Foo> mockTemplate;

    @Mock
    private ValueOperations<String, Foo> mockOperations;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


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
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockTemplate.opsForValue()).thenReturn(mockOperations);
        this.hawaiiTime = new HawaiiTime();
        this.redisCache = new RedisCache<>(mockTemplate, hawaiiTime, defaultExpiry, keyPrefix);
    }

    @Test
    public void put() {
        //Test 2 parameter put
        redisCache.put(testObject.bar, testObject);
        //Test 3 parameter put Duration
        redisCache.put(testObject.bar, testObject, duration);
        //Test 3 parameter put HawaiiTime

        hawaiiTime.useFixedClock(LocalDateTime.now());
        final var t = LocalDateTime.now().plusMinutes(5);

        final var result = Duration.between(hawaiiTime.localDateTime(), t).toMillis();

        redisCache.put(testObject.bar, testObject, t);
        //Test eternity put
        redisCache.putEternally(testObject.bar, testObject);

        //Test if the template is called with the right values.
        verify(mockTemplate.opsForValue(), times(4)).set(fullKey, testObject);
        verify(mockTemplate, times(1)).expire(eq(fullKey), any(Long.class), eq(TimeUnit.MINUTES));
        verify(mockTemplate, times(3)).expire(eq(fullKey), any(Long.class), eq(TimeUnit.MILLISECONDS));

        verify(mockTemplate, times(1)).expire(eq(fullKey), eq(defaultExpiry), eq(TimeUnit.MINUTES));
        verify(mockTemplate, times(1)).expire(eq(fullKey), eq(duration.toMillis()), eq(TimeUnit.MILLISECONDS));
        verify(mockTemplate, times(1)).expire(
                eq(fullKey), eq(Duration.ofMillis(Long.MAX_VALUE).toMillis()), eq(TimeUnit.MILLISECONDS));

        verify(mockTemplate, times(1)).expire(
                eq(fullKey), eq(result), eq(TimeUnit.MILLISECONDS));



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
        LocalDateTime t = null;
        tests.add(() -> redisCache.put(testObject.bar, testObject, t));
        tests.add(() -> redisCache.put(testObject.bar, null, hawaiiTime.localDateTime()));
        tests.add(() -> redisCache.put(null, testObject, hawaiiTime.localDateTime()));
        //Test eternity put
        tests.add(() -> redisCache.putEternally(testObject.bar, null));
        tests.add(() -> redisCache.putEternally(null, testObject));

        //Passing calls
        redisCache.put(testObject.bar, testObject);
        redisCache.put(testObject.bar, testObject, duration);
        redisCache.put(testObject.bar, testObject, hawaiiTime.localDateTime());
        redisCache.putEternally(testObject.bar, testObject);

        for (Runnable item : tests) {
            exception.expect(NullPointerException.class);
            item.run();
            //assertThrows(NullPointerException.class, item);
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
            exception.expect(NullPointerException.class);
            item.run();
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
            exception.expect(NullPointerException.class);
            item.run();
        }

        //Verify if redis template is called appropriately
        verify(mockTemplate, times(1)).delete(eq(fullKey));
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
