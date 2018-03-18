package com.oath.cyclops.vavr.adapter;


import com.oath.cyclops.anym.AnyMValue;
import com.oath.cyclops.anym.extensability.ValueAdapter;
import cyclops.conversion.vavr.FromCyclops;
import cyclops.conversion.vavr.ToCyclops;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness.option;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class OptionAdapter implements ValueAdapter<option> {


    public <T> cyclops.control.Option<T> get(AnyMValue<option,T> t){
        return ToCyclops.option(option(t));
    }
    @Override
    public <T> Iterable<T> toIterable(AnyM<option, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<option, R> ap(AnyM<option,? extends Function<? super T,? extends R>> fn, AnyM<option, T> apply) {
        Option<T> f = option(apply);
        Option<? extends Function<? super T, ? extends R>> fnF = option(fn);
        Option<R> res = FromCyclops.option(ToCyclops.maybe(fnF).zip(ToCyclops.maybe(f), (a, b) -> a.apply(b)));
        return Vavr.option(res);

    }

    @Override
    public <T> AnyM<option, T> filter(AnyM<option, T> t, Predicate<? super T> fn) {
        return Vavr.option(option(t).filter(fn));
    }

    <T> Option<T> option(AnyM<option,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<option, T> empty() {
        return Vavr.option(Option.none());
    }



    @Override
    public <T, R> AnyM<option, R> flatMap(AnyM<option, T> t,
                                     Function<? super T, ? extends AnyM<option, ? extends R>> fn) {
        return Vavr.option(option(t).flatMap(fn.andThen(a-> option(a))));

    }

    @Override
    public <T> AnyM<option, T> unitIterable(Iterable<T> it)  {
        return Vavr.option(FromCyclops.option(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<option, T> unit(T o) {
        return Vavr.option(Option.of(o));
    }


    @Override
    public <T, R> AnyM<option, R> map(AnyM<option, T> t, Function<? super T, ? extends R> fn) {
        return Vavr.option(option(t).map(fn));
    }
}
