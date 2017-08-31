package com.aol.cyclops.vavr.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.VectorX;
import cyclops.collections.mutable.ListX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Ignore;
import org.junit.Test;

import cyclops.collections.vavr.VavrVectorX;


import org.pcollections.PVector;
import org.pcollections.TreePVector;
import reactor.core.publisher.Flux;

public class LazyPVectorXTest extends AbstractOrderDependentCollectionXTest  {
    @Test @Ignore
    public void with(){

        assertEquals(of("x", "a","b", "c"), of("a", "b", "c")
                .with(0, "x"));
        assertEquals(of("x", "b", "c"), of("a", "b", "c")
                .with(0, "x"));
        assertEquals(of("a", "x", "c"), of("a", "b", "c").with(1, "x"));
        assertEquals(of("a", "b", "x"), of("a", "b", "c").with(2, "x"));
    }
    @Override
    public <T> VectorX<T> of(T... values) {
        VectorX<T> list = VavrVectorX.empty();
        for (T next : values) {
            list = list.plus(list.size(), next);
        }
       // System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(VavrVectorX.empty()
                          .onEmptySwitch(() -> VectorX.of(1, 2, 3)),
                   equalTo(VectorX.of(1, 2, 3)));
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
        return VavrVectorX.empty();
    }

    

    @Test
    public void remove() {

        VavrVectorX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return VavrVectorX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return VavrVectorX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return VavrVectorX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return VavrVectorX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return VavrVectorX.unfold(seed, unfolder);
    }
}
