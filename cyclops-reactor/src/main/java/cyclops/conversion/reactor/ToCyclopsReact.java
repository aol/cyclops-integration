package cyclops.conversion.reactor;

import com.aol.cyclops2.types.reactive.ValueSubscriber;
import cyclops.async.Future;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;

import cyclops.control.lazy.Either;
import reactor.core.publisher.Mono;


public class ToCyclopsReact {

    public static <T> Future[] futures(Mono<T>... futures){

        Future[] array = new Future[futures.length];
        for(int i=0;i<array.length;i++){
            array[i]=future(futures[i]);
        }
        return array;
    }
    public static <T> Future<T> future(Mono<T> future){
        return Future.of(future.toFuture());
    }

    public static <R> Either<Throwable,R> either(Mono<R> either){
        return Either.fromFuture(future(either));

    }

    public static <T> Maybe<T> maybe(Mono<T> opt){
        return Maybe.fromFuture(future(opt));
    }
    public static <T> Eval<T> eval(Mono<T> opt){
        return Eval.fromFuture(future(opt));
    }

}
