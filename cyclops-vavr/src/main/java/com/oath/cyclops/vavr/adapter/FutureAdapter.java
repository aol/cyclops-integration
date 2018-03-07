package com.oath.cyclops.vavr.adapter;


import com.oath.anym.AnyMValue;
import com.oath.anym.extensability.ValueAdapter;

import cyclops.conversion.vavr.FromCyclops;
import cyclops.conversion.vavr.ToCyclops;
import cyclops.monads.Vavr;

import cyclops.monads.VavrWitness.future;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;

import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class FutureAdapter implements ValueAdapter<future> {


    @Override
    public <T> cyclops.control.Option<T> get(AnyMValue<future,T> t){
       return ToCyclops.option(future(t).getValue().flatMap(tr -> tr.isFailure() ? io.vavr.control.Option.<T>none() : io.vavr.control.Option.some(tr.get())));
    }
    @Override
    public <T> Iterable<T> toIterable(AnyM<future, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<future, R> ap(AnyM<future,? extends Function<? super T,? extends R>> fn, AnyM<future, T> apply) {
        Future<T> f = future(apply);
        Future<? extends Function<? super T, ? extends R>> fnF = future(fn);
        Future<R> res = FromCyclops.future(ToCyclops.future(fnF).combine(ToCyclops.future(f), (a, b) -> a.apply(b)));
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
        return Vavr.future(FromCyclops.future(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<future, T> unit(T o) {
        return Vavr.future(Future.successful(o));
    }

    @Override
    public <T, R> AnyM<future, R> map(AnyM<future, T> t, Function<? super T, ? extends R> fn) {
        return Vavr.future(future(t).map(fn));
    }
}
