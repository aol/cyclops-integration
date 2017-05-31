package com.aol.cyclops.vavr;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.aol.cyclops2.types.functor.Transformable;
import cyclops.async.LazyReact;

import cyclops.collections.mutable.ListX;
import cyclops.collections.mutable.SortedSetX;
import cyclops.companion.vavr.Lists;
import cyclops.companion.vavr.Options;
import cyclops.companion.vavr.Trys;
import cyclops.control.Eval;
import cyclops.monads.AnyM;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness;
import cyclops.stream.ReactiveSeq;
import org.junit.Test;


import javaslang.collection.Array;
import javaslang.collection.CharSeq;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Queue;
import javaslang.collection.Stream;
import javaslang.collection.Vector;
import javaslang.concurrent.Future;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;
import lombok.Data;

public class AnyJavaslangMTest {

    public static final Function<AnyM<?, String>, java.util.List<String>> ANY_M_LIST_FUNCTION = e -> e.stream().collect(Collectors.toList());

    private ReactiveSeq<Data> loadById(int id) {
        return null;
    }

    private Integer add(Integer a, Integer b) {
        return a + b;
    }

    volatile int count = 0;

    @Test
    public void emissionTest() throws InterruptedException {

        Transformable<Integer> functor = SortedSetX.of(1, 2);
        Transformable<Integer> doubled = functor.map(i -> i * 2);

        // Functor[2]

        Thread.sleep(5000);
        Vavr.list(List.of("emit", "one", "word", "per", "second"))
                 .schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
                 .connect()
                 .map(s -> System.currentTimeMillis() / 1000 + " : " + s)
                 .printOut();

    }

    @Test
    public void arrayFlatMap() {

        Array.of(1, 2)
             .flatMap(i -> new LazyReact().range(i, 4))
             .forEach(System.out::println);
    }

    @Test
    public void listFlatMap() {

        ListX.of(1, 2)
             .flatMap(i -> Array.range(i, 4))
             .forEach(System.out::println);

    }

    @Test
    public void javaslangCyclops() {

        Lists.forEach2(List.of(1, 2, 3), a -> List.range(0, a), this::add);

        Options.forEach2(Option.none(), a -> Option.<Integer> some(a + 1), this::add);

        Option.some(1)
              .flatMap(a -> Option.some(a + 1)
                                  .map(b -> add(a, b)));

        Array.of(1, 2, 3, 4)
             .flatMap(i -> new LazyReact().range(i, 10))
             .forEach(System.out::println);

        ReactiveSeq.of(1, 2, 3, 4)
                   .flatMapI(i -> Stream.iterate(1, a -> a + 1)
                                               .take(i))
                   .map(i -> i + 2);

    }


    @Test
    public void monadTest() {
        assertThat(Trys.anyM(Try.of(this::success))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),

                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    
    @Test
    public void tryTest() {
        
        assertThat(Vavr.tryM(Try.of(this::success))
                            .map(String::toUpperCase)
                           .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void tryTestFailure() {

        Vavr.tryM(Try.failure(new RuntimeException()))
                 .stream()
                 .forEach(System.out::println);

    }

    @Test
    public void tryTestFailureProcess() {

        Exception e = new RuntimeException();
        Vavr.tryM(Try.failure(e));
        System.out.println("hello!");
        Vavr.tryM(Try.failure(e)).printOut();
        assertThat(Vavr.tryM(Try.failure(e))
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList()));

    }

    @Test
    public void whenSuccessFailureProcessDoesNothing() {

        assertThat(Vavr.tryM(Try.success("hello world"))

                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("hello world")));

    }

    @Test
    public void tryFlatMapTest() {
        assertThat(Vavr.tryM(Try.of(this::success))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)

                            .to(e->e.stream().collect(Collectors.toList())),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    private String success() {
        return "hello world";

    }

    private String exceptional() {

        throw new RuntimeException();
    }

    @Test
    public void eitherTest() {
        assertThat(Vavr.either(Either.right("hello world"))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void eitherLeftTest() {
        assertThat(Vavr.either(Either.<String, String> left("hello world"))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void eitherFlatMapTest() {
        assertThat(Vavr.either(Either.<Object, String> right("hello world"))
                            .map(String::toUpperCase)

                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }



    @Test
    public void optionTest() {
        assertThat(Vavr.option(Option.of("hello world"))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionFlatMapTest() {
        assertThat(Vavr.option(Option.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .to(e->e.stream().collect(Collectors.toList())),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionEmptyTest() {
        assertThat(Vavr.option(Option.<String> none())
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void futureTest() {
        assertThat(Vavr.future(Future.of(() -> "hello world"))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void futureFlatMapTest() {
        assertThat(Vavr.future(Future.of(() -> "hello world"))
                            .map(String::toUpperCase)
                            .to(ANY_M_LIST_FUNCTION),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }



    @Test
    public void streamTest() {
        assertThat(Vavr.stream(javaslang.collection.Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void listTest() {
        assertThat(Vavr.list(List.of("hello world"))
                            .map(String::toUpperCase)
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTest() {
        assertThat(Vavr.stream(Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(i -> Vavr.stream(Stream.of(i)))
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTestJDK() {
        assertThat(Vavr.stream(Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(i -> Vavr.stream(Stream.of(i)))
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void arrayTest() {

        assertThat(Vavr.array(Array.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void charSeqTest() {
        assertThat(Vavr.charSeq(CharSeq.of("hello world"))
                            .map(c -> c.toString()
                                       .toUpperCase()
                                       .charAt(0))
                            .join(),
                   equalTo("HELLO WORLD"));
    }

    @Test
    public void hashsetTest() {
        assertThat(Vavr.hashSet(HashSet.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void queueTest() {
        assertThat(Vavr.queue(Queue.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void vectorTest() {
        assertThat(Vavr.vector(Vector.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }
}
