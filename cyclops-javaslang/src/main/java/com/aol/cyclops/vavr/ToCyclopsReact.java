package com.aol.cyclops.vavr;

import cyclops.async.Future;
import cyclops.control.Try;
import cyclops.control.Xor;
import javaslang.control.Either;


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

}
