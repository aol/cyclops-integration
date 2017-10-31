package cyclops.collections.clojure.extension;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import com.oath.cyclops.data.collections.extensions.lazy.immutable.LazyPSetX;
import cyclops.collections.clojure.ClojureHashSetX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.PersistentSetX;
import cyclops.collections.mutable.ListX;
import cyclops.companion.Reducers;
import cyclops.companion.Semigroups;
import cyclops.stream.Streamable;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class LazyPSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        PersistentSetX<T> list = ClojureHashSetX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

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
                .toListX().size(),equalTo(ListX.of(1,3,2).size()));

    }
    @Test
    public void testScanLeftStringConcatMonoid() {
        assertThat(of("a", "b", "c").scanLeft(Reducers.toString("")).toList().size(), is(asList("", "a", "ab", "abc").size()));
    }
    @Test
    public void testScanLeftSumMonoid() {

        assertThat(of("a", "ab", "abc").map(str -> str.length()).
                peek(System.out::println).scanLeft(Reducers.toTotalInt()).toList(), is(asList(0, 6, 3, 2)));
    }
    @Test
    public void forEach2() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), (a, b) -> a + b).size(),
                equalTo(12));
    }



    @Test
    public void onEmptySwitch() {
        assertThat(ClojureHashSetX.empty()
                          .onEmptySwitch(() -> PersistentSetX.of(1, 2, 3)).toList(),
                   hasItems(ClojureHashSetX.of(1, 2, 3).toArray()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.aol.cyclops.functions.collections.extensions.AbstractCollectionXTest#
     * empty()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return ClojureHashSetX.empty();
    }



    @Test
    public void remove() {

        ClojureHashSetX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ClojureHashSetX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojureHashSetX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ClojureHashSetX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ClojureHashSetX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ClojureHashSetX.unfold(seed, unfolder);
    }
    @Test
    public void testTakeRight(){
        assertThat(of(1,2,3,4,5)
                .takeRight(2)
                .stream().collect(Collectors.toList()).size(),equalTo(2));
    }@Test
    public void testSplitAtHead() {
        assertEquals(Optional.empty(), of().headAndTail().headOptional());
        assertEquals(asList(), of().headAndTail().tail().toList());

        assertTrue( of(1).headAndTail().headOptional().isPresent());

    }
    @Test
    public void testLimitLast(){
        assertThat(of(1,2,3,4,5)
                .limitLast(2)
                .stream().collect(Collectors.toList()).size(),equalTo(2));
    }
    @Test
    public void testSkipLast(){
        assertThat(of(1,2,3,4,5)
                .skipLast(2)
                .toListX().size(),equalTo(3));
    }
    @Test
    public void dropRight(){
        assertThat(of(1,2,3).dropRight(1).toList().size(),equalTo(2));
    }
    @Test
    public void endsWith(){
        assertTrue(of(1)
                .endsWithIterable(Arrays.asList(1)));
    }
    public void visit(){

        String res= of(1,2,3).visit((x,xs)-> xs.join(x>2? "hello" : "world"),
                ()->"boo!");

        assertTrue(res.contains("world"));
        assertTrue(res.contains("2"));
        assertTrue(res.contains("3"));
    }
    @Test
    public void getMultpleStream(){
        assertThat(of(1,2,3,4,5).stream().elementAt(2).v2.toList(),hasItems(1,2,3,4,5));
    }
    @Test
    public void endsWithStream(){
        assertTrue(of(1)
                .endsWith(Stream.of(1)));
    }
    @Test
    public void takeWhileTest(){





    }
    @Test
    public void limitWhileTest(){






    }
}
