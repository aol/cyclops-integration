package com.aol.cyclops.scala.collections.extension;


import com.aol.cyclops.scala.collections.ScalaPQueue;
import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.LazyFluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPQueueX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPStackX;
import cyclops.collections.immutable.PBagX;
import cyclops.collections.immutable.PQueueX;
import cyclops.collections.immutable.PStackX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LazyPQueueXTest extends AbstractCollectionXTest {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        PQueueX<T> list = ScalaPQueue.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return (FluentCollectionX)list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(ScalaPQueue.empty()
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
        return PStackX.empty();
    }

    

    @Test
    public void remove() {

        ScalaPQueue.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  ScalaPQueue.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ScalaPQueue.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ScalaPQueue.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ScalaPQueue.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ScalaPQueue.unfold(seed, unfolder);
    }
}
