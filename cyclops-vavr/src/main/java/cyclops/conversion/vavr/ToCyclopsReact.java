package cyclops.conversion.vavr;

import com.oath.cyclops.types.Zippable;
import cyclops.async.Future;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Either;
import io.vavr.Lazy;

import io.vavr.control.Option;


public class ToCyclopsReact {

    public static <T> Future[] futures(io.vavr.concurrent.Future<T>... futures){

        Future[] array = new Future[futures.length];
        for(int i=0;i<array.length;i++){
            array[i]=future(futures[i]);
        }
        return array;
    }
    public static <T> Future<T> future(io.vavr.concurrent.Future<T> future){
        Future<T> res = Future.future();
        future.onSuccess(v->res.complete(v))
                .onFailure(t->res.completeExceptionally(t));
        return res;
    }

    public static <L,R> cyclops.control.Either<L,R> either(io.vavr.control.Either<L,R> either){
        return either.fold(cyclops.control.Either::left, cyclops.control.Either::right);
    }
    public static <T> Try<T,Throwable> toTry(io.vavr.control.Try<T> t){

        if(t.isFailure()){
            return Try.failure(t.getCause());
        }
        return Try.success(t.get());
    }

    public static <T> Maybe<T> maybe(Option<T> opt){
        return opt.isDefined() ? Maybe.just(opt.get()) : Maybe.nothing();
    }
  public static <T> cyclops.control.Option<T> option(Option<T> opt){
    return opt.isDefined() ? cyclops.control.Option.some(opt.get()) : cyclops.control.Option.none();
  }
    public static <T> Eval<T> eval(Lazy<T> opt){
        return Eval.later(opt);
    }
    public static <R> cyclops.control.LazyEither<Throwable,R> lazyEither(io.vavr.concurrent.Future<R> either){
        return cyclops.control.LazyEither.fromFuture(future(either));

    }

    public static <T> Maybe<T> maybe(io.vavr.concurrent.Future<T> opt){
        return Maybe.fromFuture(future(opt));
    }
    public static <T> Eval<T> eval(io.vavr.concurrent.Future<T> opt){
        return Eval.fromFuture(future(opt));
    }

}
