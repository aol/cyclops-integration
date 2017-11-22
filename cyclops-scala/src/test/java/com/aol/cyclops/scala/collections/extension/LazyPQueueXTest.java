package com.aol.cyclops.scala.collections.extension;


import cyclops.collections.scala.ScalaQueueX;
import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.PersistentQueueX;
import cyclops.collections.immutable.LinkedListX;
import cyclops.data.tuple.Tuple2;
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
        PersistentQueueX<T> list = ScalaQueueX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return (FluentCollectionX)list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(ScalaQueueX.empty()
                          .onEmptySwitch(() -> PersistentQueueX.of(1, 2, 3)).toList(),
                   equalTo(PersistentQueueX.of(1, 2, 3).toList()));
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
        return LinkedListX.empty();
    }



    @Test
    public void remove() {

        ScalaQueueX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  ScalaQueueX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ScalaQueueX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ScalaQueueX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ScalaQueueX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return ScalaQueueX.unfold(seed, unfolder);
    }
}
