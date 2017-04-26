package com.aol.cyclops.guava.adapter;

import com.aol.cyclops.guava.FromCyclopsReact;
import com.aol.cyclops.guava.Guava;
import com.aol.cyclops.guava.GuavaWitness;

import com.aol.cyclops.guava.Optionals;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import com.google.common.base.Optional;
import cyclops.async.Future;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import lombok.AllArgsConstructor;

import com.aol.cyclops.guava.GuavaWitness.optional;
import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class OptionalAdapter extends AbstractFunctionalAdapter<optional> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<GuavaWitness.optional, T> t) {
        return Maybe.fromIterable(t);
    }

    @Override
    public <T, R> AnyM<GuavaWitness.optional, R> ap(AnyM<optional,? extends Function<? super T,? extends R>> fn, AnyM<optional, T> apply) {
        Optional<T> f = future(apply);
        Optional<? extends Function<? super T, ? extends R>> fnF = future(fn);
        Optional<R> res = FromCyclopsReact.option(Guava.asMaybe(fnF).combine(Guava.asMaybe(f), (a, b) -> a.apply(b)));
        return Optionals.anyM(res);

    }

    @Override
    public <T> AnyM<optional, T> filter(AnyM<optional, T> t, Predicate<? super T> fn) {
        return Optionals.anyM(FromCyclopsReact.option(Guava.asMaybe(future(t)).filter(fn)));
    }

    <T> Optional<T> future(AnyM<optional,T> anyM){
        return anyM.unwrap();
    }
    <T> Maybe<T> maybe(AnyM<optional,T> anyM){
        return Guava.asMaybe(anyM.unwrap());
    }

    @Override
    public <T> AnyM<optional, T> empty() {
        return Optionals.anyM(Optional.absent());
    }



    @Override
    public <T, R> AnyM<optional, R> flatMap(AnyM<optional, T> t,
                                     Function<? super T, ? extends AnyM<optional, ? extends R>> fn) {
        return Optionals.anyM(FromCyclopsReact.option(maybe(t).flatMap(fn.andThen(a-> maybe(a)))));

    }

    @Override
    public <T> AnyM<optional, T> unitIterable(Iterable<T> it)  {
        return Optionals.anyM(FromCyclopsReact.option(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<optional, T> unit(T o) {
        return Optionals.anyM(Optional.of(o));
    }



}
