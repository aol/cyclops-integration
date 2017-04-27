package com.aol.cyclops.vavr;

import cyclops.async.Future;


public class ToCyclopsReact {
    public static <T> Future<T> future(javaslang.concurrent.Future<T> future){
        Future<T> res = Future.future();
        future.onSuccess(v->res.complete(v))
                .onFailure(t->res.completeExceptionally(t));
        return res;
    }
}
