package com.aol.cyclops.guava;

import java.util.stream.Stream;

import com.aol.cyclops.types.MonadicValue;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class FromCyclopsReact {

    public static <T> FluentIterable<T> fromSimpleReact(Stream<T> s) {
        return FluentIterable.from(() -> s.iterator());
    }

    public static <T> Optional<T> option(MonadicValue<T> value) {
        return Optional.fromNullable(value.orElse(null));
    }

}
