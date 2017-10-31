package cyclops.collections.clojure.extension;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cyclops.collections.clojure.ClojureQueueX;
import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import com.oath.cyclops.data.collections.extensions.lazy.immutable.LazyPersistentSortedSetX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.OrderedSetX;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;

import cyclops.collections.clojure.ClojureTreeSetX;


import reactor.core.publisher.Flux;

public class LazyPersistentSortedSetXTest extends AbstractCollectionXTest  {


    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        ClojureQueueX.of(1,2,3).printOut();
        OrderedSetX<T> list = (LazyPersistentSortedSetX) ClojureTreeSetX.empty((Comparator)Comparator.naturalOrder());
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }
    @Test
    public void forEach2() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), (a, b) -> a + b).size(),
                equalTo(12));
    }
    @Test
    public void onEmptySwitch() {
        assertThat((LazyPersistentSortedSetX) ClojureTreeSetX.empty((Comparator)Comparator.naturalOrder())
                          .onEmptySwitch(() -> (LazyPersistentSortedSetX) ClojureTreeSetX.of((Comparator)Comparator.naturalOrder(),1, 2, 3)),
                   equalTo(OrderedSetX.of(1, 2, 3)));
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
        return (LazyPersistentSortedSetX) ClojureTreeSetX.empty( (Comparator)Comparator.naturalOrder());
    }



    @Test
    public void remove() {

        ClojureTreeSetX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ClojureTreeSetX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojureTreeSetX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return OrderedSetX.iterate(times, seed, (UnaryOperator) fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return (FluentCollectionX) ClojureTreeSetX.generate(times, (Supplier)fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return (FluentCollectionX) ClojureTreeSetX.unfold(seed, (Function)unfolder);
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
}
