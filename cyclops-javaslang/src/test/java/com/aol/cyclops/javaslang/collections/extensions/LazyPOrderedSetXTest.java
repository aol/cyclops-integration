package com.aol.cyclops.javaslang.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.persistent.PBagX;
import com.aol.cyclops.javaslang.collections.JavaSlangPOrderedSet;
import com.aol.cyclops.reactor.collections.extensions.AbstractCollectionXTest;
import com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPOrderedSetX;

import fj.data.Option;
import javaslang.API;
import reactor.core.publisher.Flux;

public class LazyPOrderedSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> LazyFluentCollectionX<T> of(T... values) {
        LazyPOrderedSetX<T> list = (LazyPOrderedSetX)JavaSlangPOrderedSet.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat((LazyPOrderedSetX)JavaSlangPOrderedSet.empty()
                          .onEmptySwitch(() -> (LazyPOrderedSetX)JavaSlangPOrderedSet.of(1, 2, 3)),
                   equalTo(LazyPOrderedSetX.of(1, 2, 3)));
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
        return (LazyPOrderedSetX)JavaSlangPOrderedSet.empty();
    }

    

    @Test
    public void remove() {

        JavaSlangPOrderedSet.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapPublisher(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return JavaSlangPOrderedSet.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return JavaSlangPOrderedSet.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return LazyPOrderedSetX.iterate(times, seed, (UnaryOperator)fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return (FluentCollectionX)JavaSlangPOrderedSet.generate(times, (Supplier)fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return (FluentCollectionX)JavaSlangPOrderedSet.unfold(seed, (Function)unfolder);
    }
}
