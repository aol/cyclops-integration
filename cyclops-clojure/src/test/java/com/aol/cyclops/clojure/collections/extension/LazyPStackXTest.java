package com.aol.cyclops.clojure.collections.extension;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyLinkedListX;
import cyclops.Semigroups;
import cyclops.collections.ListX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.LinkedListX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.clojure.collections.ClojurePStack;


import reactor.core.publisher.Flux;

public class LazyLinkedListXTest extends AbstractOrderDependentCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        LinkedListX<T> list = ClojurePStack.empty();
        for (T next : values) {
            list = list.plus(list.size(), next);
        }
        System.out.println("List " + list);
        return list.efficientOpsOff();

    }
    @Test
    public void combineNoOrderOd(){
        assertThat(of(1,2,3)
                   .combine((a, b)->a.equals(b), Semigroups.intSum)
                   .toListX(),equalTo(ListX.of(1,2,3)));
                   
    }

    @Test
    public void onEmptySwitch() {
        assertThat(ClojurePStack.empty()
                          .onEmptySwitch(() -> LinkedListX.of(1, 2, 3)),
                   equalTo(LinkedListX.of(1, 2, 3)));
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
        return ClojurePStack.empty();
    }

    

    @Test
    public void remove() {

        ClojurePStack.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ClojurePStack.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojurePStack.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ClojurePStack.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ClojurePStack.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ClojurePStack.unfold(seed, unfolder);
    }
}
