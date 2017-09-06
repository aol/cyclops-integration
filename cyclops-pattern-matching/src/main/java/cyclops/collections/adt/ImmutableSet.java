package cyclops.collections.adt;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import com.aol.cyclops2.types.recoverable.OnEmpty;
import com.aol.cyclops2.types.recoverable.OnEmptySwitch;
import org.jooq.lambda.tuple.Tuple2;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ImmutableSet<T> extends Folds<T>,
                                         Filters<T>,
                                         Transformable<T>,
                                         OnEmpty<ImmutableSet<T>>,
                                         OnEmptySwitch<ImmutableSet<T>,ImmutableSet<T>>,
                                         Iterable<T> {

    boolean contains(T value);
    int size();
    ImmutableSet<T> add(T value);
    ImmutableSet<T> remove(T value);
    boolean isEmpty();

    <R> ImmutableSet<R> map(Function<? super T,? extends R> fn);
    <R> ImmutableSet<R> flatMap(Function<? super T,? extends ImmutableSet<? extends R>> fn);
    <R> ImmutableSet<R> flatMapI(Function<? super T,? extends Iterable<? extends R>> fn);

    ImmutableSet<T> filter(Predicate<? super T> predicate);
}
