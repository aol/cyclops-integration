package cyclops.collections.clojure.extension;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import cyclops.collections.clojure.ClojureVectorX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.VectorX;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class LazyPVectorXTest extends AbstractOrderDependentCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        VectorX<T> list = ClojureVectorX.empty();
        for (T next : values) {

            list = list.plus(list.size(), next);

        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(ClojureVectorX.empty()
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
        return ClojureVectorX.empty();
    }

    

    @Test
    public void remove() {

        ClojureVectorX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return ClojureVectorX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojureVectorX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ClojureVectorX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ClojureVectorX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ClojureVectorX.unfold(seed, unfolder);
    }
}
