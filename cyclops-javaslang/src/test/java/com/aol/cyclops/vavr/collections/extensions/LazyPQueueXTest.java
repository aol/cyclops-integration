package com.aol.cyclops.vavr.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPQueueX;
import cyclops.collections.immutable.PBagX;
import cyclops.collections.immutable.PQueueX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.vavr.collections.JavaSlangPQueue;


import reactor.core.publisher.Flux;

public class LazyPQueueXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        PQueueX<T> list = JavaSlangPQueue.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(JavaSlangPQueue.empty()
                          .onEmptySwitch(() -> PQueueX.of(1, 2, 3)).toList(),
                   equalTo(PQueueX.of(1, 2, 3).toList()));
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
        return JavaSlangPQueue.empty();
    }

    

    @Test
    public void remove() {

        JavaSlangPQueue.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  JavaSlangPQueue.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return JavaSlangPQueue.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return JavaSlangPQueue.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return JavaSlangPQueue.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return JavaSlangPQueue.unfold(seed, unfolder);
    }
}
