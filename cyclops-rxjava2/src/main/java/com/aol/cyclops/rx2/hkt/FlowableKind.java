package com.aol.cyclops.rx2.hkt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.flowable;
import cyclops.stream.ReactiveSeq;
import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.annotations.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.*;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.LongConsumer;
import io.reactivex.functions.Predicate;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.schedulers.Timed;
import io.reactivex.subscribers.TestSubscriber;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;



import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Simulates Higher Kinded Types for Reactor Flowable's
 * 
 * FlowableKind is a Flowable and a Higher Kinded Type (flowable,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Flowable
 */


public final class FlowableKind<T> implements Higher<flowable, T>, Publisher<T> {

    private FlowableKind(Flowable<T> boxed) {
        this.boxed = boxed;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        boxed.subscribe(s);
    } /**
     * @return wrapped Obsverable
     */
    public Flowable<T> narrow() {
        return boxed;
    }


    
    /**
     * Construct a HKT encoded completed Flowable
     * 
     * @param value To encode inside a HKT encoded Flowable
     * @return Completed HKT encoded FFlowable
     */
    public static <T> FlowableKind<T> just(T value){
        
        return widen(Flowable.just(value));
    }
    public static <T> FlowableKind<T> just(T... values){
            
            return widen(Flowable.fromArray(values));
    }
    public static <T> FlowableKind<T> empty(){
        return widen(Flowable.empty());
    }

    /**
     * Convert a Flowable to a simulated HigherKindedType that captures Flowable nature
     * and Flowable element data type separately. Recover via @see FlowableKind#narrow
     * 
     * If the supplied Flowable implements FlowableKind it is returned already, otherwise it
     * is wrapped into a Flowable implementation that does implement FlowableKind
     * 
     * @param completableFlowable Flowable to widen to a FlowableKind
     * @return FlowableKind encoding HKT info about Flowables
     */
    public static <T> FlowableKind<T> widen(final Flowable<T> completableFlowable) {
        
        return new FlowableKind<T>(
                         completableFlowable);
    }
    /**
     * Widen a FlowableKind nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a Flowable to widen
     * @return HKT encoded type with a widened Flowable
     */
    public static <C2,T> Higher<C2, Higher<flowable,T>> widen2(Higher<C2, FlowableKind<T>> flux){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<StreamType.µ,T> must be a StreamType
        return (Higher)flux;
    }
    public static <T> FlowableKind<T> widen(final Publisher<T> completableFlowable) {
        
        return new FlowableKind<T>(Flowable.fromPublisher(
                         completableFlowable));
    }
        
    
    /**
     * Convert the raw Higher Kinded Type for FlowableKind types into the FlowableKind type definition class
     * 
     * @param future HKT encoded list into a FlowableKind
     * @return FlowableKind
     */
    public static <T> FlowableKind<T> narrowK(final Higher<flowable, T> future) {
       return (FlowableKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Flowable into
     * 
     * @param completableFlowable Type Constructor to convert back into narrowed type
     * @return Flowable from Higher Kinded Type
     */
    public static <T> Flowable<T> narrow(final Higher<flowable, T> completableFlowable) {
      
            return ((FlowableKind<T>)completableFlowable).narrow();
           
       

    }


    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<Boolean> all(Predicate<? super T> predicate) {
        return boxed.all(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> ambWith(Publisher<? extends T> other) {
        return boxed.ambWith(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<Boolean> any(Predicate<? super T> predicate) {
        return boxed.any(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingFirst() {
        return boxed.blockingFirst();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingFirst(T defaultItem) {
        return boxed.blockingFirst(defaultItem);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public void blockingForEach(Consumer<? super T> onNext) {
        boxed.blockingForEach(onNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Iterable<T> blockingIterable() {
        return boxed.blockingIterable();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Iterable<T> blockingIterable(int bufferSize) {
        return boxed.blockingIterable(bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingLast() {
        return boxed.blockingLast();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingLast(T defaultItem) {
        return boxed.blockingLast(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Iterable<T> blockingLatest() {
        return boxed.blockingLatest();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Iterable<T> blockingMostRecent(T initialItem) {
        return boxed.blockingMostRecent(initialItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Iterable<T> blockingNext() {
        return boxed.blockingNext();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingSingle() {
        return boxed.blockingSingle();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public T blockingSingle(T defaultItem) {
        return boxed.blockingSingle(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Future<T> toFuture() {
        return boxed.toFuture();
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public void blockingSubscribe() {
        boxed.blockingSubscribe();
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext) {
        boxed.blockingSubscribe(onNext);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        boxed.blockingSubscribe(onNext, onError);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        boxed.blockingSubscribe(onNext, onError, onComplete);
    }

    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public void blockingSubscribe(Subscriber<? super T> subscriber) {
        boxed.blockingSubscribe(subscriber);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<List<T>> buffer(int count) {
        return boxed.buffer(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<List<T>> buffer(int count, int skip) {
        return boxed.buffer(count, skip);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Flowable<U> buffer(int count, int skip, Callable<U> bufferSupplier) {
        return boxed.buffer(count, skip, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Flowable<U> buffer(int count, Callable<U> bufferSupplier) {
        return boxed.buffer(count, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<List<T>> buffer(long timespan, long timeskip, TimeUnit unit) {
        return boxed.buffer(timespan, timeskip, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<List<T>> buffer(long timespan, long timeskip, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.buffer(timespan, timeskip, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public <U extends Collection<? super T>> Flowable<U> buffer(long timespan, long timeskip, TimeUnit unit, io.reactivex.Scheduler scheduler, Callable<U> bufferSupplier) {
        return boxed.buffer(timespan, timeskip, unit, scheduler, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<List<T>> buffer(long timespan, TimeUnit unit) {
        return boxed.buffer(timespan, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<List<T>> buffer(long timespan, TimeUnit unit, int count) {
        return boxed.buffer(timespan, unit, count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<List<T>> buffer(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler, int count) {
        return boxed.buffer(timespan, unit, scheduler, count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public <U extends Collection<? super T>> Flowable<U> buffer(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler, int count, Callable<U> bufferSupplier, boolean restartTimerOnMaxSize) {
        return boxed.buffer(timespan, unit, scheduler, count, bufferSupplier, restartTimerOnMaxSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<List<T>> buffer(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.buffer(timespan, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <TOpening, TClosing> Flowable<List<T>> buffer(Flowable<? extends TOpening> openingIndicator, Function<? super TOpening, ? extends Publisher<? extends TClosing>> closingIndicator) {
        return boxed.buffer(openingIndicator, closingIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <TOpening, TClosing, U extends Collection<? super T>> Flowable<U> buffer(Flowable<? extends TOpening> openingIndicator, Function<? super TOpening, ? extends Publisher<? extends TClosing>> closingIndicator, Callable<U> bufferSupplier) {
        return boxed.buffer(openingIndicator, closingIndicator, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<List<T>> buffer(Publisher<B> boundaryIndicator) {
        return boxed.buffer(boundaryIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<List<T>> buffer(Publisher<B> boundaryIndicator, int initialCapacity) {
        return boxed.buffer(boundaryIndicator, initialCapacity);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B, U extends Collection<? super T>> Flowable<U> buffer(Publisher<B> boundaryIndicator, Callable<U> bufferSupplier) {
        return boxed.buffer(boundaryIndicator, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<List<T>> buffer(Callable<? extends Publisher<B>> boundaryIndicatorSupplier) {
        return boxed.buffer(boundaryIndicatorSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B, U extends Collection<? super T>> Flowable<U> buffer(Callable<? extends Publisher<B>> boundaryIndicatorSupplier, Callable<U> bufferSupplier) {
        return boxed.buffer(boundaryIndicatorSupplier, bufferSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> cache() {
        return boxed.cache();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> cacheWithInitialCapacity(int initialCapacity) {
        return boxed.cacheWithInitialCapacity(initialCapacity);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <U> Flowable<U> cast(Class<U> clazz) {
        return boxed.cast(clazz);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <U> Single<U> collect(Callable<? extends U> initialItemSupplier, BiConsumer<? super U, ? super T> collector) {
        return boxed.collect(initialItemSupplier, collector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <U> Single<U> collectInto(U initialItem, BiConsumer<? super U, ? super T> collector) {
        return boxed.collectInto(initialItem, collector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <R> Flowable<R> compose(FlowableTransformer<? super T, ? extends R> composer) {
        return boxed.compose(composer);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMap(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.concatMap(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMap(Function<? super T, ? extends Publisher<? extends R>> mapper, int prefetch) {
        return boxed.concatMap(mapper, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.concatMapDelayError(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper, int prefetch, boolean tillTheEnd) {
        return boxed.concatMapDelayError(mapper, prefetch, tillTheEnd);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapEager(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.concatMapEager(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapEager(Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch) {
        return boxed.concatMapEager(mapper, maxConcurrency, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapEagerDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper, boolean tillTheEnd) {
        return boxed.concatMapEagerDelayError(mapper, tillTheEnd);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> concatMapEagerDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch, boolean tillTheEnd) {
        return boxed.concatMapEagerDelayError(mapper, maxConcurrency, prefetch, tillTheEnd);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<U> concatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.concatMapIterable(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<U> concatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, int prefetch) {
        return boxed.concatMapIterable(mapper, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> concatWith(Publisher<? extends T> other) {
        return boxed.concatWith(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<Boolean> contains(Object item) {
        return boxed.contains(item);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<Long> count() {
        return boxed.count();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <U> Flowable<T> debounce(Function<? super T, ? extends Publisher<U>> debounceIndicator) {
        return boxed.debounce(debounceIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> debounce(long timeout, TimeUnit unit) {
        return boxed.debounce(timeout, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> debounce(long timeout, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.debounce(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> defaultIfEmpty(T defaultItem) {
        return boxed.defaultIfEmpty(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<T> delay(Function<? super T, ? extends Publisher<U>> itemDelayIndicator) {
        return boxed.delay(itemDelayIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> delay(long delay, TimeUnit unit) {
        return boxed.delay(delay, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> delay(long delay, TimeUnit unit, boolean delayError) {
        return boxed.delay(delay, unit, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> delay(long delay, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.delay(delay, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> delay(long delay, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError) {
        return boxed.delay(delay, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, V> Flowable<T> delay(Publisher<U> subscriptionIndicator, Function<? super T, ? extends Publisher<V>> itemDelayIndicator) {
        return boxed.delay(subscriptionIndicator, itemDelayIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<T> delaySubscription(Publisher<U> subscriptionIndicator) {
        return boxed.delaySubscription(subscriptionIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> delaySubscription(long delay, TimeUnit unit) {
        return boxed.delaySubscription(delay, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> delaySubscription(long delay, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.delaySubscription(delay, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <T2> Flowable<T2> dematerialize() {
        return boxed.dematerialize();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> distinct() {
        return boxed.distinct();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K> Flowable<T> distinct(Function<? super T, K> keySelector) {
        return boxed.distinct(keySelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K> Flowable<T> distinct(Function<? super T, K> keySelector, Callable<? extends Collection<? super K>> collectionSupplier) {
        return boxed.distinct(keySelector, collectionSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> distinctUntilChanged() {
        return boxed.distinctUntilChanged();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K> Flowable<T> distinctUntilChanged(Function<? super T, K> keySelector) {
        return boxed.distinctUntilChanged(keySelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> distinctUntilChanged(BiPredicate<? super T, ? super T> comparer) {
        return boxed.distinctUntilChanged(comparer);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doFinally(Action onFinally) {
        return boxed.doFinally(onFinally);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doAfterNext(Consumer<? super T> onAfterNext) {
        return boxed.doAfterNext(onAfterNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doAfterTerminate(Action onAfterTerminate) {
        return boxed.doAfterTerminate(onAfterTerminate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnCancel(Action onCancel) {
        return boxed.doOnCancel(onCancel);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnComplete(Action onComplete) {
        return boxed.doOnComplete(onComplete);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnEach(Consumer<? super Notification<T>> onNotification) {
        return boxed.doOnEach(onNotification);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnEach(Subscriber<? super T> subscriber) {
        return boxed.doOnEach(subscriber);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnError(Consumer<? super Throwable> onError) {
        return boxed.doOnError(onError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnLifecycle(Consumer<? super Subscription> onSubscribe, LongConsumer onRequest, Action onCancel) {
        return boxed.doOnLifecycle(onSubscribe, onRequest, onCancel);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnNext(Consumer<? super T> onNext) {
        return boxed.doOnNext(onNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnRequest(LongConsumer onRequest) {
        return boxed.doOnRequest(onRequest);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnSubscribe(Consumer<? super Subscription> onSubscribe) {
        return boxed.doOnSubscribe(onSubscribe);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> doOnTerminate(Action onTerminate) {
        return boxed.doOnTerminate(onTerminate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Maybe<T> elementAt(long index) {
        return boxed.elementAt(index);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> elementAt(long index, T defaultItem) {
        return boxed.elementAt(index, defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> elementAtOrError(long index) {
        return boxed.elementAtOrError(index);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Maybe<T> firstElement() {
        return boxed.firstElement();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Single<T> first(T defaultItem) {
        return boxed.first(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Single<T> firstOrError() {
        return boxed.firstOrError();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.flatMap(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayErrors) {
        return boxed.flatMap(mapper, delayErrors);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency) {
        return boxed.flatMap(mapper, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMap(mapper, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return boxed.flatMap(mapper, delayErrors, maxConcurrency, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> onNextMapper, Function<? super Throwable, ? extends Publisher<? extends R>> onErrorMapper, Callable<? extends Publisher<? extends R>> onCompleteSupplier) {
        return boxed.flatMap(onNextMapper, onErrorMapper, onCompleteSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> onNextMapper, Function<Throwable, ? extends Publisher<? extends R>> onErrorMapper, Callable<? extends Publisher<? extends R>> onCompleteSupplier, int maxConcurrency) {
        return boxed.flatMap(onNextMapper, onErrorMapper, onCompleteSupplier, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner) {
        return boxed.flatMap(mapper, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors) {
        return boxed.flatMap(mapper, combiner, delayErrors);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMap(mapper, combiner, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return boxed.flatMap(mapper, combiner, delayErrors, maxConcurrency, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> flatMap(Function<? super T, ? extends Publisher<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, int maxConcurrency) {
        return boxed.flatMap(mapper, combiner, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper) {
        return boxed.flatMapCompletable(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMapCompletable(mapper, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<U> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flatMapIterable(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<U> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, int bufferSize) {
        return boxed.flatMapIterable(mapper, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, V> Flowable<V> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends V> resultSelector) {
        return boxed.flatMapIterable(mapper, resultSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, V> Flowable<V> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends V> resultSelector, int prefetch) {
        return boxed.flatMapIterable(mapper, resultSelector, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapMaybe(Function<? super T, ? extends MaybeSource<? extends R>> mapper) {
        return boxed.flatMapMaybe(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapMaybe(Function<? super T, ? extends MaybeSource<? extends R>> mapper, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMapMaybe(mapper, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.flatMapSingle(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMapSingle(mapper, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.NONE)
    @SchedulerSupport("none")
    public Disposable forEach(Consumer<? super T> onNext) {
        return boxed.forEach(onNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.NONE)
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext) {
        return boxed.forEachWhile(onNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.NONE)
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext, Consumer<? super Throwable> onError) {
        return boxed.forEachWhile(onNext, onError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.NONE)
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        return boxed.forEachWhile(onNext, onError, onComplete);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K> Flowable<GroupedFlowable<K, T>> groupBy(Function<? super T, ? extends K> keySelector) {
        return boxed.groupBy(keySelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K> Flowable<GroupedFlowable<K, T>> groupBy(Function<? super T, ? extends K> keySelector, boolean delayError) {
        return boxed.groupBy(keySelector, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K, V> Flowable<GroupedFlowable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.groupBy(keySelector, valueSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K, V> Flowable<GroupedFlowable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, boolean delayError) {
        return boxed.groupBy(keySelector, valueSelector, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <K, V> Flowable<GroupedFlowable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, boolean delayError, int bufferSize) {
        return boxed.groupBy(keySelector, valueSelector, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <TRight, TLeftEnd, TRightEnd, R> Flowable<R> groupJoin(Publisher<? extends TRight> other, Function<? super T, ? extends Publisher<TLeftEnd>> leftEnd, Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd, BiFunction<? super T, ? super Flowable<TRight>, ? extends R> resultSelector) {
        return boxed.groupJoin(other, leftEnd, rightEnd, resultSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> hide() {
        return boxed.hide();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Completable ignoreElements() {
        return boxed.ignoreElements();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<Boolean> isEmpty() {
        return boxed.isEmpty();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <TRight, TLeftEnd, TRightEnd, R> Flowable<R> join(Publisher<? extends TRight> other, Function<? super T, ? extends Publisher<TLeftEnd>> leftEnd, Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd, BiFunction<? super T, ? super TRight, ? extends R> resultSelector) {
        return boxed.join(other, leftEnd, rightEnd, resultSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Maybe<T> lastElement() {
        return boxed.lastElement();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> last(T defaultItem) {
        return boxed.last(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> lastOrError() {
        return boxed.lastOrError();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public <R> Flowable<R> lift(FlowableOperator<? extends R, ? super T> lifter) {
        return boxed.lift(lifter);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <R> Flowable<R> map(Function<? super T, ? extends R> mapper) {
        return boxed.map(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<Notification<T>> materialize() {
        return boxed.materialize();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> mergeWith(Publisher<? extends T> other) {
        return boxed.mergeWith(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> observeOn(io.reactivex.Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> observeOn(io.reactivex.Scheduler scheduler, boolean delayError) {
        return boxed.observeOn(scheduler, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> observeOn(io.reactivex.Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.observeOn(scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <U> Flowable<U> ofType(Class<U> clazz) {
        return boxed.ofType(clazz);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer() {
        return boxed.onBackpressureBuffer();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(boolean delayError) {
        return boxed.onBackpressureBuffer(delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(int capacity) {
        return boxed.onBackpressureBuffer(capacity);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(int capacity, boolean delayError) {
        return boxed.onBackpressureBuffer(capacity, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(int capacity, boolean delayError, boolean unbounded) {
        return boxed.onBackpressureBuffer(capacity, delayError, unbounded);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(int capacity, boolean delayError, boolean unbounded, Action onOverflow) {
        return boxed.onBackpressureBuffer(capacity, delayError, unbounded, onOverflow);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(int capacity, Action onOverflow) {
        return boxed.onBackpressureBuffer(capacity, onOverflow);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureBuffer(long capacity, Action onOverflow, BackpressureOverflowStrategy overflowStrategy) {
        return boxed.onBackpressureBuffer(capacity, onOverflow, overflowStrategy);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureDrop() {
        return boxed.onBackpressureDrop();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureDrop(Consumer<? super T> onDrop) {
        return boxed.onBackpressureDrop(onDrop);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> onBackpressureLatest() {
        return boxed.onBackpressureLatest();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> onErrorResumeNext(Function<? super Throwable, ? extends Publisher<? extends T>> resumeFunction) {
        return boxed.onErrorResumeNext(resumeFunction);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> onErrorResumeNext(Publisher<? extends T> next) {
        return boxed.onErrorResumeNext(next);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> onErrorReturn(Function<? super Throwable, ? extends T> valueSupplier) {
        return boxed.onErrorReturn(valueSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> onErrorReturnItem(T item) {
        return boxed.onErrorReturnItem(item);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> onExceptionResumeNext(Publisher<? extends T> next) {
        return boxed.onExceptionResumeNext(next);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> onTerminateDetach() {
        return boxed.onTerminateDetach();
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    @CheckReturnValue
    @Beta
    public ParallelFlowable<T> parallel() {
        return boxed.parallel();
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    @CheckReturnValue
    @Beta
    public ParallelFlowable<T> parallel(int parallelism) {
        return boxed.parallel(parallelism);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    @CheckReturnValue
    @Beta
    public ParallelFlowable<T> parallel(int parallelism, int prefetch) {
        return boxed.parallel(parallelism, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public ConnectableFlowable<T> publish() {
        return boxed.publish();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> publish(Function<? super Flowable<T>, ? extends Publisher<R>> selector) {
        return boxed.publish(selector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> publish(Function<? super Flowable<T>, ? extends Publisher<? extends R>> selector, int prefetch) {
        return boxed.publish(selector, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public ConnectableFlowable<T> publish(int bufferSize) {
        return boxed.publish(bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> rebatchRequests(int n) {
        return boxed.rebatchRequests(n);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Maybe<T> reduce(BiFunction<T, T, T> reducer) {
        return boxed.reduce(reducer);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Single<R> reduce(R seed, BiFunction<R, ? super T, R> reducer) {
        return boxed.reduce(seed, reducer);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <R> Single<R> reduceWith(Callable<R> seedSupplier, BiFunction<R, ? super T, R> reducer) {
        return boxed.reduceWith(seedSupplier, reducer);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> repeat() {
        return boxed.repeat();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> repeat(long times) {
        return boxed.repeat(times);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> repeatUntil(BooleanSupplier stop) {
        return boxed.repeatUntil(stop);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> repeatWhen(Function<? super Flowable<Object>, ? extends Publisher<?>> handler) {
        return boxed.repeatWhen(handler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public ConnectableFlowable<T> replay() {
        return boxed.replay();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector) {
        return boxed.replay(selector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, int bufferSize) {
        return boxed.replay(selector, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(selector, bufferSize, time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, int bufferSize, long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, int bufferSize, io.reactivex.Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, long time, TimeUnit unit) {
        return boxed.replay(selector, time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.replay(selector, time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public <R> Flowable<R> replay(Function<? super Flowable<T>, ? extends Publisher<R>> selector, io.reactivex.Scheduler scheduler) {
        return boxed.replay(selector, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public ConnectableFlowable<T> replay(int bufferSize) {
        return boxed.replay(bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public ConnectableFlowable<T> replay(int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(bufferSize, time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public ConnectableFlowable<T> replay(int bufferSize, long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.replay(bufferSize, time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public ConnectableFlowable<T> replay(int bufferSize, io.reactivex.Scheduler scheduler) {
        return boxed.replay(bufferSize, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public ConnectableFlowable<T> replay(long time, TimeUnit unit) {
        return boxed.replay(time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public ConnectableFlowable<T> replay(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.replay(time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public ConnectableFlowable<T> replay(io.reactivex.Scheduler scheduler) {
        return boxed.replay(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retry() {
        return boxed.retry();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retry(BiPredicate<? super Integer, ? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retry(long count) {
        return boxed.retry(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retry(long times, Predicate<? super Throwable> predicate) {
        return boxed.retry(times, predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retry(Predicate<? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retryUntil(BooleanSupplier stop) {
        return boxed.retryUntil(stop);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> retryWhen(Function<? super Flowable<Throwable>, ? extends Publisher<?>> handler) {
        return boxed.retryWhen(handler);
    }

    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public void safeSubscribe(Subscriber<? super T> s) {
        boxed.safeSubscribe(s);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> sample(long period, TimeUnit unit) {
        return boxed.sample(period, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> sample(long period, TimeUnit unit, boolean emitLast) {
        return boxed.sample(period, unit, emitLast);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> sample(long period, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.sample(period, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> sample(long period, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean emitLast) {
        return boxed.sample(period, unit, scheduler, emitLast);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <U> Flowable<T> sample(Publisher<U> sampler) {
        return boxed.sample(sampler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <U> Flowable<T> sample(Publisher<U> sampler, boolean emitLast) {
        return boxed.sample(sampler, emitLast);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> scan(BiFunction<T, T, T> accumulator) {
        return boxed.scan(accumulator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> scan(R initialValue, BiFunction<R, ? super T, R> accumulator) {
        return boxed.scan(initialValue, accumulator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> scanWith(Callable<R> seedSupplier, BiFunction<R, ? super T, R> accumulator) {
        return boxed.scanWith(seedSupplier, accumulator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> serialize() {
        return boxed.serialize();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> share() {
        return boxed.share();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Maybe<T> singleElement() {
        return boxed.singleElement();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> single(T defaultItem) {
        return boxed.single(defaultItem);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<T> singleOrError() {
        return boxed.singleOrError();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> skip(long count) {
        return boxed.skip(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> skip(long time, TimeUnit unit) {
        return boxed.skip(time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> skip(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.skip(time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> skipLast(int count) {
        return boxed.skipLast(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> skipLast(long time, TimeUnit unit) {
        return boxed.skipLast(time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Flowable<T> skipLast(long time, TimeUnit unit, boolean delayError) {
        return boxed.skipLast(time, unit, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("custom")
    public Flowable<T> skipLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.skipLast(time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("custom")
    public Flowable<T> skipLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError) {
        return boxed.skipLast(time, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("custom")
    public Flowable<T> skipLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.skipLast(time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U> Flowable<T> skipUntil(Publisher<U> other) {
        return boxed.skipUntil(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> skipWhile(Predicate<? super T> predicate) {
        return boxed.skipWhile(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> sorted() {
        return boxed.sorted();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> sorted(Comparator<? super T> sortFunction) {
        return boxed.sorted(sortFunction);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> startWith(Iterable<? extends T> items) {
        return boxed.startWith(items);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> startWith(Publisher<? extends T> other) {
        return boxed.startWith(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> startWith(T value) {
        return boxed.startWith(value);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> startWithArray(T[] items) {
        return boxed.startWithArray(items);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Disposable subscribe() {
        return boxed.subscribe();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext) {
        return boxed.subscribe(onNext);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return boxed.subscribe(onNext, onError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        return boxed.subscribe(onNext, onError, onComplete);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Subscription> onSubscribe) {
        return boxed.subscribe(onNext, onError, onComplete, onSubscribe);
    }

    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    @Beta
    public void subscribe(FlowableSubscriber<? super T> s) {
        boxed.subscribe(s);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public <E extends Subscriber<? super T>> E subscribeWith(E subscriber) {
        return boxed.subscribeWith(subscriber);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("custom")
    public Flowable<T> subscribeOn(io.reactivex.Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> switchIfEmpty(Publisher<? extends T> other) {
        return boxed.switchIfEmpty(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> switchMap(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.switchMap(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <R> Flowable<R> switchMap(Function<? super T, ? extends Publisher<? extends R>> mapper, int bufferSize) {
        return boxed.switchMap(mapper, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public <R> Flowable<R> switchMapDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.switchMapDelayError(mapper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public <R> Flowable<R> switchMapDelayError(Function<? super T, ? extends Publisher<? extends R>> mapper, int bufferSize) {
        return boxed.switchMapDelayError(mapper, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public Flowable<T> take(long count) {
        return boxed.take(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> take(long time, TimeUnit unit) {
        return boxed.take(time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("custom")
    public Flowable<T> take(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.take(time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> takeLast(int count) {
        return boxed.takeLast(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<T> takeLast(long count, long time, TimeUnit unit) {
        return boxed.takeLast(count, time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> takeLast(long count, long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.takeLast(count, time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> takeLast(long count, long time, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.takeLast(count, time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> takeLast(long time, TimeUnit unit) {
        return boxed.takeLast(time, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> takeLast(long time, TimeUnit unit, boolean delayError) {
        return boxed.takeLast(time, unit, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> takeLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.takeLast(time, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> takeLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError) {
        return boxed.takeLast(time, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> takeLast(long time, TimeUnit unit, io.reactivex.Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.takeLast(time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> takeUntil(Predicate<? super T> stopPredicate) {
        return boxed.takeUntil(stopPredicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <U> Flowable<T> takeUntil(Publisher<U> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<T> takeWhile(Predicate<? super T> predicate) {
        return boxed.takeWhile(predicate);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> throttleFirst(long windowDuration, TimeUnit unit) {
        return boxed.throttleFirst(windowDuration, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> throttleFirst(long skipDuration, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.throttleFirst(skipDuration, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> throttleLast(long intervalDuration, TimeUnit unit) {
        return boxed.throttleLast(intervalDuration, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> throttleLast(long intervalDuration, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.throttleLast(intervalDuration, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> throttleWithTimeout(long timeout, TimeUnit unit) {
        return boxed.throttleWithTimeout(timeout, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<T> throttleWithTimeout(long timeout, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.throttleWithTimeout(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timeInterval() {
        return boxed.timeInterval();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timeInterval(io.reactivex.Scheduler scheduler) {
        return boxed.timeInterval(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timeInterval(TimeUnit unit) {
        return boxed.timeInterval(unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timeInterval(TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.timeInterval(unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <V> Flowable<T> timeout(Function<? super T, ? extends Publisher<V>> itemTimeoutIndicator) {
        return boxed.timeout(itemTimeoutIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <V> Flowable<T> timeout(Function<? super T, ? extends Publisher<V>> itemTimeoutIndicator, Flowable<? extends T> other) {
        return boxed.timeout(itemTimeoutIndicator, other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> timeout(long timeout, TimeUnit timeUnit) {
        return boxed.timeout(timeout, timeUnit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<T> timeout(long timeout, TimeUnit timeUnit, Publisher<? extends T> other) {
        return boxed.timeout(timeout, timeUnit, other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("custom")
    public Flowable<T> timeout(long timeout, TimeUnit timeUnit, io.reactivex.Scheduler scheduler, Publisher<? extends T> other) {
        return boxed.timeout(timeout, timeUnit, scheduler, other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("custom")
    public Flowable<T> timeout(long timeout, TimeUnit timeUnit, io.reactivex.Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <U, V> Flowable<T> timeout(Publisher<U> firstTimeoutIndicator, Function<? super T, ? extends Publisher<V>> itemTimeoutIndicator) {
        return boxed.timeout(firstTimeoutIndicator, itemTimeoutIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, V> Flowable<T> timeout(Publisher<U> firstTimeoutIndicator, Function<? super T, ? extends Publisher<V>> itemTimeoutIndicator, Publisher<? extends T> other) {
        return boxed.timeout(firstTimeoutIndicator, itemTimeoutIndicator, other);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timestamp() {
        return boxed.timestamp();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timestamp(io.reactivex.Scheduler scheduler) {
        return boxed.timestamp(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timestamp(TimeUnit unit) {
        return boxed.timestamp(unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public Flowable<Timed<T>> timestamp(TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.timestamp(unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.SPECIAL)
    @SchedulerSupport("none")
    public <R> R to(Function<? super Flowable<T>, R> converter) {
        return boxed.to(converter);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toList() {
        return boxed.toList();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toList(int capacityHint) {
        return boxed.toList(capacityHint);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Single<U> toList(Callable<U> collectionSupplier) {
        return boxed.toList(collectionSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K> Single<Map<K, T>> toMap(Function<? super T, ? extends K> keySelector) {
        return boxed.toMap(keySelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, V>> toMap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.toMap(keySelector, valueSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, V>> toMap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<? extends Map<K, V>> mapSupplier) {
        return boxed.toMap(keySelector, valueSelector, mapSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K> Single<Map<K, Collection<T>>> toMultimap(Function<? super T, ? extends K> keySelector) {
        return boxed.toMultimap(keySelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.toMultimap(keySelector, valueSelector);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<? extends Map<K, Collection<V>>> mapSupplier, Function<? super K, ? extends Collection<? super V>> collectionFactory) {
        return boxed.toMultimap(keySelector, valueSelector, mapSupplier, collectionFactory);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<Map<K, Collection<V>>> mapSupplier) {
        return boxed.toMultimap(keySelector, valueSelector, mapSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.NONE)
    @SchedulerSupport("none")
    public Observable<T> toObservable() {
        return boxed.toObservable();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList() {
        return boxed.toSortedList();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(Comparator<? super T> comparator) {
        return boxed.toSortedList(comparator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(Comparator<? super T> comparator, int capacityHint) {
        return boxed.toSortedList(comparator, capacityHint);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(int capacityHint) {
        return boxed.toSortedList(capacityHint);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("custom")
    public Flowable<T> unsubscribeOn(io.reactivex.Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<Flowable<T>> window(long count) {
        return boxed.window(count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<Flowable<T>> window(long count, long skip) {
        return boxed.window(count, skip);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public Flowable<Flowable<T>> window(long count, long skip, int bufferSize) {
        return boxed.window(count, skip, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<Flowable<T>> window(long timespan, long timeskip, TimeUnit unit) {
        return boxed.window(timespan, timeskip, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, long timeskip, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.window(timespan, timeskip, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, long timeskip, TimeUnit unit, io.reactivex.Scheduler scheduler, int bufferSize) {
        return boxed.window(timespan, timeskip, unit, scheduler, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit) {
        return boxed.window(timespan, unit);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, long count) {
        return boxed.window(timespan, unit, count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("io.reactivex:computation")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, long count, boolean restart) {
        return boxed.window(timespan, unit, count, restart);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.window(timespan, unit, scheduler);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler, long count) {
        return boxed.window(timespan, unit, scheduler, count);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler, long count, boolean restart) {
        return boxed.window(timespan, unit, scheduler, count, restart);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("custom")
    public Flowable<Flowable<T>> window(long timespan, TimeUnit unit, io.reactivex.Scheduler scheduler, long count, boolean restart, int bufferSize) {
        return boxed.window(timespan, unit, scheduler, count, restart, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<Flowable<T>> window(Publisher<B> boundaryIndicator) {
        return boxed.window(boundaryIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<Flowable<T>> window(Publisher<B> boundaryIndicator, int bufferSize) {
        return boxed.window(boundaryIndicator, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <U, V> Flowable<Flowable<T>> window(Publisher<U> openingIndicator, Function<? super U, ? extends Publisher<V>> closingIndicator) {
        return boxed.window(openingIndicator, closingIndicator);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <U, V> Flowable<Flowable<T>> window(Publisher<U> openingIndicator, Function<? super U, ? extends Publisher<V>> closingIndicator, int bufferSize) {
        return boxed.window(openingIndicator, closingIndicator, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<Flowable<T>> window(Callable<? extends Publisher<B>> boundaryIndicatorSupplier) {
        return boxed.window(boundaryIndicatorSupplier);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.ERROR)
    @SchedulerSupport("none")
    public <B> Flowable<Flowable<T>> window(Callable<? extends Publisher<B>> boundaryIndicatorSupplier, int bufferSize) {
        return boxed.window(boundaryIndicatorSupplier, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> withLatestFrom(Publisher<? extends U> other, BiFunction<? super T, ? super U, ? extends R> combiner) {
        return boxed.withLatestFrom(other, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <T1, T2, R> Flowable<R> withLatestFrom(Publisher<T1> source1, Publisher<T2> source2, Function3<? super T, ? super T1, ? super T2, R> combiner) {
        return boxed.withLatestFrom(source1, source2, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <T1, T2, T3, R> Flowable<R> withLatestFrom(Publisher<T1> source1, Publisher<T2> source2, Publisher<T3> source3, Function4<? super T, ? super T1, ? super T2, ? super T3, R> combiner) {
        return boxed.withLatestFrom(source1, source2, source3, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <T1, T2, T3, T4, R> Flowable<R> withLatestFrom(Publisher<T1> source1, Publisher<T2> source2, Publisher<T3> source3, Publisher<T4> source4, Function5<? super T, ? super T1, ? super T2, ? super T3, ? super T4, R> combiner) {
        return boxed.withLatestFrom(source1, source2, source3, source4, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <R> Flowable<R> withLatestFrom(Publisher<?>[] others, Function<? super Object[], R> combiner) {
        return boxed.withLatestFrom(others, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport("none")
    public <R> Flowable<R> withLatestFrom(Iterable<? extends Publisher<?>> others, Function<? super Object[], R> combiner) {
        return boxed.withLatestFrom(others, combiner);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> zipWith(Iterable<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return boxed.zipWith(other, zipper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> zipWith(Publisher<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return boxed.zipWith(other, zipper);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> zipWith(Publisher<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper, boolean delayError) {
        return boxed.zipWith(other, zipper, delayError);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public <U, R> Flowable<R> zipWith(Publisher<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper, boolean delayError, int bufferSize) {
        return boxed.zipWith(other, zipper, delayError, bufferSize);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport("none")
    public TestSubscriber<T> test() {
        return boxed.test();
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public TestSubscriber<T> test(long initialRequest) {
        return boxed.test(initialRequest);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public TestSubscriber<T> test(long initialRequest, boolean cancel) {
        return boxed.test(initialRequest, cancel);
    }

    private final Flowable<T> boxed;

    
}
