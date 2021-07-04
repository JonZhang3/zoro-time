package com.zoro.time;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class Range implements Iterable<Zoro> {

    private final Zoro start;
    private final Zoro end;
    private final DateUnit unit;

    Range(Zoro start, Zoro end, DateUnit unit) {
        this.start = start;
        this.end = end;
        this.unit = unit;
    }

    @NotNull
    @Override
    public Iterator<Zoro> iterator() {
        return new RangeIterator(this.start, this.end, this.unit);
    }

    @Override
    public void forEach(Consumer<? super Zoro> action) {
        Objects.requireNonNull(action);

        for (Zoro t : this) {
            action.accept(t);
        }
    }

    public <T> List<T> forEach(@NotNull Function<? super Zoro, T> function) {
        Objects.requireNonNull(function);

        List<T> list = new LinkedList<>();
        for (Zoro t : this) {
            list.add(function.apply(t));
        }
        return list;
    }

    @Override
    public Spliterator<Zoro> spliterator() {
        throw new UnsupportedOperationException("not support spliterator");
    }

    public static class RangeIterator implements Iterator<Zoro> {

        private final Zoro end;
        private final DateUnit unit;
        private Zoro next;

        RangeIterator(Zoro start, Zoro end, DateUnit unit) {
            this.end = end;
            this.unit = unit;
            this.next = start;
        }

        @Override
        public boolean hasNext() {
            return next.compareTo(end) <= 0;
        }

        @Override
        public Zoro next() {
            Zoro result = next;
            next = add(Zoro.create(next), unit);
            return result;
        }

        private static Zoro add(Zoro zoro, DateUnit unit) {
            switch (unit) {
                case YEAR:
                    return zoro.addYears(1);
                case MONTH:
                    return zoro.addMonths(1);
                case DAY:
                    return zoro.addDays(1);
                case HOUR:
                    return zoro.addHours(1);
                case MINUTE:
                    return zoro.addMinutes(1);
                default:
                    return zoro.addSeconds(1);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("not support remove");
        }
    }

}
