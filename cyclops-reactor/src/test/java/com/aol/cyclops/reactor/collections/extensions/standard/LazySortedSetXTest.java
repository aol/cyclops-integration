package com.aol.cyclops.reactor.collections.extensions.standard;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jooq.lambda.tuple.Tuple2;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.standard.DequeX;
import com.aol.cyclops.reactor.collections.extensions.AbstractCollectionXTest;

public class LazySortedSetXTest extends AbstractCollectionXTest{

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        return LazySortedSetX.of(values);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.functions.collections.extensions.AbstractCollectionXTest#empty()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return LazySortedSetX.empty();
    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return LazySortedSetX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return LazySortedSetX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return LazySortedSetX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return LazySortedSetX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return LazySortedSetX.unfold(seed, unfolder);
    }
    

}