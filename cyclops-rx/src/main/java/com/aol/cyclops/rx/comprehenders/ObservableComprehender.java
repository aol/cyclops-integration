package com.aol.cyclops.rx.comprehenders;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import rx.Observable;
import rx.RxReactiveStreams;

public class ObservableComprehender implements Comprehender<Observable> {
    public Class getTargetClass() {
        return Observable.class;
    }

    @Override
    public Object filter(Observable t, Predicate p) {
        return t.filter(a -> p.test(a));
    }

    @Override
    public Object map(Observable t, Function fn) {
        return t.map(a -> fn.apply(a));
    }

    public Observable executeflatMap(Observable t, Function fn) {
        return flatMap(t, input -> unwrapOtherMonadTypes(this, fn.apply(input)));
    }

    @Override
    public Observable flatMap(Observable t, Function fn) {
        return t.flatMap(a -> fn.apply(a));
    }

    @Override
    public boolean instanceOfT(Object apply) {
        return apply instanceof Stream;
    }

    @Override
    public Observable empty() {
        return Observable.empty();
    }

    @Override
    public Observable of(Object o) {
        return Observable.just(o);
    }

    public Object resolveForCrossTypeFlatMap(Comprehender comp, Observable apply) {
        SeqSubscriber sub = SeqSubscriber.subscriber();
        RxReactiveStreams.toPublisher(apply)
                         .subscribe(sub);
        return comp.fromIterator(sub.iterator());
    }

    public static Observable unwrapOtherMonadTypes(Comprehender<Observable> comp, Object apply) {
        if (apply instanceof Observable)
            return (Observable) apply;
        if (apply instanceof Iterable) {
            return Observable.from((Iterable) apply);

        }
        if (apply instanceof BaseStream) {
            return Observable.from(() -> ((BaseStream) apply).iterator());
        }
        return Comprehender.unwrapOtherMonadTypes(comp, apply);

    }

    @Override
    public Observable fromIterator(Iterator o) {
        return Observable.from(() -> o);
    }

}