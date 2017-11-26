package com.oath.cyclops.rx.hkt.typeclasses.instances;
import static com.oath.cyclops.rx.hkt.ObservableKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;

import com.oath.cyclops.rx.hkt.ObservableKind;
import com.oath.cyclops.hkt.Higher;

import cyclops.collections.mutable.ListX;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Lambda;
import cyclops.monads.RxWitness.observable;
import cyclops.monads.Witness.maybe;
import cyclops.reactive.ReactiveSeq;
import org.junit.Test;


import cyclops.companion.rx.Observables;


import rx.Observable;

public class ObservableTest {

    @Test
    public void unit(){

        ObservableKind<String> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){

        ObservableKind<Integer> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Observables.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Observables.Instances.zippingApplicative()
            .ap(widen(Observable.just(l1(this::multiplyByTwo))),widen(Observable.just(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        ObservableKind<Function1<Integer,Integer>> listFn =Observables.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ObservableKind::narrowK);

        ObservableKind<Integer> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Observables.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h->Observables.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ObservableKind<Integer> list  = Observables.Instances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Observable.just(1,2,3)))
                                      .convert(ObservableKind::narrowK);
    }
    @Test
    public void monad(){

        ObservableKind<Integer> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Observables.Instances.monad().flatMap((String v) ->Observables.Instances.unit().unit(v.length()), h))
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        ObservableKind<String> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Observables.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        ObservableKind<String> list = Observables.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Observables.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ObservableKind::narrowK);

        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList()));
    }

    @Test
    public void monadPlus(){
        ObservableKind<Integer> list = Observables.Instances.<Integer>monadPlus()
                                      .plus(ObservableKind.widen(Observable.empty()), ObservableKind.widen(Observable.just(10)))
                                      .convert(ObservableKind::narrowK);
        assertThat(Observables.reactiveSeq(list.narrow()).toList(),equalTo(Arrays.asList(10)));
    }

    @Test
    public void  foldLeft(){
        int sum  = Observables.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, ObservableKind.widen(Observable.just(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Observables.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, ObservableKind.widen(Observable.just(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
        Function<Integer,Higher<maybe,Integer>> fn = (Integer a) -> Maybe.<Integer>just(a * 2);
        Higher<maybe, Higher<observable, Integer>> one = Observables.Instances.traverse()
                .traverseA(Maybe.Instances.applicative(), fn, ObservableKind.just(1, 2, 3));
       Maybe<Higher<observable, Integer>> res = Observables.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), ObservableKind.just(1,2,3))
                                                         .convert(Maybe::narrowK);


       assertThat(res.map(i->Observables.reactiveSeq(ObservableKind.narrow(i)).toList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }


}
