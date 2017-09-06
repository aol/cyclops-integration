package cyclops.collections.adt;

import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Evaluation;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.foldable.To;
import com.aol.cyclops2.types.functor.FilterableTransformable;
import com.aol.cyclops2.types.functor.Transformable;
import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


public interface ImmutableList<T> extends Sealed2<ImmutableList.Some<T>,ImmutableList.None>,
                                           Folds<T>,
                                           Filters<T>,
                                           Transformable<T>,
                                           Iterable<T> {


    default LinkedListX<T> linkdedListX(){
        return stream().to().linkedListX(Evaluation.LAZY);
    }


    default Tuple2<ImmutableList<T>, ImmutableList<T>> splitAt(int n) {
        return Tuple.tuple(take(n), drop(n));
    }

    default Zipper<T> focusAt(int pos, T alt){
        Tuple2<ImmutableList<T>, ImmutableList<T>> t2 = splitAt(pos);
        T value = t2.v2.match(c -> c.head(), n -> alt);
        ImmutableList<T> right= t2.v2.match(c->c.tail(),n->null);
        return Zipper.of(t2.v1,value, right);
    }
    default Maybe<Zipper<T>> focusAt(int pos){
        Tuple2<ImmutableList<T>, ImmutableList<T>> t2 = splitAt(pos);
        Maybe<T> value = t2.v2.match(c -> Maybe.just(c.head()), n -> Maybe.none());
        return value.map(l-> {
            ImmutableList<T> right = t2.v2.match(c -> c.tail(), n -> null);
            return Zipper.of(t2.v1, l, right);
        });
    }

    ImmutableList<T> drop(int num);
    ImmutableList<T> take(int num);


    ImmutableList<T> prepend(T value);
    ImmutableList<T> prependAll(Iterable<T> value);

    ImmutableList<T> append(T value);
    ImmutableList<T> appendAll(Iterable<T> value);

    ImmutableList<T> reverse();

    Maybe<T> get(int pos);
    T getOrElse(int pos,T alt);
    T getOrElseGet(int pos,Supplier<T> alt);
    int size();
    default boolean contains(T value){
        return stream().filter(o-> Objects.equals(value,o)).findFirst().isPresent();
    }
    boolean isEmpty();

    @Override
    ReactiveSeq<T> stream();


    @Override
    ImmutableList<T> filter(Predicate<? super T> fn);

    @Override
     <R> ImmutableList<R> map(Function<? super T, ? extends R> fn);

    @Override
    <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None, ? extends R> fn2);
    @Override
    default Iterator<T> iterator() {
        return new Iterator<T>() {
            ImmutableList<T> current= ImmutableList.this;
            @Override
            public boolean hasNext() {
                return current.match(c->true,n->false);
            }

            @Override
            public T next() {
                return current.match(c->{
                    current = c.tail();
                    return c.head();
                },n->null);
            }
        };
    }

    public static interface Some<T> extends CaseClass2<T,ImmutableList<T>>, ImmutableList<T> {
        ImmutableList<T> tail();
        T head();
        Some<T> reverse();
        @Override
        default <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None, ? extends R> fn2){
            return fn1.apply(this);
        }
    }
    public interface None<T> extends ImmutableList<T> {
        @Override
        default <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None, ? extends R> fn2){
            return fn2.apply(this);
        }

    }
}
