package com.aol.cyclops.vavr.adapter;


import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.conversion.vavr.ToCyclopsReact;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness.either;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.control.Maybe;
import cyclops.control.Xor;
import cyclops.monads.AnyM;
import javaslang.control.Either;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class EitherAdapter<L> extends AbstractFunctionalAdapter<either> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<either, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<either, R> ap(AnyM<either,? extends Function<? super T,? extends R>> fn, AnyM<either, T> apply) {
        Either<L,T> f = either(apply);
        Either<L,? extends Function<? super T, ? extends R>> fnF = either(fn);
        Either<L,R> res = FromCyclopsReact.either(ToCyclopsReact.xor(fnF).combine(ToCyclopsReact.xor(f), (a, b) -> a.apply(b)));
        return Vavr.either(res);

    }

    @Override
    public <T> AnyM<either, T> filter(AnyM<either, T> t, Predicate<? super T> fn) {
        return t;
    }

    <T> Either<L,T> either(AnyM<either,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<either, T> empty() {
        return Vavr.either(Either.left(null));
    }



    @Override
    public <T, R> AnyM<either, R> flatMap(AnyM<either, T> t,
                                     Function<? super T, ? extends AnyM<either, ? extends R>> fn) {
        return Vavr.either(either(t).flatMap(fn.andThen(a-> either(a))));

    }

    @Override
    public <T> AnyM<either, T> unitIterable(Iterable<T> it)  {

        return Vavr.either(FromCyclopsReact.either(Xor.fromIterable(it)));
    }

    @Override
    public <T> AnyM<either, T> unit(T o) {
        return Vavr.either(Either.right(o));
    }



}
