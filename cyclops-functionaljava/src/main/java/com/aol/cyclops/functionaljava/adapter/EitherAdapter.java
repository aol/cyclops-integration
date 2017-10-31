package com.aol.cyclops.functionaljava.adapter;



import com.oath.cyclops.types.anyM.AnyMValue;
import com.oath.cyclops.types.extensability.ValueAdapter;
import cyclops.companion.functionaljava.Eithers;
import cyclops.control.Option;
import cyclops.monads.FJ;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.either;
import cyclops.conversion.functionaljava.FromCyclopsReact;
import cyclops.conversion.functionaljava.ToCyclopsReact;
import com.oath.cyclops.types.extensability.AbstractFunctionalAdapter;

import cyclops.monads.AnyM;

import fj.data.Either;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class EitherAdapter<L> implements ValueAdapter<either> {
    public <T> Option<T> get(AnyMValue<either,T> t){
        return either(t).either(l->Option.none(),Option::some);
    }


    @Override
    public <T> Iterable<T> toIterable(AnyM<either, T> t) {
        return ToCyclopsReact.xor(either(t));
    }

    @Override
    public <T, R> AnyM<either, R> ap(AnyM<either,? extends Function<? super T,? extends R>> fn, AnyM<either, T> apply) {
        Either<L,T> f = either(apply);
        Either<L,? extends Function<? super T, ? extends R>> fnF = either(fn);
        Either<L,R> res = FromCyclopsReact.either(ToCyclopsReact.xor(fnF).zip(ToCyclopsReact.xor(f), (a, b) -> a.apply(b)));
        return FJ.either(res);

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
        return FJ.either(Either.left(null));
    }



    @Override
    public <T, R> AnyM<either, R> flatMap(AnyM<either, T> t,
                                     Function<? super T, ? extends AnyM<either, ? extends R>> fn) {
        return FJ.either(either(t).right().bind(a-> either((AnyM<either,R>)fn.apply(a))));

    }

    @Override
    public <T> AnyM<either, T> unitIterable(Iterable<T> it)  {

        return FJ.either(FromCyclopsReact.either(cyclops.control.Either.fromIterable(it)));
    }

    @Override
    public <T> AnyM<either, T> unit(T o) {
        return FJ.either(Either.right(o));
    }

    @Override
    public <T, R> AnyM<either, R> map(AnyM<either, T> t, Function<? super T, ? extends R> fn) {
        return Eithers.anyM(either(t).bimap(i->i, x->fn.apply(x)));
    }
}
