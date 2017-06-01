package cyclops.conversion.vavr;

import java.util.stream.Stream;


import com.aol.cyclops2.types.MonadicValue;
import com.aol.cyclops2.types.Value;
import cyclops.control.Eval;
import cyclops.control.Xor;
import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;


public class FromCyclopsReact {
    public static <T> Lazy<T> eval(Eval<T> opt){
        return Lazy.of(opt);
    }
    public static <T> Future<T> future(cyclops.async.Future<T> future){
        Promise<T> result =  Promise.make();
        
        future.peek(n->result.complete(Try.success(n)));
        return result.future();
    }
    public static <T> io.vavr.collection.Stream<T> fromStream(Stream<T> s) {
        return io.vavr.collection.Stream.ofAll(() -> s.iterator());
    }

    public static <T> Try<T> toTry(MonadicValue<T> value) {
        return value.toTry()
                    .visit(s -> Try.success(s), f -> Try.failure(f));

    }

    public static <T> Future<T> future(MonadicValue<T> value) {
        return Future.of(() -> value.get());
    }
    public static <T> Lazy<T> lazy(Value<T> value){
        return Lazy.of(value);
    }

    public static <T> Option<T> option(Value<T> value){
        return value.visit(Option::some, Option::none);
     }
    
    
    public static <L, R> Either<L, R> either(Xor<L, R> value) {
        Xor<L, R> xor = (Xor) value.toXor();
        return xor.visit(l -> Either.left(l), r -> Either.right(r));
    }

    public static <L, R> Validation<L, R> validation(Xor<L, R> value) {
        return Validation.fromEither(either(value));
    }

}
