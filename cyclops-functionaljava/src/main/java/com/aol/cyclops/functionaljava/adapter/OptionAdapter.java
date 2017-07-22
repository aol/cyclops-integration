package com.aol.cyclops.functionaljava.adapter;


import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.monads.FJ;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.option;
import cyclops.conversion.functionaljava.FromCyclopsReact;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import fj.data.Option;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class OptionAdapter extends AbstractFunctionalAdapter<option> {


    public <T> T get(AnyMValue<option,T> t){
        return option(t).some();
    }
    @Override
    public <T> Iterable<T> toIterable(AnyM<option, T> t) {
        return Maybe.fromIterable(t.unwrap());
    }

    @Override
    public <T, R> AnyM<option, R> ap(AnyM<option,? extends Function<? super T,? extends R>> fn, AnyM<option, T> apply) {
        Option<T> f = option(apply);
        Option<? extends Function<? super T, ? extends R>> fnF = option(fn);
        Option<R> res = FromCyclopsReact.option(FJ.maybe(fnF).combine(FJ.maybe(f), (a, b) -> a.apply(b)));
        return FJ.option(res);

    }

    @Override
    public <T> AnyM<option, T> filter(AnyM<option, T> t, Predicate<? super T> fn) {
        return FJ.option(option(t).filter(a->fn.test(a)));
    }

    <T> Option<T> option(AnyM<option,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<option, T> empty() {
        return FJ.option(Option.none());
    }



    @Override
    public <T, R> AnyM<option, R> flatMap(AnyM<option, T> t,
                                     Function<? super T, ? extends AnyM<option, ? extends R>> fn) {
        return FJ.option(option(t).bind(a->option((AnyM<option,R>)fn.apply(a))));

    }

    @Override
    public <T> AnyM<option, T> unitIterable(Iterable<T> it)  {
        return FJ.option(FromCyclopsReact.option(Maybe.fromIterable(it)));
    }

    @Override
    public <T> AnyM<option, T> unit(T o) {
        return FJ.option(Option.some(o));
    }

    @Override
    public <T, R> AnyM<option, R> map(AnyM<option, T> t, Function<? super T, ? extends R> fn) {
        return FJ.option(option(t).map(x->fn.apply(x)));
    }
}
