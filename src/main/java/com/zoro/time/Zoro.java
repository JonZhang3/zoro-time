package com.zoro.time;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public final class Zoro implements Serializable, Comparable<Zoro>, Cloneable {

    private static final long serialVersionUID = 1L;

    public static final String PURE_DATE_PATTERN = "yyyyMMdd";
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    public static final String NORMAL_DATE_PATTERN = "yyyy-MM-dd";
    public static final String NORMAL_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_PATTERN = "HHmmss";
    public static final String NORMAL_TIME_PATTERN = "HH:mm:ss";

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId PST = ZoneId.of("America/Los_Angeles");
    public static final ZoneId SHANG_HAI = ZoneId.of("Asia/Shanghai");
    public static final ZoneId CTT = SHANG_HAI;

    /**
     * 星座分隔时间日
     */
    private static final int[] ZODIACS_DAY_ARR = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    /**
     * 星座
     */
    private static final String[] ZODIACS_SIGN = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座",
        "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
    private static final String[] CHINESE_ZODIACS_SIGN = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴",
        "鸡", "狗", "猪"};

    private static final CachedDateTimeFormatter CACHED_FORMATTER = new CachedDateTimeFormatter();

    private ZonedDateTime date;
    private DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
    private int minimalDaysInFirstWeek = 1;

    private Zoro() {
        date = ZonedDateTime.now();
    }

    private Zoro(@NotNull ZoneId zoneId) {
        date = ZonedDateTime.now(zoneId);
    }

    private Zoro(@NotNull ZonedDateTime date) {
        this.date = date;
    }

    public static @NotNull Zoro now() {
        return new Zoro();
    }

    public static @NotNull Zoro now(@NotNull ZoneId zone) {
        return new Zoro(zone);
    }

    public static @NotNull Zoro yesterday() {
        return Zoro.now().addDays(-1);
    }

    public static @NotNull Zoro parse(@NotNull CharSequence text, @NotNull String pattern) {
        return CACHED_FORMATTER.parse(text, pattern, Zoro::from);
    }

    public static @NotNull Zoro create(long timestamp) {
        return create(new Date(timestamp));
    }

    public static @NotNull Zoro create(long timestamp, @NotNull ZoneId zone) {
        return create(new Date(timestamp), zone);
    }

    public static @NotNull Zoro create(@NotNull Date date) {
        return create(date, ZoneId.systemDefault());
    }

    public static @NotNull Zoro create(@NotNull Date date, @NotNull ZoneId zone) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(zone, "zone");

        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), zone);
        return new Zoro(dateTime);
    }

    public static @NotNull Zoro create(@NotNull Calendar calendar) {
        Objects.requireNonNull(calendar);

        ZoneId zone = calendar.getTimeZone().toZoneId();
        return create(calendar.getTime(), zone);
    }

    public static @NotNull Zoro create(@NotNull Temporal temporal) {
        Objects.requireNonNull(temporal);

        return from(temporal);
    }

    public static @NotNull Zoro create(@NotNull Temporal temporal, ZoneId zone) {
        Objects.requireNonNull(temporal);

        return from(temporal).withZoneId(zone);
    }

    public static @NotNull Zoro createByDay(int year, @NotNull Month month, int dayOfMonth) {
        return createByDay(year, month, dayOfMonth, ZoneId.systemDefault());
    }

    public static @NotNull Zoro createByDay(int year, @NotNull Month month, int dayOfMonth, @NotNull ZoneId zone) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.now();
        return new Zoro(ZonedDateTime.of(date, time, zone));
    }

    public static @NotNull Zoro createByTime(int hour, int minute, int second, int millSecond) {
        return createByTime(hour, minute, second, millSecond, ZoneId.systemDefault());
    }

    public static @NotNull Zoro createByTime(int hour, int minute, int second, int millSecond, @NotNull ZoneId zone) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(hour, minute, second, millSecond * 1000_000);
        return new Zoro(ZonedDateTime.of(date, time, zone));
    }

    public static @NotNull Zoro create(int year, int month, int dayOfMonth, int hour, int minute, int second,
                                       int millSecond) {
        return new Zoro(ZonedDateTime.of(year, month, dayOfMonth,
            hour, minute, second, millSecond * 1000_000, ZoneId.systemDefault()));
    }

    public static @NotNull Zoro create(int year, int month, int dayOfMonth, int hour, int minute, int second,
                                       int millSecond, @NotNull ZoneId zone) {
        return new Zoro(ZonedDateTime.of(year, month, dayOfMonth,
            hour, minute, second, millSecond * 1000_000, zone));
    }

    public static @NotNull Zoro create(@NotNull Zoro zoro) {
        Objects.requireNonNull(zoro);

        return new Zoro(zoro.date);
    }

    public static @NotNull Zoro create(@NotNull Zoro o, @NotNull ZoneId zone) {
        Objects.requireNonNull(o);
        Objects.requireNonNull(zone);

        return new Zoro(o.date).withZoneId(zone);
    }

    public static @NotNull Range range(@NotNull Zoro start, @NotNull Zoro end, DateUnit unit) {
        return new Range(Zoro.create(start), Zoro.create(end), unit);
    }

    public ZoneId zone() {
        return this.date.getZone();
    }

    public Zoro firstDayOfWeek(@NotNull DayOfWeek week) {
        Objects.requireNonNull(week, "week");

        this.firstDayOfWeek = week;
        return this;
    }

    public @NotNull DayOfWeek firstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    public Zoro minimalDaysInFirstWeek(@org.jetbrains.annotations.Range(from = 1, to = 7) int minimalDaysInFirstWeek) {
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
        return this;
    }

    @org.jetbrains.annotations.Range(from = 1, to = 7)
    public int minimalDaysInFirstWeek() {
        return this.minimalDaysInFirstWeek;
    }

    public boolean isBefore(Zoro zoro) {
        return this.date.isBefore(zoro.date);
    }

    public boolean isAfter(Zoro zoro) {
        return this.date.isAfter(zoro.date);
    }

    /**
     * 获取当前时间所在的季度 <br>
     * 1~3 第一季度 <br>
     * 4~6 第二季度 <br>
     * 7~9 第三季度 <br>
     * 10~12 第四季度 <br>
     *
     * @return 所在季度
     */
    public int quarter() {
        int month = monthValue();
        if (month < 4) {
            return 1;
        } else if (month < 7) {
            return 2;
        } else if (month < 10) {
            return 3;
        }
        return 4;
    }

    /**
     * 获取当前时间的年份
     *
     * @return 年份
     */
    public int year() {
        return date.getYear();
    }

    /**
     * 获取当前时间的月
     *
     * @return 月份
     */
    public int monthValue() {
        return date.getMonthValue();
    }

    public Month month() {
        return date.getMonth();
    }

    /**
     * 获取当月的第几周
     *
     * @return 当月中的第几周
     */
    public int weekOfMonth() {
        return date.get(WeekFields.of(this.firstDayOfWeek(), this.minimalDaysInFirstWeek()).weekOfMonth());
    }

    public int weekOfYear() {
        return date.get(WeekFields.of(this.firstDayOfWeek(), this.minimalDaysInFirstWeek()).weekOfYear());
    }

    public DayOfWeek dayOfWeek() {
        return date.getDayOfWeek();
    }

    public int dayOfWeekValue() {
        return date.getDayOfWeek().getValue();
    }

    public int dayOfMonth() {
        return date.getDayOfMonth();
    }

    public int dayOfYear() {
        return date.getDayOfYear();
    }

    public int hour() {
        return date.getHour();
    }

    public int minute() {
        return date.getMinute();
    }

    public int second() {
        return date.getSecond();
    }

    public int millisecond() {
        return date.get(ChronoField.MILLI_OF_SECOND);
    }

    public int nano() {
        return date.getNano();
    }

    public int get(TemporalField field) {
        return date.get(field);
    }

    /**
     * 获取当前的星座
     *
     * @return 星座（中文）
     */
    public String zodiacsSign() {
        return getZodiacSign(monthValue(), dayOfMonth());
    }

    public String chineseZodiacsSign() {
        return getChineseZodiacSign(year());
    }

    public Zoro addYears(long years) {
        this.date = date.plusYears(years);
        return this;
    }

    public Zoro addMonths(long months) {
        this.date = date.plusMonths(months);
        return this;
    }

    public Zoro addWeeks(long weeks) {
        this.date = this.date.plusWeeks(weeks);
        return this;
    }

    public Zoro addDays(long days) {
        this.date = date.plusDays(days);
        return this;
    }

    public Zoro addHours(long hours) {
        this.date = date.plusHours(hours);
        return this;
    }

    public Zoro addMinutes(long minutes) {
        this.date = date.plusMinutes(minutes);
        return this;
    }

    public Zoro addSeconds(long seconds) {
        this.date = date.plusSeconds(seconds);
        return this;
    }

    public Zoro addNanos(long nanos) {
        this.date = date.plusNanos(nanos);
        return this;
    }

    /**
     * 设置时区，如果指定的时区与当前时区一致，则不做任何操作。如果不一致，会将时间转换为指定的时区时间。
     * <p>会改变当前时间</p>
     *
     * @param zone 时区
     * @return 当前实例
     * @see ZoneId
     */
    public Zoro withZoneId(@NotNull ZoneId zone) {
        if (zone().equals(zone)) {
            return this;
        }

        this.date = this.date.withZoneSameInstant(zone);

        return this;
    }

    public Zoro withYear(int year) {
        this.date = date.withYear(year);
        return this;
    }

    public Zoro withMonth(int month) {
        this.date = date.withMonth(month);
        return this;
    }

    public Zoro withDayOfMonth(int dayOfMonth) {
        this.date = date.withDayOfMonth(dayOfMonth);
        return this;
    }

    public Zoro withDayOfYear(int dayOfYear) {
        this.date = date.withDayOfYear(dayOfYear);
        return this;
    }

    public Zoro withHour(int hour) {
        this.date = date.withHour(hour);
        return this;
    }

    public Zoro withMinute(int minute) {
        this.date = date.withMinute(minute);
        return this;
    }

    public Zoro withSecond(int second) {
        this.date = date.withSecond(second);
        return this;
    }

    public Zoro withLastDayOfMonth() {
        this.date = date.with(TemporalAdjusters.lastDayOfMonth());
        return this;
    }

    public Zoro withFirstInMonth(DayOfWeek week) {
        this.date = date.with(TemporalAdjusters.firstInMonth(week));
        return this;
    }

    public Zoro with(@NotNull TemporalAdjuster adjuster) {
        this.date = this.date.with(adjuster);
        return this;
    }

    /**
     * 获取当前月的天数
     *
     * @return 当前月的天数
     */
    public int monthDays() {
        return date.toLocalDate().lengthOfMonth();
    }

    /**
     * 获取当前年的天数
     *
     * @return 当前年的天数
     */
    public int yearDays() {
        return date.toLocalDate().lengthOfYear();
    }

    /**
     * 检查当年是否是闰年
     *
     * @return {@code true} 是闰年，{@code false} 不是闰年
     */
    public boolean isLeapYear() {
        return date.toLocalDate().isLeapYear();
    }

    public long timestamp() {
        return date.toInstant().toEpochMilli();
    }

    public long diffOfYears(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.YEARS.between(this.date, end.date);
    }

    public long diffOfMonths(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.MONTHS.between(this.date, end.date);
    }

    public long diffOfDays(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.DAYS.between(this.date, end.date);
    }

    public long diffOfHours(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.HOURS.between(this.date, end.date);
    }

    public long diffOfMinutes(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.MINUTES.between(this.date, end.date);
    }

    public long diffOfSeconds(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.SECONDS.between(this.date, end.date);
    }

    public long diffOfWeeks(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        return ChronoUnit.WEEKS.between(this.date, end.date);
    }

    private Between between(@NotNull Zoro end) {
        Objects.requireNonNull(end, "end");

        Duration duration = Duration.between(this.toLocalTime(), end.toLocalTime());
        Period period = Period.between(this.toLocalDate(), end.toLocalDate());
        return new Between(period, duration);
    }

    private String diffForHumans(@Nullable Zoro end, boolean chinese) {
        boolean isNow = false;
        if (end == null) {
            end = Zoro.now(this.zone());
            isNow = true;
        }

        String unit;
        long count;
        Between between = between(end);
        if (between.getYears() != 0) {
            count = between.getYears();
            unit = chinese ? "年" : " year";
        } else if (between.getMonths() != 0) {
            count = between.getMonths();
            unit = chinese ? "月" : " month";
        } else if (between.getDays() != 0) {
            count = between.getDays();
            unit = chinese ? "天" : " day";
        } else if (between.getHours() != 0) {
            count = between.getHours();
            unit = chinese ? "时" : " hour";
        } else if (between.getMinutes() != 0) {
            count = between.getMinutes();
            unit = chinese ? "分" : " minute";
        } else {
            count = between.getSeconds();
            unit = chinese ? "秒" : " second";
        }
        if (count == 0) {
            count = 1;
        }
        if (count > 1 || count < -1) {
            unit += "s";
        }
        if (isNow) {
            // start: now; end: this
            if (count > 0) {
                unit += (chinese ? "从现在" : " from now");
            } else {
                unit += (chinese ? "前" : " ago");
                count = -count;
            }
        } else {
            if (count > 0) {
                unit += (chinese ? "后" : " after");
            } else {
                unit += (chinese ? "前" : " before");
                count = -count;
            }
        }
        return count + unit;
    }

    public String format(String pattern) {
        return CACHED_FORMATTER.format(date, pattern);
    }

    public Date toDate() {
        return Date.from(this.date.toInstant());
    }

    public Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(this.zone()));
        calendar.setTime(toDate());
        calendar.setFirstDayOfWeek(this.firstDayOfWeek().getValue() % 7 + 1);
        calendar.setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek());
        return calendar;
    }

    public ZonedDateTime toZonedDateTime() {
        return date;
    }

    public LocalDateTime toLocalDateTime() {
        return date.toLocalDateTime();
    }

    public LocalDate toLocalDate() {
        return date.toLocalDate();
    }

    public LocalTime toLocalTime() {
        return date.toLocalTime();
    }

    public Instant toInstant() {
        return date.toInstant();
    }

    /**
     * 判断当前日期是否与给定的日期相同，即判断两个日期年、月、日是否相同
     *
     * @param o 指定的日期
     * @return {@code true} 相同
     */
    public boolean isSameDay(Zoro o) {
        if (o == null) {
            return false;
        }
        return year() == o.year() && month() == o.month() && dayOfMonth() == o.dayOfMonth();
    }

    public boolean isSameTime(Zoro o) {
        return isSameTime(o, false);
    }

    public boolean isSameTime(Zoro o, boolean containsNano) {
        if (o == null) {
            return false;
        }
        boolean isSame = hour() == o.hour() && minute() == o.minute() && second() == o.second();
        boolean isSameNano = !containsNano || nano() == o.nano();
        return isSame && isSameNano;
    }

    /**
     * 比较两个时间的大小
     *
     * @param o 要比较的执行时间
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     * the specified object.
     */
    @Override
    public int compareTo(@NotNull Zoro o) {
        return compareTo(o, false);
    }

    public int compareTo(@NotNull Zoro o, boolean containsNano) {
        Objects.requireNonNull(o);

        long diffOfTimestamp = this.timestamp() - o.timestamp();
        if (diffOfTimestamp != 0) {
            return diffOfTimestamp > 0 ? 1 : -1;
        }

        return containsNano ? this.nano() - o.nano() : 0;
    }

    @Override
    public String toString() {
        return date.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zoro other = (Zoro) o;

        if (!Objects.equals(date, other.date)) return false;
        return this.firstDayOfWeek().equals(other.firstDayOfWeek())
            && this.minimalDaysInFirstWeek() == other.minimalDaysInFirstWeek();
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public Zoro clone() {
        return create(this);
    }

    private static Zoro from(TemporalAccessor temporal) {
        if (temporal instanceof LocalDateTime) {
            return new Zoro(ZonedDateTime.of((LocalDateTime) temporal, ZoneId.systemDefault()));
        } else if (temporal instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) temporal;
            return new Zoro(zonedDateTime);
        } else if (temporal instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) temporal;
            return new Zoro(offsetDateTime.toZonedDateTime());
        }
        LocalDate localDate;
        LocalTime localTime;
        try {
            localDate = LocalDate.from(temporal);
        } catch (DateTimeException e) {
            localDate = LocalDate.now();
        }
        try {
            localTime = LocalTime.from(temporal);
        } catch (DateTimeException e) {
            localTime = LocalTime.now();
        }
        return new Zoro(ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault()));
    }

    public static String getZodiacSign(int month, int day) {
        if (month <= 0) {
            throw new IllegalArgumentException("month <= 0");
        }
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("day < 1 or day > 31");
        }
        return day < ZODIACS_DAY_ARR[month - 1] ? ZODIACS_SIGN[month - 1] : ZODIACS_SIGN[month];
    }

    public static @Nullable String getChineseZodiacSign(int year) {
        if (year < 1900) {
            return null;
        }
        return CHINESE_ZODIACS_SIGN[(year - 1900) % CHINESE_ZODIACS_SIGN.length];
    }

    public static DateTimeFormatter createCachedFormatter(@NotNull String pattern) {
        Objects.requireNonNull(pattern, "pattern is null");

        return CACHED_FORMATTER.getFormatter(pattern);
    }

}
