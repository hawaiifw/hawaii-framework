package org.hawaiiframework.time;

import java.time.Clock;
import java.time.ZoneId;

/** A zoned clock. */
class HawaiiZonedClock {

  private HawaiiTime hawaiiTime;

  private Clock clock;

  private ZoneId zone;

  HawaiiTime getHawaiiTime() {
    return hawaiiTime;
  }

  void setHawaiiTime(HawaiiTime hawaiiTime) {
    this.hawaiiTime = hawaiiTime;
  }

  Clock getClock() {
    return clock;
  }

  void setClock(Clock clock) {
    this.clock = clock;
  }

  ZoneId getZone() {
    return zone;
  }

  void setZone(ZoneId zone) {
    this.zone = zone;
  }
}
