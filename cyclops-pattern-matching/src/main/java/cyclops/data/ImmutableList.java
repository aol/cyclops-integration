package cyclops.data;

import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Evaluation;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import com.aol.cyclops2.types.recoverable.OnEmpty;
import com.aol.cyclops2.types.recoverable.OnEmptySwitch;
import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.control.Maybe;
import cyclops.control.Trampoline;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.*;


public interface ImmutableList<T> extends Sealed2<ImmutableList.Some<T>,ImmutableList.None<T>>,
                                           Folds<T>,
                                           Filters<T>,
                                           Transformable<T>,
                                           OnEmpty<ImmutableList<T>>,
                                           OnEmptySwitch<ImmutableList<T>,ImmutableList<T>>,
                                           Iterable<T>{


    ImmutableList<T> emptyUnit();

    default ImmutableList<T> replace(T currentElement, T newElement){
        ImmutableList<T> preceding = emptyUnit();
        ImmutableList<T> tail = this;
        while(!tail.isEmpty()){
            ImmutableList<T> ref=  preceding;
            ImmutableList<T> tailRef = tail;
            Tuple3<ImmutableList<T>, ImmutableList<T>, Boolean> t3 = tail.match(c -> {
                if (Objects.equals(c.head(), currentElement))
                    return Tuple.tuple(ref, tailRef, true);
                return Tuple.tuple(ref.prepend(c.head()), c.tail(), false);
            }, nil -> Tuple.tuple(ref, tailRef, true));

            preceding = t3.v1;
            tail = t3.v2;
            if(t3.v3)
                break;

        }

        ImmutableList<T> start = preceding;
        return tail.match(cons->cons.tail().prepend(newElement).prependAll(start),nil->this);
    }
    default ImmutableList<T> removeFirst(Predicate<? super T> pred){
        ImmutableList<T> res[] = new ImmutableList[]{emptyUnit()};
        ImmutableList<T> rem = this;
        boolean[] found = {false};
        do {
            rem = rem.match(s -> {
                return s.match((head, tail2) -> {
                    found[0] = pred.test(head);
                    if(!found[0]) {
                        res[0] = res[0].prepend(head);
                        return tail2;
                    }
                    return tail2;
                });
            }, n -> n);
        }while(!rem.isEmpty() && !found[0]);

        ImmutableList<T> ar = rem.match(s -> s.match((h, t) -> t), n -> n);
        return res[0].foldLeft(ar, (a,b)->a.prepend(b));

    }
    default LinkedListX<T> linkdedListX(){
        return stream().to().linkedListX(Evaluation.LAZY);
    }

    default ImmutableList<T> subList(int start, int end){
        return drop(start).take(end-start);
    }
    default LazySeq<T> lazySeq(){
        if(this instanceof LazySeq){
            return (LazySeq<T>)this;
        }
        return match(c->LazySeq.lazy(c.head(),()->c.tail().lazySeq()), nil->LazySeq.empty());
    }
    default Seq<T> seq(){
        if(this instanceof Seq){
            return (Seq<T>)this;
        }
        return match(c->Seq.cons(c.head(),c.tail().seq()),nil->Seq.empty());
    }
    default Maybe<NonEmptyList<T>> nonEmptyList(){
        return Maybe.ofNullable(match(c->NonEmptyList.cons(c.head(),c.tail()),nil->null));
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
    default <U> ImmutableList<U> ofType(Class<? extends U> type) {
        return (ImmutableList<U>)Filters.super.ofType(type);
    }

    @Override
    default ImmutableList<T> filterNot(Predicate<? super T> predicate) {
        return (ImmutableList<T>)Filters.super.filterNot(predicate);
    }

    @Override
    default ImmutableList<T> notNull() {
        return (ImmutableList<T>)Filters.super.notNull();
    }

    @Override
    ReactiveSeq<T> stream();


    @Override
    ImmutableList<T> filter(Predicate<? super T> fn);

    @Override
    default <U> ImmutableList<U> cast(Class<? extends U> type) {
        return null;
    }

    @Override
    <R> ImmutableList<R> map(Function<? super T, ? extends R> fn);

    @Override
    default ImmutableList<T> peek(Consumer<? super T> c) {
        return (ImmutableList<T>)Transformable.super.peek(c);
    }

    @Override
    default <R> ImmutableList<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (ImmutableList<R>)Transformable.super.trampoline(mapper);
    }

    @Override
    default <R> ImmutableList<R> retry(Function<? super T, ? extends R> fn) {
        return (ImmutableList<R>)Transformable.super.retry(fn);
    }

    @Override
    default <R> ImmutableList<R> retry(Function<? super T, ? extends R> fn, int retries, long delay, TimeUnit timeUnit) {
        return (ImmutableList<R>)Transformable.super.retry(fn,retries,delay,timeUnit);
    }

    <R> ImmutableList<R> flatMap(Function<? super T, ? extends ImmutableList<? extends R>> fn);
    <R> ImmutableList<R> flatMapI(Function<? super T, ? extends Iterable<? extends R>> fn);

    @Override
    <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None<T>, ? extends R> fn2);
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

    @Override
    ImmutableList<T> onEmpty(ImmutableList<T> value);

    @Override
    ImmutableList<T> onEmptyGet(Supplier<? extends ImmutableList<T>> supplier);

    @Override
    <X extends Throwable> ImmutableList<T> onEmptyThrow(Supplier<? extends X> supplier);

    @Override
    ImmutableList<T> onEmptySwitch(Supplier<? extends ImmutableList<T>> supplier);

    public static interface Some<T> extends CaseClass2<T,ImmutableList<T>>, ImmutableList<T> {
        ImmutableList<T> tail();
        T head();
        Some<T> reverse();
        @Override
        default <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None<T>, ? extends R> fn2){
            return fn1.apply(this);
        }
    }
    public interface None<T> extends ImmutableList<T> {
        @Override
        default <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None<T>, ? extends R> fn2){
            return fn2.apply(this);
        }

    }



    default <R1, R2, R3, R> ImmutableList<R> forEach4(Function<? super T, ? extends Iterable<R1>> iterable1,
                                              BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2,
                                              Fn3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> iterable3,
                                              Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return this.flatMapI(in -> {

            ReactiveSeq<R1> a = ReactiveSeq.fromIterable(iterable1.apply(in));
            return a.flatMap(ina -> {
                ReactiveSeq<R2> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                return b.flatMap(inb -> {
                    ReactiveSeq<R3> c = ReactiveSeq.fromIterable(iterable3.apply(in, ina, inb));
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    default <R1, R2, R3, R> ImmutableList<R> forEach4(Function<? super T, ? extends Iterable<R1>> iterable1,
                                              BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2,
                                              Fn3<? super T, ? super R1, ? super R2, ? extends Iterable<R3>> iterable3,
                                              Fn4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                              Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return this.flatMapI(in -> {

            ReactiveSeq<R1> a = ReactiveSeq.fromIterable(iterable1.apply(in));
            return a.flatMap(ina -> {
                ReactiveSeq<R2> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                return b.flatMap(inb -> {
                    ReactiveSeq<R3> c = ReactiveSeq.fromIterable(iterable3.apply(in, ina, inb));
                    return c.filter(in2 -> filterFunction.apply(in, ina, inb, in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }


    default <R1, R2, R> ImmutableList<R> forEach3(Function<? super T, ? extends Iterable<R1>> iterable1,
                                          BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2,
                                          Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapI(in -> {

            Iterable<R1> a = iterable1.apply(in);
            return ReactiveSeq.fromIterable(a)
                    .flatMap(ina -> {
                        ReactiveSeq<R2> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                        return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
                    });

        });
    }


    default <R1, R2, R> ImmutableList<R> forEach3(Function<? super T, ? extends Iterable<R1>> iterable1,
                                          BiFunction<? super T, ? super R1, ? extends Iterable<R2>> iterable2,
                                          Fn3<? super T, ? super R1, ? super R2, Boolean> filterFunction,
                                          Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapI(in -> {

            Iterable<R1> a = iterable1.apply(in);
            return ReactiveSeq.fromIterable(a)
                    .flatMap(ina -> {
                        ReactiveSeq<R2> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                        return b.filter(in2 -> filterFunction.apply(in, ina, in2))
                                .map(in2 -> yieldingFunction.apply(in, ina, in2));
                    });

        });
    }


    default <R1, R> ImmutableList<R> forEach2(Function<? super T, ? extends Iterable<R1>> iterable1,
                                      BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return this.flatMapI(in-> {

            Iterable<? extends R1> b = iterable1.apply(in);
            return ReactiveSeq.fromIterable(b)
                    .map(in2->yieldingFunction.apply(in, in2));
        });
    }


    default <R1, R> ImmutableList<R> forEach2(Function<? super T, ? extends Iterable<R1>> iterable1,
                                      BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                      BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return this.flatMapI(in-> {

            Iterable<? extends R1> b = iterable1.apply(in);
            return ReactiveSeq.fromIterable(b)
                    .filter(in2-> filterFunction.apply(in,in2))
                    .map(in2->yieldingFunction.apply(in, in2));
        });
    }


}
