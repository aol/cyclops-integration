package com.aol.cyclops.vavr.adapter;

import com.aol.cyclops.vavr.Vavr;
import com.aol.cyclops.vavr.VavrWitness;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;
import cyclops.stream.ReactiveSeq;
import javaslang.collection.Traversable;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class TraversableAdapter<W extends VavrWitness.TraversableWitness<W>> extends AbstractFunctionalAdapter<W> {





    @Override
    public <T> Iterable<T> toIterable(AnyM<W, T> t) {
        return ()->stream(t).iterator();
    }

    @Override
    public <T, R> AnyM<W, R> ap(AnyM<W,? extends Function<? super T,? extends R>> fn, AnyM<W, T> apply) {
        Traversable<T> f = stream(apply);
        Traversable<? extends Function<? super T, ? extends R>> fnF = stream(fn);
        Traversable<R> res = Traversable.from(ReactiveSeq.fromIterable(fnF).zip(f, (a, b) -> a.apply(b)));
        return Vavr.anyM(res);

    }

    @Override
    public <T> AnyM<W, T> filter(AnyM<W, T> t, Predicate<? super T> fn) {
        return Vavr.anyM(stream(t).filter(fn));
    }

    <T> Traversable<T> stream(AnyM<W,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<W, T> empty() {
        return Vavr.anyM(Traversable.of());
    }



    @Override
    public <T, R> AnyM<W, R> flatMap(AnyM<W, T> t,
                                     Function<? super T, ? extends AnyM<W, ? extends R>> fn) {
        return Vavr.anyM(stream(t).flatMap(a->stream(fn.apply(a))));

    }

    @Override
    public <T> AnyM<W, T> unitIterable(Iterable<T> it)  {
        return Vavr.anyM(Traversable.from(it));
    }

    @Override
    public <T> AnyM<W, T> unit(T o) {
        return Vavr.anyM(Traversable.of(o));
    }



}
