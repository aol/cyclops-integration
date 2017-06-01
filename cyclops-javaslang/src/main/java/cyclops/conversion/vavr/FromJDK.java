package cyclops.conversion.vavr;

import java.util.function.BiFunction;
import java.util.function.Function;



import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

public class FromJDK<T, R> {

    public static <T, R> Function1<T, R> f1(Function<T, R> fn) {
        return (t) -> fn.apply(t);
    }

    public static <T, X, R> Function2<T, X, R> f2(BiFunction<T, X, R> fn) {
        return (t, x) -> fn.apply(t, x);
    }

    public static <T> Option<T> option(java.util.Optional<T> o) {
        return Option.of(o.orElse(null));
    }

    public static <T> Stream<T> stream(java.util.stream.Stream<T> stream) {
        return Stream.ofAll(() -> stream.iterator());
    }

}
