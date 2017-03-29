package com.aol.cyclops.reactor;


import static java.util.stream.Stream.concat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import cyclops.collections.ListX;
import cyclops.collections.SetX;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;



import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorTest {

    static class LinkedList{
        
    }
    @Test
    public void amb() {

        Stream<List<Integer>> stream = Stream.of(Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30));

        SetX.fromPublisher(Flux.firstEmitting(ReactiveSeq.of(1, 2, 3), Flux.just(10, 20, 30)));
    }


}
