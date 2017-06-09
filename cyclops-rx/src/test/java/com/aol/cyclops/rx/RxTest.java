package com.aol.cyclops.rx;

import static cyclops.collections.mutable.ListX.listX;
import static cyclops.companion.rx.Observables.reactiveSeq;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.collections.mutable.ListX;
import cyclops.companion.rx.Observables;
import cyclops.stream.Spouts;
import org.junit.Test;


import reactor.core.publisher.Flux;
import rx.Observable;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class RxTest {
    @Test
    public void observableTest(){
        Observable.just(1,2).single().toBlocking().first();
    }
    @Test
    public void asyncList(){

        AtomicBoolean complete = new AtomicBoolean(false);


        Observable<Integer> async =  Observables.fromStream(Spouts.async(Stream.of(100,200,300), Executors.newFixedThreadPool(1)))
                                                .doOnCompleted(()->complete.set(true));

        ListX<Integer> asyncList = listX(reactiveSeq(async))
                                        .map(i->i+1);

        System.out.println("Blocked? " + complete.get());

        System.out.println("First value is "  + asyncList.get(0));

        System.out.println("Blocked? " + complete.get());




    }
    @Test
    public void observable() {
        assertThat(Observables.anyM(Observable.just(1, 2, 3))
                            .toListX(),
                   equalTo(ListX.of(1, 2, 3)));
    }

    @Test
    public void observableFlatMap() {
        assertThat(Observables.anyM(Observable.just(1, 2, 3))
                            .flatMap(a -> Observables.anyM(Observable.just(a + 10)))
                            .toListX(),
                   equalTo(ListX.of(11, 12, 13)));
    }


    @Test
    public void observableComp() {
        Observable<Integer> result = Observables.forEach(Observable.just(10, 20),
                                                                   a -> Observable.<Integer> just(a + 10),
                                                                   (a, b) -> a + b);

        assertThat(result.toList()
                         .toBlocking()
                         .single(),
                   equalTo(ListX.of(30, 50)));

    }



}
