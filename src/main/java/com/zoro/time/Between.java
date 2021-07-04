package com.zoro.time;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;

final class Between implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;

    private final Period period;
    private Duration duration;
    private int hour;
    private int minute;
    private int second;

    Between(Period period, Duration duration) {
        this.period = period;
        this.duration = duration;
    }

    Between(Period period, LocalTime start, LocalTime end) {
        this.period = period;
        this.duration = Duration.between(start, end);
    }

    public int getYears() {
        return period.getYears();
    }

    public int getMonths() {
        return period.getMonths();
    }

    public int getDays() {
        return period.getDays();
    }

    public int getHours() {
        return (int) (duration.toHours() % 24);
    }

    public int getMinutes() {
        return (int) (duration.toMinutes() % MINUTES_PER_HOUR);
    }

    public int getSeconds() {
        return (int) (duration.getSeconds() % SECONDS_PER_MINUTE);
    }

}
