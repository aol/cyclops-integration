package com.aol.cyclops.vavr;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.companion.vavr.Lists;
import cyclops.companion.vavr.Options;
import cyclops.companion.vavr.Streams;
import cyclops.data.tuple.Tuple;
import org.junit.Test;


import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

public class ComprehensionTest {
    @Test
    public void optionalTest() {

        assertTrue(Options.forEach2(Option.of(10), a -> Option.none(), (a, b) -> "failed")
                                     .isEmpty());
    }

    @Test
    public void option2Test() {

        assertThat(Options.forEach2(Option.of(10), a -> Option.<Integer> of(a + 20), (a, b) -> a + b)
                                     .get(),
                   equalTo(40));
    }

    @Test
    public void generate() {

        String s = Lists.forEach2(List.of(1, 2, 3), a -> List.<Integer> of(a + 10), Tuple::tuple)
                                           .toString();

        assertThat(s, equalTo("List((1, 11), (2, 12), (3, 13))"));
    }

    @Test
    public void generateStream() {

        String s = Streams.forEach2(Stream.of(1, 2, 3), a -> Stream.<Integer> of(a + 10), Tuple::tuple)
                                           .toString();

        assertThat(s, equalTo("Stream((1, 11), ?)"));
    }

    @Test
    public void generateListStream() {

        String s = Lists.forEach2(List.of(1, 2, 3), a -> List.<Integer> of(a + 10), Tuple::tuple)
                                           .toString();

        assertThat(s, equalTo("List((1, 11), (2, 12), (3, 13))"));
    }


}
