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

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.hawaiiframework.time.HawaiiTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@RunWith(MockitoJUnitRunner.class)
public class RedisCacheTest {

  private final String keyPrefix = "redisCacheTest_";
  private final Duration duration = Duration.ofMillis(1000);
  private final String fullKey;
  private final Duration defaultExpiry = Duration.ofMinutes(1L);
  private final Foo testObject;

  private HawaiiTime hawaiiTime;

  @Mock private RedisTemplate<String, Foo> mockTemplate;

  @Mock private ValueOperations<String, Foo> mockOperations;

  /** Object to be tested. */
  private RedisCache<Foo> redisCache;

  public RedisCacheTest() {
    this.hawaiiTime = new HawaiiTime();
    testObject = new Foo("test-foo", "test-hello", "test-world");
    fullKey = constructKey(testObject.bar);
  }

  @Before
  public void setUp() {
    //        MockitoAnnotations.openMocks(this);
    when(mockTemplate.opsForValue()).thenReturn(mockOperations);
    this.hawaiiTime = new HawaiiTime();
    this.redisCache = new RedisCache<>(mockTemplate, hawaiiTime, defaultExpiry, keyPrefix);
  }

  @Test
  public void put1() {
    redisCache.put(testObject.bar, testObject);

    verify(mockTemplate.opsForValue(), times(1)).set(fullKey, testObject);
  }

  @Test
  public void put2() {
    redisCache.put(testObject.bar, testObject, duration);

    verify(mockTemplate.opsForValue(), times(1)).set(fullKey, testObject);
    verify(mockTemplate, times(1)).expire(eq(fullKey), eq(duration));
  }

  @Test
  public void put3() {
    hawaiiTime.useFixedClock(LocalDateTime.now());
    var localDateTimeTest = LocalDateTime.now().plusMinutes(5);
    var resultLocalDateTime =
        Duration.between(hawaiiTime.localDateTime(), localDateTimeTest).toMillis();

    redisCache.put(testObject.bar, testObject, localDateTimeTest);
    verify(mockTemplate.opsForValue(), times(1)).set(fullKey, testObject);
    verify(mockTemplate, times(1))
        .expire(eq(fullKey), eq(resultLocalDateTime), eq(TimeUnit.MILLISECONDS));
  }

  @Test
  public void put4() {
    hawaiiTime.useFixedClock(LocalDateTime.now());
    var zonedDateTimeTest = ZonedDateTime.now().plusMinutes(10);
    var resultZonedDateTime =
        Duration.between(hawaiiTime.zonedDateTime(), zonedDateTimeTest).toMillis();

    // Testing with zoned date time
    redisCache.put(testObject.bar, testObject, zonedDateTimeTest);

    verify(mockTemplate.opsForValue(), times(1)).set(fullKey, testObject);
    verify(mockTemplate, times(1))
        .expire(eq(fullKey), eq(resultZonedDateTime), eq(TimeUnit.MILLISECONDS));
  }

  @Test
  public void put5() {
    // Test eternity put
    redisCache.putEternally(testObject.bar, testObject);

    verify(mockTemplate.opsForValue(), times(1)).set(fullKey, testObject);
    verify(mockTemplate, times(1)).expire(eq(fullKey), eq(Duration.ofMillis(Long.MAX_VALUE)));
  }

  @Test
  public void putProducesNullPointerOnPassingNull() {
    // Test if put produces an error
    Map<String, Runnable> tests = new HashMap<>();
    // Test put default duration
    tests.put("Test_put_default_duration_1", () -> redisCache.put(null, testObject));
    tests.put("Test_put_default_duration_2", () -> redisCache.put(testObject.bar, null));
    // Test put duration
    Duration d = null;
    tests.put("Test_put_duration_1", () -> redisCache.put(testObject.bar, testObject, d));
    tests.put("Test_put_duration_2", () -> redisCache.put(testObject.bar, null, duration));
    tests.put("Test_put_duration_3", () -> redisCache.put(null, testObject, duration));
    // Test hawaiiTime LocalDateTime
    LocalDateTime t = null;
    tests.put(
        "Test_hawaiiTime_LocalDateTime_1", () -> redisCache.put(testObject.bar, testObject, t));
    tests.put(
        "Test_hawaiiTime_LocalDateTime_2",
        () -> redisCache.put(testObject.bar, null, hawaiiTime.localDateTime()));
    tests.put(
        "Test_hawaiiTime_LocalDateTime_3",
        () -> redisCache.put(null, testObject, hawaiiTime.localDateTime()));
    // Test hawaiiTime ZonedDateTime
    ZonedDateTime z = null;
    tests.put(
        "Test_hawaiiTime_ZonedDateTime_1", () -> redisCache.put(testObject.bar, testObject, z));
    tests.put(
        "Test_hawaiiTime_ZonedDateTime_2",
        () -> redisCache.put(testObject.bar, null, hawaiiTime.zonedDateTime()));
    tests.put(
        "Test_hawaiiTime_ZonedDateTime_3",
        () -> redisCache.put(null, testObject, hawaiiTime.zonedDateTime()));
    // Test eternity put
    tests.put("Test_eternity_put_1", () -> redisCache.putEternally(testObject.bar, null));
    tests.put("Test_eternity_put_2", () -> redisCache.putEternally(null, testObject));

    // Passing calls
    redisCache.put(testObject.bar, testObject);
    redisCache.put(testObject.bar, testObject, duration);
    redisCache.put(testObject.bar, testObject, hawaiiTime.localDateTime());
    redisCache.put(testObject.bar, testObject, hawaiiTime.zonedDateTime());
    redisCache.putEternally(testObject.bar, testObject);

    for (var entry : tests.entrySet()) {
      assertThrows(
          "Expected NPE for '" + entry.getKey() + "'",
          NullPointerException.class,
          () -> entry.getValue().run());
    }

    // Verify if redis template is called appropriately
    verify(mockTemplate.opsForValue(), times(5)).set(eq(fullKey), any(Foo.class));
  }

  @Test
  public void get() {
    redisCache.get(testObject.bar);
    redisCache.get(testObject.bar);

    // Verify if redis template is called x times
    verify(mockTemplate.opsForValue(), times(2)).get(eq(fullKey));
  }

  @Test
  public void getProducesNullPointerOnPassingNull() {
    assertThrows(NullPointerException.class, () -> redisCache.get(null));

    // Passing calls
    redisCache.get(testObject.bar);

    // Verify if redis template is called appropriately
    verify(mockTemplate.opsForValue(), times(1)).get(eq(fullKey));
  }

  @Test
  public void remove() {
    redisCache.remove(testObject.bar);
    redisCache.remove(testObject.bar);

    // Verify if redis template is called x times
    verify(mockTemplate, times(2)).delete(eq(fullKey));
  }

  @Test
  public void removeProducesNullPointerOnPassingNull() {
    assertThrows(NullPointerException.class, () -> redisCache.remove(null));

    // Passing calls
    redisCache.remove(testObject.bar);

    // Verify if redis template is called appropriately
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
