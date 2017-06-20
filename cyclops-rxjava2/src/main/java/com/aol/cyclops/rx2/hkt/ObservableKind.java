package com.aol.cyclops.rx2.hkt;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.aol.cyclops2.hkt.Higher;
import io.reactivex.*;
import io.reactivex.annotations.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Timed;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import cyclops.companion.rx2.Observables;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Reactor Observable's
 * 
 * ObservableKind is a Observable and a Higher Kinded Type (ObservableKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Observable
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObservableKind<T> implements Higher<ObservableKind.µ, T>, Publisher<T> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Construct a HKT encoded completed Observable
     * 
     * @param value To encode inside a HKT encoded Observable
     * @return Completed HKT encoded FObservable
     */
    public static <T> ObservableKind<T> just(T value) {

        return widen(Observable.just(value));
    }
    public static <T> ObservableKind<T> just(T... values) {

        return widen(Observable.fromArray(values));
    }

    public static <T> ObservableKind<T> empty() {
        return widen(Observable.empty());
    }

    /**
     * Convert a Observable to a simulated HigherKindedType that captures Observable nature
     * and Observable element data type separately. Recover via @see ObservableKind#narrow
     *
     * If the supplied Observable implements ObservableKind it is returned already, otherwise it
     * is wrapped into a Observable implementation that does implement ObservableKind
     *
     * @param observable Observable to widen to a ObservableKind
     * @return ObservableKind encoding HKT info about Observables
     */
    public static <T> ObservableKind<T> widen(final Observable<T> observable) {

        return new ObservableKind<T>(
                                    observable);
    }

    /**
     * Widen a ObservableKind nested inside another HKT encoded type
     *
     * @param flux HTK encoded type containing  a Observable to widen
     * @return HKT encoded type with a widened Observable
     */
    public static <C2, T> Higher<C2, Higher<µ, T>> widen2(Higher<C2, ObservableKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<StreamType.µ,T> must be a StreamType
        return (Higher) flux;
    }

    public static <T> ObservableKind<T> widen(final Publisher<T> completableObservable) {

        return new ObservableKind<T>(
                                    Observables.observable(completableObservable));
    }

    /**
     * Convert the raw Higher Kinded Type for ObservableKind types into the ObservableKind type definition class
     *
     * @param future HKT encoded list into a ObservableKind
     * @return ObservableKind
     */
    public static <T> ObservableKind<T> narrowK(final Higher<µ, T> future) {
        return (ObservableKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Observable into
     *
     * @param observable Type Constructor to convert back into narrowed type
     * @return Observable from Higher Kinded Type
     */
    public static <T> Observable<T> narrow(final Higher<µ, T> observable) {

        return ((ObservableKind<T>) observable).narrow();

    }


  
    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> all(Predicate<? super T> predicate) {
        return boxed.all(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> ambWith(ObservableSource<? extends T> other) {
        return boxed.ambWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> any(Predicate<? super T> predicate) {
        return boxed.any(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingFirst() {
        return boxed.blockingFirst();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingFirst(T defaultItem) {
        return boxed.blockingFirst(defaultItem);
    }

    @SchedulerSupport("none")
    public void blockingForEach(Consumer<? super T> onNext) {
        boxed.blockingForEach(onNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Iterable<T> blockingIterable() {
        return boxed.blockingIterable();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Iterable<T> blockingIterable(int bufferSize) {
        return boxed.blockingIterable(bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingLast() {
        return boxed.blockingLast();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingLast(T defaultItem) {
        return boxed.blockingLast(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Iterable<T> blockingLatest() {
        return boxed.blockingLatest();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Iterable<T> blockingMostRecent(T initialValue) {
        return boxed.blockingMostRecent(initialValue);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Iterable<T> blockingNext() {
        return boxed.blockingNext();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingSingle() {
        return boxed.blockingSingle();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingSingle(T defaultItem) {
        return boxed.blockingSingle(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Future<T> toFuture() {
        return boxed.toFuture();
    }

    @SchedulerSupport("none")
    public void blockingSubscribe() {
        boxed.blockingSubscribe();
    }

    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext) {
        boxed.blockingSubscribe(onNext);
    }

    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        boxed.blockingSubscribe(onNext, onError);
    }

    @SchedulerSupport("none")
    public void blockingSubscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        boxed.blockingSubscribe(onNext, onError, onComplete);
    }

    @SchedulerSupport("none")
    public void blockingSubscribe(Observer<? super T> subscriber) {
        boxed.blockingSubscribe(subscriber);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<List<T>> buffer(int count) {
        return boxed.buffer(count);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<List<T>> buffer(int count, int skip) {
        return boxed.buffer(count, skip);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Observable<U> buffer(int count, int skip, Callable<U> bufferSupplier) {
        return boxed.buffer(count, skip, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Observable<U> buffer(int count, Callable<U> bufferSupplier) {
        return boxed.buffer(count, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<List<T>> buffer(long timespan, long timeskip, TimeUnit unit) {
        return boxed.buffer(timespan, timeskip, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<List<T>> buffer(long timespan, long timeskip, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, timeskip, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <U extends Collection<? super T>> Observable<U> buffer(long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, Callable<U> bufferSupplier) {
        return boxed.buffer(timespan, timeskip, unit, scheduler, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<List<T>> buffer(long timespan, TimeUnit unit) {
        return boxed.buffer(timespan, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<List<T>> buffer(long timespan, TimeUnit unit, int count) {
        return boxed.buffer(timespan, unit, count);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<List<T>> buffer(long timespan, TimeUnit unit, Scheduler scheduler, int count) {
        return boxed.buffer(timespan, unit, scheduler, count);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <U extends Collection<? super T>> Observable<U> buffer(long timespan, TimeUnit unit, Scheduler scheduler, int count, Callable<U> bufferSupplier, boolean restartTimerOnMaxSize) {
        return boxed.buffer(timespan, unit, scheduler, count, bufferSupplier, restartTimerOnMaxSize);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<List<T>> buffer(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <TOpening, TClosing> Observable<List<T>> buffer(ObservableSource<? extends TOpening> openingIndicator, Function<? super TOpening, ? extends ObservableSource<? extends TClosing>> closingIndicator) {
        return boxed.buffer(openingIndicator, closingIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <TOpening, TClosing, U extends Collection<? super T>> Observable<U> buffer(ObservableSource<? extends TOpening> openingIndicator, Function<? super TOpening, ? extends ObservableSource<? extends TClosing>> closingIndicator, Callable<U> bufferSupplier) {
        return boxed.buffer(openingIndicator, closingIndicator, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<List<T>> buffer(ObservableSource<B> boundary) {
        return boxed.buffer(boundary);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<List<T>> buffer(ObservableSource<B> boundary, int initialCapacity) {
        return boxed.buffer(boundary, initialCapacity);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B, U extends Collection<? super T>> Observable<U> buffer(ObservableSource<B> boundary, Callable<U> bufferSupplier) {
        return boxed.buffer(boundary, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<List<T>> buffer(Callable<? extends ObservableSource<B>> boundarySupplier) {
        return boxed.buffer(boundarySupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B, U extends Collection<? super T>> Observable<U> buffer(Callable<? extends ObservableSource<B>> boundarySupplier, Callable<U> bufferSupplier) {
        return boxed.buffer(boundarySupplier, bufferSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> cache() {
        return boxed.cache();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> cacheWithInitialCapacity(int initialCapacity) {
        return boxed.cacheWithInitialCapacity(initialCapacity);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> cast(Class<U> clazz) {
        return boxed.cast(clazz);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<U> collect(Callable<? extends U> initialValueSupplier, BiConsumer<? super U, ? super T> collector) {
        return boxed.collect(initialValueSupplier, collector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<U> collectInto(U initialValue, BiConsumer<? super U, ? super T> collector) {
        return boxed.collectInto(initialValue, collector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> compose(ObservableTransformer<? super T, ? extends R> composer) {
        return boxed.compose(composer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.concatMap(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int prefetch) {
        return boxed.concatMap(mapper, prefetch);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.concatMapDelayError(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int prefetch, boolean tillTheEnd) {
        return boxed.concatMapDelayError(mapper, prefetch, tillTheEnd);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapEager(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.concatMapEager(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapEager(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int maxConcurrency, int prefetch) {
        return boxed.concatMapEager(mapper, maxConcurrency, prefetch);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapEagerDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper, boolean tillTheEnd) {
        return boxed.concatMapEagerDelayError(mapper, tillTheEnd);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> concatMapEagerDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int maxConcurrency, int prefetch, boolean tillTheEnd) {
        return boxed.concatMapEagerDelayError(mapper, maxConcurrency, prefetch, tillTheEnd);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> concatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.concatMapIterable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> concatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, int prefetch) {
        return boxed.concatMapIterable(mapper, prefetch);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> concatWith(ObservableSource<? extends T> other) {
        return boxed.concatWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> contains(Object element) {
        return boxed.contains(element);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Long> count() {
        return boxed.count();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> debounce(Function<? super T, ? extends ObservableSource<U>> debounceSelector) {
        return boxed.debounce(debounceSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> debounce(long timeout, TimeUnit unit) {
        return boxed.debounce(timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> debounce(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.debounce(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> defaultIfEmpty(T defaultItem) {
        return boxed.defaultIfEmpty(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> delay(Function<? super T, ? extends ObservableSource<U>> itemDelay) {
        return boxed.delay(itemDelay);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> delay(long delay, TimeUnit unit) {
        return boxed.delay(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> delay(long delay, TimeUnit unit, boolean delayError) {
        return boxed.delay(delay, unit, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delay(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> delay(long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        return boxed.delay(delay, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<T> delay(ObservableSource<U> subscriptionDelay, Function<? super T, ? extends ObservableSource<V>> itemDelay) {
        return boxed.delay(subscriptionDelay, itemDelay);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> delaySubscription(ObservableSource<U> other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> delaySubscription(long delay, TimeUnit unit) {
        return boxed.delaySubscription(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> delaySubscription(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delaySubscription(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <T2> Observable<T2> dematerialize() {
        return boxed.dematerialize();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> distinct() {
        return boxed.distinct();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Observable<T> distinct(Function<? super T, K> keySelector) {
        return boxed.distinct(keySelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Observable<T> distinct(Function<? super T, K> keySelector, Callable<? extends Collection<? super K>> collectionSupplier) {
        return boxed.distinct(keySelector, collectionSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> distinctUntilChanged() {
        return boxed.distinctUntilChanged();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Observable<T> distinctUntilChanged(Function<? super T, K> keySelector) {
        return boxed.distinctUntilChanged(keySelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> distinctUntilChanged(BiPredicate<? super T, ? super T> comparer) {
        return boxed.distinctUntilChanged(comparer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doAfterNext(Consumer<? super T> onAfterNext) {
        return boxed.doAfterNext(onAfterNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doAfterTerminate(Action onFinally) {
        return boxed.doAfterTerminate(onFinally);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doFinally(Action onFinally) {
        return boxed.doFinally(onFinally);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnDispose(Action onDispose) {
        return boxed.doOnDispose(onDispose);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnComplete(Action onComplete) {
        return boxed.doOnComplete(onComplete);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnEach(Consumer<? super Notification<T>> onNotification) {
        return boxed.doOnEach(onNotification);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnEach(Observer<? super T> observer) {
        return boxed.doOnEach(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnError(Consumer<? super Throwable> onError) {
        return boxed.doOnError(onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnLifecycle(Consumer<? super Disposable> onSubscribe, Action onDispose) {
        return boxed.doOnLifecycle(onSubscribe, onDispose);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnNext(Consumer<? super T> onNext) {
        return boxed.doOnNext(onNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnSubscribe(Consumer<? super Disposable> onSubscribe) {
        return boxed.doOnSubscribe(onSubscribe);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> doOnTerminate(Action onTerminate) {
        return boxed.doOnTerminate(onTerminate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> elementAt(long index) {
        return boxed.elementAt(index);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> elementAt(long index, T defaultItem) {
        return boxed.elementAt(index, defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> elementAtOrError(long index) {
        return boxed.elementAtOrError(index);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> firstElement() {
        return boxed.firstElement();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> first(T defaultItem) {
        return boxed.first(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> firstOrError() {
        return boxed.firstOrError();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.flatMap(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, boolean delayErrors) {
        return boxed.flatMap(mapper, delayErrors);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMap(mapper, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return boxed.flatMap(mapper, delayErrors, maxConcurrency, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> onNextMapper, Function<? super Throwable, ? extends ObservableSource<? extends R>> onErrorMapper, Callable<? extends ObservableSource<? extends R>> onCompleteSupplier) {
        return boxed.flatMap(onNextMapper, onErrorMapper, onCompleteSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> onNextMapper, Function<Throwable, ? extends ObservableSource<? extends R>> onErrorMapper, Callable<? extends ObservableSource<? extends R>> onCompleteSupplier, int maxConcurrency) {
        return boxed.flatMap(onNextMapper, onErrorMapper, onCompleteSupplier, maxConcurrency);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int maxConcurrency) {
        return boxed.flatMap(mapper, maxConcurrency);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.flatMap(mapper, resultSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors) {
        return boxed.flatMap(mapper, combiner, delayErrors);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors, int maxConcurrency) {
        return boxed.flatMap(mapper, combiner, delayErrors, maxConcurrency);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return boxed.flatMap(mapper, combiner, delayErrors, maxConcurrency, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner, int maxConcurrency) {
        return boxed.flatMap(mapper, combiner, maxConcurrency);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper) {
        return boxed.flatMapCompletable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper, boolean delayErrors) {
        return boxed.flatMapCompletable(mapper, delayErrors);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flatMapIterable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<V> flatMapIterable(Function<? super T, ? extends Iterable<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends V> resultSelector) {
        return boxed.flatMapIterable(mapper, resultSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMapMaybe(Function<? super T, ? extends MaybeSource<? extends R>> mapper) {
        return boxed.flatMapMaybe(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMapMaybe(Function<? super T, ? extends MaybeSource<? extends R>> mapper, boolean delayErrors) {
        return boxed.flatMapMaybe(mapper, delayErrors);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.flatMapSingle(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayErrors) {
        return boxed.flatMapSingle(mapper, delayErrors);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable forEach(Consumer<? super T> onNext) {
        return boxed.forEach(onNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext) {
        return boxed.forEachWhile(onNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext, Consumer<? super Throwable> onError) {
        return boxed.forEachWhile(onNext, onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable forEachWhile(Predicate<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        return boxed.forEachWhile(onNext, onError, onComplete);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Observable<GroupedObservable<K, T>> groupBy(Function<? super T, ? extends K> keySelector) {
        return boxed.groupBy(keySelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Observable<GroupedObservable<K, T>> groupBy(Function<? super T, ? extends K> keySelector, boolean delayError) {
        return boxed.groupBy(keySelector, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Observable<GroupedObservable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.groupBy(keySelector, valueSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Observable<GroupedObservable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, boolean delayError) {
        return boxed.groupBy(keySelector, valueSelector, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Observable<GroupedObservable<K, V>> groupBy(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, boolean delayError, int bufferSize) {
        return boxed.groupBy(keySelector, valueSelector, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <TRight, TLeftEnd, TRightEnd, R> Observable<R> groupJoin(ObservableSource<? extends TRight> other, Function<? super T, ? extends ObservableSource<TLeftEnd>> leftEnd, Function<? super TRight, ? extends ObservableSource<TRightEnd>> rightEnd, BiFunction<? super T, ? super Observable<TRight>, ? extends R> resultSelector) {
        return boxed.groupJoin(other, leftEnd, rightEnd, resultSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> hide() {
        return boxed.hide();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable ignoreElements() {
        return boxed.ignoreElements();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> isEmpty() {
        return boxed.isEmpty();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <TRight, TLeftEnd, TRightEnd, R> Observable<R> join(ObservableSource<? extends TRight> other, Function<? super T, ? extends ObservableSource<TLeftEnd>> leftEnd, Function<? super TRight, ? extends ObservableSource<TRightEnd>> rightEnd, BiFunction<? super T, ? super TRight, ? extends R> resultSelector) {
        return boxed.join(other, leftEnd, rightEnd, resultSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> lastElement() {
        return boxed.lastElement();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> last(T defaultItem) {
        return boxed.last(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> lastOrError() {
        return boxed.lastOrError();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> lift(ObservableOperator<? extends R, ? super T> lifter) {
        return boxed.lift(lifter);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return boxed.map(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Notification<T>> materialize() {
        return boxed.materialize();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> mergeWith(ObservableSource<? extends T> other) {
        return boxed.mergeWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> observeOn(Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> observeOn(Scheduler scheduler, boolean delayError) {
        return boxed.observeOn(scheduler, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.observeOn(scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> ofType(Class<U> clazz) {
        return boxed.ofType(clazz);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onErrorResumeNext(Function<? super Throwable, ? extends ObservableSource<? extends T>> resumeFunction) {
        return boxed.onErrorResumeNext(resumeFunction);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onErrorResumeNext(ObservableSource<? extends T> next) {
        return boxed.onErrorResumeNext(next);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onErrorReturn(Function<? super Throwable, ? extends T> valueSupplier) {
        return boxed.onErrorReturn(valueSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onErrorReturnItem(T item) {
        return boxed.onErrorReturnItem(item);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onExceptionResumeNext(ObservableSource<? extends T> next) {
        return boxed.onExceptionResumeNext(next);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> onTerminateDetach() {
        return boxed.onTerminateDetach();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public ConnectableObservable<T> publish() {
        return boxed.publish();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> publish(Function<? super Observable<T>, ? extends ObservableSource<R>> selector) {
        return boxed.publish(selector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> reduce(BiFunction<T, T, T> reducer) {
        return boxed.reduce(reducer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> reduce(R seed, BiFunction<R, ? super T, R> reducer) {
        return boxed.reduce(seed, reducer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> reduceWith(Callable<R> seedSupplier, BiFunction<R, ? super T, R> reducer) {
        return boxed.reduceWith(seedSupplier, reducer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> repeat() {
        return boxed.repeat();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> repeat(long times) {
        return boxed.repeat(times);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> repeatUntil(BooleanSupplier stop) {
        return boxed.repeatUntil(stop);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> repeatWhen(Function<? super Observable<Object>, ? extends ObservableSource<?>> handler) {
        return boxed.repeatWhen(handler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public ConnectableObservable<T> replay() {
        return boxed.replay();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector) {
        return boxed.replay(selector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, int bufferSize) {
        return boxed.replay(selector, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(selector, bufferSize, time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, int bufferSize, Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, long time, TimeUnit unit) {
        return boxed.replay(selector, time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(selector, time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <R> Observable<R> replay(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, Scheduler scheduler) {
        return boxed.replay(selector, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public ConnectableObservable<T> replay(int bufferSize) {
        return boxed.replay(bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public ConnectableObservable<T> replay(int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(bufferSize, time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public ConnectableObservable<T> replay(int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(bufferSize, time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public ConnectableObservable<T> replay(int bufferSize, Scheduler scheduler) {
        return boxed.replay(bufferSize, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public ConnectableObservable<T> replay(long time, TimeUnit unit) {
        return boxed.replay(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public ConnectableObservable<T> replay(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public ConnectableObservable<T> replay(Scheduler scheduler) {
        return boxed.replay(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retry() {
        return boxed.retry();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retry(BiPredicate<? super Integer, ? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retry(long times) {
        return boxed.retry(times);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retry(long times, Predicate<? super Throwable> predicate) {
        return boxed.retry(times, predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retry(Predicate<? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retryUntil(BooleanSupplier stop) {
        return boxed.retryUntil(stop);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> retryWhen(Function<? super Observable<Throwable>, ? extends ObservableSource<?>> handler) {
        return boxed.retryWhen(handler);
    }

    @SchedulerSupport("none")
    public void safeSubscribe(Observer<? super T> s) {
        boxed.safeSubscribe(s);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> sample(long period, TimeUnit unit) {
        return boxed.sample(period, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> sample(long period, TimeUnit unit, boolean emitLast) {
        return boxed.sample(period, unit, emitLast);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> sample(long period, TimeUnit unit, Scheduler scheduler) {
        return boxed.sample(period, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> sample(long period, TimeUnit unit, Scheduler scheduler, boolean emitLast) {
        return boxed.sample(period, unit, scheduler, emitLast);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> sample(ObservableSource<U> sampler) {
        return boxed.sample(sampler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> sample(ObservableSource<U> sampler, boolean emitLast) {
        return boxed.sample(sampler, emitLast);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> scan(BiFunction<T, T, T> accumulator) {
        return boxed.scan(accumulator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> scan(R initialValue, BiFunction<R, ? super T, R> accumulator) {
        return boxed.scan(initialValue, accumulator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> scanWith(Callable<R> seedSupplier, BiFunction<R, ? super T, R> accumulator) {
        return boxed.scanWith(seedSupplier, accumulator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> serialize() {
        return boxed.serialize();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> share() {
        return boxed.share();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> singleElement() {
        return boxed.singleElement();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> single(T defaultItem) {
        return boxed.single(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> singleOrError() {
        return boxed.singleOrError();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> skip(long count) {
        return boxed.skip(count);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> skip(long time, TimeUnit unit) {
        return boxed.skip(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> skip(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skip(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> skipLast(int count) {
        return boxed.skipLast(count);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:trampoline")
    public Observable<T> skipLast(long time, TimeUnit unit) {
        return boxed.skipLast(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:trampoline")
    public Observable<T> skipLast(long time, TimeUnit unit, boolean delayError) {
        return boxed.skipLast(time, unit, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> skipLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skipLast(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> skipLast(long time, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        return boxed.skipLast(time, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> skipLast(long time, TimeUnit unit, Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.skipLast(time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> skipUntil(ObservableSource<U> other) {
        return boxed.skipUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> skipWhile(Predicate<? super T> predicate) {
        return boxed.skipWhile(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> sorted() {
        return boxed.sorted();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> sorted(Comparator<? super T> sortFunction) {
        return boxed.sorted(sortFunction);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> startWith(Iterable<? extends T> items) {
        return boxed.startWith(items);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> startWith(ObservableSource<? extends T> other) {
        return boxed.startWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> startWith(T item) {
        return boxed.startWith(item);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> startWithArray(T[] items) {
        return boxed.startWithArray(items);
    }

    @SchedulerSupport("none")
    public Disposable subscribe() {
        return boxed.subscribe();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext) {
        return boxed.subscribe(onNext);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return boxed.subscribe(onNext, onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        return boxed.subscribe(onNext, onError, onComplete);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        return boxed.subscribe(onNext, onError, onComplete, onSubscribe);
    }

    @SchedulerSupport("none")
    public void subscribe(Observer<? super T> observer) {
        boxed.subscribe(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E extends Observer<? super T>> E subscribeWith(E observer) {
        return boxed.subscribeWith(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> switchIfEmpty(ObservableSource<? extends T> other) {
        return boxed.switchIfEmpty(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> switchMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.switchMap(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> switchMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int bufferSize) {
        return boxed.switchMap(mapper, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    @Experimental
    @NonNull
    public <R> Observable<R> switchMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.switchMapSingle(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    @Experimental
    @NonNull
    public <R> Observable<R> switchMapSingleDelayError(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.switchMapSingleDelayError(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> switchMapDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.switchMapDelayError(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> switchMapDelayError(Function<? super T, ? extends ObservableSource<? extends R>> mapper, int bufferSize) {
        return boxed.switchMapDelayError(mapper, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> take(long count) {
        return boxed.take(count);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> take(long time, TimeUnit unit) {
        return boxed.take(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> take(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.take(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> takeLast(int count) {
        return boxed.takeLast(count);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:trampoline")
    public Observable<T> takeLast(long count, long time, TimeUnit unit) {
        return boxed.takeLast(count, time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> takeLast(long count, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(count, time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> takeLast(long count, long time, TimeUnit unit, Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.takeLast(count, time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:trampoline")
    public Observable<T> takeLast(long time, TimeUnit unit) {
        return boxed.takeLast(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:trampoline")
    public Observable<T> takeLast(long time, TimeUnit unit, boolean delayError) {
        return boxed.takeLast(time, unit, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> takeLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> takeLast(long time, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        return boxed.takeLast(time, unit, scheduler, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> takeLast(long time, TimeUnit unit, Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.takeLast(time, unit, scheduler, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<T> takeUntil(ObservableSource<U> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> takeUntil(Predicate<? super T> stopPredicate) {
        return boxed.takeUntil(stopPredicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> takeWhile(Predicate<? super T> predicate) {
        return boxed.takeWhile(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> throttleFirst(long windowDuration, TimeUnit unit) {
        return boxed.throttleFirst(windowDuration, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> throttleFirst(long skipDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleFirst(skipDuration, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> throttleLast(long intervalDuration, TimeUnit unit) {
        return boxed.throttleLast(intervalDuration, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> throttleLast(long intervalDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleLast(intervalDuration, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> throttleWithTimeout(long timeout, TimeUnit unit) {
        return boxed.throttleWithTimeout(timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> throttleWithTimeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleWithTimeout(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timeInterval() {
        return boxed.timeInterval();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timeInterval(Scheduler scheduler) {
        return boxed.timeInterval(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timeInterval(TimeUnit unit) {
        return boxed.timeInterval(unit);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timeInterval(TimeUnit unit, Scheduler scheduler) {
        return boxed.timeInterval(unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <V> Observable<T> timeout(Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator) {
        return boxed.timeout(itemTimeoutIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <V> Observable<T> timeout(Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator, ObservableSource<? extends T> other) {
        return boxed.timeout(itemTimeoutIndicator, other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> timeout(long timeout, TimeUnit timeUnit) {
        return boxed.timeout(timeout, timeUnit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<T> timeout(long timeout, TimeUnit timeUnit, ObservableSource<? extends T> other) {
        return boxed.timeout(timeout, timeUnit, other);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler, ObservableSource<? extends T> other) {
        return boxed.timeout(timeout, timeUnit, scheduler, other);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<T> timeout(ObservableSource<U> firstTimeoutIndicator, Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator) {
        return boxed.timeout(firstTimeoutIndicator, itemTimeoutIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<T> timeout(ObservableSource<U> firstTimeoutIndicator, Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator, ObservableSource<? extends T> other) {
        return boxed.timeout(firstTimeoutIndicator, itemTimeoutIndicator, other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timestamp() {
        return boxed.timestamp();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timestamp(Scheduler scheduler) {
        return boxed.timestamp(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timestamp(TimeUnit unit) {
        return boxed.timestamp(unit);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Timed<T>> timestamp(TimeUnit unit, Scheduler scheduler) {
        return boxed.timestamp(unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> R to(Function<? super Observable<T>, R> converter) {
        return boxed.to(converter);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toList() {
        return boxed.toList();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toList(int capacityHint) {
        return boxed.toList(capacityHint);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U extends Collection<? super T>> Single<U> toList(Callable<U> collectionSupplier) {
        return boxed.toList(collectionSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Single<Map<K, T>> toMap(Function<? super T, ? extends K> keySelector) {
        return boxed.toMap(keySelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, V>> toMap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.toMap(keySelector, valueSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, V>> toMap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<? extends Map<K, V>> mapSupplier) {
        return boxed.toMap(keySelector, valueSelector, mapSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K> Single<Map<K, Collection<T>>> toMultimap(Function<? super T, ? extends K> keySelector) {
        return boxed.toMultimap(keySelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector) {
        return boxed.toMultimap(keySelector, valueSelector);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<? extends Map<K, Collection<V>>> mapSupplier, Function<? super K, ? extends Collection<? super V>> collectionFactory) {
        return boxed.toMultimap(keySelector, valueSelector, mapSupplier, collectionFactory);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <K, V> Single<Map<K, Collection<V>>> toMultimap(Function<? super T, ? extends K> keySelector, Function<? super T, ? extends V> valueSelector, Callable<Map<K, Collection<V>>> mapSupplier) {
        return boxed.toMultimap(keySelector, valueSelector, mapSupplier);
    }

    @BackpressureSupport(BackpressureKind.SPECIAL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> toFlowable(BackpressureStrategy strategy) {
        return boxed.toFlowable(strategy);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList() {
        return boxed.toSortedList();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(Comparator<? super T> comparator) {
        return boxed.toSortedList(comparator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(Comparator<? super T> comparator, int capacityHint) {
        return boxed.toSortedList(comparator, capacityHint);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<List<T>> toSortedList(int capacityHint) {
        return boxed.toSortedList(capacityHint);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<T> unsubscribeOn(Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Observable<T>> window(long count) {
        return boxed.window(count);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Observable<T>> window(long count, long skip) {
        return boxed.window(count, skip);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<Observable<T>> window(long count, long skip, int bufferSize) {
        return boxed.window(count, skip, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<Observable<T>> window(long timespan, long timeskip, TimeUnit unit) {
        return boxed.window(timespan, timeskip, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, long timeskip, TimeUnit unit, Scheduler scheduler) {
        return boxed.window(timespan, timeskip, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, int bufferSize) {
        return boxed.window(timespan, timeskip, unit, scheduler, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit) {
        return boxed.window(timespan, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, long count) {
        return boxed.window(timespan, unit, count);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, long count, boolean restart) {
        return boxed.window(timespan, unit, count, restart);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.window(timespan, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler, long count) {
        return boxed.window(timespan, unit, scheduler, count);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler, long count, boolean restart) {
        return boxed.window(timespan, unit, scheduler, count, restart);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler, long count, boolean restart, int bufferSize) {
        return boxed.window(timespan, unit, scheduler, count, restart, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<Observable<T>> window(ObservableSource<B> boundary) {
        return boxed.window(boundary);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<Observable<T>> window(ObservableSource<B> boundary, int bufferSize) {
        return boxed.window(boundary, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<Observable<T>> window(ObservableSource<U> openingIndicator, Function<? super U, ? extends ObservableSource<V>> closingIndicator) {
        return boxed.window(openingIndicator, closingIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, V> Observable<Observable<T>> window(ObservableSource<U> openingIndicator, Function<? super U, ? extends ObservableSource<V>> closingIndicator, int bufferSize) {
        return boxed.window(openingIndicator, closingIndicator, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<Observable<T>> window(Callable<? extends ObservableSource<B>> boundary) {
        return boxed.window(boundary);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <B> Observable<Observable<T>> window(Callable<? extends ObservableSource<B>> boundary, int bufferSize) {
        return boxed.window(boundary, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> withLatestFrom(ObservableSource<? extends U> other, BiFunction<? super T, ? super U, ? extends R> combiner) {
        return boxed.withLatestFrom(other, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <T1, T2, R> Observable<R> withLatestFrom(ObservableSource<T1> o1, ObservableSource<T2> o2, Function3<? super T, ? super T1, ? super T2, R> combiner) {
        return boxed.withLatestFrom(o1, o2, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <T1, T2, T3, R> Observable<R> withLatestFrom(ObservableSource<T1> o1, ObservableSource<T2> o2, ObservableSource<T3> o3, Function4<? super T, ? super T1, ? super T2, ? super T3, R> combiner) {
        return boxed.withLatestFrom(o1, o2, o3, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <T1, T2, T3, T4, R> Observable<R> withLatestFrom(ObservableSource<T1> o1, ObservableSource<T2> o2, ObservableSource<T3> o3, ObservableSource<T4> o4, Function5<? super T, ? super T1, ? super T2, ? super T3, ? super T4, R> combiner) {
        return boxed.withLatestFrom(o1, o2, o3, o4, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> withLatestFrom(ObservableSource<?>[] others, Function<? super Object[], R> combiner) {
        return boxed.withLatestFrom(others, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> withLatestFrom(Iterable<? extends ObservableSource<?>> others, Function<? super Object[], R> combiner) {
        return boxed.withLatestFrom(others, combiner);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> zipWith(Iterable<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return boxed.zipWith(other, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> zipWith(ObservableSource<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return boxed.zipWith(other, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> zipWith(ObservableSource<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper, boolean delayError) {
        return boxed.zipWith(other, zipper, delayError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Observable<R> zipWith(ObservableSource<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper, boolean delayError, int bufferSize) {
        return boxed.zipWith(other, zipper, delayError, bufferSize);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public TestObserver<T> test() {
        return boxed.test();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public TestObserver<T> test(boolean dispose) {
        return boxed.test(dispose);
    }

    private final Observable<T> boxed;

    /**
     * @return wrapped Obsverable
     */
    public Observable<T> narrow() {
        return boxed;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        Observables.publisher(boxed)
                   .subscribe(s);

    }

    /**
     * @return
     * @see Object#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }

    /**
     * @param obj
     * @return
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }


}
