package com.aol.cyclops.javaslang.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.persistent.PBagX;
import com.aol.cyclops.javaslang.collections.JavaSlangPSet;
import com.aol.cyclops.reactor.collections.extensions.AbstractCollectionXTest;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPSetX;

import reactor.core.publisher.Flux;

public class LazyPSetXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        LazyPSetX<T> list = JavaSlangPSet.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(JavaSlangPSet.empty()
                          .onEmptySwitch(() -> LazyPSetX.of(1, 2, 3)).toList(),
                   equalTo(JavaSlangPSet.of(1, 2, 3).toList()));
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
        return JavaSlangPSet.empty();
    }

    

    @Test
    public void remove() {

        JavaSlangPSet.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapPublisher(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return JavaSlangPSet.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return JavaSlangPSet.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return JavaSlangPSet.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return JavaSlangPSet.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return JavaSlangPSet.unfold(seed, unfolder);
    }
    @Test
    public void takeWhileTest(){
        
        
        
        
        
    }
    @Test
    public void limitWhileTest(){
        
        
        
        
        
        
    }
}
