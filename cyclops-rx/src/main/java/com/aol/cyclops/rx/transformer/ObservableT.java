package com.aol.cyclops.rx.transformer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
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
import org.reactivestreams.Publisher;

import com.aol.cyclops.Matchables;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.control.monads.transformers.values.FoldableTransformerSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.rx.RxCyclops;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;

import rx.Observable;

/**
 * Monad Transformer for RxJava Observables
 * 
 * ObservableT consists of an AnyM instance that in turns wraps anoter Monad type that contains an Observable
 * 
 * ObservableT<AnyM<*SOME_MONAD_TYPE*<Observable<T>>>>
 * 
 * ObservableT allows the deeply wrapped Observable to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T>
 */
public interface ObservableT<T> extends FoldableTransformerSeq<T> {

    public <R> ObservableT<R> unitIterator(Iterator<R> it);

    public <R> ObservableT<R> unit(R t);

    public <R> ObservableT<R> empty();

    public <B> ObservableT<B> flatMap(Function<? super T, ? extends Observable<? extends B>> f);

    /**
     * @return The wrapped AnyM
     */
    public AnyM<Observable<T>> unwrap();

    /**
     * Peek at the current value of the Observable
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromObservable(Arrays.asObservable(10))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of Observable
     * @return ObservableT with peek call
     */
    public ObservableT<T> peek(Consumer<? super T> peek);

    /**
     * Filter the wrapped Observable
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromObservable(Arrays.asObservable(10,11))
     *             .filter(t->t!=10);
     *             
     *     //ObservableT<AnyM<Observable<Observable[11]>>>
     * }
     * </pre>
     * @param test Predicate to filter the wrapped Observable
     * @return ObservableT that applies the provided filter
     */
    public ObservableT<T> filter(Predicate<? super T> test);

    /**
     * Map the wrapped Observable
     * 
     * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromObservable(Arrays.asObservable(10))
     *             .map(t->t=t+1);
     *  
     *  
     *  //ObservableT<AnyM<Observable<Observable[11]>>>
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Observable
     * @return ObservableT that applies the map function to the wrapped Observable
     */
    public <B> ObservableT<B> map(Function<? super T, ? extends B> f);

    /**
     * Flat Map the wrapped Observable
      * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromObservable(Observable.just(10))
     *             .flatMap(t->Observable.empty());
     *  
     *  
     *  //ObservableT<AnyM<Observable<Observable.empty>>>
     * }
     * </pre>
     * @param f FlatMap function
     * @return ObservableT that applies the flatMap function to the wrapped Observable
     */
    default <B> ObservableT<B> bind(Function<? super T, ObservableT<? extends B>> f) {
        return of(unwrap().map(observable -> observable.flatMap(a -> RxCyclops.toObservable(f.apply(a)
                                                                                             .unwrap()
                                                                                             .stream()))
                                                       .<B> flatMap(a -> a)));
    }

