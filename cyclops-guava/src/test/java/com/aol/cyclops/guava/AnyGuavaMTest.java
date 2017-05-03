package com.aol.cyclops.guava;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import org.junit.Test;


import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class AnyGuavaMTest {

    private String success() {
        return "hello world";

    }

    private String exceptional() {
        throw new RuntimeException();
    }

    @Test
    public void optionalTest() {
        assertThat(ToCyclopsReact.maybe(Optional.of("hello world"))
                        .map(String::toUpperCase).toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionFlatMapTest() {
        assertThat(ToCyclopsReact.maybe(Optional.of("hello world"))
                        .map(String::toUpperCase)
                        .flatMap(a -> AnyM.fromMaybe(Maybe.just(a)))
                        .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionEmptyTest() {
        assertThat(ToCyclopsReact.maybe(Optional.<String> absent())
                        .map(String::toUpperCase)
                        .toLazyCollection(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void streamTest() {
        assertThat(ToCyclopsReact.reactiveSeq(FluentIterable.of(new String[] { "hello world" }))
                        .map(String::toUpperCase)
                        .stream()
                        .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTestJDK() {
        assertThat(ToCyclopsReact.reactiveSeq(FluentIterable.of(new String[] { "hello world" }))
                        .map(String::toUpperCase)
                        .flatMapI(i -> AnyM.fromStream(java.util.stream.Stream.of(i)))
                        .stream()
                        .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }
}
