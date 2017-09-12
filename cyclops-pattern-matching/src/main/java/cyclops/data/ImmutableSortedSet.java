package cyclops.data;


import com.aol.cyclops2.types.foldable.Evaluation;
import com.aol.cyclops2.types.recoverable.OnEmptySwitch;
import cyclops.collections.immutable.OrderedSetX;
import cyclops.collections.immutable.PersistentSetX;
import cyclops.control.Maybe;
import cyclops.control.Trampoline;
import cyclops.function.Fn3;
import cyclops.function.Fn4;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public interface ImmutableSortedSet<T> extends ImmutableSet<T> {

    default OrderedSetX<T> orderedSetX(){
        return stream().to().orderedSetX(Evaluation.LAZY);
    }
    Comparator<? super T> comparator();
    ImmutableSortedSet<T> subSet(T fromElement, T toElement);

    Maybe<T> first();
    Maybe<T> last();

    ImmutableSortedSet<T> drop(int num);
    ImmutableSortedSet<T> take(int num);

    @Override
    default <U> ImmutableSortedSet<U> ofType(Class<? extends U> type) {
        return (ImmutableSortedSet<U>)ImmutableSet.super.ofType(type);
    }

    @Override
    default ImmutableSortedSet<T> filterNot(Predicate<? super T> predicate) {
        return (ImmutableSortedSet<T>)ImmutableSet.super.filterNot(predicate);
    }

    @Override
    default ImmutableSortedSet<T> notNull() {
        return (ImmutableSortedSet<T>)ImmutableSet.super.notNull();
    }

    @Override
    default <U> ImmutableSortedSet<U> cast(Class<? extends U> type) {
        return (ImmutableSortedSet<U>)ImmutableSet.super.cast(type);
    }

    @Override
    default ImmutableSortedSet<T> peek(Consumer<? super T> c) {
        return (ImmutableSortedSet<T>)ImmutableSet.super.peek(c);
    }

    @Override
    default <R> ImmutableSortedSet<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.trampoline(mapper);
    }

    @Override
    default <R> ImmutableSortedSet<R> retry(Function<? super T, ? extends R> fn) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.retry(fn);
    }

    @Override
    default <R> ImmutableSortedSet<R> retry(Function<? super T, ? extends R> fn, int retries, long delay, TimeUnit timeUnit) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.retry(fn,retries,delay,timeUnit);
    }





    @Override
    ImmutableSortedSet<T> add(T value);

    @Override
    ImmutableSortedSet<T> remove(T value);



    @Override
    <R> ImmutableSortedSet<R> map(Function<? super T, ? extends R> fn);

    @Override
    <R> ImmutableSortedSet<R> flatMap(Function<? super T, ? extends ImmutableSet<? extends R>> fn);

    @Override
    <R> ImmutableSortedSet<R> flatMapI(Function<? super T, ? extends Iterable<? extends R>> fn);

    @Override
    ImmutableSortedSet<T> filter(Predicate<? super T> predicate);

    @Override
    default <R1, R2, R3, R> ImmutableSortedSet<R> forEach4(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2, Fn3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> iterable3, Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach4(iterable1,iterable2,iterable3,yieldingFunction);
    }

    @Override
    default <R1, R2, R3, R> ImmutableSortedSet<R> forEach4(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2, Fn3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> iterable3, Fn4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction, Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach4(iterable1,iterable2,iterable3,filterFunction,yieldingFunction);
    }

    @Override
    default <R1, R2, R> ImmutableSortedSet<R> forEach3(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2, Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach3(iterable1,iterable2,yieldingFunction);
    }

    @Override
    default <R1, R2, R> ImmutableSortedSet<R> forEach3(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2, Fn3<? super T, ? super R1, ? super R2, Boolean> filterFunction, Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach3(iterable1,iterable2,filterFunction,yieldingFunction);

    }

    @Override
    default <R1, R> ImmutableSortedSet<R> forEach2(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach2(iterable1,yieldingFunction);
    }

    @Override
    default <R1, R> ImmutableSortedSet<R> forEach2(Function<? super T, ? extends Iterable<R1>> iterable1, BiFunction<? super T, ? super R1, Boolean> filterFunction, BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {
        return (ImmutableSortedSet<R>)ImmutableSet.super.forEach2(iterable1,filterFunction,yieldingFunction);
    }

    @Override
    default ImmutableSortedSet<T> onEmpty(T value) {
        return (ImmutableSortedSet<T>)ImmutableSet.super.onEmpty(value);
    }

    @Override
    default ImmutableSortedSet<T> onEmptyGet(Supplier<? extends T> supplier) {
        return (ImmutableSortedSet<T>)ImmutableSet.super.onEmptyGet(supplier);
    }

    @Override
    default <X extends Throwable> ImmutableSortedSet<T> onEmptyThrow(Supplier<? extends X> supplier) {
        return (ImmutableSortedSet<T>)ImmutableSet.super.onEmptyThrow(supplier);
    }


}
