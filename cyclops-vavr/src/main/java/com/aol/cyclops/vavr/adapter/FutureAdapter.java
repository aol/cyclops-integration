package com.aol.cyclops.vavr.adapter;


import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.conversion.vavr.ToCyclopsReact;
import cyclops.monads.Vavr;

import cyclops.monads.VavrWitness.future;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;

import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class FutureAdapter extends AbstractFunctionalAdapter<future> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<future, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<future, R> ap(AnyM<future,? extends Function<? super T,? extends R>> fn, AnyM<future, T> apply) {
        Future<T> f = future(apply);
        Future<? extends Function<? super T, ? extends R>> fnF = future(fn);
        Future<R> res = FromCyclopsReact.future(ToCyclopsReact.future(fnF).combine(ToCyclopsReact.future(f), (a, b) -> a.apply(b)));
        return Vavr.future(res);

    }

    @Override
    public <T> AnyM<future, T> filter(AnyM<future, T> t, Predicate<? super T> fn) {
        return Vavr.future(future(t).filter(fn));
    }

    <T> Future<T> future(AnyM<future,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<future, T> empty() {

        return Vavr.future(Promise.<T>make().future());
    }



    @Override
    public <T, R> AnyM<future, R> flatMap(AnyM<future, T> t,
                                     Function<? super T, ? extends AnyM<future, ? extends R>> fn) {
        return Vavr.future(future(t).flatMap(fn.andThen(a-> future(a))));

    }

    @Override
    public <T> AnyM<future, T> unitIterable(Iterable<T> it)  {
        return Vavr.future(FromCyclopsReact.future(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<future, T> unit(T o) {
        return Vavr.future(Future.successful(o));
    }



}
