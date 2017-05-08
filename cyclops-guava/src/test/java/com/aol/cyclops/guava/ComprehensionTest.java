package com.aol.cyclops.guava;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CompletableFuture;

import cyclops.collections.ListX;
import org.jooq.lambda.tuple.Tuple;
import org.junit.Test;


import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import javaslang.collection.List;

public class ComprehensionTest {


    private String loadData() {
        return "loaded";
    }

    @Test
    public void optionalTest() {



        assertFalse(Optionals.forEach2(Optional.of(10), a -> Optional.absent(), (a, b) -> "failed")
                                     .isPresent());
    }

    @Test
    public void option2Test() {

        assertThat(Optionals.forEach2(Optional.of(10), a -> Optional.<Integer> of(a + 20), (a, b) -> a + b)
                                    .get(),
                   equalTo(40));
    }

    @Test
    public void generate() {

        String s = FluentIterables.forEach2(FluentIterable.from(ListX.of(1, 2, 3)),
                                                 a -> FluentIterable.<Integer> from(ListX.of(a + 10)), Tuple::tuple)
                                          .toString();

        assertThat(s, equalTo("[(1, 11), (2, 12), (3, 13)]"));
    }
}
