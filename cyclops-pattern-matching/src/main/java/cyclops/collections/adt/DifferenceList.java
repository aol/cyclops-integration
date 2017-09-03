package cyclops.collections.adt;


import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.adt.Witness.lazylist;
import cyclops.function.Fn0;
import cyclops.monads.Witness.supplier;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Kleisli;
import cyclops.typeclasses.free.Free;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DifferenceList<T> implements Folds<T>, Transformable<T> {

    private final Function<LazyList<T>,Free<supplier, LazyList<T>>> appending;

    public <R> DifferenceList<R> map(Function<? super T, ? extends R> fn){
        return new DifferenceList<>(l->Free.done(run().map(fn)));
    }
    public <R> DifferenceList<R> flatMap(Function<? super T, ? extends DifferenceList<? extends R>> fn){
        return new DifferenceList<>(l->Free.done(run().flatMap(fn.andThen(DifferenceList::run))));
    }
    public LazyList<T> run(){
        return Fn0.run(appending.apply(LazyList.empty()));
    }
    public static <T> DifferenceList<T> of(LazyList<T> list){
        return new DifferenceList<>(l->Free.done(list.appendAll(l)));
    }
    public static <T> DifferenceList<T> of(T... values){
        return  of(LazyList.of(values));
    }
    public static <T> DifferenceList<T> empty(){
        return new DifferenceList<>(l->Free.done(l));
    }
    public DifferenceList<T> prepend(DifferenceList<T> prepend) {
        return prepend.append(this);
    }
    public DifferenceList<T> append(DifferenceList<T> append) {
        Function<LazyList<T>, Free<supplier, LazyList<T>>> appending2 = append.appending;

        return new DifferenceList<T>(l-> appending2.apply(l).flatMap(l2->{
                                    Fn0.SupplierKind<Free<supplier, LazyList<T>>> s = ()->appending.apply(l2);
                                    Free<supplier, LazyList<T>> x = Fn0.suspend(s);
                                    return x;
                                     }));
    }

    @Override
    public ReactiveSeq<T> stream() {
        return ReactiveSeq.fromIterable(run().iterable());
    }
}
