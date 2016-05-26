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

package org.hawaiiframework.time;

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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * This class provides a application wide {@link Clock} reference to instantiate new {@code
 * java.time} date and time objects.
 *
 * <p>
 * It provides convenient methods to use a fixed date time which is particular useful for unit
 * testing but also to change the date time in a running application to test how it behaves on a
 * given date or time.
 *
 * <p>
 * Be aware that third-party libraries being used by the application do obviously not use {@code
 * HawaiiTime} and probably instantiate date and time objects based on the {@code System} time.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class HawaiiTime {

    /**
     * The default zone.
     */
    protected static final ZoneId DEFAULT_ZONE = ZoneOffset.UTC;

    private Clock clock;
    private ZoneId zone;

    /**
     * Constructs a new {@code HawaiiTime} with the default {@link #DEFAULT_ZONE}.
     */
    public HawaiiTime() {
        this(DEFAULT_ZONE);
    }

    /**
     * Constructs a new {@code HawaiiTime} with the supplied zone.
     *
     * @param zone the zone, not null
     */
    public HawaiiTime(ZoneId zone) {
        this.zone = Objects.requireNonNull(zone, "'zone' must not be null");
        useSystemClock();
    }

    /**
     * Constructs a new {@code HawaiiTime} with the supplied clock. Also the {@code ZoneId} is
     * derived from the supplied clock.
     *
     * @param clock the clock, not null
     */
    public HawaiiTime(Clock clock) {
        Objects.requireNonNull(clock, "'clock' must not be null");
        this.zone = clock.getZone();
        setClock(clock);
    }

    /**
     * Returns the clock used by this {@code HawaiiTime}.
     *
     * @return the clock
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Sets the clock to be used by this {@code HawaiiTime}.
     *
     * @param clock the clock, not null
     */
    public void setClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "'clock' must not be null");
    }

    /**
     * Returns the zone used by this {@code HawaiiTime}.
     *
     * @return the zone
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Sets the zone to be used by this {@code HawaiiTime}.
     *
     * @param zone the zone
     */
    public void setZone(ZoneId zone) {
        this.zone = zone;
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param clock the fixed clock, not null
     */
    public void useFixedClock(Clock clock) {
        Objects.requireNonNull(clock, "'clock' must not be null");
        useFixedClock(clock.instant(), clock.getZone());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param millis the millis since epoch
     */
    public void useFixedClock(long millis) {
        useFixedClock(Instant.ofEpochMilli(millis), this.zone);
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "'dateTime' must not be null");
        useFixedClock(dateTime.atZone(this.zone).toInstant(), this.zone);
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(OffsetDateTime dateTime) {
        Objects.requireNonNull(dateTime, "'dateTime' must not be null");
        useFixedClock(dateTime.toInstant(), dateTime.getOffset());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(ZonedDateTime dateTime) {
        Objects.requireNonNull(dateTime, "'dateTime' must not be null");
        useFixedClock(dateTime.toInstant(), dateTime.getZone());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param instant the fixed instant, not null
     */
    protected void useFixedClock(Instant instant) {
        Objects.requireNonNull(instant, "'instant' must not be null");
        setClock(Clock.fixed(instant, this.zone));
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param instant the fixed instant, not null
     * @param zone the fixed zone, not null
     */
    protected void useFixedClock(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "'instant' must not be null");
        Objects.requireNonNull(zone, "'zone' must not be null");
        setClock(Clock.fixed(instant, zone));
    }

    /**
     * Sets the system clock to be used.
     */
    public void useSystemClock() {
        setClock(Clock.system(zone));
    }

    /**
     * Returns the current {@code Instant}.
     *
     * @return the {@code Instant}
     */
    public Instant instant() {
        return Instant.now(this.clock);
    }

    /**
     * Returns the current {@code LocalDate}.
     *
     * @return the {@code LocalDate}
     */
    public LocalDate localDate() {
        return LocalDate.now(this.clock);
    }

    /**
     * Returns the current {@code LocalDateTime}.
     *
     * @return the {@code LocalDateTime}
     */
    public LocalDateTime localDateTime() {
        return LocalDateTime.now(this.clock);
    }

    /**
     * Returns the current {@code LocalTime}.
     *
     * @return the {@code LocalTime}
     */
    public LocalTime localTime() {
        return LocalTime.now(this.clock);
    }

    /**
     * Returns the current {@code millis}.
     *
     * @return the {@code millis}
     */
    public long millis() {
        return this.clock.millis();
    }

    /**
     * Returns the current {@code MonthDay}.
     *
     * @return the {@code MonthDay}
     */
    public MonthDay monthDay() {
        return MonthDay.now(this.clock);
    }

    /**
     * Returns the current {@code OffsetDateTime}.
     *
     * @return the {@code OffsetDateTime}
     */
    public OffsetDateTime offsetDateTime() {
        return OffsetDateTime.now(this.clock);
    }

    /**
     * Returns the current {@code OffsetTime}.
     *
     * @return the {@code OffsetTime}
     */
    public OffsetTime offsetTime() {
        return OffsetTime.now(this.clock);
    }

    /**
     * Returns the current {@code Year}.
     *
     * @return {@code Year}
     */
    public Year year() {
        return Year.now(this.clock);
    }

    /**
     * Returns the current {@code YearMonth}.
     *
     * @return the {@code YearMonth}
     */
    public YearMonth yearMonth() {
        return YearMonth.now(this.clock);
    }

    /**
     * Returns the current {@code ZonedDateTime}.
     *
     * @return the {@code ZonedDateTime}
     */
    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now(this.clock);
    }
}
