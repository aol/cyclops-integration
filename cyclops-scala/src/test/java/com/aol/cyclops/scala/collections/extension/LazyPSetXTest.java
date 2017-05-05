package com.aol.cyclops.scala.collections.extension;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.LazyFluentCollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPSetX;
import cyclops.collections.immutable.PBagX;
import cyclops.collections.immutable.PSetX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.scala.collections.ScalaHashPSet;

import reactor.core.publisher.Flux;

public class LazyPSetXTest extends AbstractCollectionXTest {

    @Test
    public void forEach2() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), (a, b) -> a + b).size(),
                equalTo(12));
    }
    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        PSetX<T> list = ScalaHashPSet.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return (FluentCollectionX)list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(ScalaHashPSet.empty()
                          .onEmptySwitch(() -> PSetX.of(1, 2, 3)).toList(),
                   equalTo(ScalaHashPSet.of(1, 2, 3).toList()));
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
        return ScalaHashPSet.empty();
    }

    

    @Test
    public void remove() {

        ScalaHashPSet.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ScalaHashPSet.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ScalaHashPSet.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ScalaHashPSet.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ScalaHashPSet.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ScalaHashPSet.unfold(seed, unfolder);
    }
    @Test
    public void takeWhileTest(){
        
        
        
        
        
    }
    @Test
    public void limitWhileTest(){
        
        
        
        
        
        
    }
}
