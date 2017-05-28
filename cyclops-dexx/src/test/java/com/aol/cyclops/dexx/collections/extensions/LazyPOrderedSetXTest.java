package com.aol.cyclops.dexx.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.LazyFluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPOrderedSetX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.OrderedSetX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;


import com.aol.cyclops.dexx.collections.DexxPOrderedSet;



import reactor.core.publisher.Flux;

public class LazyPOrderedSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        OrderedSetX<T> list = (LazyPOrderedSetX)DexxPOrderedSet.empty();
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
        assertThat((LazyPOrderedSetX)DexxPOrderedSet.empty()
                          .onEmptySwitch(() -> (LazyPOrderedSetX)DexxPOrderedSet.of(1, 2, 3)),
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
        return (LazyPOrderedSetX)DexxPOrderedSet.empty();
    }

    

    @Test
    public void remove() {

        DexxPOrderedSet.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return DexxPOrderedSet.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return DexxPOrderedSet.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {

        return DexxPOrderedSet.<Comparable>iterate(times, (Comparable)seed, (UnaryOperator)fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return (FluentCollectionX)DexxPOrderedSet.generate(times, (Supplier)fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return (FluentCollectionX)DexxPOrderedSet.unfold(seed, (Function)unfolder);
    }
}
