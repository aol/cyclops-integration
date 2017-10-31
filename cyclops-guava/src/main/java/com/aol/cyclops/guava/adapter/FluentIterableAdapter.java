package com.aol.cyclops.guava.adapter;

import cyclops.companion.guava.FluentIterables;

import cyclops.monads.GuavaWitness;
import cyclops.monads.GuavaWitness.fluentIterable;
import com.oath.cyclops.types.extensability.AbstractFunctionalAdapter;
import com.google.common.collect.FluentIterable;
import cyclops.monads.AnyM;
import cyclops.reactive.ReactiveSeq;
import lombok.AllArgsConstructor;


import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class FluentIterableAdapter extends AbstractFunctionalAdapter<fluentIterable> {





    @Override
    public <T> Iterable<T> toIterable(AnyM<fluentIterable, T> t) {
        return ()->stream(t).iterator();
    }

    @Override
    public <T, R> AnyM<fluentIterable, R> ap(AnyM<fluentIterable,? extends Function<? super T,? extends R>> fn, AnyM<fluentIterable, T> apply) {
        FluentIterable<T> f = stream(apply);
        FluentIterable<? extends Function<? super T, ? extends R>> fnF = stream(fn);
        FluentIterable<R> res = FluentIterable.from(ReactiveSeq.fromIterable(fnF).zip(f, (a, b) -> a.apply(b)));
        return FluentIterables.anyM(res);

    }

    @Override
    public <T> AnyM<fluentIterable, T> filter(AnyM<fluentIterable, T> t, Predicate<? super T> fn) {
        return FluentIterables.anyM(stream(t).filter(a->fn.test(a)));
    }

    <T> FluentIterable<T> stream(AnyM<fluentIterable,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<fluentIterable, T> empty() {
        return FluentIterables.anyM(FluentIterable.of());
    }



    @Override
    public <T, R> AnyM<fluentIterable, R> flatMap(AnyM<fluentIterable, T> t,
                                     Function<? super T, ? extends AnyM<fluentIterable, ? extends R>> fn) {
        return FluentIterables.anyM(stream(t).transformAndConcat(a->stream(fn.apply(a))));

    }

    @Override
    public <T> AnyM<fluentIterable, T> unitIterable(Iterable<T> it)  {
        return FluentIterables.anyM(FluentIterable.from(it));
    }

    @Override
    public <T> AnyM<fluentIterable, T> unit(T o) {
        return FluentIterables.anyM(FluentIterable.of(o));
    }

    @Override
    public <T, R> AnyM<fluentIterable, R> map(AnyM<fluentIterable, T> t, Function<? super T, ? extends R> fn) {
        return FluentIterables.anyM(stream(t).transform(x->fn.apply(x)));
    }
}
