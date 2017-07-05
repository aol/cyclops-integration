package com.aol.cyclops.functionaljava.adapter;

import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.nonEmptyList;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;
import cyclops.stream.ReactiveSeq;
import fj.data.List;
import fj.data.NonEmptyList;

import java.util.function.Function;
import java.util.function.Predicate;


public class NonEmptyListAdapter extends AbstractFunctionalAdapter<nonEmptyList> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<nonEmptyList, T> t) {
        return ()->stream(t).iterator();
    }

    @Override
    public <T, R> AnyM<nonEmptyList, R> ap(AnyM<nonEmptyList,? extends Function<? super T,? extends R>> fn, AnyM<nonEmptyList, T> apply) {
        NonEmptyList<T> f = stream(apply);
        NonEmptyList<? extends Function<? super T, ? extends R>> fnF = stream(fn);
        return unitIterable(ReactiveSeq.fromIterable(fnF).zip(f, (a, b) -> a.apply(b)));

    }

    @Override
    public <T> AnyM<nonEmptyList, T> filter(AnyM<nonEmptyList, T> t, Predicate<? super T> fn) {
        return t;
    }

    <T> NonEmptyList<T> stream(AnyM<nonEmptyList,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<nonEmptyList, T> empty() {
        throw new IllegalArgumentException("Can't create an Empty NonEmptyList");
    }

    private <T> AnyM<nonEmptyList,T> anyM(NonEmptyList<T> t){
        return AnyM.ofSeq(t, nonEmptyList.INSTANCE);
    }



    @Override
    public <T, R> AnyM<nonEmptyList, R> flatMap(AnyM<nonEmptyList, T> t,
                                     Function<? super T, ? extends AnyM<nonEmptyList, ? extends R>> fn) {
        return anyM(stream(t).bind(a->stream((AnyM<nonEmptyList,R>)fn.apply(a))));

    }

    @Override
    public <T> AnyM<nonEmptyList, T> unitIterable(Iterable<T> it)  {
        return anyM(NonEmptyList.fromList(List.fromIterator(it.iterator())).some());
    }

    @Override
    public <T> AnyM<nonEmptyList, T> unit(T o) {
        return anyM(NonEmptyList.nel(o));
    }

    @Override
    public <T, R> AnyM<nonEmptyList, R> map(AnyM<nonEmptyList, T> t, Function<? super T, ? extends R> fn) {
        return anyM(stream(t).map(x->fn.apply(x)));
    }
}
