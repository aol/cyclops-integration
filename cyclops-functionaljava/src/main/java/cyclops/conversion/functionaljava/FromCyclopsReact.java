package cyclops.conversion.functionaljava;


import com.oath.cyclops.types.MonadicValue;
import cyclops.control.Xor;
import fj.data.Either;
import fj.data.Option;
import fj.data.Validation;

public class FromCyclopsReact {
    public static <T> fj.data.Stream<T> stream(java.util.stream.Stream<T> s) {

        return fj.data.Stream.iteratorStream(s.iterator());
    }

    public static <T> Option<T> option(MonadicValue<T> value) {
        return Option.fromNull(value.orElse(null));
    }

    public static <L, R> Either<L, R> either(Xor<L, R> value) {
        Xor<L, R> xor = (Xor) value.toXor();
        return xor.visit(l -> Either.left(l), r -> Either.right(r));
    }

    public static <L, R> Validation<L, R> validation(Xor<L, R> value) {
        return Validation.validation(either(value));
    }

}
