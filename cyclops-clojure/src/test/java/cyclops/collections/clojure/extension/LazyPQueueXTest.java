package cyclops.collections.clojure.extension;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import cyclops.collections.immutable.BagX;
import cyclops.collections.immutable.PersistentQueueX;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;

import cyclops.collections.clojure.ClojureQueueX;


import reactor.core.publisher.Flux;

public class LazyPersistentQueueXTest extends AbstractCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        PersistentQueueX<T> list = ClojureQueueX.empty();
        for (T next : values) {
            list = list.plus(next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void onEmptySwitch() {
        assertThat(ClojureQueueX.empty()
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
        return ClojureQueueX.empty();
    }



    @Test
    public void remove() {

        ClojureQueueX.of(1, 2, 3)
               .minusAll(BagX.of(2, 3))
               .flatMapP(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return  ClojureQueueX.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return ClojureQueueX.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return ClojureQueueX.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return ClojureQueueX.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return ClojureQueueX.unfold(seed, unfolder);
    }
}
