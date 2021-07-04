package com.zoro.time;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CachedDateTimeFormatter {

    private static final ConcurrentMap<String, DateTimeFormatter> CACHE = new ConcurrentHashMap<>(4, 0.75f, 2);

    static {
        CACHE.put(Zoro.PURE_DATE_PATTERN, DateTimeFormatter.ofPattern(Zoro.PURE_DATE_PATTERN));
        CACHE.put(Zoro.PURE_DATETIME_PATTERN, DateTimeFormatter.ofPattern(Zoro.PURE_DATETIME_PATTERN));
        CACHE.put(Zoro.NORMAL_DATE_PATTERN, DateTimeFormatter.ofPattern(Zoro.NORMAL_DATE_PATTERN));
        CACHE.put(Zoro.NORMAL_DATETIME_PATTERN, DateTimeFormatter.ofPattern(Zoro.NORMAL_DATETIME_PATTERN));
        CACHE.put(Zoro.TIME_PATTERN, DateTimeFormatter.ofPattern(Zoro.TIME_PATTERN));
        CACHE.put(Zoro.NORMAL_TIME_PATTERN, DateTimeFormatter.ofPattern(Zoro.NORMAL_TIME_PATTERN));
    }

    TemporalAccessor parse(CharSequence text, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return formatter.parse(text);
    }

    <T> T parse(CharSequence text, String pattern, TemporalQuery<T> query) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return formatter.parse(text, query);
    }

    String format(TemporalAccessor accessor, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return formatter.format(accessor);
    }

    DateTimeFormatter getFormatter(String pattern) {
        DateTimeFormatter formatter = CACHE.get(pattern);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(pattern);
            formatter = CACHE.putIfAbsent(pattern, formatter);
        }
        return formatter;
    }

}
