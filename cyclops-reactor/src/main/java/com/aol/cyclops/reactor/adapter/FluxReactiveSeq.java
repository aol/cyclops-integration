package com.aol.cyclops.reactor.adapter;

import com.aol.cyclops2.internal.stream.ReactiveStreamX;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.stream.HeadAndTail;
import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.async.adapters.QueueFactory;
import cyclops.collections.immutable.VectorX;
import cyclops.collections.mutable.ListX;
import cyclops.control.Maybe;
import cyclops.control.lazy.Either;
import cyclops.function.Monoid;
import cyclops.function.Reducer;
import cyclops.monads.AnyM;
import cyclops.monads.Witness;
import cyclops.monads.Witness.reactiveSeq;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Streams.*;


@AllArgsConstructor
public class FluxReactiveSeq<T> implements ReactiveSeq<T> {
    @Wither
    Flux<T> flux;

    public <R> FluxReactiveSeq<R> flux(Flux<R> flux){
        return new FluxReactiveSeq<>(flux);
    }

    @Override
    public <R> ReactiveSeq<R> coflatMap(Function<? super ReactiveSeq<T>, ? extends R> fn) {
        return flux(Flux.just(fn.apply(this)));
    }

    @Override
    public <T1> ReactiveSeq<T1> unit(T1 unit) {
        return flux(Flux.just(unit));
    }

    @Override
    public <U> U foldRight(U identity, BiFunction<? super T, ? super U, ? extends U> accumulator) {
        return flux(flux.reduce(identity,(a,b)->accumulator.apply(a,b)).block());
    }

    @Override
    public <U, R> ReactiveSeq<R> zipS(Stream<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        if(other instanceof Publisher){
            return zipP((Publisher<U>)other,zipper);
        }
        return flux(flux.zipWithIterable(ReactiveSeq.fromStream((Stream<U>)other),zipper));
    }

