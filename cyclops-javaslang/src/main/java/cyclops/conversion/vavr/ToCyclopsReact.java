package cyclops.conversion.vavr;

import com.aol.cyclops2.types.Zippable;
import cyclops.async.Future;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;
import javaslang.Lazy;
import javaslang.control.Either;
import javaslang.control.Option;


public class ToCyclopsReact {

    public static <T> Future<T> future(javaslang.concurrent.Future<T> future){
        Future<T> res = Future.future();
        future.onSuccess(v->res.complete(v))
                .onFailure(t->res.completeExceptionally(t));
        return res;
    }

    public static <L,R> Xor<L,R> xor(Either<L,R> either){
        return either.fold(Xor::secondary,Xor::primary);
    }
    public static <T> Try<T,Throwable> toTry(javaslang.control.Try<T> t){
        if(t.isFailure()){
            return Try.failure(t.getCause());
        }
        return Try.success(t.get());
    }

    public static <T> Maybe<T> maybe(Option<T> opt){
        return opt.isDefined() ? Maybe.just(opt.get()) : Maybe.none();
    }
    public static <T> Eval<T> eval(Lazy<T> opt){
        return Eval.later(opt);
    }

}
