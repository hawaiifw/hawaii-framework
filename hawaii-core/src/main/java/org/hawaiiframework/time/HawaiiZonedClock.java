package org.hawaiiframework.time;

import java.time.Clock;
import java.time.ZoneId;

class HawaiiZonedClock {

    private HawaiiTime hawaiiTime;

    private Clock clock;

    private ZoneId zone;

    HawaiiTime getHawaiiTime() {
        return hawaiiTime;
    }

    void setHawaiiTime(final HawaiiTime hawaiiTime) {
        this.hawaiiTime = hawaiiTime;
    }

    Clock getClock() {
        return clock;
    }

    void setClock(final Clock clock) {
        this.clock = clock;
    }

    ZoneId getZone() {
        return zone;
    }

    void setZone(final ZoneId zone) {
        this.zone = zone;
    }
}
