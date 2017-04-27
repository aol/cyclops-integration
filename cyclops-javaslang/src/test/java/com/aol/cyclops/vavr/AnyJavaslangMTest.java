package com.aol.cyclops.vavr;

import static com.aol.cyclops.vavr.Vavr.traversable;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.LazyReact;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.data.collections.extensions.standard.SortedSetX;
import com.aol.cyclops.types.Functor;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import javaslang.Lazy;
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

    private ReactiveSeq<Data> loadById(int id) {
        return null;
    }

    private Integer add(Integer a, Integer b) {
        return a + b;
    }

    volatile int count = 0;

    @Test
    public void emissionTest() throws InterruptedException {

        Functor<Integer> functor = SortedSetX.of(1, 2);
        Functor<Integer> doubled = functor.map(i -> i * 2);

        // Functor[2]

        Thread.sleep(5000);
        Vavr.traversable(List.of("emit", "one", "word", "per", "second"))
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

        Vavr.ForTraversable.each2(List.of(1, 2, 3), a -> List.range(0, a), this::add);

        Vavr.ForValue.each2(Option.none(), a -> Option.<Integer> some(a + 1), this::add);

        Option.some(1)
              .flatMap(a -> Option.some(a + 1)
                                  .map(b -> add(a, b)));

        Array.of(1, 2, 3, 4)
             .flatMap(i -> new LazyReact().range(i, 10))
             .forEach(System.out::println);

        ReactiveSeq.of(1, 2, 3, 4)
                   .flatMapIterable(i -> Stream.iterate(1, a -> a + 1)
                                               .take(i))
                   .map(i -> i + 2);

    }

    @Test
    public void testToList() {

        SeqSubscriber<Integer> sub = SeqSubscriber.subscriber();
        traversable(List.of(1, 2, 3)).subscribe(sub);
        sub.stream()
           .forEachWithError(System.out::println, System.err::println);

        assertThat(traversable(List.of(1, 2, 3)).toList(), equalTo(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void monadTest() {
        assertThat(Vavr.value(Try.of(this::success))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void tryTest() {
        assertThat(Vavr.tryM(Try.of(this::success))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
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
        assertThat(Vavr.tryM(Try.failure(e))
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList()));

    }

    @Test
    public void whenSuccessFailureProcessDoesNothing() {

        assertThat(Vavr.tryM(Try.success("hello world"))
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("hello world")));

    }

    @Test
    public void tryFlatMapTest() {
        assertThat(Vavr.tryM(Try.of(this::success))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
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
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void eitherLeftTest() {
        assertThat(Vavr.either(Either.<String, String> left("hello world"))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void eitherFlatMapTest() {
        assertThat(Vavr.either(Either.<Object, String> right("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void rightProjectionTest() {
        assertThat(Vavr.right(Either.<Object, String> right("hello world")
                                         .right())
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void rightProjectionLeftTest() {
        assertThat(Vavr.right(Either.<String, String> left("hello world")
                                         .right())
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void rightProjectionFlatMapTest() {
        assertThat(Vavr.right(Either.<Object, String> right("hello world")
                                         .right())
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void leftProjectionTest() {
        assertThat(Vavr.right(Either.<String, String> left("hello world")
                                         .right())
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void leftProjectionLeftTest() {
        assertThat(Vavr.left(Either.<String, String> left("hello world")
                                        .left())
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void leftProjectionLeftFlatMapTest() {
        assertThat(Vavr.left(Either.<String, String> left("hello world")
                                        .left())
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionTest() {
        assertThat(Vavr.option(Option.of("hello world"))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionFlatMapTest() {
        assertThat(Vavr.option(Option.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void optionEmptyTest() {
        assertThat(Vavr.option(Option.<String> none())
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList()));
    }

    @Test
    public void futureTest() {
        assertThat(Vavr.value(Future.of(() -> "hello world"))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void futureFlatMapTest() {
        assertThat(Vavr.value(Future.of(() -> "hello world"))
                            .map(String::toUpperCase)
                            .flatMap(AnyM::ofNullable)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void lazyTest() {
        assertThat(Vavr.value(Lazy.of(() -> "hello world"))
                            .map(String::toUpperCase)
                            .toSequence()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamTest() {
        assertThat(Vavr.traversable(Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void listTest() {
        assertThat(Vavr.traversable(List.of("hello world"))
                            .map(String::toUpperCase)
                            .stream()
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTest() {
        assertThat(Vavr.traversable(Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(i -> Vavr.traversable(List.of(i)))
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void streamFlatMapTestJDK() {
        assertThat(Vavr.traversable(Stream.of("hello world"))
                            .map(String::toUpperCase)
                            .flatMap(i -> AnyM.fromStream(java.util.stream.Stream.of(i)))
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void arrayTest() {
        assertThat(Vavr.traversable(Array.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void charSeqTest() {
        assertThat(Vavr.traversable(CharSeq.of("hello world"))
                            .map(c -> c.toString()
                                       .toUpperCase()
                                       .charAt(0))
                            .join(),
                   equalTo("HELLO WORLD"));
    }

    @Test
    public void hashsetTest() {
        assertThat(Vavr.traversable(HashSet.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void queueTest() {
        assertThat(Vavr.traversable(Queue.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }

    @Test
    public void vectorTest() {
        assertThat(Vavr.traversable(Vector.of("hello world"))
                            .map(String::toUpperCase)
                            .toList(),
                   equalTo(Arrays.asList("HELLO WORLD")));
    }
}
