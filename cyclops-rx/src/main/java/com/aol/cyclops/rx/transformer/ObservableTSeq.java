package com.aol.cyclops.rx.transformer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.rx.Observables;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import rx.Observable;

/**
 * Monad Transformer for Rx Observables
 * 
 * ObservableT consists of an AnyM instance that in turns wraps anoter Monad type that contains an Stream
 * 
 * ObservableT<AnyM<*SOME_MONAD_TYPE*<Stream<T>>>>
 * 
 * ObservableT allows the deeply wrapped Stream to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T>
 */
public class ObservableTSeq<T> implements ObservableT<T> {

    private final AnyMSeq<Observable<T>> run;

    private ObservableTSeq(final AnyMSeq<? extends Observable<T>> run) {
        this.run = (AnyMSeq) run;
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#isSeqPresent()
     */
    @Override
    public boolean isSeqPresent() {
        return !run.isEmpty();
    }

    /**
     * @return The wrapped AnyM
     */
    @Override
    public AnyMSeq<Observable<T>> unwrap() {
        return run;
    }

    /**
     * Peek at the current value of the Stream
     * <pre>
     * {@code 
     *    ObservableT.fromIterable(ListX.of(Observable.just(10))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of Stream
     * @return ObservableT with peek call
     */
    @Override
    public ObservableTSeq<T> peek(final Consumer<? super T> peek) {
        return map(a -> {
            peek.accept(a);
            return a;
        });
    }

    /**
     * Filter the wrapped Stream
     * <pre>
     * {@code 
     *   ObservableT.fromIterable(ListX.of(Observable.just(10,11))
     *          .filter(t->t!=10);
     *             
     *     //ObservableT<[11]>>
     * }
     * </pre>
     * @param test Predicate to filter the wrapped Stream
     * @return ObservableT that applies the provided filter
     */
    @Override
    public ObservableTSeq<T> filter(final Predicate<? super T> test) {
        return of(run.map(stream -> stream.filter(i -> test.test(i))));
    }

    /**
     * Map the wrapped Stream
     * 
     * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromStream(Stream.of(Observable.just(10)))
     *             .map(t->t=t+1);
     *  
     *  
     *  //ObservableT<AnyM<Stream<Stream[11]>>>
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Stream
     * @return ObservableT that applies the map function to the wrapped Stream
     */
    @Override
    public <B> ObservableTSeq<B> map(final Function<? super T, ? extends B> f) {
        
        return new ObservableTSeq<B>(
                                     run.map(o -> o.map(i -> f.apply(i))));
    }

    /**
     * Flat Map the wrapped Stream
      * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromStream(Stream.of(Observable.just(10)))
     *             .flatMapT(t->ObservableT.emptyObservable());
     *  
     *  
     * }
     * </pre>
     * @param f FlatMap function
     * @return ObservableT that applies the flatMap function to the wrapped Stream
     */
    public <B> ObservableTSeq<B> flatMapT(final Function<? super T, ObservableTSeq<? extends B>> f) {
        return of(run.map(stream -> stream.flatMap(a -> Observables.observable(f.apply(a).run.stream()))
                                          .<B> flatMap(a -> a)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.rx.transformer.ObservableT#flatMap(java.util.function.Function)
     */
    @Override
    public <B> ObservableTSeq<B> flatMap(final Function<? super T, ? extends Observable<? extends B>> f) {

        return new ObservableTSeq<B>(
                                     run.map(o -> o.flatMap(i -> f.apply(i))));

    }

    /**
     * Lift a function into one that accepts and returns an ObservableT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * 
     * 
     * @param fn Function to enhance with functionality from Stream and another monad type
     * @return Function that accepts and returns an ObservableT
     */
    public static <U, R> Function<ObservableTSeq<U>, ObservableTSeq<R>> lift(
            final Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Construct an ObservableT from an AnyM that contains a monad type that contains type other than Stream
     * The values in the underlying monad will be mapped to Stream<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Stream
     * @return ObservableT
     */
    public static <A> ObservableTSeq<A> fromAnyM(final AnyMSeq<A> anyM) {
        return of(anyM.map(Observable::just));
    }

    /**
     * Create a ObservableT from an AnyM that wraps a monad containing a Stream
     * 
     * @param monads
     * @return
     */
    public static <A> ObservableTSeq<A> of(final AnyMSeq<? extends Observable<A>> monads) {
        return new ObservableTSeq<>(
                                    monads);
    }

    /**
     * Create a ObservableT from an AnyM that wraps a monad containing a Observable
     * 
     * @param monads AnyM that wraps a Observable containing monad
     * @return ObservableTSeq
     */
    public static <A> ObservableTSeq<A> of(final Observable<A> monads) {
        return ObservableT.fromIterable(ReactiveSeq.of(monads));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ObservableTSeq[%s]", run);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> ObservableTSeq<T> unit(final T unit) {
        return of(run.unit(Observable.just(unit)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.FoldableTransformerSeq
     * #stream()
     */
    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i -> Observables.reactiveSeq(i))
                  .stream()
                  .flatMap(e -> e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.rx.transformer.ObservableT#observable()
     */
    @Override
    public Observable<T> observable() {
        return Observables.observable(stream());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.rx.transformer.ObservableT#unitIterator(java.util.
     * Iterator)
     */
    @Override
    public <R> ObservableTSeq<R> unitIterator(final Iterator<R> it) {
        return of(run.unitIterator(it)
                     .map(i -> Observable.just(i)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.rx.transformer.ObservableT#empty()
     */
    @Override
    public <R> ObservableT<R> empty() {
        return of(run.empty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.anyM.NestedFoldable#nestedFoldables()
     */
    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.anyM.NestedCollectable#nestedCollectables()
     */
    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * unitAnyM(com.aol.cyclops.control.AnyM)
     */
    @Override
    public <T> ObservableTSeq<T> unitAnyM(final AnyM<Traversable<T>> traversable) {

        return of((AnyMSeq) traversable.map(t -> Observable.from(t)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * transformerStream()
     */
    @Override
    public AnyMSeq<? extends Traversable<T>> transformerStream() {
        return run.map(i -> Observables.reactiveSeq(i));
    }

    public static <T> ObservableTSeq<T> emptyStream() {
        return ObservableT.fromIterable(ReactiveSeq.empty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#combine(
     * java.util.function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    public ObservableTSeq<T> combine(final BiPredicate<? super T, ? super T> predicate, final BinaryOperator<T> op) {

        return (ObservableTSeq<T>) ObservableT.super.combine(predicate, op);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#cycle(int)
     */
    @Override
    public ObservableTSeq<T> cycle(final int times) {

        return (ObservableTSeq<T>) ObservableT.super.cycle(times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#cycle(com.
     * aol.cyclops.Monoid, int)
     */
    @Override
    public ObservableTSeq<T> cycle(final Monoid<T> m, final int times) {

        return (ObservableTSeq<T>) ObservableT.super.cycle(m, times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#cycleWhile
     * (java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> cycleWhile(final Predicate<? super T> predicate) {

        return (ObservableTSeq<T>) ObservableT.super.cycleWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#cycleUntil
     * (java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> cycleUntil(final Predicate<? super T> predicate) {

        return (ObservableTSeq<T>) ObservableT.super.cycleUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#zip(java.
     * lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    public <U, R> ObservableTSeq<R> zip(final Iterable<? extends U> other,
            final BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (ObservableTSeq<R>) ObservableT.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#zipStream(
     * java.util.stream.Stream)
     */
    @Override
    public <U> ObservableTSeq<Tuple2<T, U>> zip(final Stream<? extends U> other) {

        return (ObservableTSeq) ObservableT.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#zip3(java.
     * util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> ObservableTSeq<Tuple3<T, S, U>> zip3(final Stream<? extends S> second,
            final Stream<? extends U> third) {

        return (ObservableTSeq) ObservableT.super.zip3(second, third);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#zip4(java.
     * util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> ObservableTSeq<Tuple4<T, T2, T3, T4>> zip4(final Stream<? extends T2> second,
            final Stream<? extends T3> third, final Stream<? extends T4> fourth) {

        return (ObservableTSeq) ObservableT.super.zip4(second, third, fourth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * zipWithIndex()
     */
    @Override
    public ObservableTSeq<Tuple2<T, Long>> zipWithIndex() {

        return (ObservableTSeq<Tuple2<T, Long>>) ObservableT.super.zipWithIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#sliding(
     * int)
     */
    @Override
    public ObservableTSeq<ListX<T>> sliding(final int windowSize) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.sliding(windowSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#sliding(
     * int, int)
     */
    @Override
    public ObservableTSeq<ListX<T>> sliding(final int windowSize, final int increment) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.sliding(windowSize, increment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(
     * int, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> grouped(final int size, final Supplier<C> supplier) {

        return (ObservableTSeq<C>) ObservableT.super.grouped(size, supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * groupedUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedUntil(final Predicate<? super T> predicate) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.groupedUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * groupedStatefullyWhile(java.util.function.BiPredicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedStatefullyUntil(final BiPredicate<ListX<? super T>, ? super T> predicate) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.groupedStatefullyUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * groupedWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedWhile(final Predicate<? super T> predicate) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.groupedWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> groupedWhile(final Predicate<? super T> predicate,
            final Supplier<C> factory) {

        return (ObservableTSeq<C>) ObservableT.super.groupedWhile(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> groupedUntil(final Predicate<? super T> predicate,
            final Supplier<C> factory) {

        return (ObservableTSeq<C>) ObservableT.super.groupedUntil(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(
     * int)
     */
    @Override
    public ObservableTSeq<ListX<T>> grouped(final int groupSize) {

        return (ObservableTSeq<ListX<T>>) ObservableT.super.grouped(groupSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(
     * java.util.function.Function, java.util.stream.Collector)
     */
    @Override
    public <K, A, D> ObservableTSeq<Tuple2<K, D>> grouped(final Function<? super T, ? extends K> classifier,
            final Collector<? super T, A, D> downstream) {

        return (ObservableTSeq) ObservableT.super.grouped(classifier, downstream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(
     * java.util.function.Function)
     */
    @Override
    public <K> ObservableTSeq<Tuple2<K, Seq<T>>> grouped(final Function<? super T, ? extends K> classifier) {

        return (ObservableTSeq) ObservableT.super.grouped(classifier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#distinct()
     */
    @Override
    public ObservableTSeq<T> distinct() {

        return (ObservableTSeq<T>) ObservableT.super.distinct();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#scanLeft(
     * com.aol.cyclops.Monoid)
     */
    @Override
    public ObservableTSeq<T> scanLeft(final Monoid<T> monoid) {

        return (ObservableTSeq<T>) ObservableT.super.scanLeft(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#scanLeft(
     * java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> ObservableTSeq<U> scanLeft(final U seed, final BiFunction<? super U, ? super T, ? extends U> function) {

        return (ObservableTSeq<U>) ObservableT.super.scanLeft(seed, function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#scanRight(
     * com.aol.cyclops.Monoid)
     */
    @Override
    public ObservableTSeq<T> scanRight(final Monoid<T> monoid) {

        return (ObservableTSeq<T>) ObservableT.super.scanRight(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#scanRight(
     * java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> ObservableTSeq<U> scanRight(final U identity,
            final BiFunction<? super T, ? super U, ? extends U> combiner) {

        return (ObservableTSeq<U>) ObservableT.super.scanRight(identity, combiner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted()
     */
    @Override
    public ObservableTSeq<T> sorted() {

        return (ObservableTSeq<T>) ObservableT.super.sorted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted(
     * java.util.Comparator)
     */
    @Override
    public ObservableTSeq<T> sorted(final Comparator<? super T> c) {

        return (ObservableTSeq<T>) ObservableT.super.sorted(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#takeWhile(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> takeWhile(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.takeWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#dropWhile(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> dropWhile(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.dropWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#takeUntil(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> takeUntil(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.takeUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#dropUntil(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> dropUntil(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.dropUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#dropRight(
     * int)
     */
    @Override
    public ObservableTSeq<T> dropRight(final int num) {

        return (ObservableTSeq<T>) ObservableT.super.dropRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#takeRight(
     * int)
     */
    @Override
    public ObservableTSeq<T> takeRight(final int num) {

        return (ObservableTSeq<T>) ObservableT.super.takeRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#skip(long)
     */
    @Override
    public ObservableTSeq<T> skip(final long num) {

        return (ObservableTSeq<T>) ObservableT.super.skip(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#skipWhile(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> skipWhile(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.skipWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#skipUntil(
     * java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> skipUntil(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.skipUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#limit(
     * long)
     */
    @Override
    public ObservableTSeq<T> limit(final long num) {

        return (ObservableTSeq<T>) ObservableT.super.limit(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#limitWhile
     * (java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> limitWhile(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.limitWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#limitUntil
     * (java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> limitUntil(final Predicate<? super T> p) {

        return (ObservableTSeq<T>) ObservableT.super.limitUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * intersperse(java.lang.Object)
     */
    @Override
    public ObservableTSeq<T> intersperse(final T value) {

        return (ObservableTSeq<T>) ObservableT.super.intersperse(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#reverse()
     */
    @Override
    public ObservableTSeq<T> reverse() {

        return (ObservableTSeq<T>) ObservableT.super.reverse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#shuffle()
     */
    @Override
    public ObservableTSeq<T> shuffle() {

        return (ObservableTSeq<T>) ObservableT.super.shuffle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#skipLast(
     * int)
     */
    @Override
    public ObservableTSeq<T> skipLast(final int num) {

        return (ObservableTSeq<T>) ObservableT.super.skipLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#limitLast(
     * int)
     */
    @Override
    public ObservableTSeq<T> limitLast(final int num) {

        return (ObservableTSeq<T>) ObservableT.super.limitLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#onEmpty(
     * java.lang.Object)
     */
    @Override
    public ObservableTSeq<T> onEmpty(final T value) {

        return (ObservableTSeq<T>) ObservableT.super.onEmpty(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#onEmptyGet
     * (java.util.function.Supplier)
     */
    @Override
    public ObservableTSeq<T> onEmptyGet(final Supplier<? extends T> supplier) {

        return (ObservableTSeq<T>) ObservableT.super.onEmptyGet(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#
     * onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> ObservableTSeq<T> onEmptyThrow(final Supplier<? extends X> supplier) {

        return (ObservableTSeq<T>) ObservableT.super.onEmptyThrow(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#shuffle(
     * java.util.Random)
     */
    @Override
    public ObservableTSeq<T> shuffle(final Random random) {

        return (ObservableTSeq<T>) ObservableT.super.shuffle(random);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#slice(
     * long, long)
     */
    @Override
    public ObservableTSeq<T> slice(final long from, final long to) {

        return (ObservableTSeq<T>) ObservableT.super.slice(from, to);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted(
     * java.util.function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> ObservableTSeq<T> sorted(final Function<? super T, ? extends U> function) {
        return (ObservableTSeq) ObservableT.super.sorted(function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return run.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ObservableTSeq) {
            return run.equals(((ObservableTSeq) o).run);
        }
        return false;
    }
}