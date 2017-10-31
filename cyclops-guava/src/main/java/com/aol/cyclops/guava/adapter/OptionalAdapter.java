package com.aol.cyclops.guava.adapter;

import com.oath.cyclops.types.anyM.AnyMValue;
import com.oath.cyclops.types.extensability.ValueAdapter;
import cyclops.conversion.guava.FromCyclopsReact;
import cyclops.conversion.guava.ToCyclopsReact;
import cyclops.monads.GuavaWitness;

import cyclops.companion.guava.Optionals;
import com.google.common.base.Optional;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import lombok.AllArgsConstructor;

import cyclops.monads.GuavaWitness.optional;
import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class OptionalAdapter implements ValueAdapter<optional> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<GuavaWitness.optional, T> t) {
        return Maybe.fromIterable(t);
    }

    @Override
    public <T, R> AnyM<GuavaWitness.optional, R> ap(AnyM<optional,? extends Function<? super T,? extends R>> fn, AnyM<optional, T> apply) {
        Optional<T> f = optional(apply);
        Optional<? extends Function<? super T, ? extends R>> fnF = optional(fn);
        Optional<R> res = FromCyclopsReact.optional(ToCyclopsReact.maybe(fnF).combine(ToCyclopsReact.maybe(f), (a, b) -> a.apply(b)));
        return Optionals.anyM(res);

    }

    @Override
    public <T> AnyM<optional, T> filter(AnyM<optional, T> t, Predicate<? super T> fn) {
        return Optionals.anyM(FromCyclopsReact.optional(ToCyclopsReact.maybe(optional(t)).filter(fn)));
    }

    <T> Optional<T> optional(AnyM<optional,T> anyM){
        return anyM.unwrap();
    }
    <T> Maybe<T> maybe(AnyM<optional,T> anyM){
        return ToCyclopsReact.maybe(anyM.unwrap());
    }

    @Override
    public <T> AnyM<optional, T> empty() {
        return Optionals.anyM(Optional.absent());
    }



    @Override
    public <T, R> AnyM<optional, R> flatMap(AnyM<optional, T> t,
                                     Function<? super T, ? extends AnyM<optional, ? extends R>> fn) {
        return Optionals.anyM(FromCyclopsReact.optional(maybe(t).flatMap(fn.andThen(a-> maybe(a)))));

    }

    @Override
    public <T> AnyM<optional, T> unitIterable(Iterable<T> it)  {
        return Optionals.anyM(FromCyclopsReact.optional(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<optional, T> unit(T o) {
        return Optionals.anyM(Optional.of(o));
    }

    @Override
    public <T, R> AnyM<optional, R> map(AnyM<optional, T> t, Function<? super T, ? extends R> fn) {
        return Optionals.anyM(optional(t).transform(x->fn.apply(x)));
    }

    @Override
    public <T> T get(AnyMValue<optional, T> t) {
        return optional(t).get();
    }
}
