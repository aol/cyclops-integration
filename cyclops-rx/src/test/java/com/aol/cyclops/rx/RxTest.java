package com.aol.cyclops.rx;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.collections.ListX;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;



import rx.Observable;

public class RxTest {

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