    @Override
    public ReactiveSeq<T> cycle() {
        return flux(flux.repeat());
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> duplicate() {
        return Spouts.from(flux).duplicate();
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> duplicate(Supplier<Deque<T>> bufferFactory) {
        return Spouts.from(flux).duplicate(bufferFactory);
    }

    @Override
    public Tuple3<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> triplicate() {
        return Spouts.from(flux).triplicate();
    }

    @Override
    public Tuple3<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> triplicate(Supplier<Deque<T>> bufferFactory) {
        return Spouts.from(flux).triplicate(bufferFactory);
    }

    @Override
    public Tuple4<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> quadruplicate() {
        return Spouts.from(flux).quadruplicate();
    }

    @Override
    public Tuple4<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> quadruplicate(Supplier<Deque<T>> bufferFactory) {
        return Spouts.from(flux).quadruplicate(bufferFactory);
    }

    @Override
    public Tuple2<Optional<T>, ReactiveSeq<T>> splitAtHead() {
        return Spouts.from(flux).splitAtHead();
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> splitAt(int where) {
        return Spouts.from(flux).splitAt(where);
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> splitBy(Predicate<T> splitter) {
        return Spouts.from(flux).splitBy(splitter);
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> partition(Predicate<? super T> splitter) {
        return Spouts.from(flux).partition(splitter);
    }

    @Override
    public <U> ReactiveSeq<Tuple2<T, U>> zipS(Stream<? extends U> other) {
        if(other instanceof Publisher){
            return zipP((Publisher<U>)other);
        }
        return zipS(other,Tuple::tuple);
    }

    @Override
    public <S, U> ReactiveSeq<Tuple3<T, S, U>> zip3(Iterable<? extends S> second, Iterable<? extends U> third) {
        return zip(second,Tuple::tuple).zip(third,(a,b)->Tuple.tuple(a.v1,a.v2,b));
    }

    @Override
    public <T2, T3, T4> ReactiveSeq<Tuple4<T, T2, T3, T4>> zip4(Iterable<? extends T2> second, Iterable<? extends T3> third, Iterable<? extends T4> fourth) {
        return zip(second,Tuple::tuple).zip(third,(a,b)->Tuple.tuple(a.v1,a.v2,b))
                .zip(fourth,(a,b)->(Tuple4<T,T2,T3,T4>)Tuple.tuple(a.v1,a.v2,a.v3,b));
    }

    @Override
    public ReactiveSeq<VectorX<T>> sliding(int windowSize, int increment) {
        return Spouts.from(flux).sliding(windowSize,increment);
    }

    @Override
    public ReactiveSeq<ListX<T>> grouped(int groupSize) {
        return flux(flux.buffer(groupSize).map(ListX::fromIterable));
    }

    @Override
    public ReactiveSeq<ListX<T>> groupedStatefullyUntil(BiPredicate<ListX<? super T>, ? super T> predicate) {
        return Spouts.from(flux).groupedStatefullyUntil(predicate);
    }

    @Override
    public <C extends Collection<T>, R> ReactiveSeq<R> groupedStatefullyUntil(BiPredicate<C, ? super T> predicate, Supplier<C> factory, Function<? super C, ? extends R> finalizer) {
        return Spouts.from(flux).groupedStatefullyUntil(predicate,factory,finalizer);
    }

    @Override
    public ReactiveSeq<ListX<T>> groupedStatefullyWhile(BiPredicate<ListX<? super T>, ? super T> predicate) {
        return Spouts.from(flux).groupedStatefullyWhile(predicate);
    }

    @Override
    public <C extends Collection<T>, R> ReactiveSeq<R> groupedStatefullyWhile(BiPredicate<C, ? super T> predicate, Supplier<C> factory, Function<? super C, ? extends R> finalizer) {
        return Spouts.from(flux).groupedStatefullyWhile(predicate,factory,finalizer);
    }

    @Override
    public ReactiveSeq<ListX<T>> groupedBySizeAndTime(int size, long time, TimeUnit t) {
        return flux(flux.buffer(size, Duration.ofNanos(t.toNanos(time))).map(ListX::fromIterable));
    }

    @Override
    public <C extends Collection<? super T>> ReactiveSeq<C> groupedBySizeAndTime(int size, long time, TimeUnit unit, Supplier<C> factory) {
        return flux(flux.buffer(size, Duration.ofNanos(unit.toNanos(time)),factory));
    }

    @Override
    public <C extends Collection<? super T>, R> ReactiveSeq<R> groupedBySizeAndTime(int size, long time, TimeUnit unit, Supplier<C> factory, Function<? super C, ? extends R> finalizer) {
        return flux(flux.buffer(size, Duration.ofNanos(unit.toNanos(time)),factory).map(finalizer));
    }

    @Override
    public <C extends Collection<? super T>, R> ReactiveSeq<R> groupedByTime(long time, TimeUnit unit, Supplier<C> factory, Function<? super C, ? extends R> finalizer) {
        return groupedBySizeAndTime(Integer.MAX_VALUE,time,unit,factory,finalizer);
    }

    @Override
    public ReactiveSeq<ListX<T>> groupedByTime(long time, TimeUnit t) {
        return groupedBySizeAndTime(Integer.MAX_VALUE,time,t);
    }

    @Override
    public <C extends Collection<? super T>> ReactiveSeq<C> groupedByTime(long time, TimeUnit unit, Supplier<C> factory) {
        return groupedBySizeAndTime(Integer.MAX_VALUE,time,unit,factory);
    }

    @Override
    public <C extends Collection<? super T>> ReactiveSeq<C> grouped(int size, Supplier<C> supplier) {
        return flux(flux.buffer(size,supplier));
    }

    @Override
    public ReactiveSeq<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
        return Spouts.from(flux).groupedWhile(predicate);
    }

    @Override
    public <C extends Collection<? super T>> ReactiveSeq<C> groupedWhile(Predicate<? super T> predicate, Supplier<C> factory) {
        return Spouts.from(flux).groupedWhile(predicate,factory);
    }

    @Override
    public ReactiveSeq<T> distinct() {
        return flux(flux.distinct());
    }

    @Override
    public <U> ReactiveSeq<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {
        return flux(flux.scan(seed,(a,b)->function.apply(a,b)));
    }

    @Override
    public ReactiveSeq<T> sorted() {
        return flux(flux.sort());
    }

    @Override
    public ReactiveSeq<T> skip(long num) {
        return drop(num);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        flux.subscribe(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        flux.subscribe(action);
    }

    @Override
    public Object[] toArray() {
        return Spouts.from(flux).toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return Spouts.from(flux).toArray(generator);
    }

    @Override
    public ReactiveSeq<T> skipWhile(Predicate<? super T> p) {
        return flux(flux.skipWhile(p));
    }

    @Override
    public ReactiveSeq<T> limit(long num) {
        return flux(flux.take(num));
    }

    @Override
    public ReactiveSeq<T> limitWhile(Predicate<? super T> p) {
        return flux(flux.takeWhile(p));
    }

    @Override
    public ReactiveSeq<T> limitUntil(Predicate<? super T> p) {
        return flux(flux.takeUntil(p));
    }

    @Override
    public ReactiveSeq<T> parallel() {
        return this;
    }

    @Override
    public boolean allMatch(Predicate<? super T> c) {
        return Spouts.from(flux).allMatch(c);
    }

    @Override
    public boolean anyMatch(Predicate<? super T> c) {
        return Spouts.from(flux).anyMatch(c);
    }

    @Override
    public boolean xMatch(int num, Predicate<? super T> c) {
        return Spouts.from(flux).xMatch(num,c);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> c) {
        return Spouts.from(flux).noneMatch(c);
    }

    @Override
    public String join() {
        return Spouts.from(flux).join();
    }

    @Override
    public String join(String sep) {
        return Spouts.from(flux).join(sep);
    }

    @Override
    public String join(String sep, String start, String end) {
        return Spouts.from(flux).join(sep,start,end);
    }

    @Override
    public HeadAndTail<T> headAndTail() {
        return Spouts.from(flux).headAndTail();
    }

    @Override
    public Optional<T> findFirst() {
        return Spouts.from(flux).findFirst();
    }

    @Override
    public Maybe<T> findOne() {
        return Spouts.from(flux).findOne();
    }

    @Override
    public Either<Throwable, T> findFirstOrError() {
        return Spouts.from(flux).findFirstOrError();
    }

    @Override
    public Optional<T> findAny() {
        return Spouts.from(flux).findAny();
    }

    @Override
    public <R> R mapReduce(Reducer<R> reducer) {
        return Spouts.from(flux).mapReduce(reducer);
    }

    @Override
    public <R> R mapReduce(Function<? super T, ? extends R> mapper, Monoid<R> reducer) {
        return Spouts.from(flux).mapReduce(mapper,reducer);
    }

    @Override
    public T reduce(Monoid<T> reducer) {
        return Spouts.from(flux).reduce(reducer);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return Spouts.from(flux).reduce(accumulator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return Spouts.from(flux).reduce(identity,accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return Spouts.from(flux).reduce(identity, accumulator, combiner);
    }

    @Override
    public ListX<T> reduce(Stream<? extends Monoid<T>> reducers) {
        return Spouts.from(flux).reduce(reducers);
    }

    @Override
    public ListX<T> reduce(Iterable<? extends Monoid<T>> reducers) {
        return Spouts.from(flux).reduce(reducers);
    }

    @Override
    public T foldRight(Monoid<T> reducer) {
        return Spouts.from(flux).foldRight(reducer);
    }

    @Override
    public T foldRight(T identity, BinaryOperator<T> accumulator) {
        return Spouts.from(flux).foldRight(identity,accumulator);
    }

    @Override
    public <T1> T1 foldRightMapToType(Reducer<T1> reducer) {
        return Spouts.from(flux).foldRightMapToType(reducer);
    }

    @Override
    public ReactiveSeq<T> stream() {
        return Spouts.from(flux);
    }

    @Override
    public <U> Traversable<U> unitIterator(Iterator<U> U) {
        return new FluxReactiveSeq<>(Flux.fromIterable(()->U));
    }

    @Override
    public boolean startsWithIterable(Iterable<T> iterable) {
        return Spouts.from(flux).startsWithIterable(iterable);
    }

    @Override
    public boolean startsWith(Stream<T> stream) {
        return Spouts.from(flux).startsWith(stream);
    }

    @Override
    public AnyMSeq<reactiveSeq, T> anyM() {
        return AnyM.fromStream(this);
    }

    @Override
    public <R> ReactiveSeq<R> map(Function<? super T, ? extends R> fn) {
        return flux(flux.map(fn));
    }

    @Override
    public <R> ReactiveSeq<R> flatMap(Function<? super T, ? extends Stream<? extends R>> fn) {
        return flux(flux.flatMap(s->ReactiveSeq.fromStream(fn.apply(s)));
    }

    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return Spouts.from(flux).flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return Spouts.from(flux).flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return Spouts.from(flux).flatMapToDouble(mapper);
    }

    @Override
    public <R> ReactiveSeq<R> flatMapAnyM(Function<? super T, AnyM<Witness.stream, ? extends R>> fn) {
        return flux(flux.flatMap(fn));
    }

    @Override
    public <R> ReactiveSeq<R> flatMapI(Function<? super T, ? extends Iterable<? extends R>> fn) {
        return flux(flux.flatMapIterable(fn));
    }

    @Override
    public <R> ReactiveSeq<R> flatMapP(int maxConcurrency, QueueFactory<R> factory, Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return flux(flux.flatMap(mapper,maxConcurrency));
    }

    @Override
    public <R> ReactiveSeq<R> flatMapP(Function<? super T, ? extends Publisher<? extends R>> fn) {
        return flux(flux.flatMap(fn));
    }

    @Override
    public <R> ReactiveSeq<R> flatMapP(int maxConcurrency, Function<? super T, ? extends Publisher<? extends R>> fn) {
        return flux(flux.flatMap(fn,maxConcurrency));
    }

    @Override
    public <R> ReactiveSeq<R> flatMapStream(Function<? super T, BaseStream<? extends R, ?>> fn) {
        
        return flux(flux.flatMap(fn.andThen(s->
            s instanceof ReactiveSeq ? (ReactiveSeq)s : ReactiveSeq.fromSpliterator(s.spliterator())
        )));
    }

    @Override
    public ReactiveSeq<T> filter(Predicate<? super T> fn) {
        return flux(flux.filter(fn));
    }

    @Override
    public Iterator<T> iterator() {
        return flux.toIterable().iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return flux.toIterable().spliterator();
    }

    @Override
    public boolean isParallel() {
        return false;
    }

    @Override
    public ReactiveSeq<T> sequential() {
        return this;
    }

    @Override
    public ReactiveSeq<T> unordered() {
        return this;
    }

    @Override
    public ReactiveSeq<T> reverse() {
        return Spouts.from(flux).reverse();
    }

    @Override
    public ReactiveSeq<T> onClose(Runnable closeHandler) {
        return flux(flux.doOnComplete(closeHandler));
    }

    @Override
    public void close() {
        
    }

    @Override
    public ReactiveSeq<T> prependS(Stream<? extends T> stream) {
        return Spouts.from(flux).prependS(stream);
    }

    @Override
    public ReactiveSeq<T> append(T[] values) {
        return null;
    }

    @Override
    public ReactiveSeq<T> append(T value) {
        return null;
    }

    @Override
    public ReactiveSeq<T> prepend(T value) {
        return null;
    }

    @Override
    public ReactiveSeq<T> prepend(T[] values) {
        return null;
    }

    @Override
    public boolean endsWithIterable(Iterable<T> iterable) {
        return false;
    }

    @Override
    public boolean endsWith(Stream<T> stream) {
        return false;
    }

    @Override
    public ReactiveSeq<T> skip(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public ReactiveSeq<T> limit(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public ReactiveSeq<T> skipLast(int num) {
        return null;
    }

    @Override
    public ReactiveSeq<T> limitLast(int num) {
        return null;
    }

    @Override
    public T firstValue() {
        return null;
    }

    @Override
    public ReactiveSeq<T> onEmptySwitch(Supplier<? extends Stream<T>> switchTo) {
        return null;
    }

    @Override
    public ReactiveSeq<T> onEmptyGet(Supplier<? extends T> supplier) {
        return null;
    }

    @Override
    public <X extends Throwable> ReactiveSeq<T> onEmptyThrow(Supplier<? extends X> supplier) {
        return null;
    }

    @Override
    public <U> ReactiveSeq<T> distinct(Function<? super T, ? extends U> keyExtractor) {
        return null;
    }

    @Override
    public ReactiveSeq<T> xPer(int x, long time, TimeUnit t) {
        return null;
    }

    @Override
    public ReactiveSeq<T> onePer(long time, TimeUnit t) {
        return null;
    }

    @Override
    public ReactiveSeq<T> debounce(long time, TimeUnit t) {
        return null;
    }

    @Override
    public ReactiveSeq<T> fixedDelay(long l, TimeUnit unit) {
        return Spouts.from(flux).fixedDelay(l,unit);
    }

    @Override
    public ReactiveSeq<T> jitter(long maxJitterPeriodInNanos) {
        return Spouts.from(flux).jitter(maxJitterPeriodInNanos);
    }

    @Override
    public ReactiveSeq<T> recover(Function<? super Throwable, ? extends T> fn) {
        return Spouts.from(flux).recover(fn);
    }

    @Override
    public <EX extends Throwable> ReactiveSeq<T> recover(Class<EX> exceptionClass, Function<? super EX, ? extends T> fn) {
        return Spouts.from(flux).recover(exceptionClass,fn);
    }

    @Override
    public long count() {
        return Spouts.from(flux).count();
    }

    @Override
    public ReactiveSeq<T> appendS(Stream<? extends T> other) {
        return null;
    }

    @Override
    public ReactiveSeq<T> append(Iterable<? extends T> other) {
        return null;
    }

    @Override
    public ReactiveSeq<T> prepend(Iterable<? extends T> other) {
        return null;
    }

    @Override
    public ReactiveSeq<T> cycle(long times) {
        return flux(flux.repeat(times));
    }

    @Override
    public ReactiveSeq<T> skipWhileClosed(Predicate<? super T> predicate) {
        return Spouts.from(flux).skipWhileClosed(predicate);
    }

    @Override
    public ReactiveSeq<T> limitWhileClosed(Predicate<? super T> predicate) {
        return Spouts.from(flux).limitWhileClosed(predicate);
    }

    @Override
    public String format() {
        return Spouts.from(flux).format();
    }

    @Override
    public ReactiveSeq<T> changes() {
        return Spouts.from(flux).changes();
    }
}
