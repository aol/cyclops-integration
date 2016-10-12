package com.aol.cyclops.reactor;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.data.collections.extensions.standard.ListX;

import reactor.core.publisher.Flux;

public class FluxesTest {
    @Test
    public void fluxComp() {

        Fluxes.forEach(Flux.range(1, 10), i -> Flux.range(i, 10), Tuple::tuple)
              .subscribe(System.out::println);

        Flux<Tuple2<Integer, Integer>> stream = Fluxes.forEach(Flux.range(1, 10), i -> Flux.range(i, 10), Tuple::tuple);
        Flux<Integer> result = Fluxes.forEach(Flux.just(10, 20), a -> Flux.<Integer> just(a + 10), (a, b) -> a + b);
        assertThat(result.collectList()
                         .block(),
                   equalTo(ListX.of(30, 50)));
    }
}
