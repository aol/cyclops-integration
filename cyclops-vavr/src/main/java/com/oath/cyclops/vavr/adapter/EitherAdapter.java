package com.oath.cyclops.vavr.adapter;


import com.oath.anym.AnyMValue;
import com.oath.anym.extensability.ValueAdapter;

import cyclops.control.Either;
import cyclops.control.Option;
import cyclops.conversion.vavr.FromCyclops;
import cyclops.conversion.vavr.ToCyclops;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness.either;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class EitherAdapter<L> implements ValueAdapter<either> {

    public <T> Option<T> get(AnyMValue<either,T> t){

      io.vavr.control.Either<L, T> e = either(t);
      if(e.isRight())
        return Option.some(e.get());
      return Option.none();
    }

    @Override
    public <T> Iterable<T> toIterable(AnyM<either, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<either, R> ap(AnyM<either,? extends Function<? super T,? extends R>> fn, AnyM<either, T> apply) {
      io.vavr.control.Either<L,T> f = either(apply);
      io.vavr.control.Either<L,? extends Function<? super T, ? extends R>> fnF = either(fn);
      io.vavr.control.Either<L,R> res = FromCyclops.either(ToCyclops.either(fnF).zip(ToCyclops.either(f), (a, b) -> a.apply(b)));
        return Vavr.either(res);

    }

    @Override
    public <T> AnyM<either, T> filter(AnyM<either, T> t, Predicate<? super T> fn) {
        return t;
    }

    <T> io.vavr.control.Either<L,T> either(AnyM<either,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<either, T> empty() {
        return Vavr.either(io.vavr.control.Either.left(null));
    }



    @Override
    public <T, R> AnyM<either, R> flatMap(AnyM<either, T> t,
                                     Function<? super T, ? extends AnyM<either, ? extends R>> fn) {
        return Vavr.either(either(t).flatMap(fn.andThen(a-> either(a))));

    }

    @Override
    public <T> AnyM<either, T> unitIterable(Iterable<T> it)  {

        return Vavr.either(FromCyclops.either(Either.fromIterable(it)));
    }

    @Override
    public <T> AnyM<either, T> unit(T o) {
        return Vavr.either(io.vavr.control.Either.right(o));
    }

    @Override
    public <T, R> AnyM<either, R> map(AnyM<either, T> t, Function<? super T, ? extends R> fn) {
        return Vavr.either(either(t).map(fn));
    }
}
