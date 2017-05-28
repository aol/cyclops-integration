package com.aol.cyclops.functionaljava;

import static com.aol.cyclops.functionaljava.FJ.stream;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cyclops.companion.functionaljava.Options;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;


import fj.data.Either;
import fj.data.IterableW;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;

public class AnyFunctionalJavaMTest {
    @Rule
    public final SystemOutRule sout = new SystemOutRule().enableLog();
    private static final String SEP = System.getProperty("line.separator");

    private String success() {
        return "hello world";

    }

    private String exceptional() {

        throw new RuntimeException();
    }

    @Test
    public void streamSchedule() {
        Executor ex;

        stream(Stream.stream(1, 2, 3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
                                      .connect()
                                      .forEach(System.out::println);
    }

    @Test
    @Ignore
    public void flatMapCrossTypeNotCollectionUnwrap() {
        assertThat(FJ.option(Option.some(1))
                     .flatMap(i -> Options.anyM(Option.some(i + 2)))
                     .unwrap(),
                   equalTo(Option.some(Arrays.asList(3))));
    }

    @Test
    @Ignore
    public void flatMapCrossTypeNotCollection() {

        assertThat(FJ.option(Option.some(1))
                        .flatMap(i -> Options.anyM(Option.some(i + 2)))
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList(Arrays.asList(3))));
    }

    @Test
    public void eitherTest() {
        assertThat(FJ.either(Either.right("hello world"))
                     .map(String::toUpperCase)
                     .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void eitherLeftTest() {
        assertThat(FJ.either(Either.<String, String> left("hello world"))
                     .map(String::toUpperCase)
                     .toLazyCollection(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void eitherFlatMapTest() {
        assertThat(FJ.either(Either.right("hello world"))
                     .map(String::toUpperCase)
                     .flatMap(a -> FJ.option(Option.some(a)))
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }



    @Test
    public void optionTest() {
        assertThat(FJ.option(Option.some("hello world"))
                     .map(String::toUpperCase)
                     .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionFlatMapTest() {
        assertThat(FJ.option(Option.some("hello world"))
                     .map(String::toUpperCase)
                     .flatMap(a -> FJ.option(Option.some(a)))
                     .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionEmptyTest() {
        assertThat(FJ.option(Option.<String> none())
                     .map(String::toUpperCase)
                     .toLazyCollection(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void streamTest() {
        assertThat(FJ.stream(Stream.stream("hello world"))
                     .map(String::toUpperCase)
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void listTest() {
        assertThat(FJ.list(List.list("hello world"))
                     .map(String::toUpperCase)
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void iterableWTest() {
        assertThat(FJ.iterableW(IterableW.wrap(Arrays.asList("hello world")))
                     .map(String::toUpperCase)
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTest() {
        assertThat(FJ.stream(Stream.stream("hello world"))
                     .map(String::toUpperCase)
                     .flatMap(i -> FJ.stream(Stream.stream(i)))
                     .stream()
                     .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }


    public String finalStage() {
        return "hello world";
    }


    @Test
    public void validateTest() {
        assertThat(FJ.validation(Validation.success(success()))
                     .map(String::toUpperCase)
                     .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test // (expected=vavr.control.Failure.NonFatal.class)
    public void validationTestFailure() {

        FJ.validation(Validation.fail(new RuntimeException()))
                .toLazyCollection()
          .forEach(System.out::println);

    }

    @Test
    public void validateTestFailureProcess() {

        Exception e = new RuntimeException();
        assertThat(FJ.validation(Validation.fail(e))
                        .toLazyCollection(),
                   equalTo(Arrays.asList()));

    }

    @Test
    public void tryFlatMapTest() {
        assertThat(FJ.validation(Validation.success(success()))
                     .map(String::toUpperCase)
                     .flatMap(a -> FJ.option(Option.some(a)))
                        .toLazyCollection(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }



}
