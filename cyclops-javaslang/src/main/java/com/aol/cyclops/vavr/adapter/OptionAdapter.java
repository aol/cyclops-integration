package com.aol.cyclops.vavr.adapter;


import com.aol.cyclops.vavr.FromCyclopsReact;
import com.aol.cyclops.vavr.VavrWitness;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import com.google.common.base.Optional;
import cyclops.Optionals;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class OptionAdapter extends AbstractFunctionalAdapter<VavrWitness.option> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<VavrWitness.option, T> t) {
        return Maybe.fromIterable(t);
    }

    @Override
    public <T, R> AnyM<VavrWitness.option, R> ap(AnyM<VavrWitness.option,? extends Function<? super T,? extends R>> fn, AnyM<option, T> apply) {
        Optional<T> f = future(apply);
        Optional<? extends Function<? super T, ? extends R>> fnF = future(fn);
        Optional<R> res = FromCyclopsReact.option(Guava.asMaybe(fnF).combine(Guava.asMaybe(f), (a, b) -> a.apply(b)));
        return Optionals.anyM(res);

    }

    @Override
    public <T> AnyM<VavrWitness.option, T> filter(AnyM<option, T> t, Predicate<? super T> fn) {
        return Optionals.anyM(FromCyclopsReact.option(Guava.asMaybe(future(t)).filter(fn)));
    }

    <T> Optional<T> future(AnyM<option,T> anyM){
        return anyM.unwrap();
    }
    <T> Maybe<T> maybe(AnyM<option,T> anyM){
        return Guava.asMaybe(anyM.unwrap());
    }

    @Override
    public <T> AnyM<option, T> empty() {
        return Optionals.anyM(Optional.absent());
    }



    @Override
    public <T, R> AnyM<option, R> flatMap(AnyM<option, T> t,
                                     Function<? super T, ? extends AnyM<option, ? extends R>> fn) {
        return Optionals.anyM(FromCyclopsReact.option(maybe(t).flatMap(fn.andThen(a-> maybe(a)))));

    }

    @Override
    public <T> AnyM<option, T> unitIterable(Iterable<T> it)  {
        return Optionals.anyM(FromCyclopsReact.option(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<option, T> unit(T o) {
        return Optionals.anyM(Optional.of(o));
    }



}
