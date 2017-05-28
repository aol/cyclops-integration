package com.aol.cyclops.vavr.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPQueueX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.PersistentQueueX;
import cyclops.collections.vavr.VavrQueueX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class LazyPQueueXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
    return VavrQueueX.of(values);
    /**
        PersistentQueueX<T> list = VavrQueueX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;
**/
    }

    @Test
    public void onEmptySwitch() {
        assertThat(VavrQueueX.empty()
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
        return VavrQueueX.empty();
    }

    

    @Test
    public void remove() {

        VavrQueueX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  VavrQueueX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return VavrQueueX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return VavrQueueX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return VavrQueueX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return VavrQueueX.unfold(seed, unfolder);
    }

    @Test
    public void takeWhileTest(){

        List<Integer> list = new ArrayList<>();
        while(list.size()==0){
            list = of(1,2,3,4,5,6).takeWhile(it -> it<7)
                    .peek(it -> System.out.println(it)).collect(Collectors.toList());

        }
        assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(list.get(0)));




    }
}
