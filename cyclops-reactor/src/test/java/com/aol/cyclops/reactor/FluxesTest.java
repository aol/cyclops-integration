package com.aol.cyclops.reactor;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.collections.mutable.ListX;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;



import reactor.core.publisher.Flux;

public class FluxesTest {
    @Test
    public void fluxComp() {
       

        Flux<Tuple2<Integer, Integer>> stream = Fluxs.forEach(Flux.range(1, 10), i -> Flux.range(i, 10), Tuple::tuple);
        Flux<Integer> result = Fluxs.forEach(Flux.just(10, 20), a -> Flux.<Integer> just(a + 10), (a, b) -> a + b);
        assertThat(result.collectList()
                         .block(),
                   equalTo(ListX.of(30, 50)));
    }
    @Test
    public void tupleGen(){
        Fluxs.forEach(Flux.range(1, 10), i -> Flux.range(i, 10), Tuple::tuple)
              .subscribe(System.out::println);
    }
    @Test
    public void tupleGenFilter(){
        Fluxs.forEach(Flux.range(1, 10), i -> Flux.range(i, 10),(a, b) -> a>2 && b<10,Tuple::tuple)
                          .subscribe(System.out::println);
    }
    

}
