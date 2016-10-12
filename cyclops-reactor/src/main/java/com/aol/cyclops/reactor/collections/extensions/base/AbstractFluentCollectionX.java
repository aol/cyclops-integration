package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
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
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Streamable;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.data.collections.extensions.CollectionX;
import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.Fluxes;
import com.aol.cyclops.reactor.types.ReactorConvertable;
import com.aol.cyclops.types.IterableFunctor;
import com.aol.cyclops.types.Zippable;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract Class representing a Fluent, Lazy, extended Collection
 * 
 * @author johnmcclean
 *
 * @param <T>
 */
public abstract class AbstractFluentCollectionX<T> implements LazyFluentCollectionX<T>, ReactorConvertable<T> {
    @AllArgsConstructor
    public static class LazyCollection<T, C extends Collection<T>> implements LazyFluentCollection<T, C> {
        private volatile C list;
        private volatile Flux<T> seq;
        private final Collector<T, ?, C> collector;

        @Override
        public C get() {
            if (seq != null) {
                list = seq.collect(collector)
                          .block();
                seq = null;
            }

            return list;

        }

        @Override
        public Flux<T> flux() {
            if (seq != null) {
                return seq;
            }
            return Flux.fromIterable(list);
        }
    }

    @AllArgsConstructor
    public static class PersistentLazyCollection<T, C extends Collection<T>> implements LazyFluentCollection<T, C> {
        private volatile C list;
        private volatile Flux<T> seq;
        private final Reducer<C> reducer;

