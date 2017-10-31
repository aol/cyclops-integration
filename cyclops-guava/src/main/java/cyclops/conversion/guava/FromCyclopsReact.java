package cyclops.conversion.guava;

import java.util.stream.Stream;

import com.oath.cyclops.types.MonadicValue;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import cyclops.reactive.ReactiveSeq;

public class FromCyclopsReact {

    public static <T> FluentIterable<T> fromSimpleReact(Stream<T> s) {
        return FluentIterable.from(() -> s.iterator());
    }

    public static <T> Optional<T> optional(MonadicValue<T> value) {
        return Optional.fromNullable(value.orElse(null));
    }
    public static <T> FluentIterable<T> fromReactiveSeq(ReactiveSeq<T> s) {
        return FluentIterable.from(() -> s.iterator());
    }
}
