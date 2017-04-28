package com.aol.cyclops.functionaljava.adapter;

import com.aol.cyclops.functionaljava.FJWitness.stream;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;
import cyclops.stream.ReactiveSeq;
import fj.data.Stream;
import lombok.AllArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;



public class StreamAdapter extends AbstractFunctionalAdapter<stream> {



    @Override
    public <T> Iterable<T> toIterable(AnyM<stream, T> t) {
        return ()->stream(t).iterator();
    }

    @Override
    public <T, R> AnyM<stream, R> ap(AnyM<stream,? extends Function<? super T,? extends R>> fn, AnyM<stream, T> apply) {
        Stream<T> f = stream(apply);
        Stream<? extends Function<? super T, ? extends R>> fnF = stream(fn);
        return unitIterable(ReactiveSeq.fromIterable(fnF).zip(f, (a, b) -> a.apply(b)));

    }

    @Override
    public <T> AnyM<stream, T> filter(AnyM<stream, T> t, Predicate<? super T> fn) {
        return anyM(stream(t).filter(a->fn.test(a)));
    }

    <T> Stream<T> stream(AnyM<stream,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<stream, T> empty() {
        return anyM(Stream.nil());
    }

    private <T> AnyM<stream,T> anyM(Stream<T> t){
        return AnyM.ofSeq(t, stream.INSTANCE);
    }



    @Override
    public <T, R> AnyM<stream, R> flatMap(AnyM<stream, T> t,
                                     Function<? super T, ? extends AnyM<stream, ? extends R>> fn) {
        return anyM(stream(t).bind(a->stream((AnyM<stream,R>)fn.apply(a))));

    }

    @Override
    public <T> AnyM<stream, T> unitIterable(Iterable<T> it)  {
        return anyM(Stream.iterableStream(it));
    }

    @Override
    public <T> AnyM<stream, T> unit(T o) {
        return anyM(Stream.stream(o));
    }



}
