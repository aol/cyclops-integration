package com.aol.cyclops.rx2.adapter;

import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;

import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.flowable;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class FlowableAdapter extends AbstractFunctionalAdapter<flowable> {





    @Override
    public <T> Iterable<T> toIterable(AnyM<flowable, T> t) {
        return ()->stream(t).toIterable().iterator();
    }

    @Override
    public <T, R> AnyM<flowable, R> ap(AnyM<flowable,? extends Function<? super T,? extends R>> fn, AnyM<flowable, T> apply) {
        Flux<T> f = stream(apply);
        Flux<? extends Function<? super T, ? extends R>> fnF = stream(fn);
        Flux<R> res = fnF.zipWith(f, (a, b) -> a.apply(b));
        return Fluxs.anyM(res);

    }

    @Override
    public <T> AnyM<flowable, T> filter(AnyM<flowable, T> t, Predicate<? super T> fn) {
        return Fluxs.anyM(stream(t).filter(fn));
    }

    <T> Flux<T> stream(AnyM<flowable,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<flowable, T> empty() {
        return Fluxs.anyM(Flux.empty());
    }



    @Override
    public <T, R> AnyM<flowable, R> flatMap(AnyM<flowable, T> t,
                                     Function<? super T, ? extends AnyM<flowable, ? extends R>> fn) {
        return Fluxs.anyM(stream(t).flatMap(fn.andThen(a->stream(a))));

    }

    @Override
    public <T> AnyM<flowable, T> unitIterable(Iterable<T> it)  {
        return Fluxs.anyM(Flux.fromIterable(it));
    }

    @Override
    public <T> AnyM<flowable, T> unit(T o) {
        return Fluxs.anyM(Flux.just(o));
    }

    @Override
    public <T> ReactiveSeq<T> toStream(AnyM<flowable, T> t) {
        return ReactiveSeq.fromPublisher(Rx2Witness.flowable(t));
    }
}
