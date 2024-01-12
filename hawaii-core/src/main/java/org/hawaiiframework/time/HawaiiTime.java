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

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
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

/**
 * This class provides a application wide {@link Clock} reference to instantiate new {@code
 * java.time} date and time objects.
 *
 * <p>It provides convenient methods to use a fixed date time which is particular useful for unit
 * testing but also to change the date time in a running application to test how it behaves on a
 * given date or time.
 *
 * <p>Be aware that third-party libraries being used by the application do obviously not use {@code
 * HawaiiTime} and probably instantiate date and time objects based on the {@code System} time.
 *
 * <p>Furthermore, this class should be injected using constructor injection. This will provide the
 * flexibility to test this object using the fixed clock methods.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class HawaiiTime {

  /** The default zone. */
  protected static final ZoneId DEFAULT_ZONE = UTC;

  private static final String CLOCK_MUST_NOT_BE_NULL = "'clock' must not be null";
  private static final String DATETIME_MUST_NOT_BE_NULL = "'dateTime' must not be null";
  private static final String INSTANT_MUST_NOT_BE_NULL = "'instant' must not be null";
  private static final String ZONE_MUST_NOT_BE_NULL = "'zone' must not be null";

  private static final HawaiiZonedClock INSTANCE = new HawaiiZonedClock();

  /** Constructs a new {@code HawaiiTime} with the default {@link #DEFAULT_ZONE}. */
  public HawaiiTime() {
    this(DEFAULT_ZONE);
  }

  /**
   * Constructs a new {@code HawaiiTime} with the supplied zone.
   *
   * @param zone the zone, not null
   */
  public HawaiiTime(ZoneId zone) {
    this(Clock.system(requireNonNull(zone, ZONE_MUST_NOT_BE_NULL)));
  }

  /**
   * Constructs a new {@code HawaiiTime} with the supplied clock. Also the {@code ZoneId} is derived
   * from the supplied clock.
   *
   * @param clock the clock, not null
   */
  public HawaiiTime(Clock clock) {
    INSTANCE.setClock(requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL));
    INSTANCE.setZone(clock.getZone());
    INSTANCE.setHawaiiTime(this);
  }

  public static HawaiiTime getInstance() {
    return INSTANCE.getHawaiiTime();
  }

  /**
   * Returns the clock used by this {@code HawaiiTime}.
   *
   * @return the clock
   */
  public Clock getClock() {
    return INSTANCE.getClock();
  }

  /**
   * Sets the clock to be used by this {@code HawaiiTime}.
   *
   * @param clock the clock, not null
   */
  public void setClock(Clock clock) {
    INSTANCE.setClock(requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL));
  }

  /**
   * Returns the zone used by this {@code HawaiiTime}.
   *
   * @return the zone
   */
  public ZoneId getZone() {
    return INSTANCE.getZone();
  }

  /**
   * Sets the zone to be used by this {@code HawaiiTime}.
   *
   * @param zone the zone
   */
  public void setZone(ZoneId zone) {
    INSTANCE.setZone(zone);
  }

  /**
   * Calculates the difference between this INSTANCE and the provided {@link Instant}.
   *
   * @param time which to compare
   * @return the result of the difference calculation as a {@link Long}
   */
  public Long between(Instant time) {
    return Duration.between(instant(), time).toMillis();
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param clock the fixed clock, not null
   */
  public void useFixedClock(Clock clock) {
    requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL);
    useFixedClock(clock.instant(), clock.getZone());
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param millis the millis since epoch
   */
  public void useFixedClock(long millis) {
    useFixedClock(Instant.ofEpochMilli(millis), getZone());
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param dateTime the fixed date time, not null
   */
  public void useFixedClock(LocalDateTime dateTime) {
    requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
    useFixedClock(dateTime.atZone(getZone()).toInstant(), getZone());
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param dateTime the fixed date time, not null
   */
  public void useFixedClock(OffsetDateTime dateTime) {
    requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
    useFixedClock(dateTime.toInstant(), dateTime.getOffset());
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param dateTime the fixed date time, not null
   */
  public void useFixedClock(ZonedDateTime dateTime) {
    requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
    useFixedClock(dateTime.toInstant(), dateTime.getZone());
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param instant the fixed instant, not null
   */
  protected void useFixedClock(Instant instant) {
    requireNonNull(instant, INSTANT_MUST_NOT_BE_NULL);
    setClock(Clock.fixed(instant, getZone()));
  }

  /**
   * Sets a fixed clock to be used.
   *
   * @param instant the fixed instant, not null
   * @param zone the fixed zone, not null
   */
  protected void useFixedClock(Instant instant, ZoneId zone) {
    requireNonNull(instant, INSTANT_MUST_NOT_BE_NULL);
    requireNonNull(zone, ZONE_MUST_NOT_BE_NULL);
    setClock(Clock.fixed(instant, zone));
  }

  /** Sets the system clock to be used. */
  public void useSystemClock() {
    setClock(Clock.system(getZone()));
  }

  /**
   * Returns the current {@code Instant}.
   *
   * @return the {@code Instant}
   */
  public Instant instant() {
    return Instant.now(getClock());
  }

  /**
   * Returns the current {@code LocalDate}.
   *
   * @return the {@code LocalDate}
   */
  public LocalDate localDate() {
    return LocalDate.now(getClock());
  }

  /**
   * Returns the current {@code LocalDateTime}.
   *
   * @return the {@code LocalDateTime}
   */
  public LocalDateTime localDateTime() {
    return LocalDateTime.now(getClock());
  }

  /**
   * Returns the current {@code LocalTime}.
   *
   * @return the {@code LocalTime}
   */
  public LocalTime localTime() {
    return LocalTime.now(getClock());
  }

  /**
   * Returns the current {@code millis}.
   *
   * @return the {@code millis}
   */
  public long millis() {
    return getClock().millis();
  }

  /**
   * Returns the current {@code MonthDay}.
   *
   * @return the {@code MonthDay}
   */
  public MonthDay monthDay() {
    return MonthDay.now(getClock());
  }

  /**
   * Returns the current {@code OffsetDateTime}.
   *
   * @return the {@code OffsetDateTime}
   */
  public OffsetDateTime offsetDateTime() {
    return OffsetDateTime.now(getClock());
  }

  /**
   * Returns the current {@code OffsetTime}.
   *
   * @return the {@code OffsetTime}
   */
  public OffsetTime offsetTime() {
    return OffsetTime.now(getClock());
  }

  /**
   * Returns the current {@code Year}.
   *
   * @return {@code Year}
   */
  public Year year() {
    return Year.now(getClock());
  }

  /**
   * Returns the current {@code YearMonth}.
   *
   * @return the {@code YearMonth}
   */
  public YearMonth yearMonth() {
    return YearMonth.now(getClock());
  }

  /**
   * Returns the current {@code ZonedDateTime}.
   *
   * @return the {@code ZonedDateTime}
   */
  public ZonedDateTime zonedDateTime() {
    return ZonedDateTime.now(getClock());
  }
}
