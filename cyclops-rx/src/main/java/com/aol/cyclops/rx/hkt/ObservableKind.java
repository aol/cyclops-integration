package com.aol.cyclops.rx.hkt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aol.cyclops2.hkt.Higher;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import com.aol.cyclops.rx.Observables;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import rx.BackpressureOverflow.Strategy;
import rx.Completable;
import rx.Notification;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Observable.Transformer;
import rx.Observer;
import rx.Scheduler;
import rx.Single;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.BlockingObservable;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

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
        
        return widen(Observable.from(values));
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

        return new ObservableKind<>(
                                    observable);
    }

    /**
     * Widen a ObservableKind nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a Observable to widen
     * @return HKT encoded type with a widened Observable
     */
    public static <C2, T> Higher<C2, Higher<ObservableKind.µ, T>> widen2(Higher<C2, ObservableKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<StreamType.µ,T> must be a StreamType
        return (Higher) flux;
    }

    public static <T> ObservableKind<T> widen(final Publisher<T> completableObservable) {

        return new ObservableKind<>(
                                    Observables.observable(completableObservable));
    }

    /**
     * Convert the raw Higher Kinded Type for ObservableKind types into the ObservableKind type definition class
     * 
     * @param future HKT encoded list into a ObservableKind
     * @return ObservableKind
     */
    public static <T> ObservableKind<T> narrowK(final Higher<ObservableKind.µ, T> future) {
        return (ObservableKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Observable into
     * 
     * @param observable Type Constructor to convert back into narrowed type
     * @return Observable from Higher Kinded Type
     */
    public static <T> Observable<T> narrow(final Higher<ObservableKind.µ, T> observable) {

        return ((ObservableKind<T>) observable).narrow();

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
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }

    /**
     * @param conversion
     * @return
     * @see rx.Observable#extend(rx.functions.Func1)
     */
    public <R> R extend(Func1<? super OnSubscribe<T>, ? extends R> conversion) {
        return boxed.extend(conversion);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return boxed.toString();
    }

    /**
     * @param operator
     * @return
     * @see rx.Observable#lift(rx.Observable.Operator)
     */
    public final <R> Observable<R> lift(Operator<? extends R, ? super T> operator) {
        return boxed.lift(operator);
    }

    /**
     * @param transformer
     * @return
     * @see rx.Observable#compose(rx.Observable.Transformer)
     */
    public <R> Observable<R> compose(Transformer<? super T, ? extends R> transformer) {
        return boxed.compose(transformer);
    }

    /**
     * @return
     * @see rx.Observable#toSingle()
     */
    public Single<T> toSingle() {
        return boxed.toSingle();
    }

    /**
     * @return
     * @see rx.Observable#toCompletable()
     */
    public Completable toCompletable() {
        return boxed.toCompletable();
    }

    /**
     * @return
     * @see rx.Observable#nest()
     */
    public final Observable<Observable<T>> nest() {
        return boxed.nest();
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#all(rx.functions.Func1)
     */
    public final Observable<Boolean> all(Func1<? super T, Boolean> predicate) {
        return boxed.all(predicate);
    }

    /**
     * @param t1
     * @return
     * @see rx.Observable#ambWith(rx.Observable)
     */
    public final Observable<T> ambWith(Observable<? extends T> t1) {
        return boxed.ambWith(t1);
    }

    /**
     * @return
     * @see rx.Observable#asObservable()
     */
    public final Observable<T> asObservable() {
        return boxed.asObservable();
    }

    /**
     * @param bufferClosingSelector
     * @return
     * @see rx.Observable#buffer(rx.functions.Func0)
     */
    public final <TClosing> Observable<List<T>> buffer(
            Func0<? extends Observable<? extends TClosing>> bufferClosingSelector) {
        return boxed.buffer(bufferClosingSelector);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#buffer(int)
     */
    public final Observable<List<T>> buffer(int count) {
        return boxed.buffer(count);
    }

    /**
     * @param count
     * @param skip
     * @return
     * @see rx.Observable#buffer(int, int)
     */
    public final Observable<List<T>> buffer(int count, int skip) {
        return boxed.buffer(count, skip);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @return
     * @see rx.Observable#buffer(long, long, java.util.concurrent.TimeUnit)
     */
    public final Observable<List<T>> buffer(long timespan, long timeshift, TimeUnit unit) {
        return boxed.buffer(timespan, timeshift, unit);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#buffer(long, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, long timeshift, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, timeshift, unit, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @return
     * @see rx.Observable#buffer(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit) {
        return boxed.buffer(timespan, unit);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @return
     * @see rx.Observable#buffer(long, java.util.concurrent.TimeUnit, int)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit, int count) {
        return boxed.buffer(timespan, unit, count);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @param scheduler
     * @return
     * @see rx.Observable#buffer(long, java.util.concurrent.TimeUnit, int, rx.Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit, int count, Scheduler scheduler) {
        return boxed.buffer(timespan, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#buffer(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, unit, scheduler);
    }

    /**
     * @param bufferOpenings
     * @param bufferClosingSelector
     * @return
     * @see rx.Observable#buffer(rx.Observable, rx.functions.Func1)
     */
    public final <TOpening, TClosing> Observable<List<T>> buffer(Observable<? extends TOpening> bufferOpenings,
            Func1<? super TOpening, ? extends Observable<? extends TClosing>> bufferClosingSelector) {
        return boxed.buffer(bufferOpenings, bufferClosingSelector);
    }

    /**
     * @param boundary
     * @return
     * @see rx.Observable#buffer(rx.Observable)
     */
    public final <B> Observable<List<T>> buffer(Observable<B> boundary) {
        return boxed.buffer(boundary);
    }

    /**
     * @param boundary
     * @param initialCapacity
     * @return
     * @see rx.Observable#buffer(rx.Observable, int)
     */
    public final <B> Observable<List<T>> buffer(Observable<B> boundary, int initialCapacity) {
        return boxed.buffer(boundary, initialCapacity);
    }

    /**
     * @return
     * @see rx.Observable#cache()
     */
    public final Observable<T> cache() {
        return boxed.cache();
    }

    /**
     * @param initialCapacity
     * @return
     * @deprecated
     * @see rx.Observable#cache(int)
     */
    public final Observable<T> cache(int initialCapacity) {
        return boxed.cache(initialCapacity);
    }

    /**
     * @param initialCapacity
     * @return
     * @see rx.Observable#cacheWithInitialCapacity(int)
     */
    public final Observable<T> cacheWithInitialCapacity(int initialCapacity) {
        return boxed.cacheWithInitialCapacity(initialCapacity);
    }

    /**
     * @param klass
     * @return
     * @see rx.Observable#cast(java.lang.Class)
     */
    public final <R> Observable<R> cast(Class<R> klass) {
        return boxed.cast(klass);
    }

    /**
     * @param stateFactory
     * @param collector
     * @return
     * @see rx.Observable#collect(rx.functions.Func0, rx.functions.Action2)
     */
    public final <R> Observable<R> collect(Func0<R> stateFactory, Action2<R, ? super T> collector) {
        return boxed.collect(stateFactory, collector);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#concatMap(rx.functions.Func1)
     */
    public final <R> Observable<R> concatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.concatMap(func);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#concatMapDelayError(rx.functions.Func1)
     */
    public final <R> Observable<R> concatMapDelayError(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.concatMapDelayError(func);
    }

    /**
     * @param collectionSelector
     * @return
     * @see rx.Observable#concatMapIterable(rx.functions.Func1)
     */
    public final <R> Observable<R> concatMapIterable(
            Func1<? super T, ? extends Iterable<? extends R>> collectionSelector) {
        return boxed.concatMapIterable(collectionSelector);
    }

    /**
     * @param t1
     * @return
     * @see rx.Observable#concatWith(rx.Observable)
     */
    public final Observable<T> concatWith(Observable<? extends T> t1) {
        return boxed.concatWith(t1);
    }

    /**
     * @param element
     * @return
     * @see rx.Observable#contains(java.lang.Object)
     */
    public final Observable<Boolean> contains(Object element) {
        return boxed.contains(element);
    }

    /**
     * @return
     * @see rx.Observable#count()
     */
    public final Observable<Integer> count() {
        return boxed.count();
    }

    /**
     * @return
     * @see rx.Observable#countLong()
     */
    public final Observable<Long> countLong() {
        return boxed.countLong();
    }

    /**
     * @param debounceSelector
     * @return
     * @see rx.Observable#debounce(rx.functions.Func1)
     */
    public final <U> Observable<T> debounce(Func1<? super T, ? extends Observable<U>> debounceSelector) {
        return boxed.debounce(debounceSelector);
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @see rx.Observable#debounce(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> debounce(long timeout, TimeUnit unit) {
        return boxed.debounce(timeout, unit);
    }

    /**
     * @param timeout
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#debounce(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> debounce(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.debounce(timeout, unit, scheduler);
    }

    /**
     * @param defaultValue
     * @return
     * @see rx.Observable#defaultIfEmpty(java.lang.Object)
     */
    public final Observable<T> defaultIfEmpty(T defaultValue) {
        return boxed.defaultIfEmpty(defaultValue);
    }

    /**
     * @param alternate
     * @return
     * @see rx.Observable#switchIfEmpty(rx.Observable)
     */
    public final Observable<T> switchIfEmpty(Observable<? extends T> alternate) {
        return boxed.switchIfEmpty(alternate);
    }

    /**
     * @param subscriptionDelay
     * @param itemDelay
     * @return
     * @see rx.Observable#delay(rx.functions.Func0, rx.functions.Func1)
     */
    public final <U, V> Observable<T> delay(Func0<? extends Observable<U>> subscriptionDelay,
            Func1<? super T, ? extends Observable<V>> itemDelay) {
        return boxed.delay(subscriptionDelay, itemDelay);
    }

    /**
     * @param itemDelay
     * @return
     * @see rx.Observable#delay(rx.functions.Func1)
     */
    public final <U> Observable<T> delay(Func1<? super T, ? extends Observable<U>> itemDelay) {
        return boxed.delay(itemDelay);
    }

    /**
     * @param delay
     * @param unit
     * @return
     * @see rx.Observable#delay(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> delay(long delay, TimeUnit unit) {
        return boxed.delay(delay, unit);
    }

    /**
     * @param delay
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#delay(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delay(delay, unit, scheduler);
    }

    /**
     * @param delay
     * @param unit
     * @return
     * @see rx.Observable#delaySubscription(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> delaySubscription(long delay, TimeUnit unit) {
        return boxed.delaySubscription(delay, unit);
    }

    /**
     * @param delay
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#delaySubscription(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> delaySubscription(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delaySubscription(delay, unit, scheduler);
    }

    /**
     * @param subscriptionDelay
     * @return
     * @see rx.Observable#delaySubscription(rx.functions.Func0)
     */
    public final <U> Observable<T> delaySubscription(Func0<? extends Observable<U>> subscriptionDelay) {
        return boxed.delaySubscription(subscriptionDelay);
    }

    /**
     * @param other
     * @return
     * @see rx.Observable#delaySubscription(rx.Observable)
     */
    public final <U> Observable<T> delaySubscription(Observable<U> other) {
        return boxed.delaySubscription(other);
    }

    /**
     * @return
     * @see rx.Observable#dematerialize()
     */
    public final <T2> Observable<T2> dematerialize() {
        return boxed.dematerialize();
    }

    /**
     * @return
     * @see rx.Observable#distinct()
     */
    public final Observable<T> distinct() {
        return boxed.distinct();
    }

    /**
     * @param keySelector
     * @return
     * @see rx.Observable#distinct(rx.functions.Func1)
     */
    public final <U> Observable<T> distinct(Func1<? super T, ? extends U> keySelector) {
        return boxed.distinct(keySelector);
    }

    /**
     * @return
     * @see rx.Observable#distinctUntilChanged()
     */
    public final Observable<T> distinctUntilChanged() {
        return boxed.distinctUntilChanged();
    }

    /**
     * @param keySelector
     * @return
     * @see rx.Observable#distinctUntilChanged(rx.functions.Func1)
     */
    public final <U> Observable<T> distinctUntilChanged(Func1<? super T, ? extends U> keySelector) {
        return boxed.distinctUntilChanged(keySelector);
    }

    /**
     * @param onCompleted
     * @return
     * @see rx.Observable#doOnCompleted(rx.functions.Action0)
     */
    public final Observable<T> doOnCompleted(Action0 onCompleted) {
        return boxed.doOnCompleted(onCompleted);
    }

    /**
     * @param onNotification
     * @return
     * @see rx.Observable#doOnEach(rx.functions.Action1)
     */
    public final Observable<T> doOnEach(Action1<Notification<? super T>> onNotification) {
        return boxed.doOnEach(onNotification);
    }

    /**
     * @param observer
     * @return
     * @see rx.Observable#doOnEach(rx.Observer)
     */
    public final Observable<T> doOnEach(Observer<? super T> observer) {
        return boxed.doOnEach(observer);
    }

    /**
     * @param onError
     * @return
     * @see rx.Observable#doOnError(rx.functions.Action1)
     */
    public final Observable<T> doOnError(Action1<Throwable> onError) {
        return boxed.doOnError(onError);
    }

    /**
     * @param onNext
     * @return
     * @see rx.Observable#doOnNext(rx.functions.Action1)
     */
    public final Observable<T> doOnNext(Action1<? super T> onNext) {
        return boxed.doOnNext(onNext);
    }

    /**
     * @param onRequest
     * @return
     * @see rx.Observable#doOnRequest(rx.functions.Action1)
     */
    public final Observable<T> doOnRequest(Action1<Long> onRequest) {
        return boxed.doOnRequest(onRequest);
    }

    /**
     * @param subscribe
     * @return
     * @see rx.Observable#doOnSubscribe(rx.functions.Action0)
     */
    public final Observable<T> doOnSubscribe(Action0 subscribe) {
        return boxed.doOnSubscribe(subscribe);
    }

    /**
     * @param onTerminate
     * @return
     * @see rx.Observable#doOnTerminate(rx.functions.Action0)
     */
    public final Observable<T> doOnTerminate(Action0 onTerminate) {
        return boxed.doOnTerminate(onTerminate);
    }

    /**
     * @param unsubscribe
     * @return
     * @see rx.Observable#doOnUnsubscribe(rx.functions.Action0)
     */
    public final Observable<T> doOnUnsubscribe(Action0 unsubscribe) {
        return boxed.doOnUnsubscribe(unsubscribe);
    }

    /**
     * @param mapper
     * @return
     * @see rx.Observable#concatMapEager(rx.functions.Func1)
     */
    public final <R> Observable<R> concatMapEager(Func1<? super T, ? extends Observable<? extends R>> mapper) {
        return boxed.concatMapEager(mapper);
    }

    /**
     * @param mapper
     * @param capacityHint
     * @return
     * @see rx.Observable#concatMapEager(rx.functions.Func1, int)
     */
    public final <R> Observable<R> concatMapEager(Func1<? super T, ? extends Observable<? extends R>> mapper,
            int capacityHint) {
        return boxed.concatMapEager(mapper, capacityHint);
    }

    /**
     * @param mapper
     * @param capacityHint
     * @param maxConcurrent
     * @return
     * @see rx.Observable#concatMapEager(rx.functions.Func1, int, int)
     */
    public final <R> Observable<R> concatMapEager(Func1<? super T, ? extends Observable<? extends R>> mapper,
            int capacityHint, int maxConcurrent) {
        return boxed.concatMapEager(mapper, capacityHint, maxConcurrent);
    }

    /**
     * @param index
     * @return
     * @see rx.Observable#elementAt(int)
     */
    public final Observable<T> elementAt(int index) {
        return boxed.elementAt(index);
    }

    /**
     * @param index
     * @param defaultValue
     * @return
     * @see rx.Observable#elementAtOrDefault(int, java.lang.Object)
     */
    public final Observable<T> elementAtOrDefault(int index, T defaultValue) {
        return boxed.elementAtOrDefault(index, defaultValue);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#exists(rx.functions.Func1)
     */
    public final Observable<Boolean> exists(Func1<? super T, Boolean> predicate) {
        return boxed.exists(predicate);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#filter(rx.functions.Func1)
     */
    public final Observable<T> filter(Func1<? super T, Boolean> predicate) {
        return boxed.filter(predicate);
    }

    /**
     * @param action
     * @return
     * @deprecated
     * @see rx.Observable#finallyDo(rx.functions.Action0)
     */
    public final Observable<T> finallyDo(Action0 action) {
        return boxed.finallyDo(action);
    }

    /**
     * @param action
     * @return
     * @see rx.Observable#doAfterTerminate(rx.functions.Action0)
     */
    public final Observable<T> doAfterTerminate(Action0 action) {
        return boxed.doAfterTerminate(action);
    }

    /**
     * @return
     * @see rx.Observable#first()
     */
    public final Observable<T> first() {
        return boxed.first();
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#first(rx.functions.Func1)
     */
    public final Observable<T> first(Func1<? super T, Boolean> predicate) {
        return boxed.first(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see rx.Observable#firstOrDefault(java.lang.Object)
     */
    public final Observable<T> firstOrDefault(T defaultValue) {
        return boxed.firstOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see rx.Observable#firstOrDefault(java.lang.Object, rx.functions.Func1)
     */
    public final Observable<T> firstOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.firstOrDefault(defaultValue, predicate);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1)
     */
    public final <R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.flatMap(func);
    }

    /**
     * @param func
     * @param maxConcurrent
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1, int)
     */
    public final <R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> func,
            int maxConcurrent) {
        return boxed.flatMap(func, maxConcurrent);
    }

    /**
     * @param onNext
     * @param onError
     * @param onCompleted
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1, rx.functions.Func1, rx.functions.Func0)
     */
    public final <R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> onNext,
            Func1<? super Throwable, ? extends Observable<? extends R>> onError,
            Func0<? extends Observable<? extends R>> onCompleted) {
        return boxed.flatMap(onNext, onError, onCompleted);
    }

    /**
     * @param onNext
     * @param onError
     * @param onCompleted
     * @param maxConcurrent
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1, rx.functions.Func1, rx.functions.Func0, int)
     */
    public final <R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> onNext,
            Func1<? super Throwable, ? extends Observable<? extends R>> onError,
            Func0<? extends Observable<? extends R>> onCompleted, int maxConcurrent) {
        return boxed.flatMap(onNext, onError, onCompleted, maxConcurrent);
    }

    /**
     * @param collectionSelector
     * @param resultSelector
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1, rx.functions.Func2)
     */
    public final <U, R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.flatMap(collectionSelector, resultSelector);
    }

    /**
     * @param collectionSelector
     * @param resultSelector
     * @param maxConcurrent
     * @return
     * @see rx.Observable#flatMap(rx.functions.Func1, rx.functions.Func2, int)
     */
    public final <U, R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector, int maxConcurrent) {
        return boxed.flatMap(collectionSelector, resultSelector, maxConcurrent);
    }

    /**
     * @param collectionSelector
     * @return
     * @see rx.Observable#flatMapIterable(rx.functions.Func1)
     */
    public final <R> Observable<R> flatMapIterable(
            Func1<? super T, ? extends Iterable<? extends R>> collectionSelector) {
        return boxed.flatMapIterable(collectionSelector);
    }

    /**
     * @param collectionSelector
     * @param maxConcurrent
     * @return
     * @see rx.Observable#flatMapIterable(rx.functions.Func1, int)
     */
    public final <R> Observable<R> flatMapIterable(Func1<? super T, ? extends Iterable<? extends R>> collectionSelector,
            int maxConcurrent) {
        return boxed.flatMapIterable(collectionSelector, maxConcurrent);
    }

    /**
     * @param collectionSelector
     * @param resultSelector
     * @return
     * @see rx.Observable#flatMapIterable(rx.functions.Func1, rx.functions.Func2)
     */
    public final <U, R> Observable<R> flatMapIterable(
            Func1<? super T, ? extends Iterable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.flatMapIterable(collectionSelector, resultSelector);
    }

    /**
     * @param collectionSelector
     * @param resultSelector
     * @param maxConcurrent
     * @return
     * @see rx.Observable#flatMapIterable(rx.functions.Func1, rx.functions.Func2, int)
     */
    public final <U, R> Observable<R> flatMapIterable(
            Func1<? super T, ? extends Iterable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector, int maxConcurrent) {
        return boxed.flatMapIterable(collectionSelector, resultSelector, maxConcurrent);
    }

    /**
     * @param onNext
     * @see rx.Observable#forEach(rx.functions.Action1)
     */
    public final void forEach(Action1<? super T> onNext) {
        boxed.forEach(onNext);
    }

    /**
     * @param onNext
     * @param onError
     * @see rx.Observable#forEach(rx.functions.Action1, rx.functions.Action1)
     */
    public final void forEach(Action1<? super T> onNext, Action1<Throwable> onError) {
        boxed.forEach(onNext, onError);
    }

    /**
     * @param onNext
     * @param onError
     * @param onComplete
     * @see rx.Observable#forEach(rx.functions.Action1, rx.functions.Action1, rx.functions.Action0)
     */
    public final void forEach(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onComplete) {
        boxed.forEach(onNext, onError, onComplete);
    }

    /**
     * @param keySelector
     * @param elementSelector
     * @return
     * @see rx.Observable#groupBy(rx.functions.Func1, rx.functions.Func1)
     */
    public final <K, R> Observable<GroupedObservable<K, R>> groupBy(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends R> elementSelector) {
        return boxed.groupBy(keySelector, elementSelector);
    }

    /**
     * @param keySelector
     * @return
     * @see rx.Observable#groupBy(rx.functions.Func1)
     */
    public final <K> Observable<GroupedObservable<K, T>> groupBy(Func1<? super T, ? extends K> keySelector) {
        return boxed.groupBy(keySelector);
    }

    /**
     * @param right
     * @param leftDuration
     * @param rightDuration
     * @param resultSelector
     * @return
     * @see rx.Observable#groupJoin(rx.Observable, rx.functions.Func1, rx.functions.Func1, rx.functions.Func2)
     */
    public final <T2, D1, D2, R> Observable<R> groupJoin(Observable<T2> right,
            Func1<? super T, ? extends Observable<D1>> leftDuration,
            Func1<? super T2, ? extends Observable<D2>> rightDuration,
            Func2<? super T, ? super Observable<T2>, ? extends R> resultSelector) {
        return boxed.groupJoin(right, leftDuration, rightDuration, resultSelector);
    }

    /**
     * @return
     * @see rx.Observable#ignoreElements()
     */
    public final Observable<T> ignoreElements() {
        return boxed.ignoreElements();
    }

    /**
     * @return
     * @see rx.Observable#isEmpty()
     */
    public final Observable<Boolean> isEmpty() {
        return boxed.isEmpty();
    }

    /**
     * @param right
     * @param leftDurationSelector
     * @param rightDurationSelector
     * @param resultSelector
     * @return
     * @see rx.Observable#join(rx.Observable, rx.functions.Func1, rx.functions.Func1, rx.functions.Func2)
     */
    public final <TRight, TLeftDuration, TRightDuration, R> Observable<R> join(Observable<TRight> right,
            Func1<T, Observable<TLeftDuration>> leftDurationSelector,
            Func1<TRight, Observable<TRightDuration>> rightDurationSelector, Func2<T, TRight, R> resultSelector) {
        return boxed.join(right, leftDurationSelector, rightDurationSelector, resultSelector);
    }

    /**
     * @return
     * @see rx.Observable#last()
     */
    public final Observable<T> last() {
        return boxed.last();
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#last(rx.functions.Func1)
     */
    public final Observable<T> last(Func1<? super T, Boolean> predicate) {
        return boxed.last(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see rx.Observable#lastOrDefault(java.lang.Object)
     */
    public final Observable<T> lastOrDefault(T defaultValue) {
        return boxed.lastOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see rx.Observable#lastOrDefault(java.lang.Object, rx.functions.Func1)
     */
    public final Observable<T> lastOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.lastOrDefault(defaultValue, predicate);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#limit(int)
     */
    public final Observable<T> limit(int count) {
        return boxed.limit(count);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#map(rx.functions.Func1)
     */
    public final <R> Observable<R> map(Func1<? super T, ? extends R> func) {
        return boxed.map(func);
    }

    /**
     * @return
     * @see rx.Observable#materialize()
     */
    public final Observable<Notification<T>> materialize() {
        return boxed.materialize();
    }

    /**
     * @param t1
     * @return
     * @see rx.Observable#mergeWith(rx.Observable)
     */
    public final Observable<T> mergeWith(Observable<? extends T> t1) {
        return boxed.mergeWith(t1);
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#observeOn(rx.Scheduler)
     */
    public final Observable<T> observeOn(Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    /**
     * @param scheduler
     * @param bufferSize
     * @return
     * @see rx.Observable#observeOn(rx.Scheduler, int)
     */
    public final Observable<T> observeOn(Scheduler scheduler, int bufferSize) {
        return boxed.observeOn(scheduler, bufferSize);
    }

    /**
     * @param scheduler
     * @param delayError
     * @return
     * @see rx.Observable#observeOn(rx.Scheduler, boolean)
     */
    public final Observable<T> observeOn(Scheduler scheduler, boolean delayError) {
        return boxed.observeOn(scheduler, delayError);
    }

    /**
     * @param scheduler
     * @param delayError
     * @param bufferSize
     * @return
     * @see rx.Observable#observeOn(rx.Scheduler, boolean, int)
     */
    public final Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.observeOn(scheduler, delayError, bufferSize);
    }

    /**
     * @param klass
     * @return
     * @see rx.Observable#ofType(java.lang.Class)
     */
    public final <R> Observable<R> ofType(Class<R> klass) {
        return boxed.ofType(klass);
    }

    /**
     * @return
     * @see rx.Observable#onBackpressureBuffer()
     */
    public final Observable<T> onBackpressureBuffer() {
        return boxed.onBackpressureBuffer();
    }

    /**
     * @param capacity
     * @return
     * @see rx.Observable#onBackpressureBuffer(long)
     */
    public final Observable<T> onBackpressureBuffer(long capacity) {
        return boxed.onBackpressureBuffer(capacity);
    }

    /**
     * @param capacity
     * @param onOverflow
     * @return
     * @see rx.Observable#onBackpressureBuffer(long, rx.functions.Action0)
     */
    public final Observable<T> onBackpressureBuffer(long capacity, Action0 onOverflow) {
        return boxed.onBackpressureBuffer(capacity, onOverflow);
    }

    /**
     * @param capacity
     * @param onOverflow
     * @param overflowStrategy
     * @return
     * @see rx.Observable#onBackpressureBuffer(long, rx.functions.Action0, rx.BackpressureOverflow.Strategy)
     */
    public final Observable<T> onBackpressureBuffer(long capacity, Action0 onOverflow, Strategy overflowStrategy) {
        return boxed.onBackpressureBuffer(capacity, onOverflow, overflowStrategy);
    }

    /**
     * @param onDrop
     * @return
     * @see rx.Observable#onBackpressureDrop(rx.functions.Action1)
     */
    public final Observable<T> onBackpressureDrop(Action1<? super T> onDrop) {
        return boxed.onBackpressureDrop(onDrop);
    }

    /**
     * @return
     * @see rx.Observable#onBackpressureDrop()
     */
    public final Observable<T> onBackpressureDrop() {
        return boxed.onBackpressureDrop();
    }

    /**
     * @return
     * @see rx.Observable#onBackpressureLatest()
     */
    public final Observable<T> onBackpressureLatest() {
        return boxed.onBackpressureLatest();
    }

    /**
     * @param resumeFunction
     * @return
     * @see rx.Observable#onErrorResumeNext(rx.functions.Func1)
     */
    public final Observable<T> onErrorResumeNext(Func1<Throwable, ? extends Observable<? extends T>> resumeFunction) {
        return boxed.onErrorResumeNext(resumeFunction);
    }

    /**
     * @param resumeSequence
     * @return
     * @see rx.Observable#onErrorResumeNext(rx.Observable)
     */
    public final Observable<T> onErrorResumeNext(Observable<? extends T> resumeSequence) {
        return boxed.onErrorResumeNext(resumeSequence);
    }

    /**
     * @param resumeFunction
     * @return
     * @see rx.Observable#onErrorReturn(rx.functions.Func1)
     */
    public final Observable<T> onErrorReturn(Func1<Throwable, ? extends T> resumeFunction) {
        return boxed.onErrorReturn(resumeFunction);
    }

    /**
     * @param resumeSequence
     * @return
     * @see rx.Observable#onExceptionResumeNext(rx.Observable)
     */
    public final Observable<T> onExceptionResumeNext(Observable<? extends T> resumeSequence) {
        return boxed.onExceptionResumeNext(resumeSequence);
    }

    /**
     * @return
     * @see rx.Observable#publish()
     */
    public final ConnectableObservable<T> publish() {
        return boxed.publish();
    }

    /**
     * @param selector
     * @return
     * @see rx.Observable#publish(rx.functions.Func1)
     */
    public final <R> Observable<R> publish(Func1<? super Observable<T>, ? extends Observable<R>> selector) {
        return boxed.publish(selector);
    }

    /**
     * @param accumulator
     * @return
     * @see rx.Observable#reduce(rx.functions.Func2)
     */
    public final Observable<T> reduce(Func2<T, T, T> accumulator) {
        return boxed.reduce(accumulator);
    }

    /**
     * @param initialValue
     * @param accumulator
     * @return
     * @see rx.Observable#reduce(java.lang.Object, rx.functions.Func2)
     */
    public final <R> Observable<R> reduce(R initialValue, Func2<R, ? super T, R> accumulator) {
        return boxed.reduce(initialValue, accumulator);
    }

    /**
     * @return
     * @see rx.Observable#repeat()
     */
    public final Observable<T> repeat() {
        return boxed.repeat();
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#repeat(rx.Scheduler)
     */
    public final Observable<T> repeat(Scheduler scheduler) {
        return boxed.repeat(scheduler);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#repeat(long)
     */
    public final Observable<T> repeat(long count) {
        return boxed.repeat(count);
    }

    /**
     * @param count
     * @param scheduler
     * @return
     * @see rx.Observable#repeat(long, rx.Scheduler)
     */
    public final Observable<T> repeat(long count, Scheduler scheduler) {
        return boxed.repeat(count, scheduler);
    }

    /**
     * @param notificationHandler
     * @param scheduler
     * @return
     * @see rx.Observable#repeatWhen(rx.functions.Func1, rx.Scheduler)
     */
    public final Observable<T> repeatWhen(
            Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler,
            Scheduler scheduler) {
        return boxed.repeatWhen(notificationHandler, scheduler);
    }

    /**
     * @param notificationHandler
     * @return
     * @see rx.Observable#repeatWhen(rx.functions.Func1)
     */
    public final Observable<T> repeatWhen(
            Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler) {
        return boxed.repeatWhen(notificationHandler);
    }

    /**
     * @return
     * @see rx.Observable#replay()
     */
    public final ConnectableObservable<T> replay() {
        return boxed.replay();
    }

    /**
     * @param selector
     * @return
     * @see rx.Observable#replay(rx.functions.Func1)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector) {
        return boxed.replay(selector);
    }

    /**
     * @param selector
     * @param bufferSize
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, int)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            int bufferSize) {
        return boxed.replay(selector, bufferSize);
    }

    /**
     * @param selector
     * @param bufferSize
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, int, long, java.util.concurrent.TimeUnit)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(selector, bufferSize, time, unit);
    }

    /**
     * @param selector
     * @param bufferSize
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, int, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, time, unit, scheduler);
    }

    /**
     * @param selector
     * @param bufferSize
     * @param scheduler
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, int, rx.Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            int bufferSize, Scheduler scheduler) {
        return boxed.replay(selector, bufferSize, scheduler);
    }

    /**
     * @param selector
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, long, java.util.concurrent.TimeUnit)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector, long time,
            TimeUnit unit) {
        return boxed.replay(selector, time, unit);
    }

    /**
     * @param selector
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector, long time,
            TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(selector, time, unit, scheduler);
    }

    /**
     * @param selector
     * @param scheduler
     * @return
     * @see rx.Observable#replay(rx.functions.Func1, rx.Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            Scheduler scheduler) {
        return boxed.replay(selector, scheduler);
    }

    /**
     * @param bufferSize
     * @return
     * @see rx.Observable#replay(int)
     */
    public final ConnectableObservable<T> replay(int bufferSize) {
        return boxed.replay(bufferSize);
    }

    /**
     * @param bufferSize
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#replay(int, long, java.util.concurrent.TimeUnit)
     */
    public final ConnectableObservable<T> replay(int bufferSize, long time, TimeUnit unit) {
        return boxed.replay(bufferSize, time, unit);
    }

    /**
     * @param bufferSize
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#replay(int, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final ConnectableObservable<T> replay(int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(bufferSize, time, unit, scheduler);
    }

    /**
     * @param bufferSize
     * @param scheduler
     * @return
     * @see rx.Observable#replay(int, rx.Scheduler)
     */
    public final ConnectableObservable<T> replay(int bufferSize, Scheduler scheduler) {
        return boxed.replay(bufferSize, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#replay(long, java.util.concurrent.TimeUnit)
     */
    public final ConnectableObservable<T> replay(long time, TimeUnit unit) {
        return boxed.replay(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#replay(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final ConnectableObservable<T> replay(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(time, unit, scheduler);
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#replay(rx.Scheduler)
     */
    public final ConnectableObservable<T> replay(Scheduler scheduler) {
        return boxed.replay(scheduler);
    }

    /**
     * @return
     * @see rx.Observable#retry()
     */
    public final Observable<T> retry() {
        return boxed.retry();
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#retry(long)
     */
    public final Observable<T> retry(long count) {
        return boxed.retry(count);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#retry(rx.functions.Func2)
     */
    public final Observable<T> retry(Func2<Integer, Throwable, Boolean> predicate) {
        return boxed.retry(predicate);
    }

    /**
     * @param notificationHandler
     * @return
     * @see rx.Observable#retryWhen(rx.functions.Func1)
     */
    public final Observable<T> retryWhen(
            Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
        return boxed.retryWhen(notificationHandler);
    }

    /**
     * @param notificationHandler
     * @param scheduler
     * @return
     * @see rx.Observable#retryWhen(rx.functions.Func1, rx.Scheduler)
     */
    public final Observable<T> retryWhen(
            Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler,
            Scheduler scheduler) {
        return boxed.retryWhen(notificationHandler, scheduler);
    }

    /**
     * @param period
     * @param unit
     * @return
     * @see rx.Observable#sample(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> sample(long period, TimeUnit unit) {
        return boxed.sample(period, unit);
    }

    /**
     * @param period
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#sample(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> sample(long period, TimeUnit unit, Scheduler scheduler) {
        return boxed.sample(period, unit, scheduler);
    }

    /**
     * @param sampler
     * @return
     * @see rx.Observable#sample(rx.Observable)
     */
    public final <U> Observable<T> sample(Observable<U> sampler) {
        return boxed.sample(sampler);
    }

    /**
     * @param accumulator
     * @return
     * @see rx.Observable#scan(rx.functions.Func2)
     */
    public final Observable<T> scan(Func2<T, T, T> accumulator) {
        return boxed.scan(accumulator);
    }

    /**
     * @param initialValue
     * @param accumulator
     * @return
     * @see rx.Observable#scan(java.lang.Object, rx.functions.Func2)
     */
    public final <R> Observable<R> scan(R initialValue, Func2<R, ? super T, R> accumulator) {
        return boxed.scan(initialValue, accumulator);
    }

    /**
     * @return
     * @see rx.Observable#serialize()
     */
    public final Observable<T> serialize() {
        return boxed.serialize();
    }

    /**
     * @return
     * @see rx.Observable#share()
     */
    public final Observable<T> share() {
        return boxed.share();
    }

    /**
     * @return
     * @see rx.Observable#single()
     */
    public final Observable<T> single() {
        return boxed.single();
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#single(rx.functions.Func1)
     */
    public final Observable<T> single(Func1<? super T, Boolean> predicate) {
        return boxed.single(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see rx.Observable#singleOrDefault(java.lang.Object)
     */
    public final Observable<T> singleOrDefault(T defaultValue) {
        return boxed.singleOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see rx.Observable#singleOrDefault(java.lang.Object, rx.functions.Func1)
     */
    public final Observable<T> singleOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.singleOrDefault(defaultValue, predicate);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#skip(int)
     */
    public final Observable<T> skip(int count) {
        return boxed.skip(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#skip(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> skip(long time, TimeUnit unit) {
        return boxed.skip(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#skip(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> skip(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skip(time, unit, scheduler);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#skipLast(int)
     */
    public final Observable<T> skipLast(int count) {
        return boxed.skipLast(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#skipLast(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> skipLast(long time, TimeUnit unit) {
        return boxed.skipLast(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#skipLast(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> skipLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skipLast(time, unit, scheduler);
    }

    /**
     * @param other
     * @return
     * @see rx.Observable#skipUntil(rx.Observable)
     */
    public final <U> Observable<T> skipUntil(Observable<U> other) {
        return boxed.skipUntil(other);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#skipWhile(rx.functions.Func1)
     */
    public final Observable<T> skipWhile(Func1<? super T, Boolean> predicate) {
        return boxed.skipWhile(predicate);
    }

    /**
     * @param values
     * @return
     * @see rx.Observable#startWith(rx.Observable)
     */
    public final Observable<T> startWith(Observable<T> values) {
        return boxed.startWith(values);
    }

    /**
     * @param values
     * @return
     * @see rx.Observable#startWith(java.lang.Iterable)
     */
    public final Observable<T> startWith(Iterable<T> values) {
        return boxed.startWith(values);
    }

    /**
     * @param t1
     * @return
     * @see rx.Observable#startWith(java.lang.Object)
     */
    public final Observable<T> startWith(T t1) {
        return boxed.startWith(t1);
    }

    /**
     * @param t1
     * @param t2
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2) {
        return boxed.startWith(t1, t2);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3) {
        return boxed.startWith(t1, t2, t3);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4) {
        return boxed.startWith(t1, t2, t3, t4);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @param t5
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5) {
        return boxed.startWith(t1, t2, t3, t4, t5);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @param t5
     * @param t6
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5, T t6) {
        return boxed.startWith(t1, t2, t3, t4, t5, t6);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @param t5
     * @param t6
     * @param t7
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return boxed.startWith(t1, t2, t3, t4, t5, t6, t7);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @param t5
     * @param t6
     * @param t7
     * @param t8
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return boxed.startWith(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @param t5
     * @param t6
     * @param t7
     * @param t8
     * @param t9
     * @return
     * @see rx.Observable#startWith(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8, T t9) {
        return boxed.startWith(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    /**
     * @return
     * @see rx.Observable#subscribe()
     */
    public final Subscription subscribe() {
        return boxed.subscribe();
    }

    /**
     * @param onNext
     * @return
     * @see rx.Observable#subscribe(rx.functions.Action1)
     */
    public final Subscription subscribe(Action1<? super T> onNext) {
        return boxed.subscribe(onNext);
    }

    /**
     * @param onNext
     * @param onError
     * @return
     * @see rx.Observable#subscribe(rx.functions.Action1, rx.functions.Action1)
     */
    public final Subscription subscribe(Action1<? super T> onNext, Action1<Throwable> onError) {
        return boxed.subscribe(onNext, onError);
    }

    /**
     * @param onNext
     * @param onError
     * @param onComplete
     * @return
     * @see rx.Observable#subscribe(rx.functions.Action1, rx.functions.Action1, rx.functions.Action0)
     */
    public final Subscription subscribe(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onComplete) {
        return boxed.subscribe(onNext, onError, onComplete);
    }

    /**
     * @param observer
     * @return
     * @see rx.Observable#subscribe(rx.Observer)
     */
    public final Subscription subscribe(Observer<? super T> observer) {
        return boxed.subscribe(observer);
    }

    /**
     * @param subscriber
     * @return
     * @see rx.Observable#unsafeSubscribe(rx.Subscriber)
     */
    public final Subscription unsafeSubscribe(rx.Subscriber<? super T> subscriber) {
        return boxed.unsafeSubscribe(subscriber);
    }

    /**
     * @param subscriber
     * @return
     * @see rx.Observable#subscribe(rx.Subscriber)
     */
    public final Subscription subscribe(rx.Subscriber<? super T> subscriber) {
        return boxed.subscribe(subscriber);
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#subscribeOn(rx.Scheduler)
     */
    public final Observable<T> subscribeOn(Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#switchMap(rx.functions.Func1)
     */
    public final <R> Observable<R> switchMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.switchMap(func);
    }

    /**
     * @param func
     * @return
     * @see rx.Observable#switchMapDelayError(rx.functions.Func1)
     */
    public final <R> Observable<R> switchMapDelayError(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.switchMapDelayError(func);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#take(int)
     */
    public final Observable<T> take(int count) {
        return boxed.take(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#take(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> take(long time, TimeUnit unit) {
        return boxed.take(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#take(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> take(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.take(time, unit, scheduler);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#takeFirst(rx.functions.Func1)
     */
    public final Observable<T> takeFirst(Func1<? super T, Boolean> predicate) {
        return boxed.takeFirst(predicate);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#takeLast(int)
     */
    public final Observable<T> takeLast(int count) {
        return boxed.takeLast(count);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#takeLast(int, long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> takeLast(int count, long time, TimeUnit unit) {
        return boxed.takeLast(count, time, unit);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#takeLast(int, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> takeLast(int count, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(count, time, unit, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#takeLast(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> takeLast(long time, TimeUnit unit) {
        return boxed.takeLast(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#takeLast(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> takeLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(time, unit, scheduler);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#takeLastBuffer(int)
     */
    public final Observable<List<T>> takeLastBuffer(int count) {
        return boxed.takeLastBuffer(count);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#takeLastBuffer(int, long, java.util.concurrent.TimeUnit)
     */
    public final Observable<List<T>> takeLastBuffer(int count, long time, TimeUnit unit) {
        return boxed.takeLastBuffer(count, time, unit);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#takeLastBuffer(int, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<List<T>> takeLastBuffer(int count, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLastBuffer(count, time, unit, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see rx.Observable#takeLastBuffer(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<List<T>> takeLastBuffer(long time, TimeUnit unit) {
        return boxed.takeLastBuffer(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#takeLastBuffer(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<List<T>> takeLastBuffer(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLastBuffer(time, unit, scheduler);
    }

    /**
     * @param other
     * @return
     * @see rx.Observable#takeUntil(rx.Observable)
     */
    public final <E> Observable<T> takeUntil(Observable<? extends E> other) {
        return boxed.takeUntil(other);
    }

    /**
     * @param predicate
     * @return
     * @see rx.Observable#takeWhile(rx.functions.Func1)
     */
    public final Observable<T> takeWhile(Func1<? super T, Boolean> predicate) {
        return boxed.takeWhile(predicate);
    }

    /**
     * @param stopPredicate
     * @return
     * @see rx.Observable#takeUntil(rx.functions.Func1)
     */
    public final Observable<T> takeUntil(Func1<? super T, Boolean> stopPredicate) {
        return boxed.takeUntil(stopPredicate);
    }

    /**
     * @param windowDuration
     * @param unit
     * @return
     * @see rx.Observable#throttleFirst(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> throttleFirst(long windowDuration, TimeUnit unit) {
        return boxed.throttleFirst(windowDuration, unit);
    }

    /**
     * @param skipDuration
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#throttleFirst(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> throttleFirst(long skipDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleFirst(skipDuration, unit, scheduler);
    }

    /**
     * @param intervalDuration
     * @param unit
     * @return
     * @see rx.Observable#throttleLast(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> throttleLast(long intervalDuration, TimeUnit unit) {
        return boxed.throttleLast(intervalDuration, unit);
    }

    /**
     * @param intervalDuration
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#throttleLast(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> throttleLast(long intervalDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleLast(intervalDuration, unit, scheduler);
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @see rx.Observable#throttleWithTimeout(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> throttleWithTimeout(long timeout, TimeUnit unit) {
        return boxed.throttleWithTimeout(timeout, unit);
    }

    /**
     * @param timeout
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#throttleWithTimeout(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> throttleWithTimeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleWithTimeout(timeout, unit, scheduler);
    }

    /**
     * @return
     * @see rx.Observable#timeInterval()
     */
    public final Observable<TimeInterval<T>> timeInterval() {
        return boxed.timeInterval();
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#timeInterval(rx.Scheduler)
     */
    public final Observable<TimeInterval<T>> timeInterval(Scheduler scheduler) {
        return boxed.timeInterval(scheduler);
    }

    /**
     * @param firstTimeoutSelector
     * @param timeoutSelector
     * @return
     * @see rx.Observable#timeout(rx.functions.Func0, rx.functions.Func1)
     */
    public final <U, V> Observable<T> timeout(Func0<? extends Observable<U>> firstTimeoutSelector,
            Func1<? super T, ? extends Observable<V>> timeoutSelector) {
        return boxed.timeout(firstTimeoutSelector, timeoutSelector);
    }

    /**
     * @param firstTimeoutSelector
     * @param timeoutSelector
     * @param other
     * @return
     * @see rx.Observable#timeout(rx.functions.Func0, rx.functions.Func1, rx.Observable)
     */
    public final <U, V> Observable<T> timeout(Func0<? extends Observable<U>> firstTimeoutSelector,
            Func1<? super T, ? extends Observable<V>> timeoutSelector, Observable<? extends T> other) {
        return boxed.timeout(firstTimeoutSelector, timeoutSelector, other);
    }

    /**
     * @param timeoutSelector
     * @return
     * @see rx.Observable#timeout(rx.functions.Func1)
     */
    public final <V> Observable<T> timeout(Func1<? super T, ? extends Observable<V>> timeoutSelector) {
        return boxed.timeout(timeoutSelector);
    }

    /**
     * @param timeoutSelector
     * @param other
     * @return
     * @see rx.Observable#timeout(rx.functions.Func1, rx.Observable)
     */
    public final <V> Observable<T> timeout(Func1<? super T, ? extends Observable<V>> timeoutSelector,
            Observable<? extends T> other) {
        return boxed.timeout(timeoutSelector, other);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @return
     * @see rx.Observable#timeout(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit) {
        return boxed.timeout(timeout, timeUnit);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @param other
     * @return
     * @see rx.Observable#timeout(long, java.util.concurrent.TimeUnit, rx.Observable)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit, Observable<? extends T> other) {
        return boxed.timeout(timeout, timeUnit, other);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @param other
     * @param scheduler
     * @return
     * @see rx.Observable#timeout(long, java.util.concurrent.TimeUnit, rx.Observable, rx.Scheduler)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit, Observable<? extends T> other,
            Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, other, scheduler);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @param scheduler
     * @return
     * @see rx.Observable#timeout(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, scheduler);
    }

    /**
     * @return
     * @see rx.Observable#timestamp()
     */
    public final Observable<Timestamped<T>> timestamp() {
        return boxed.timestamp();
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#timestamp(rx.Scheduler)
     */
    public final Observable<Timestamped<T>> timestamp(Scheduler scheduler) {
        return boxed.timestamp(scheduler);
    }

    /**
     * @return
     * @see rx.Observable#toBlocking()
     */
    public final BlockingObservable<T> toBlocking() {
        return boxed.toBlocking();
    }

    /**
     * @return
     * @see rx.Observable#toList()
     */
    public final Observable<List<T>> toList() {
        return boxed.toList();
    }

    /**
     * @param keySelector
     * @return
     * @see rx.Observable#toMap(rx.functions.Func1)
     */
    public final <K> Observable<Map<K, T>> toMap(Func1<? super T, ? extends K> keySelector) {
        return boxed.toMap(keySelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @return
     * @see rx.Observable#toMap(rx.functions.Func1, rx.functions.Func1)
     */
    public final <K, V> Observable<Map<K, V>> toMap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector) {
        return boxed.toMap(keySelector, valueSelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @param mapFactory
     * @return
     * @see rx.Observable#toMap(rx.functions.Func1, rx.functions.Func1, rx.functions.Func0)
     */
    public final <K, V> Observable<Map<K, V>> toMap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, V>> mapFactory) {
        return boxed.toMap(keySelector, valueSelector, mapFactory);
    }

    /**
     * @param keySelector
     * @return
     * @see rx.Observable#toMultimap(rx.functions.Func1)
     */
    public final <K> Observable<Map<K, Collection<T>>> toMultimap(Func1<? super T, ? extends K> keySelector) {
        return boxed.toMultimap(keySelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @return
     * @see rx.Observable#toMultimap(rx.functions.Func1, rx.functions.Func1)
     */
    public final <K, V> Observable<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector) {
        return boxed.toMultimap(keySelector, valueSelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @param mapFactory
     * @return
     * @see rx.Observable#toMultimap(rx.functions.Func1, rx.functions.Func1, rx.functions.Func0)
     */
    public final <K, V> Observable<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, Collection<V>>> mapFactory) {
        return boxed.toMultimap(keySelector, valueSelector, mapFactory);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @param mapFactory
     * @param collectionFactory
     * @return
     * @see rx.Observable#toMultimap(rx.functions.Func1, rx.functions.Func1, rx.functions.Func0, rx.functions.Func1)
     */
    public final <K, V> Observable<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, Collection<V>>> mapFactory,
            Func1<? super K, ? extends Collection<V>> collectionFactory) {
        return boxed.toMultimap(keySelector, valueSelector, mapFactory, collectionFactory);
    }

    /**
     * @return
     * @see rx.Observable#toSortedList()
     */
    public final Observable<List<T>> toSortedList() {
        return boxed.toSortedList();
    }

    /**
     * @param sortFunction
     * @return
     * @see rx.Observable#toSortedList(rx.functions.Func2)
     */
    public final Observable<List<T>> toSortedList(Func2<? super T, ? super T, Integer> sortFunction) {
        return boxed.toSortedList(sortFunction);
    }

    /**
     * @param initialCapacity
     * @return
     * @see rx.Observable#toSortedList(int)
     */
    public final Observable<List<T>> toSortedList(int initialCapacity) {
        return boxed.toSortedList(initialCapacity);
    }

    /**
     * @param sortFunction
     * @param initialCapacity
     * @return
     * @see rx.Observable#toSortedList(rx.functions.Func2, int)
     */
    public final Observable<List<T>> toSortedList(Func2<? super T, ? super T, Integer> sortFunction,
            int initialCapacity) {
        return boxed.toSortedList(sortFunction, initialCapacity);
    }

    /**
     * @param scheduler
     * @return
     * @see rx.Observable#unsubscribeOn(rx.Scheduler)
     */
    public final Observable<T> unsubscribeOn(Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    /**
     * @param other
     * @param resultSelector
     * @return
     * @see rx.Observable#withLatestFrom(rx.Observable, rx.functions.Func2)
     */
    public final <U, R> Observable<R> withLatestFrom(Observable<? extends U> other,
            Func2<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.withLatestFrom(other, resultSelector);
    }

    /**
     * @param closingSelector
     * @return
     * @see rx.Observable#window(rx.functions.Func0)
     */
    public final <TClosing> Observable<Observable<T>> window(
            Func0<? extends Observable<? extends TClosing>> closingSelector) {
        return boxed.window(closingSelector);
    }

    /**
     * @param count
     * @return
     * @see rx.Observable#window(int)
     */
    public final Observable<Observable<T>> window(int count) {
        return boxed.window(count);
    }

    /**
     * @param count
     * @param skip
     * @return
     * @see rx.Observable#window(int, int)
     */
    public final Observable<Observable<T>> window(int count, int skip) {
        return boxed.window(count, skip);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @return
     * @see rx.Observable#window(long, long, java.util.concurrent.TimeUnit)
     */
    public final Observable<Observable<T>> window(long timespan, long timeshift, TimeUnit unit) {
        return boxed.window(timespan, timeshift, unit);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#window(long, long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, long timeshift, TimeUnit unit, Scheduler scheduler) {
        return boxed.window(timespan, timeshift, unit, scheduler);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @param count
     * @param scheduler
     * @return
     * @see rx.Observable#window(long, long, java.util.concurrent.TimeUnit, int, rx.Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, long timeshift, TimeUnit unit, int count,
            Scheduler scheduler) {
        return boxed.window(timespan, timeshift, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @return
     * @see rx.Observable#window(long, java.util.concurrent.TimeUnit)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit) {
        return boxed.window(timespan, unit);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @return
     * @see rx.Observable#window(long, java.util.concurrent.TimeUnit, int)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit, int count) {
        return boxed.window(timespan, unit, count);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @param scheduler
     * @return
     * @see rx.Observable#window(long, java.util.concurrent.TimeUnit, int, rx.Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit, int count, Scheduler scheduler) {
        return boxed.window(timespan, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @param scheduler
     * @return
     * @see rx.Observable#window(long, java.util.concurrent.TimeUnit, rx.Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.window(timespan, unit, scheduler);
    }

    /**
     * @param windowOpenings
     * @param closingSelector
     * @return
     * @see rx.Observable#window(rx.Observable, rx.functions.Func1)
     */
    public final <TOpening, TClosing> Observable<Observable<T>> window(Observable<? extends TOpening> windowOpenings,
            Func1<? super TOpening, ? extends Observable<? extends TClosing>> closingSelector) {
        return boxed.window(windowOpenings, closingSelector);
    }

    /**
     * @param boundary
     * @return
     * @see rx.Observable#window(rx.Observable)
     */
    public final <U> Observable<Observable<T>> window(Observable<U> boundary) {
        return boxed.window(boundary);
    }

    /**
     * @param other
     * @param zipFunction
     * @return
     * @see rx.Observable#zipWith(java.lang.Iterable, rx.functions.Func2)
     */
    public final <T2, R> Observable<R> zipWith(Iterable<? extends T2> other,
            Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return boxed.zipWith(other, zipFunction);
    }

    /**
     * @param other
     * @param zipFunction
     * @return
     * @see rx.Observable#zipWith(rx.Observable, rx.functions.Func2)
     */
    public final <T2, R> Observable<R> zipWith(Observable<? extends T2> other,
            Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return boxed.zipWith(other, zipFunction);
    }

}
