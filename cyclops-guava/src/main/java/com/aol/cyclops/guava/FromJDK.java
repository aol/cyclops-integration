package com.aol.cyclops.guava;

import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class FromJDK<T, R> {

    public static <T, R> com.google.common.base.Function<T, R> f1(Function<T, R> fn) {
        return (t) -> fn.apply(t);
    }

    public static <T> Optional<T> option(java.util.Optional<T> o) {
        if (o.isPresent())
            return Optional.of(o.get());
        return Optional.absent();

    }
    public static <T> FluentIterable<T> fromStream(Stream<T> s) {
        return FluentIterable.from(() -> s.iterator());
    }
    public static <T> FluentIterable<T> fromIterable(Iterable<T> s) {
        return FluentIterable.from(() -> s.iterator());
    }

}