    /**
     * Lift a function into one that accepts and returns an ObservableT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add iteration handling (via Observable) and nullhandling (via Optional) to an existing function
     * <pre>
     * {@code 
    	Function<Integer,Integer> add2 = i -> i+2;
    	Function<ObservableT<Integer>, ObservableT<Integer>> optTAdd2 = ObservableT.lift(add2);
    	
    	Observable<Integer> nums = Observable.of(1,2);
    	AnyM<Observable<Integer>> Observable = AnyM.fromOptional(Optional.of(nums));
    	
    	List<Integer> results = optTAdd2.apply(ObservableT.of(Observable))
    									.unwrap()
    									.<Optional<Observable<Integer>>>unwrap()
    									.get()
    									.collect(Collectors.toList());
    	//Observable.of(3,4);
     * 
     * 
     * }</pre>
     * 
     * 
     * @param fn Function to enhance with functionality from Observable and another monad type
     * @return Function that accepts and returns an ObservableT
     */
    public static <U, R> Function<ObservableT<U>, ObservableT<R>> lift(Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Construct an ObservableT from an AnyM that contains a monad type that contains type other than Observable
     * The values in the underlying monad will be mapped to Observable<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Observable
     * @return ObservableT
     */
    public static <A> ObservableT<A> fromAnyM(AnyM<A> anyM) {
        return of(anyM.map(Observable::just));
    }

    /**
     * Create a ObservableT from an AnyM that wraps a monad containing a Observable
     * 
     * @param monads
     * @return
     */
    public static <A> ObservableT<A> of(AnyM<? extends Observable<A>> monads) {
        return Matchables.anyM(monads)
                         .visit(v -> ObservableTValue.of(v), s -> ObservableTSeq.of(s));
    }

    public static <A> ObservableTValue<A> fromAnyMValue(AnyMValue<A> anyM) {
        return ObservableTValue.fromAnyM(anyM);
    }

    public static <A> ObservableTSeq<A> fromAnyMSeq(AnyMSeq<A> anyM) {
        return ObservableTSeq.fromAnyM(anyM);
    }

    public static <A> ObservableTSeq<A> fromIterable(Iterable<Observable<A>> iterableOfObservables) {
        return ObservableTSeq.of(AnyM.fromIterable(iterableOfObservables));
    }

    public static <A> ObservableTSeq<A> fromObservable(Observable<Observable<A>> ObservableOfObservables) {
        return ObservableTSeq.of(RxCyclops.observable(ObservableOfObservables));
    }

    public static <A> ObservableTSeq<A> fromPublisher(Publisher<Observable<A>> publisherOfObservables) {
        return ObservableTSeq.of(AnyM.fromPublisher(publisherOfObservables));
    }

    public static <A, V extends MonadicValue<? extends Observable<A>>> ObservableTValue<A> fromValue(V monadicValue) {
        return ObservableTValue.fromValue(monadicValue);
    }

    public static <A> ObservableTValue<A> fromOptional(Optional<Observable<A>> optional) {
        return ObservableTValue.of(AnyM.fromOptional(optional));
    }

    public static <A> ObservableTValue<A> fromFuture(CompletableFuture<Observable<A>> future) {
        return ObservableTValue.of(AnyM.fromCompletableFuture(future));
    }

    public static <A> ObservableTValue<A> fromIterableValue(Iterable<Observable<A>> iterableOfObservables) {
        return ObservableTValue.of(AnyM.fromIterableValue(iterableOfObservables));
    }

    public static <T> ObservableTSeq<T> emptyObservable() {
        return ObservableT.fromIterable(ReactiveSeq.empty());
    }

    default Observable<Observable<T>> observableOfObservable() {
        return Observable.from(this.unwrap()
                                   .stream());
    }

    public Observable<T> observable();

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
     */
    @Override
    default <U> ObservableT<U> cast(Class<? extends U> type) {
        return (ObservableT<U>) FoldableTransformerSeq.super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
     */
    @Override
    default <R> ObservableT<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (ObservableT<R>) FoldableTransformerSeq.super.trampoline(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    default <R> ObservableT<R> patternMatch(Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,
            Supplier<? extends R> otherwise) {
        return (ObservableT<R>) FoldableTransformerSeq.super.patternMatch(case1, otherwise);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
     */
    @Override
    default <U> ObservableT<U> ofType(Class<? extends U> type) {

        return (ObservableT<U>) FoldableTransformerSeq.super.ofType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filterNot(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> filterNot(Predicate<? super T> fn) {

        return (ObservableT<T>) FoldableTransformerSeq.super.filterNot(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#notNull()
     */
    @Override
    default ObservableT<T> notNull() {

        return (ObservableT<T>) FoldableTransformerSeq.super.notNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#combine
     * (java.util.function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    default ObservableT<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {

        return (ObservableT<T>) FoldableTransformerSeq.super.combine(predicate, op);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycle(
     * int)
     */
    @Override
    default ObservableT<T> cycle(int times) {

        return (ObservableT<T>) FoldableTransformerSeq.super.cycle(times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycle(
     * com.aol.cyclops.Monoid, int)
     */
    @Override
    default ObservableT<T> cycle(Monoid<T> m, int times) {

        return (ObservableT<T>) FoldableTransformerSeq.super.cycle(m, times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * cycleWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> cycleWhile(Predicate<? super T> predicate) {

        return (ObservableT<T>) FoldableTransformerSeq.super.cycleWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * cycleUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> cycleUntil(Predicate<? super T> predicate) {

        return (ObservableT<T>) FoldableTransformerSeq.super.cycleUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip(
     * java.lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    default <U, R> ObservableT<R> zip(Iterable<? extends U> other,
            BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (ObservableT<R>) FoldableTransformerSeq.super.zip(other, zipper);
    }

    @Override
    default <U, R> ObservableT<R> zip(Seq<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (ObservableT<R>) FoldableTransformerSeq.super.zip(other, zipper);
    }

    @Override
    default <U, R> ObservableT<R> zip(Stream<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (ObservableT<R>) FoldableTransformerSeq.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip(
     * java.util.Observable.Observable)
     */
    @Override
    default <U> ObservableT<Tuple2<T, U>> zip(Stream<? extends U> other) {

        return (ObservableT) FoldableTransformerSeq.super.zip(other);
    }

    @Override
    default <U> ObservableT<Tuple2<T, U>> zip(Seq<? extends U> other) {

        return (ObservableT) FoldableTransformerSeq.super.zip(other);
    }

    @Override
    default <U> ObservableT<Tuple2<T, U>> zip(Iterable<? extends U> other) {

        return (ObservableT) FoldableTransformerSeq.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip3(
     * java.util.Observable.Observable, java.util.Observable.Observable)
     */
    @Override
    default <S, U> ObservableT<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {

        return (ObservableT) FoldableTransformerSeq.super.zip3(second, third);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip4(
     * java.util.Observable.Observable, java.util.Observable.Observable,
     * java.util.Observable.Observable)
     */
    @Override
    default <T2, T3, T4> ObservableT<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second,
            Stream<? extends T3> third, Stream<? extends T4> fourth) {

        return (ObservableT) FoldableTransformerSeq.super.zip4(second, third, fourth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * zipWithIndex()
     */
    @Override
    default ObservableT<Tuple2<T, Long>> zipWithIndex() {

        return (ObservableT<Tuple2<T, Long>>) FoldableTransformerSeq.super.zipWithIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sliding
     * (int)
     */
    @Override
    default ObservableT<ListX<T>> sliding(int windowSize) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.sliding(windowSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sliding
     * (int, int)
     */
    @Override
    default ObservableT<ListX<T>> sliding(int windowSize, int increment) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.sliding(windowSize, increment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped
     * (int, java.util.function.Supplier)
     */
    @Override
    default <C extends Collection<? super T>> ObservableT<C> grouped(int size, Supplier<C> supplier) {

        return (ObservableT<C>) FoldableTransformerSeq.super.grouped(size, supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * groupedUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<ListX<T>> groupedUntil(Predicate<? super T> predicate) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.groupedUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * groupedStatefullyWhile(java.util.function.BiPredicate)
     */
    @Override
    default ObservableT<ListX<T>> groupedStatefullyUntil(BiPredicate<ListX<? super T>, ? super T> predicate) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.groupedStatefullyUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * groupedWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<ListX<T>> groupedWhile(Predicate<? super T> predicate) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.groupedWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    default <C extends Collection<? super T>> ObservableT<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {

        return (ObservableT<C>) FoldableTransformerSeq.super.groupedWhile(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    default <C extends Collection<? super T>> ObservableT<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {

        return (ObservableT<C>) FoldableTransformerSeq.super.groupedUntil(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped
     * (int)
     */
    @Override
    default ObservableT<ListX<T>> grouped(int groupSize) {

        return (ObservableT<ListX<T>>) FoldableTransformerSeq.super.grouped(groupSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped
     * (java.util.function.Function, java.util.Observable.Collector)
     */
    @Override
    default <K, A, D> ObservableT<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier,
            Collector<? super T, A, D> downObservable) {

        return (ObservableT) FoldableTransformerSeq.super.grouped(classifier, downObservable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped
     * (java.util.function.Function)
     */
    @Override
    default <K> ObservableT<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier) {

        return (ObservableT) FoldableTransformerSeq.super.grouped(classifier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * distinct()
     */
    @Override
    default ObservableT<T> distinct() {

        return (ObservableT<T>) FoldableTransformerSeq.super.distinct();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * scanLeft(com.aol.cyclops.Monoid)
     */
    @Override
    default ObservableT<T> scanLeft(Monoid<T> monoid) {

        return (ObservableT<T>) FoldableTransformerSeq.super.scanLeft(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * scanLeft(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    default <U> ObservableT<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {

        return (ObservableT<U>) FoldableTransformerSeq.super.scanLeft(seed, function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * scanRight(com.aol.cyclops.Monoid)
     */
    @Override
    default ObservableT<T> scanRight(Monoid<T> monoid) {

        return (ObservableT<T>) FoldableTransformerSeq.super.scanRight(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * scanRight(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    default <U> ObservableT<U> scanRight(U identity, BiFunction<? super T, ? super U, ? extends U> combiner) {

        return (ObservableT<U>) FoldableTransformerSeq.super.scanRight(identity, combiner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted(
     * )
     */
    @Override
    default ObservableT<T> sorted() {

        return (ObservableT<T>) FoldableTransformerSeq.super.sorted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted(
     * java.util.Comparator)
     */
    @Override
    default ObservableT<T> sorted(Comparator<? super T> c) {

        return (ObservableT<T>) FoldableTransformerSeq.super.sorted(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * takeWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> takeWhile(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.takeWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * dropWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> dropWhile(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.dropWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * takeUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> takeUntil(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.takeUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * dropUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> dropUntil(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.dropUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * dropRight(int)
     */
    @Override
    default ObservableT<T> dropRight(int num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.dropRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * takeRight(int)
     */
    @Override
    default ObservableT<T> takeRight(int num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.takeRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#skip(
     * long)
     */
    @Override
    default ObservableT<T> skip(long num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.skip(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * skipWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> skipWhile(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.skipWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * skipUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> skipUntil(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.skipUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#limit(
     * long)
     */
    @Override
    default ObservableT<T> limit(long num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.limit(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * limitWhile(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> limitWhile(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.limitWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * limitUntil(java.util.function.Predicate)
     */
    @Override
    default ObservableT<T> limitUntil(Predicate<? super T> p) {

        return (ObservableT<T>) FoldableTransformerSeq.super.limitUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * intersperse(java.lang.Object)
     */
    @Override
    default ObservableT<T> intersperse(T value) {

        return (ObservableT<T>) FoldableTransformerSeq.super.intersperse(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#reverse
     * ()
     */
    @Override
    default ObservableT<T> reverse() {

        return (ObservableT<T>) FoldableTransformerSeq.super.reverse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#shuffle
     * ()
     */
    @Override
    default ObservableT<T> shuffle() {

        return (ObservableT<T>) FoldableTransformerSeq.super.shuffle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * skipLast(int)
     */
    @Override
    default ObservableT<T> skipLast(int num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.skipLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * limitLast(int)
     */
    @Override
    default ObservableT<T> limitLast(int num) {

        return (ObservableT<T>) FoldableTransformerSeq.super.limitLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#onEmpty
     * (java.lang.Object)
     */
    @Override
    default ObservableT<T> onEmpty(T value) {

        return (ObservableT<T>) FoldableTransformerSeq.super.onEmpty(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * onEmptyGet(java.util.function.Supplier)
     */
    @Override
    default ObservableT<T> onEmptyGet(Supplier<? extends T> supplier) {

        return (ObservableT<T>) FoldableTransformerSeq.super.onEmptyGet(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    default <X extends Throwable> ObservableT<T> onEmptyThrow(Supplier<? extends X> supplier) {

        return (ObservableT<T>) FoldableTransformerSeq.super.onEmptyThrow(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#shuffle
     * (java.util.Random)
     */
    @Override
    default ObservableT<T> shuffle(Random random) {

        return (ObservableT<T>) FoldableTransformerSeq.super.shuffle(random);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#slice(
     * long, long)
     */
    @Override
    default ObservableT<T> slice(long from, long to) {

        return (ObservableT<T>) FoldableTransformerSeq.super.slice(from, to);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted(
     * java.util.function.Function)
     */
    @Override
    default <U extends Comparable<? super U>> ObservableT<T> sorted(Function<? super T, ? extends U> function) {
        return (ObservableT) FoldableTransformerSeq.super.sorted(function);
    }
}