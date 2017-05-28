package com.aol.cyclops.rx.adapter;


import cyclops.companion.rx.Observables;
import cyclops.monads.RxWitness.obsvervable;
import com.aol.cyclops2.types.extensability.AbstractFunctionalAdapter;
import cyclops.monads.AnyM;
import lombok.AllArgsConstructor;
import rx.Observable;


import java.util.function.Function;
import java.util.function.Predicate;


@AllArgsConstructor
public class ObservableAdapter extends AbstractFunctionalAdapter<obsvervable> {





    @Override
    public <T> Iterable<T> toIterable(AnyM<obsvervable, T> t) {
        return ()-> observable(t).toBlocking().toIterable().iterator();
    }

    @Override
    public <T, R> AnyM<obsvervable, R> ap(AnyM<obsvervable,? extends Function<? super T,? extends R>> fn, AnyM<obsvervable, T> apply) {
        Observable<T> f = observable(apply);
        Observable<? extends Function<? super T, ? extends R>> fnF = observable(fn);
        Observable<R> res = fnF.zipWith(f, (a, b) -> a.apply(b));
        return Observables.anyM(res);

    }

    @Override
    public <T> AnyM<obsvervable, T> filter(AnyM<obsvervable, T> t, Predicate<? super T> fn) {
        return Observables.anyM(observable(t).filter(e->fn.test(e)));
    }

    <T> Observable<T> observable(AnyM<obsvervable,T> anyM){
        return anyM.unwrap();
    }

    @Override
    public <T> AnyM<obsvervable, T> empty() {
        return Observables.anyM(Observable.empty());
    }



    @Override
    public <T, R> AnyM<obsvervable, R> flatMap(AnyM<obsvervable, T> t,
                                     Function<? super T, ? extends AnyM<obsvervable, ? extends R>> fn) {
        return Observables.anyM(observable(t).flatMap(x->observable(fn.apply(x))));

    }

    @Override
    public <T> AnyM<obsvervable, T> unitIterable(Iterable<T> it)  {
        return Observables.anyM(Observable.from(it));
    }

    @Override
    public <T> AnyM<obsvervable, T> unit(T o) {
        return Observables.anyM(Observable.just(o));
    }



}
