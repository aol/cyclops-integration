package com.aol.cyclops.vavr.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import com.oath.cyclops.data.collections.extensions.lazy.immutable.LazyPersistentSortedSetX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.OrderedSetX;
import cyclops.collections.vavr.VavrTreeSetX;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class LazyPersistentSortedSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        OrderedSetX<T> list = (LazyPersistentSortedSetX) VavrTreeSetX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat((LazyPersistentSortedSetX) VavrTreeSetX.empty()
                          .onEmptySwitch(() -> (LazyPersistentSortedSetX) VavrTreeSetX.of(1, 2, 3)),
                   equalTo(OrderedSetX.of(1, 2, 3)));
    }

    @Test
    public void forEach2() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), (a, b) -> a + b).size(),
                equalTo(12));
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
        return (LazyPersistentSortedSetX) VavrTreeSetX.empty();
    }



    @Test
    public void remove() {

        VavrTreeSetX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return VavrTreeSetX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return VavrTreeSetX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return VavrTreeSetX.iterate(times, (Comparable)seed, (UnaryOperator)fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return (FluentCollectionX) VavrTreeSetX.generate(times, (Supplier)fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return (FluentCollectionX) VavrTreeSetX.unfold(seed, (Function)unfolder);
    }
}
