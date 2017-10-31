package cyclops.conversion.functionaljava;


import com.oath.cyclops.types.MonadicValue;

import fj.data.Option;
import fj.data.Validation;

public class FromCyclopsReact {
    public static <T> fj.data.Stream<T> stream(java.util.stream.Stream<T> s) {

        return fj.data.Stream.iteratorStream(s.iterator());
    }

    public static <T> Option<T> option(MonadicValue<T> value) {
        return Option.fromNull(value.orElse(null));
    }

    public static <L, R> fj.data.Either<L, R> either(cyclops.control.Either<L, R> value) {

        return value.visit(l -> fj.data.Either.left(l), r -> fj.data.Either.right(r));
    }

    public static <L, R> Validation<L, R> validation(cyclops.control.Either<L, R> value) {
        return Validation.validation(either(value));
    }

}
