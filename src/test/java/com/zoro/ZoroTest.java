package com.zoro;

import com.zoro.time.DateUnit;
import com.zoro.time.Zoro;
import org.junit.Test;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZoroTest {

    @Test
    public void testCreate() {
        // create by LocalTime
        LocalTime time = LocalTime.of(11, 7, 5);
        Zoro zoro = Zoro.create(time);
        assertEquals(LocalDate.now(), zoro.toLocalDate());
        assertEquals(time, zoro.toLocalTime());

        // create by LocalDate
        LocalDate date = LocalDate.of(2020, 2, 1);
        zoro = Zoro.create(date);
        assertEquals(date, zoro.toLocalDate());
        assertTrue(zoro.isSameTime(Zoro.now()));

        // create by LocalDateTime
        LocalDateTime now = LocalDateTime.now();
        zoro = Zoro.create(now);
        assertEquals(now.getYear(), zoro.year());
        assertEquals(now.getMonth(), zoro.month());
        assertEquals(now.getDayOfMonth(), zoro.dayOfMonth());
        assertEquals(now.getDayOfYear(), zoro.dayOfYear());
        assertEquals(now.getDayOfWeek(), zoro.dayOfWeek());
        assertEquals(now.getHour(), zoro.hour());
        assertEquals(now.getMinute(), zoro.minute());

        // create by Zoro
        Zoro copyZoro = Zoro.create(zoro);
        assertEquals(zoro, copyZoro);

        // yesterday
        LocalDateTime yesterday = LocalDateTime.now().plusDays(-1);
        Zoro yesterdayTest = Zoro.yesterday();
        assertEquals(yesterday.toLocalDate(), yesterdayTest.toLocalDate());

        // parse
        String text = "20210101121212";
        zoro = Zoro.parse(text, Zoro.PURE_DATETIME_PATTERN);
        assertEquals(2021, zoro.year());
        LocalDateTime localDateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern(Zoro.PURE_DATETIME_PATTERN));
        assertEquals(Zoro.create(localDateTime), zoro);

        // create by Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        zoro = Zoro.create(calendar);
        assertEquals(calendar, zoro.toCalendar());
        assertEquals(calendar.get(Calendar.YEAR), zoro.year());
        assertEquals(calendar.get(Calendar.MONTH) + 1, zoro.monthValue());
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), zoro.dayOfMonth());
        assertEquals(calendar.get(Calendar.DAY_OF_YEAR), zoro.dayOfYear());
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), zoro.hour());
        assertEquals(calendar.get(Calendar.MINUTE), zoro.minute());

        Date date1 = new Date();
        zoro = Zoro.create(date1);
        assertEquals(date1.getTime(), zoro.timestamp());

        // create by timestamp
        long currentTimeMillis = System.currentTimeMillis();
        zoro = Zoro.create(currentTimeMillis);
        assertEquals(currentTimeMillis, zoro.timestamp());

        zoro = Zoro.createByDay(2021, Month.of(6), 1);
        assertEquals(LocalDate.of(2021, 6, 1), zoro.toLocalDate());
    }

    @Test
    public void testTimestamp() {
        Zoro now = Zoro.now();
        assertTrue(System.currentTimeMillis() - now.timestamp() < 10);
    }

    @Test
    public void testRange() {
        Zoro start = Zoro.create(2020, 1, 31, 10, 0, 10, 0);
        Zoro end = Zoro.create(2021, 2, 1, 1, 0, 0, 0);
        assertEquals(1, start.diffOfYears(end));
        assertEquals(12, start.diffOfMonths(end));

        List<String> list = new ArrayList<>();
        Zoro.range(start, end, DateUnit.YEAR).forEach(zoro -> {
            list.add(zoro.year() + "");
        });
        assertEquals(2, list.size());
        assertArrayEquals(new String[]{"2020", "2021"}, list.toArray(new String[0]));
    }

    @Test
    public void testZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Zoro.UTC));
        calendar.set(Calendar.YEAR, 2021);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        Zoro zoro = Zoro.create(2021, 1, 1, 15, 10, 10, 10, Zoro.UTC);
        zoro.withZoneId(Zoro.PST);
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), zoro.hour());
    }

    @Test
    public void test() {
        Zoro byDay = Zoro.createByDay(2019, Month.of(5), 11);
        assertTrue(Zoro.now().isAfter(byDay));
        assertFalse(Zoro.now().isBefore(byDay));
        assertFalse(byDay.isLeapYear());
        assertTrue(Zoro.now().compareTo(byDay) > 0);

        Zoro leapYear = Zoro.createByDay(2008, Month.of(1), 1);
        assertTrue(leapYear.isLeapYear());

        Zoro zoro = Zoro.createByDay(2021, Month.JULY, 5);
        assertEquals(2, zoro.weekOfMonth());
        assertEquals(28, zoro.weekOfYear());
        assertEquals(Month.JULY, zoro.month());
        assertEquals(3, zoro.quarter());
        assertEquals(DayOfWeek.MONDAY, zoro.dayOfWeek());
        assertEquals(5, zoro.dayOfMonth());
        assertEquals(31, zoro.monthDays());
        assertEquals(LocalDate.of(2021, Month.JULY, 5).lengthOfYear(), zoro.yearDays());
    }

}
