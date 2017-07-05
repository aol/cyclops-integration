package com.aol.cyclops.functionaljava.adapter;

import cyclops.monads.FJ;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.iterableW;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;

import cyclops.stream.ReactiveSeq;
import fj.data.IterableW;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;


public class IterableWAdapter extends AbstractFunctionalAdapter<iterableW> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<iterableW, T> t) {
        return ()->iterableW(t).iterator();
    }

    @Override
    public <T, R> AnyM<iterableW, R> ap(AnyM<iterableW,? extends Function<? super T,? extends R>> fn, AnyM<iterableW, T> apply) {
        IterableW<T> f = iterableW(apply);
        IterableW<? extends Function<? super T, ? extends R>> fnF = iterableW(fn);
        return unitIterable(ReactiveSeq.fromIterable(fnF).zip(f, (a, b) -> a.apply(b)));

    }

    @Override
    public <T> AnyM<iterableW, T> filter(AnyM<iterableW, T> t, Predicate<? super T> fn) {
        return anyM(IterableW.wrap(ReactiveSeq.fromIterable(iterableW(t)).filter(fn)));
    }

    <T> IterableW<T> iterableW(AnyM<iterableW,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<iterableW, T> empty() {
        return anyM(IterableW.wrap(new ArrayList<>()));
    }

    private <T> AnyM<iterableW,T> anyM(IterableW<T> t){
        return AnyM.ofSeq(t, iterableW.INSTANCE);
    }



    @Override
    public <T, R> AnyM<iterableW, R> flatMap(AnyM<iterableW, T> t,
                                     Function<? super T, ? extends AnyM<iterableW, ? extends R>> fn) {
        return anyM(iterableW(t).bind(a->iterableW((AnyM<iterableW,R>)fn.apply(a))));

    }

    @Override
    public <T> AnyM<iterableW, T> unitIterable(Iterable<T> it)  {
        return anyM(IterableW.wrap(it));
    }

    @Override
    public <T> AnyM<iterableW, T> unit(T o) {
        return anyM(IterableW.iterable(o));
    }

    @Override
    public <T, R> AnyM<iterableW, R> map(AnyM<iterableW, T> t, Function<? super T, ? extends R> fn) {
        return FJ.iterableW(iterableW(t).map(x->fn.apply(x)));
    }
}
