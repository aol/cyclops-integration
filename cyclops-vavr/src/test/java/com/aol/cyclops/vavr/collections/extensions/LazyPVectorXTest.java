package com.aol.cyclops.vavr.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.VectorX;
import cyclops.control.Option;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;

import cyclops.collections.vavr.VavrVectorX;


import reactor.core.publisher.Flux;

public class LazyPVectorXTest extends AbstractOrderDependentCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        VectorX<T> list = VavrVectorX.empty();
        for (T next : values) {
            list = list.insertAt(list.size(), next);
        }
        System.out.println("List " + list);
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
               .removeAll((Iterable<Integer>)BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }
  @Test
  public void collect() {
    assertThat(of(1, 2, 3, 4, 5).collect(Collectors.toList()).size(), is(5));
    assertThat(of(1, 1, 1, 2).collect(Collectors.toSet()).size(), is(2));
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
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return VavrVectorX.unfold(seed, unfolder);
    }
}
