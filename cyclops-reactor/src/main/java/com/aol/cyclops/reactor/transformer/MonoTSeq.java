package com.aol.cyclops.reactor.transformer;

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

import org.jooq.lambda.Collectable;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.monads.transformers.values.ValueTransformerSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.Sequential;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.stream.ConvertableSequence;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import reactor.core.publisher.Mono;

/**
 * Monad Transformer for Reactor Monos nested inside Sequential data types
 * 
 * @author johnmcclean
 *
 * @param <A> the type of elements held in the nested Monos
 */
public class MonoTSeq<A> implements MonoT<A>, ValueTransformerSeq<A>, IterableFoldable<A>, ConvertableSequence<A>,
        CyclopsCollectable<A>, Sequential<A> {

    private final AnyMSeq<Mono<A>> run;

    /**
     * @return The wrapped AnyM
     */
    @Override
    public AnyMSeq<Mono<A>> unwrap() {
        return run;
    }

    private MonoTSeq(final AnyMSeq<Mono<A>> run) {
        this.run = run;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ValueTransformerSeq#
     * unitStream(com.aol.cyclops.control.ReactiveSeq)
     */
    @Override
    public <T> MonoTSeq<T> unitStream(final ReactiveSeq<T> traversable) {
        return MonoT.fromStream(traversable.map(Mono::just));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * unitAnyM(com.aol.cyclops.control.AnyM)
     */
    @Override
    public <T> MonoTSeq<T> unitAnyM(final AnyM<Traversable<T>> traversable) {

        return of((AnyMSeq) traversable.map(t -> Mono.from(t)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * transformerStream()
     */
    @Override
    public AnyMSeq<? extends Traversable<A>> transformerStream() {

        return run.map(f -> ListX.of(f.block()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.transformer.MonoT#filter(java.util.function.
     * Predicate)
     */
    @Override
    public MonoTSeq<A> filter(final Predicate<? super A> test) {
        return MonoTSeq.of(run.map(opt -> opt.filter(test)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.transformer.MonoT#peek(java.util.function.
     * Consumer)
     */
    @Override
    public MonoTSeq<A> peek(final Consumer<? super A> peek) {
        return of(run.peek(future -> future.map(a -> {
            peek.accept(a);
            return a;
        })));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.transformer.MonoT#map(java.util.function.
     * Function)
     */
    @Override
    public <B> MonoTSeq<B> map(final Function<? super A, ? extends B> f) {
        return new MonoTSeq<B>(
                               run.map(o -> o.map(f)));
    }

    /**
     * Flat Map the wrapped Mono
      * <pre>
     * {@code 
     *  MonoT.of(AnyM.fromStream(Arrays.asMono(10))
     *             .flatMap(t->Mono.completedFuture(20));
     *  
     *  
     *  //MonoT<AnyMSeq<Stream<Mono[20]>>>
     * }
     * </pre>
     * @param f FlatMap function
     * @return MonoT that applies the flatMap function to the wrapped Mono
     */

    public <B> MonoTSeq<B> flatMapT(final Function<? super A, MonoTSeq<B>> f) {
        return of(run.map(future -> Mono.from(future.flatMap(a -> f.apply(a).run.stream()
                                                                                .toList()
                                                                                .get(0)))));
    }

    private static <B> AnyMSeq<Mono<B>> narrow(final AnyMSeq<Mono<? extends B>> run) {
        return (AnyMSeq) run;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.transformer.MonoT#flatMap(java.util.function.
     * Function)
     */
    @Override
    public <B> MonoTSeq<B> flatMap(final Function<? super A, ? extends MonadicValue<? extends B>> f) {

        final AnyMSeq<Mono<? extends B>> mapped = run.map(o -> Mono.from(o.flatMap(f)));
        return of(narrow(mapped));

    }

    /**
     * Lift a function into one that accepts and returns an MonoT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling  / iteration (via Mono) and iteration (via Stream) to an existing function
    
     * 
     * 
     * @param fn Function to enhance with functionality from Mono and another monad type
     * @return Function that accepts and returns an MonoT
     */
    public static <U, R> Function<MonoTSeq<U>, MonoTSeq<R>> lift(final Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Lift a BiFunction into one that accepts and returns  MonoTs
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling / iteration (via Mono), iteration (via Stream)  and asynchronous execution (Mono) 
     * to an existing function
    
     * @param fn BiFunction to enhance with functionality from Mono and another monad type
     * @return Function that accepts and returns an MonoT
     */
    public static <U1, U2, R> BiFunction<MonoTSeq<U1>, MonoTSeq<U2>, MonoTSeq<R>> lift2(
            final BiFunction<? super U1, ? super U2, ? extends R> fn) {
        return (optTu1, optTu2) -> optTu1.flatMapT(input1 -> optTu2.map(input2 -> fn.apply(input1, input2)));
    }

    /**
     * Construct an MonoT from an AnyM that contains a monad type that contains type other than Mono
     * The values in the underlying monad will be mapped to Mono<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Mono
     * @return MonoT
     */
    public static <A> MonoTSeq<A> fromAnyM(final AnyMSeq<A> anyM) {
        return of(anyM.map(Mono::just));
    }

    /**
     * Construct an MonoT from an AnyM that wraps a monad containing  Monos
     * 
     * @param monads AnyM that contains a monad wrapping an Mono
     * @return MonoT
     */
    public static <A> MonoTSeq<A> of(final AnyMSeq<Mono<A>> monads) {
        return new MonoTSeq<>(
                              monads);
    }

    /**
     * Construct a MonoTSeq containing the supplied Mono inside a List
     * 
     * @param mono Mono to nest inside a List
     * @return MonoTSeq wrapping a List containing a Mono
     */
    public static <A> MonoTSeq<A> of(final Mono<A> mono) {
        return MonoT.fromIterable(ListX.of(mono));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("FutureTSeq[%s]", run);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.stream.ToStream#stream()
     */
    @Override
    public ReactiveSeq<A> stream() {
        return run.stream()
                  .map(cf -> cf.block());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.stream.ToStream#iterator()
     */
    @Override
    public Iterator<A> iterator() {
        return stream().iterator();
    }

    public <R> MonoTSeq<R> unitIterator(final Iterator<R> it) {
        return of(run.unitIterator(it)
                     .map(i -> Mono.just(i)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <R> MonoTSeq<R> unit(final R value) {
        return of(run.unit(Mono.just(value)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.transformer.MonoT#empty()
     */
    @Override
    public <R> MonoTSeq<R> empty() {
        return of(run.unit(Mono.empty()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.stream.CyclopsCollectable#collectable()
     */
    @Override
    public Collectable<A> collectable() {
        return stream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * isSeqPresent()
     */
    @Override
    public boolean isSeqPresent() {
        return !run.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#combine(
     * java.util.function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    public MonoTSeq<A> combine(final BiPredicate<? super A, ? super A> predicate, final BinaryOperator<A> op) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.combine(predicate, op);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#cycle(int)
     */
    @Override
    public MonoTSeq<A> cycle(final int times) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.cycle(times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#cycle(com.
     * aol.cyclops.Monoid, int)
     */
    @Override
    public MonoTSeq<A> cycle(final Monoid<A> m, final int times) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.cycle(m, times);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#cycleWhile
     * (java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> cycleWhile(final Predicate<? super A> predicate) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.cycleWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#cycleUntil
     * (java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> cycleUntil(final Predicate<? super A> predicate) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.cycleUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#zip(java.
     * lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    public <U, R> MonoTSeq<R> zip(final Iterable<? extends U> other,
            final BiFunction<? super A, ? super U, ? extends R> zipper) {

        return (MonoTSeq<R>) ValueTransformerSeq.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ValueTransformerSeq#
     * zip(java.util.stream.Stream, java.util.function.BiFunction)
     */
    @Override
    public <U, R> MonoTSeq<R> zip(final Stream<? extends U> other,
            final BiFunction<? super A, ? super U, ? extends R> zipper) {

        return (MonoTSeq<R>) ValueTransformerSeq.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ValueTransformerSeq#
     * zip(org.jooq.lambda.Seq, java.util.function.BiFunction)
     */
    @Override
    public <U, R> MonoTSeq<R> zip(final Seq<? extends U> other,
            final BiFunction<? super A, ? super U, ? extends R> zipper) {

        return (MonoTSeq<R>) ValueTransformerSeq.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#zip(java.
     * util.stream.Stream)
     */
    @Override
    public <U> MonoTSeq<Tuple2<A, U>> zip(final Stream<? extends U> other) {

        return (MonoTSeq) ValueTransformerSeq.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.ValueTransformerSeq#
     * zip(java.lang.Iterable)
     */
    @Override
    public <U> MonoTSeq<Tuple2<A, U>> zip(final Iterable<? extends U> other) {

        return (MonoTSeq) ValueTransformerSeq.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#zip(org.
     * jooq.lambda.Seq)
     */
    @Override
    public <U> MonoTSeq<Tuple2<A, U>> zip(final Seq<? extends U> other) {

        return (MonoTSeq) ValueTransformerSeq.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#zip3(java.
     * util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> MonoTSeq<Tuple3<A, S, U>> zip3(final Stream<? extends S> second, final Stream<? extends U> third) {

        return (MonoTSeq) ValueTransformerSeq.super.zip3(second, third);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#zip4(java.
     * util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> MonoTSeq<Tuple4<A, T2, T3, T4>> zip4(final Stream<? extends T2> second,
            final Stream<? extends T3> third, final Stream<? extends T4> fourth) {

        return (MonoTSeq) ValueTransformerSeq.super.zip4(second, third, fourth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * zipWithIndex()
     */
    @Override
    public MonoTSeq<Tuple2<A, Long>> zipWithIndex() {

        return (MonoTSeq<Tuple2<A, Long>>) ValueTransformerSeq.super.zipWithIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#sliding(
     * int)
     */
    @Override
    public MonoTSeq<ListX<A>> sliding(final int windowSize) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.sliding(windowSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#sliding(
     * int, int)
     */
    @Override
    public MonoTSeq<ListX<A>> sliding(final int windowSize, final int increment) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.sliding(windowSize, increment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#grouped(
     * int, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super A>> MonoTSeq<C> grouped(final int size, final Supplier<C> supplier) {

        return (MonoTSeq<C>) ValueTransformerSeq.super.grouped(size, supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * groupedUntil(java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<ListX<A>> groupedUntil(final Predicate<? super A> predicate) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.groupedUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * groupedStatefullyUntil(java.util.function.BiPredicate)
     */
    @Override
    public MonoTSeq<ListX<A>> groupedStatefullyUntil(final BiPredicate<ListX<? super A>, ? super A> predicate) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.groupedStatefullyUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * groupedWhile(java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<ListX<A>> groupedWhile(final Predicate<? super A> predicate) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.groupedWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super A>> MonoTSeq<C> groupedWhile(final Predicate<? super A> predicate,
            final Supplier<C> factory) {

        return (MonoTSeq<C>) ValueTransformerSeq.super.groupedWhile(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super A>> MonoTSeq<C> groupedUntil(final Predicate<? super A> predicate,
            final Supplier<C> factory) {

        return (MonoTSeq<C>) ValueTransformerSeq.super.groupedUntil(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#grouped(
     * int)
     */
    @Override
    public MonoTSeq<ListX<A>> grouped(final int groupSize) {

        return (MonoTSeq<ListX<A>>) ValueTransformerSeq.super.grouped(groupSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#grouped(
     * java.util.function.Function, java.util.stream.Collector)
     */
    @Override
    public <K, T, D> MonoTSeq<Tuple2<K, D>> grouped(final Function<? super A, ? extends K> classifier,
            final Collector<? super A, T, D> downstream) {

        return (MonoTSeq) ValueTransformerSeq.super.grouped(classifier, downstream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#grouped(
     * java.util.function.Function)
     */
    @Override
    public <K> MonoTSeq<Tuple2<K, Seq<A>>> grouped(final Function<? super A, ? extends K> classifier) {

        return (MonoTSeq) ValueTransformerSeq.super.grouped(classifier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#distinct()
     */
    @Override
    public MonoTSeq<A> distinct() {

        return (MonoTSeq<A>) ValueTransformerSeq.super.distinct();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#scanLeft(
     * com.aol.cyclops.Monoid)
     */
    @Override
    public MonoTSeq<A> scanLeft(final Monoid<A> monoid) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.scanLeft(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#scanLeft(
     * java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> MonoTSeq<U> scanLeft(final U seed, final BiFunction<? super U, ? super A, ? extends U> function) {

        return (MonoTSeq<U>) ValueTransformerSeq.super.scanLeft(seed, function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#scanRight(
     * com.aol.cyclops.Monoid)
     */
    @Override
    public MonoTSeq<A> scanRight(final Monoid<A> monoid) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.scanRight(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#scanRight(
     * java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> MonoTSeq<U> scanRight(final U identity, final BiFunction<? super A, ? super U, ? extends U> combiner) {

        return (MonoTSeq<U>) ValueTransformerSeq.super.scanRight(identity, combiner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#sorted()
     */
    @Override
    public MonoTSeq<A> sorted() {

        return (MonoTSeq<A>) ValueTransformerSeq.super.sorted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#sorted(
     * java.util.Comparator)
     */
    @Override
    public MonoTSeq<A> sorted(final Comparator<? super A> c) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.sorted(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#takeWhile(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> takeWhile(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.takeWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#dropWhile(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> dropWhile(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.dropWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#takeUntil(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> takeUntil(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.takeUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#dropUntil(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> dropUntil(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.dropUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#dropRight(
     * int)
     */
    @Override
    public MonoTSeq<A> dropRight(final int num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.dropRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#takeRight(
     * int)
     */
    @Override
    public MonoTSeq<A> takeRight(final int num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.takeRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#skip(long)
     */
    @Override
    public MonoTSeq<A> skip(final long num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.skip(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#skipWhile(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> skipWhile(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.skipWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#skipUntil(
     * java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> skipUntil(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.skipUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#limit(
     * long)
     */
    @Override
    public MonoTSeq<A> limit(final long num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.limit(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#limitWhile
     * (java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> limitWhile(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.limitWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#limitUntil
     * (java.util.function.Predicate)
     */
    @Override
    public MonoTSeq<A> limitUntil(final Predicate<? super A> p) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.limitUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * intersperse(java.lang.Object)
     */
    @Override
    public MonoTSeq<A> intersperse(final A value) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.intersperse(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#reverse()
     */
    @Override
    public MonoTSeq<A> reverse() {

        return (MonoTSeq<A>) ValueTransformerSeq.super.reverse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#shuffle()
     */
    @Override
    public MonoTSeq<A> shuffle() {

        return (MonoTSeq<A>) ValueTransformerSeq.super.shuffle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#skipLast(
     * int)
     */
    @Override
    public MonoTSeq<A> skipLast(final int num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.skipLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#limitLast(
     * int)
     */
    @Override
    public MonoTSeq<A> limitLast(final int num) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.limitLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#onEmpty(
     * java.lang.Object)
     */
    @Override
    public MonoTSeq<A> onEmpty(final A value) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.onEmpty(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#onEmptyGet
     * (java.util.function.Supplier)
     */
    @Override
    public MonoTSeq<A> onEmptyGet(final Supplier<? extends A> supplier) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.onEmptyGet(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.Traversable#
     * onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> MonoTSeq<A> onEmptyThrow(final Supplier<? extends X> supplier) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.onEmptyThrow(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#shuffle(
     * java.util.Random)
     */
    @Override
    public MonoTSeq<A> shuffle(final Random random) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.shuffle(random);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#slice(
     * long, long)
     */
    @Override
    public MonoTSeq<A> slice(final long from, final long to) {

        return (MonoTSeq<A>) ValueTransformerSeq.super.slice(from, to);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.control.monads.transformers.values.Traversable#sorted(
     * java.util.function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> MonoTSeq<A> sorted(final Function<? super A, ? extends U> function) {
        return (MonoTSeq) ValueTransformerSeq.super.sorted(function);
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
        if (o instanceof MonoTSeq) {
            return run.equals(((MonoTSeq) o).run);
        }
        return false;
    }

}