package com.oath.cyclops.vavr.collections.extensions;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static cyclops.data.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.PersistentQueueX;
import cyclops.collections.mutable.ListX;
import cyclops.collections.vavr.VavrQueueX;
import cyclops.companion.Reducers;
import cyclops.companion.Semigroups;
import cyclops.control.Option;
import cyclops.reactive.Streamable;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class LazyPQueueXTest extends AbstractCollectionXTest  {


    @Override
    public <T> FluentCollectionX<T> of(T... values) {
    return VavrQueueX.of(values);
    /**
        PersistentQueueX<T> list = VavrQueueX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;
**/
    }

    @Test
    public void concurrentLazyStreamable(){
        Streamable<Integer> repeat = of(1,2,3,4,5,6)
                .map(i->i*2)
                .to().lazyStreamableSynchronized();

        assertThat(repeat.reactiveSeq()
                .toList()
                .size(),equalTo(Arrays.asList(2,4,6,8,10,12).size()));
        assertThat(repeat.reactiveSeq()
                .toList()
                .size(),equalTo(Arrays.asList(2,4,6,8,10,12).size()));
    }
    @Test
    public void sorted() {
        assertThat(of(1,5,3,4,2).sorted().collect(Collectors.toList()).size(),is(Arrays.asList(1,2,3,4,5).size()));
    }
    @Test
    public void streamable(){
        Streamable<Integer> repeat = (of(1,2,3,4,5,6)
                .map(i->i*2)
        )
                .to().streamable();

        assertThat(repeat.reactiveSeq().toList().size(),equalTo(Arrays.asList(2,4,6,8,10,12).size()));
        assertThat(repeat.reactiveSeq().toList().size(),equalTo(Arrays.asList(2,4,6,8,10,12).size()));
    }

    @Test
    public void testScanRightSumMonoid() {
        assertThat(of("a", "ab", "abc").peek(System.out::println)
                .map(str -> str.length())
                .peek(System.out::println)
                .scanRight(Reducers.toTotalInt()).toList().size(), is(4));

    }
    @Test
    public void combineNoOrder(){
        assertThat(of(1,2,3)
                .combine((a, b)->a.equals(b), Semigroups.intSum)
                .to(ReactiveConvertableSequence::converter).listX().size(),equalTo(ListX.of(1,3,2).size()));

    }
    @Test
    public void testScanLeftStringConcatMonoid() {
        assertThat(of("a", "b", "c").scanLeft(Reducers.toString("")).toList().size(), is(asList("", "a", "ab", "abc").size()));
    }
    @Test
    public void testScanLeftSumMonoid() {

        assertThat(of("a", "ab", "abc").map(str -> str.length()).
                peek(System.out::println).scanLeft(Reducers.toTotalInt()).toList().size(), is(asList(0, 6, 3, 2).size()));
    }

    @Test
    public void onEmptySwitch() {
        assertThat(VavrQueueX.empty()
                          .onEmptySwitch(() -> PersistentQueueX.of(1, 2, 3)).toList(),
                   equalTo(PersistentQueueX.of(1, 2, 3).toList()));
    }
    @Test
    public void testSkipLast(){
        assertThat(of(1,2,3,4,5)
                .skipLast(2)
                .to(ReactiveConvertableSequence::converter).listX().size(),equalTo(Arrays.asList(1,2,3).size()));
    }
    @Test
    public void testSorted() {
        CollectionX<Tuple2<Integer, String>> t1 = of(tuple(2, "two"), tuple(1, "one"));

        List<Tuple2<Integer, String>> s1 = t1.sorted().materialize().toList();
        assertEquals(tuple(1, "one"), s1.get(0));
        assertEquals(tuple(2, "two"), s1.get(1));

        CollectionX<Tuple2<Integer, String>> t2 = of(tuple(2, "two"), tuple(1, "one"));
        List<Tuple2<Integer, String>> s2 = t2.sorted(comparing(t -> t._1())).toList();
        assertEquals(tuple(1, "one"), s2.get(0));
        assertEquals(tuple(2, "two"), s2.get(1));

        CollectionX<Tuple2<Integer, String>> t3 = of(tuple(2, "two"), tuple(1, "one"));
        List<Tuple2<Integer, String>> s3 = t3.sorted(t -> t._1()).toList();
        assertEquals(tuple(1, "one"), s3.get(0));
        assertEquals(tuple(2, "two"), s3.get(1));
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * com.oath.cyclops.functions.collections.extensions.AbstractCollectionXTest#
     * empty()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return VavrQueueX.empty();
    }



    @Test
    public void remove() {

        VavrQueueX.of(1, 2, 3)
               .removeAll((Iterable<Integer>)BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  VavrQueueX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return VavrQueueX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return VavrQueueX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return VavrQueueX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return VavrQueueX.unfold(seed, unfolder);
    }

    @Test
    public void takeWhileTest(){

        List<Integer> list = new ArrayList<>();
        while(list.size()==0){
            list = of(1,2,3,4,5,6).takeWhile(it -> it<7)
                    .peek(it -> System.out.println(it)).collect(Collectors.toList());

        }
        assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(list.get(0)));




    }
}