        @Override
        public C get() {
            if (seq != null) {
                list = reducer.mapReduce(seq.toStream());
                seq = null;
            }

            return list;

        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.reactor.collections.extensions.base.
         * LazyFluentCollection#stream()
         */
        @Override
        public Flux<T> flux() {
            if (seq != null)
                return seq;
            return Flux.fromIterable(list);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.types.ReactorConvertable#flux()
     */
    @Override
    abstract public Flux<T> flux();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #stream(reactor.core.publisher.Flux)
     */
    @Override
    abstract public <X> FluentCollectionX<X> stream(Flux<X> stream);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #plusLazy(java.lang.Object)
     */
    @Override
    public LazyFluentCollectionX<T> plusLazy(final T e) {

        return (LazyFluentCollectionX<T>) stream(Flux.concat(Mono.just(e)));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #plus(java.lang.Object)
     */
    @Override
    public FluentCollectionX<T> plus(final T e) {
        add(e);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #plusAll(java.util.Collection)
     */
    @Override
    public FluentCollectionX<T> plusAll(final Collection<? extends T> list) {
        addAll(list);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #minus(java.lang.Object)
     */
    @Override
    public FluentCollectionX<T> minus(final Object e) {
        remove(e);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #minusAll(java.util.Collection)
     */
    @Override
    public FluentCollectionX<T> minusAll(final Collection<?> list) {
        removeAll(list);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #plusAllLazy(java.util.Collection)
     */
    @Override
    public LazyFluentCollectionX<T> plusAllLazy(final Collection<? extends T> list) {
        return (LazyFluentCollectionX<T>) stream(flux().concatWith(ReactiveSeq.fromIterable(list)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #minusLazy(java.lang.Object)
     */
    @Override
    public LazyFluentCollectionX<T> minusLazy(final Object e) {

        return (LazyFluentCollectionX<T>) stream(flux().filter(t -> !Objects.equals(t, e)));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #minusAllLazy(java.util.Collection)
     */
    @Override
    public LazyFluentCollectionX<T> minusAllLazy(final Collection<?> list) {
        return (LazyFluentCollectionX<T>) this.removeAll((Iterable) list);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#combine(java.util
     * .function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    public FluentCollectionX<T> combine(final BiPredicate<? super T, ? super T> predicate, final BinaryOperator<T> op) {
        return stream(Fluxes.combine(flux(), predicate, op));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#reverse()
     */
    @Override
    public FluentCollectionX<T> reverse() {
        return stream(Fluxes.reverse(flux()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#filter(java.util.
     * function.Predicate)
     */
    @Override
    public FluentCollectionX<T> filter(final Predicate<? super T> pred) {
        return stream(flux().filter(pred));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#map(java.util.
     * function.Function)
     */
    @Override
    public <R> CollectionX<R> map(final Function<? super T, ? extends R> mapper) {
        return stream(flux().map(mapper));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#flatMap(java.util
     * .function.Function)
     */
    @Override
    public <R> CollectionX<R> flatMap(final Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return stream(flux().flatMap(mapper.andThen(ReactiveSeq::fromIterable)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#limit(long)
     */
    @Override
    public FluentCollectionX<T> limit(final long num) {
        return stream(flux().take(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#skip(long)
     */
    @Override
    public FluentCollectionX<T> skip(final long num) {
        return stream(flux().skip(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#takeRight(int)
     */
    @Override
    public FluentCollectionX<T> takeRight(final int num) {
        return stream(flux().takeLast(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#dropRight(int)
     */
    @Override
    public FluentCollectionX<T> dropRight(final int num) {
        return stream(flux().skipLast(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#takeWhile(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> takeWhile(final Predicate<? super T> p) {
        return stream(flux().takeWhile(p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#dropWhile(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> dropWhile(final Predicate<? super T> p) {
        return stream(flux().skipWhile(p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#takeUntil(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> takeUntil(final Predicate<? super T> p) {
        return stream(Fluxes.takeUntil(flux(), p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#dropUntil(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> dropUntil(final Predicate<? super T> p) {
        return stream(flux().skipWhile(p.negate()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#trampoline(java.
     * util.function.Function)
     */
    @Override
    public <R> FluentCollectionX<R> trampoline(final Function<? super T, ? extends Trampoline<? extends R>> mapper) {

        return stream(Fluxes.trampoline(flux(), mapper));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#slice(long,
     * long)
     */
    @Override
    public FluentCollectionX<T> slice(final long from, final long to) {
        return stream(flux().skip(from)
                            .take(to - from));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#grouped(int)
     */
    @Override
    public FluentCollectionX<ListX<T>> grouped(final int groupSize) {
        return stream(Fluxes.grouped(flux(), groupSize));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#grouped(java.util
     * .function.Function, java.util.stream.Collector)
     */
    @Override
    public <K, A, D> FluentCollectionX<Tuple2<K, D>> grouped(final Function<? super T, ? extends K> classifier,
            final Collector<? super T, A, D> downstream) {
        return stream(Fluxes.grouped(flux(), classifier, downstream));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#grouped(java.util
     * .function.Function)
     */
    @Override
    public <K> FluentCollectionX<Tuple2<K, Seq<T>>> grouped(final Function<? super T, ? extends K> classifier) {
        return stream(Fluxes.grouped(flux(), classifier));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(java.lang.
     * Iterable)
     */
    @Override
    public <U> FluentCollectionX<Tuple2<T, U>> zip(final Iterable<? extends U> other) {
        return (FluentCollectionX) stream(flux().zipWithIterable(other, Tuple::tuple));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(java.lang.
     * Iterable, java.util.function.BiFunction)
     */
    @Override
    public <U, R> FluentCollectionX<R> zip(final Iterable<? extends U> other,
            final BiFunction<? super T, ? super U, ? extends R> zipper) {
        return stream(flux().zipWith(ReactiveSeq.fromIterable(other), zipper));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#sliding(int)
     */
    @Override
    public FluentCollectionX<ListX<T>> sliding(final int windowSize) {
        return stream(Fluxes.sliding(flux(), windowSize, 1)
                            .map(ListX::fromPublisher));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#sliding(int,
     * int)
     */
    @Override
    public FluentCollectionX<ListX<T>> sliding(final int windowSize, final int increment) {
        return stream(Fluxes.sliding(flux(), windowSize, increment)
                            .map(ListX::fromPublisher));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#scanLeft(com.aol.
     * cyclops.Monoid)
     */
    @Override
    public FluentCollectionX<T> scanLeft(final Monoid<T> monoid) {
        return stream(flux().scan(monoid.zero(), (BiFunction) monoid.combiner()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#scanLeft(java.
     * lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> FluentCollectionX<U> scanLeft(final U seed,
            final BiFunction<? super U, ? super T, ? extends U> function) {
        return stream(flux().scan(seed, (BiFunction) function));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#scanRight(com.aol
     * .cyclops.Monoid)
     */
    @Override
    public FluentCollectionX<T> scanRight(final Monoid<T> monoid) {
        return stream(Fluxes.scanRight(flux(), monoid));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#scanRight(java.
     * lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> FluentCollectionX<U> scanRight(final U identity,
            final BiFunction<? super T, ? super U, ? extends U> combiner) {
        return stream(Fluxes.scanRight(flux(), identity, combiner));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#sorted(java.util.
     * function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> FluentCollectionX<T> sorted(
            final Function<? super T, ? extends U> function) {
        return stream(Fluxes.sorted(flux(), function));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#cycle(int)
     */
    @Override
    public FluentCollectionX<T> cycle(final int times) {

        return stream(flux().repeat(times));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#cycle(com.aol.
     * cyclops.Monoid, int)
     */
    @Override
    public FluentCollectionX<T> cycle(final Monoid<T> m, final int times) {

        return stream(Fluxes.cycle(flux(), m, times));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#cycleWhile(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> cycleWhile(final Predicate<? super T> predicate) {

        return stream(Fluxes.cycleWhile(flux(), predicate));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#cycleUntil(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> cycleUntil(final Predicate<? super T> predicate) {

        return stream(Fluxes.cycleUntil(flux(), predicate));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(org.jooq.
     * lambda.Seq)
     */
    @Override
    public <U> FluentCollectionX<Tuple2<T, U>> zip(final Seq<? extends U> other) {

        return (FluentCollectionX) stream(flux().zipWithIterable(other, Tuple::tuple));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip3(java.util.
     * stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> FluentCollectionX<Tuple3<T, S, U>> zip3(final Stream<? extends S> second,
            final Stream<? extends U> third) {

        return (FluentCollectionX) stream(Flux.zip(flux(), Flux.fromStream(second), Flux.fromStream(third))
                                              .map(t -> Tuple.tuple(t.getT1(), t.getT2(), t.getT3())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip4(java.util.
     * stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> FluentCollectionX<Tuple4<T, T2, T3, T4>> zip4(final Stream<? extends T2> second,
            final Stream<? extends T3> third, final Stream<? extends T4> fourth) {

        return (FluentCollectionX) stream(Flux.zip(flux(), Flux.fromStream(second), Flux.fromStream(third),
                                                   Flux.fromStream(fourth))
                                              .map(t -> Tuple.tuple(t.getT1(), t.getT2(), t.getT3(), t.getT4())));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zipWithIndex()
     */
    @Override
    public FluentCollectionX<Tuple2<T, Long>> zipWithIndex() {

        return stream(flux().zipWith(ReactiveSeq.rangeLong(0, Long.MAX_VALUE), Tuple::tuple));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#distinct()
     */
    @Override
    public FluentCollectionX<T> distinct() {

        return stream(flux().distinct());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#sorted()
     */
    @Override
    public FluentCollectionX<T> sorted() {

        return stream(Fluxes.sorted(flux()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#sorted(java.util.
     * Comparator)
     */
    @Override
    public FluentCollectionX<T> sorted(final Comparator<? super T> c) {

        return stream(Fluxes.sorted(flux(), c));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#skipWhile(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> skipWhile(final Predicate<? super T> p) {

        return stream(flux().skipWhile(p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#skipUntil(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> skipUntil(final Predicate<? super T> p) {

        return stream(flux().skipWhile(p.negate()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#limitWhile(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> limitWhile(final Predicate<? super T> p) {

        return stream(flux().takeWhile(p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#limitUntil(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> limitUntil(final Predicate<? super T> p) {

        return stream(Fluxes.takeUntil(flux(), p));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#intersperse(java.
     * lang.Object)
     */
    @Override
    public FluentCollectionX<T> intersperse(final T value) {

        return stream(Fluxes.intersperse(flux(), value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#shuffle()
     */
    @Override
    public FluentCollectionX<T> shuffle() {

        return stream(Fluxes.shuffle(flux()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#skipLast(int)
     */
    @Override
    public FluentCollectionX<T> skipLast(final int num) {

        return stream(flux().skipLast(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#limitLast(int)
     */
    @Override
    public FluentCollectionX<T> limitLast(final int num) {

        return stream(flux().takeLast(num));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#onEmpty(java.lang
     * .Object)
     */
    @Override
    public FluentCollectionX<T> onEmpty(final T value) {
        return stream(Fluxes.onEmpty(flux(), value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#onEmptyGet(java.
     * util.function.Supplier)
     */
    @Override
    public FluentCollectionX<T> onEmptyGet(final Supplier<? extends T> supplier) {
        return stream(Fluxes.onEmptyGet(flux(), supplier));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#onEmptyThrow(java
     * .util.function.Supplier)
     */
    @Override
    public <X extends Throwable> FluentCollectionX<T> onEmptyThrow(final Supplier<? extends X> supplier) {
        return stream(Fluxes.onEmptyThrow(flux(), supplier));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#shuffle(java.util
     * .Random)
     */
    @Override
    public FluentCollectionX<T> shuffle(final Random random) {
        return stream(Fluxes.shuffle(flux(), random));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
     */
    @Override
    public <U> FluentCollectionX<U> ofType(final Class<? extends U> type) {

        return stream(Fluxes.ofType(flux(), type));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#filterNot(java.
     * util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> filterNot(final Predicate<? super T> fn) {
        return stream(flux().filter(fn.negate()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#notNull()
     */
    @Override
    public FluentCollectionX<T> notNull() {
        return stream(flux().filter(Objects::nonNull));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#removeAll(java.
     * util.stream.Stream)
     */
    @Override
    public FluentCollectionX<T> removeAll(final Stream<? extends T> stream) {
        return stream(Fluxes.removeAll(flux(), ReactiveSeq.fromStream(stream)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#removeAll(org.
     * jooq.lambda.Seq)
     */
    @Override
    public FluentCollectionX<T> removeAll(final Seq<? extends T> stream) {

        return stream(Fluxes.removeAll(flux(), stream));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#removeAll(java.
     * lang.Iterable)
     */
    @Override
    public FluentCollectionX<T> removeAll(final Iterable<? extends T> it) {
        return stream(Fluxes.removeAll(flux(), it));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#removeAll(java.
     * lang.Object[])
     */
    @Override
    public FluentCollectionX<T> removeAll(final T... values) {
        return stream(Fluxes.removeAll(flux(), Arrays.asList(values)));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#retainAll(java.
     * lang.Iterable)
     */
    @Override
    public FluentCollectionX<T> retainAll(final Iterable<? extends T> it) {
        return stream(Fluxes.retainAll(flux(), it));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#retainAll(java.
     * util.stream.Stream)
     */
    @Override
    public FluentCollectionX<T> retainAll(final Stream<? extends T> stream) {
        return stream(Fluxes.retainAll(flux(), Seq.seq(stream)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#retainAll(org.
     * jooq.lambda.Seq)
     */
    @Override
    public FluentCollectionX<T> retainAll(final Seq<? extends T> stream) {
        return stream(Fluxes.retainAll(flux(), stream));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#retainAll(java.
     * lang.Object[])
     */
    @Override
    public FluentCollectionX<T> retainAll(final T... values) {
        return stream(Fluxes.retainAll(flux(), Arrays.asList(values)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#cast(java.lang.
     * Class)
     */
    @Override
    public <U> FluentCollectionX<U> cast(final Class<? extends U> type) {
        return stream(flux().map(e -> type.cast(e)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#patternMatch(java
     * .util.function.Function, java.util.function.Supplier)
     */
    @Override
    public <R> FluentCollectionX<R> patternMatch(final Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,
            final Supplier<? extends R> otherwise) {

        return stream(Fluxes.patternMatch(flux(), case1, otherwise));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#permutations()
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> permutations() {
        return stream(Flux.from(Streamable.fromPublisher(flux())
                                          .permutations()
                                          .map(s -> s.stream())));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#combinations(int)
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> combinations(final int size) {
        return stream(Flux.from(Streamable.fromPublisher(flux())
                                          .combinations(size)
                                          .map(s -> s.stream())));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#combinations()
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> combinations() {
        return stream(Flux.from(Streamable.fromPublisher(flux())
                                          .combinations()
                                          .map(s -> s.stream())));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#grouped(int,
     * java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> grouped(final int size, final Supplier<C> supplier) {

        return stream(Fluxes.grouped(flux(), size, supplier));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#groupedUntil(java
     * .util.function.Predicate)
     */
    @Override
    public FluentCollectionX<ListX<T>> groupedUntil(final Predicate<? super T> predicate) {

        return stream(Fluxes.groupedUntil(flux(), predicate));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#groupedWhile(java
     * .util.function.Predicate)
     */
    @Override
    public FluentCollectionX<ListX<T>> groupedWhile(final Predicate<? super T> predicate) {

        return stream(Fluxes.groupedWhile(flux(), predicate));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#groupedWhile(java
     * .util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> groupedWhile(final Predicate<? super T> predicate,
            final Supplier<C> factory) {
        return stream(Fluxes.groupedWhile(flux(), predicate, factory));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#groupedUntil(java
     * .util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> groupedUntil(final Predicate<? super T> predicate,
            final Supplier<C> factory) {
        return stream(Fluxes.groupedWhile(flux(), predicate.negate(), factory));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#
     * groupedStatefullyUntil(java.util.function.BiPredicate)
     */
    @Override
    public FluentCollectionX<ListX<T>> groupedStatefullyUntil(
            final BiPredicate<ListX<? super T>, ? super T> predicate) {
        return stream(Fluxes.groupedStatefullyUntil(flux(), predicate));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#from(java.util.
     * Collection)
     */
    @Override
    public abstract <T1> FluentCollectionX<T1> from(Collection<T1> c);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#peek(java.util.
     * function.Consumer)
     */
    @Override
    public FluentCollectionX<T> peek(final Consumer<? super T> c) {
        return stream(flux().map(e -> {
            c.accept(e);
            return e;
        }));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(org.jooq.
     * lambda.Seq, java.util.function.BiFunction)
     */
    @Override
    public <U, R> FluentCollectionX<R> zip(final Seq<? extends U> other,
            final BiFunction<? super T, ? super U, ? extends R> zipper) {
        return stream(flux().zipWith(ReactiveSeq.fromStream(other), zipper));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(java.util.
     * stream.Stream, java.util.function.BiFunction)
     */
    @Override
    public <U, R> FluentCollectionX<R> zip(final Stream<? extends U> other,
            final BiFunction<? super T, ? super U, ? extends R> zipper) {
        return stream(flux().zipWith(ReactiveSeq.fromStream(other), zipper));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.CollectionX#zip(java.util.
     * stream.Stream)
     */
    @Override
    public <U> FluentCollectionX<Tuple2<T, U>> zip(final Stream<? extends U> other) {
        return zip(ReactiveSeq.fromStream(other));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.IterableFunctor#unitIterator(java.util.Iterator)
     */
    @Override
    public abstract <U> IterableFunctor<U> unitIterator(Iterator<U> U);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.function.BiFunction,
     * org.reactivestreams.Publisher)
     */
    @Override
    public <T2, R> Zippable<R> zip(final BiFunction<? super T, ? super T2, ? extends R> fn,
            final Publisher<? extends T2> publisher) {
        // TODO Auto-generated method stub
        return LazyFluentCollectionX.super.zip(fn, publisher);
    }

}
