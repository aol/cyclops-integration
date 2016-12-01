package com.aol.cyclops.reactor.collections.extensions.standard;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jooq.lambda.tuple.Tuple2;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.reactor.collections.extensions.AbstractCollectionXTest;
import com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX;

public class LazyDequeXTest extends AbstractCollectionXTest{

    @Override
    public <T> LazyFluentCollectionX<T> of(T... values) {
        return LazyDequeX.of(values);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.functions.collections.extensions.AbstractCollectionXTest#empty()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return LazyDequeX.empty();
    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return LazyDequeX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return LazyDequeX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return LazyDequeX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return LazyDequeX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return LazyDequeX.unfold(seed, unfolder);
    }
    

}