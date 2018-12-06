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

import java.time.*;

import static java.util.Objects.requireNonNull;

/**
 * This class provides a application wide {@link Clock} reference to instantiate new {@code java.time} date and time objects.
 * <p>
 * It provides convenient methods to use a fixed date time which is particular useful for unit testing but also to change the date time in
 * a running application to test how it behaves on a given date or time.
 * <p>
 * Be aware that third-party libraries being used by the application do obviously not use {@code HawaiiTime} and probably instantiate date
 * and time objects based on the {@code System} time.
 * <p>
 * Furthermore this class should be injected using constructor injection. This will provide the flexibility to test this object using the
 * the fixed clock methods.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class HawaiiTime {

    /**
     * The default zone.
     */
    protected static final ZoneId DEFAULT_ZONE = ZoneOffset.UTC;

    private static final String CLOCK_MUST_NOT_BE_NULL = "'clock' must not be null";
    private static final String DATETIME_MUST_NOT_BE_NULL = "'dateTime' must not be null";
    private static final String INSTANT_MUST_NOT_BE_NULL = "'instant' must not be null";
    private static final String ZONE_MUST_NOT_BE_NULL = "'zone' must not be null";

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
    public HawaiiTime(final ZoneId zone) {
        this.zone = requireNonNull(zone, ZONE_MUST_NOT_BE_NULL);
        this.clock = Clock.system(zone);
    }

    /**
     * Constructs a new {@code HawaiiTime} with the supplied clock. Also the {@code ZoneId} is derived from the supplied clock.
     *
     * @param clock the clock, not null
     */
    public HawaiiTime(final Clock clock) {
        this.clock = requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL);
        this.zone = clock.getZone();
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
    public void setClock(final Clock clock) {
        this.clock = requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL);
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
    public void setZone(final ZoneId zone) {
        this.zone = zone;
    }

    /**
     * Calculates the difference between this instance and the provided {@link Instant}.
     *
     * @param time which to compare
     * @return the result of the of the difference calculation as a {@link Long}
     */
    public Long between(final Instant time) {
        return Duration.between(instant(), time).toMillis();
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param clock the fixed clock, not null
     */
    public void useFixedClock(final Clock clock) {
        requireNonNull(clock, CLOCK_MUST_NOT_BE_NULL);
        useFixedClock(clock.instant(), clock.getZone());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param millis the millis since epoch
     */
    public void useFixedClock(final long millis) {
        useFixedClock(Instant.ofEpochMilli(millis), this.zone);
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(final LocalDateTime dateTime) {
        requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
        useFixedClock(dateTime.atZone(this.zone).toInstant(), this.zone);
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(final OffsetDateTime dateTime) {
        requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
        useFixedClock(dateTime.toInstant(), dateTime.getOffset());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param dateTime the fixed date time, not null
     */
    public void useFixedClock(final ZonedDateTime dateTime) {
        requireNonNull(dateTime, DATETIME_MUST_NOT_BE_NULL);
        useFixedClock(dateTime.toInstant(), dateTime.getZone());
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param instant the fixed instant, not null
     */
    protected void useFixedClock(final Instant instant) {
        requireNonNull(instant, INSTANT_MUST_NOT_BE_NULL);
        setClock(Clock.fixed(instant, this.zone));
    }

    /**
     * Sets a fixed clock to be used.
     *
     * @param instant the fixed instant, not null
     * @param zone    the fixed zone, not null
     */
    protected void useFixedClock(final Instant instant, final ZoneId zone) {
        requireNonNull(instant, INSTANT_MUST_NOT_BE_NULL);
        requireNonNull(zone, ZONE_MUST_NOT_BE_NULL);
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
