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

package org.hawaiiframework.time;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link HawaiiTime}.
 *
 * @author Marcel Overdijk
 */
public class HawaiiTimeTests {

  private static final Clock FIXED_EPOCH_CLOCK =
      Clock.fixed(Instant.EPOCH, HawaiiTime.DEFAULT_ZONE);
  private static final ZoneId EUROPE_AMSTERDAM_ZONE_ID = ZoneId.of("Europe/Amsterdam");

  private HawaiiTime hawaiiTime;

  @Before
  public void setUp() {
    this.hawaiiTime = new HawaiiTime();
  }

  @Test
  public void testDefaultConstructorUsesSystemClock() {
    assertThat(new HawaiiTime().getClock(), is(equalTo(Clock.system(HawaiiTime.DEFAULT_ZONE))));
  }

  @Test
  public void testConstructorWithZone() {
    hawaiiTime = new HawaiiTime(EUROPE_AMSTERDAM_ZONE_ID);
    assertThat(hawaiiTime.getClock(), is(equalTo(Clock.system(EUROPE_AMSTERDAM_ZONE_ID))));
  }

  @Test
  public void testConstructorWithClock() {
    hawaiiTime = new HawaiiTime(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testSetClock() {
    this.hawaiiTime.setClock(FIXED_EPOCH_CLOCK);
    assertThat(this.hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test(expected = NullPointerException.class)
  public void testSetClockCannotBeNull() {
    this.hawaiiTime.setClock(null);
  }

  @Test
  public void testUseFixedClock() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithMillis() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK.millis());
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithLocalDateTime() {
    hawaiiTime.useFixedClock(LocalDateTime.now(FIXED_EPOCH_CLOCK));
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithOffsetDateTime() {
    hawaiiTime.useFixedClock(OffsetDateTime.now(FIXED_EPOCH_CLOCK));
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithZonedDateTime() {
    hawaiiTime.useFixedClock(ZonedDateTime.now(FIXED_EPOCH_CLOCK));
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithInstant() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK.instant());
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseFixedClockWithInstantAndZone() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK.instant(), FIXED_EPOCH_CLOCK.getZone());
    assertThat(hawaiiTime.getClock(), is(equalTo(FIXED_EPOCH_CLOCK)));
  }

  @Test
  public void testUseSystemClock() {
    hawaiiTime.useSystemClock();
    assertThat(hawaiiTime.getClock(), is(equalTo(Clock.system(HawaiiTime.DEFAULT_ZONE))));
  }

  @Test
  public void testUseSystemClockWithCustomZome() {
    hawaiiTime.setZone(EUROPE_AMSTERDAM_ZONE_ID);
    hawaiiTime.useSystemClock();
    assertThat(hawaiiTime.getClock(), is(equalTo(Clock.system(EUROPE_AMSTERDAM_ZONE_ID))));
  }

  @Test
  public void testInstant() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.instant(), is(equalTo(Instant.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testLocalDate() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.localDate(), is(equalTo(LocalDate.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testLocalDateTime() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.localDateTime(), is(equalTo(LocalDateTime.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testLocalTime() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.localTime(), is(equalTo(LocalTime.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testMillis() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.millis(), is(equalTo(FIXED_EPOCH_CLOCK.millis())));
  }

  @Test
  public void testMonthDay() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.monthDay(), is(equalTo(MonthDay.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testOffsetDateTime() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.offsetDateTime(), is(equalTo(OffsetDateTime.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testOffsetTime() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.offsetTime(), is(equalTo(OffsetTime.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testYear() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.year(), is(equalTo(Year.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testYearMonth() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.yearMonth(), is(equalTo(YearMonth.now(FIXED_EPOCH_CLOCK))));
  }

  @Test
  public void testZonedDateTime() {
    hawaiiTime.useFixedClock(FIXED_EPOCH_CLOCK);
    assertThat(hawaiiTime.zonedDateTime(), is(equalTo(ZonedDateTime.now(FIXED_EPOCH_CLOCK))));
  }
}
