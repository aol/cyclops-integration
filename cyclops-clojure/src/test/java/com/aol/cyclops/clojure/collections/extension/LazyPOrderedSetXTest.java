package com.aol.cyclops.clojure.collections.extension;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.LazyFluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPOrderedSetX;
import cyclops.collections.immutable.PBagX;
import cyclops.collections.immutable.POrderedSetX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.clojure.collections.ClojureTreePOrderedSet;


import reactor.core.publisher.Flux;

public class LazyPOrderedSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        POrderedSetX<T> list = (LazyPOrderedSetX)ClojureTreePOrderedSet.empty((Comparator)Comparator.naturalOrder());
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
        assertThat((LazyPOrderedSetX)ClojureTreePOrderedSet.empty((Comparator)Comparator.naturalOrder())
                          .onEmptySwitch(() -> (LazyPOrderedSetX)ClojureTreePOrderedSet.of((Comparator)Comparator.naturalOrder(),1, 2, 3)),
                   equalTo(POrderedSetX.of(1, 2, 3)));
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
        return (LazyPOrderedSetX)ClojureTreePOrderedSet.empty( (Comparator)Comparator.naturalOrder());
    }

    

    @Test
    public void remove() {

        ClojureTreePOrderedSet.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ClojureTreePOrderedSet.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojureTreePOrderedSet.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return POrderedSetX.iterate(times, seed, (UnaryOperator) fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return (FluentCollectionX)ClojureTreePOrderedSet.generate(times, (Supplier)fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return (FluentCollectionX)ClojureTreePOrderedSet.unfold(seed, (Function)unfolder);
    }
}
