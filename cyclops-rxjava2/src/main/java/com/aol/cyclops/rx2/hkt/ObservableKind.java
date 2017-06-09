package com.aol.cyclops.rx2.hkt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aol.cyclops2.hkt.Higher;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import cyclops.companion.rx2.Observables;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import io.reactivex.Observable;

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

    /**
     * @param conversion
     * @return
     * @see Observable#extend(Func1)
     */
    public <R> R extend(Func1<? super OnSubscribe<T>, ? extends R> conversion) {
        return boxed.extend(conversion);
    }

    /**
     * @return
     * @see Object#toString()
     */
    public String toString() {
        return boxed.toString();
    }

    /**
     * @param operator
     * @return
     * @see Observable#lift(Operator)
     */
    public final <R> Observable<R> lift(Operator<? extends R, ? super T> operator) {
        return boxed.lift(operator);
    }

    /**
     * @param transformer
     * @return
     * @see Observable#compose(Transformer)
     */
    public <R> Observable<R> compose(Transformer<? super T, ? extends R> transformer) {
        return boxed.compose(transformer);
    }

    /**
     * @return
     * @see Observable#toSingle()
     */
    public Single<T> toSingle() {
        return boxed.toSingle();
    }

    /**
     * @return
     * @see Observable#toCompletable()
     */
    public Completable toCompletable() {
        return boxed.toCompletable();
    }

    /**
     * @return
     * @see Observable#nest()
     */
    public final Observable<Observable<T>> nest() {
        return boxed.nest();
    }

    /**
     * @param predicate
     * @return
     * @see Observable#all(Func1)
     */
    public final Observable<Boolean> all(Func1<? super T, Boolean> predicate) {
        return boxed.all(predicate);
    }

    /**
     * @param t1
     * @return
     * @see Observable#ambWith(Observable)
     */
    public final Observable<T> ambWith(Observable<? extends T> t1) {
        return boxed.ambWith(t1);
    }

    /**
     * @return
     * @see Observable#asObservable()
     */
    public final Observable<T> asObservable() {
        return boxed.asObservable();
    }

    /**
     * @param bufferClosingSelector
     * @return
     * @see Observable#buffer(Func0)
     */
    public final <TClosing> Observable<List<T>> buffer(
            Func0<? extends Observable<? extends TClosing>> bufferClosingSelector) {
        return boxed.buffer(bufferClosingSelector);
    }

    /**
     * @param count
     * @return
     * @see Observable#buffer(int)
     */
    public final Observable<List<T>> buffer(int count) {
        return boxed.buffer(count);
    }

    /**
     * @param count
     * @param skip
     * @return
     * @see Observable#buffer(int, int)
     */
    public final Observable<List<T>> buffer(int count, int skip) {
        return boxed.buffer(count, skip);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @return
     * @see Observable#buffer(long, long, TimeUnit)
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
     * @see Observable#buffer(long, long, TimeUnit, Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, long timeshift, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, timeshift, unit, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @return
     * @see Observable#buffer(long, TimeUnit)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit) {
        return boxed.buffer(timespan, unit);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @return
     * @see Observable#buffer(long, TimeUnit, int)
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
     * @see Observable#buffer(long, TimeUnit, int, Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit, int count, Scheduler scheduler) {
        return boxed.buffer(timespan, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#buffer(long, TimeUnit, Scheduler)
     */
    public final Observable<List<T>> buffer(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.buffer(timespan, unit, scheduler);
    }

    /**
     * @param bufferOpenings
     * @param bufferClosingSelector
     * @return
     * @see Observable#buffer(Observable, Func1)
     */
    public final <TOpening, TClosing> Observable<List<T>> buffer(Observable<? extends TOpening> bufferOpenings,
            Func1<? super TOpening, ? extends Observable<? extends TClosing>> bufferClosingSelector) {
        return boxed.buffer(bufferOpenings, bufferClosingSelector);
    }

    /**
     * @param boundary
     * @return
     * @see Observable#buffer(Observable)
     */
    public final <B> Observable<List<T>> buffer(Observable<B> boundary) {
        return boxed.buffer(boundary);
    }

    /**
     * @param boundary
     * @param initialCapacity
     * @return
     * @see Observable#buffer(Observable, int)
     */
    public final <B> Observable<List<T>> buffer(Observable<B> boundary, int initialCapacity) {
        return boxed.buffer(boundary, initialCapacity);
    }

    /**
     * @return
     * @see Observable#cache()
     */
    public final Observable<T> cache() {
        return boxed.cache();
    }

    /**
     * @param initialCapacity
     * @return
     * @deprecated
     * @see Observable#cache(int)
     */
    public final Observable<T> cache(int initialCapacity) {
        return boxed.cache(initialCapacity);
    }

    /**
     * @param initialCapacity
     * @return
     * @see Observable#cacheWithInitialCapacity(int)
     */
    public final Observable<T> cacheWithInitialCapacity(int initialCapacity) {
        return boxed.cacheWithInitialCapacity(initialCapacity);
    }

    /**
     * @param klass
     * @return
     * @see Observable#cast(Class)
     */
    public final <R> Observable<R> cast(Class<R> klass) {
        return boxed.cast(klass);
    }

    /**
     * @param stateFactory
     * @param collector
     * @return
     * @see Observable#collect(Func0, Action2)
     */
    public final <R> Observable<R> collect(Func0<R> stateFactory, Action2<R, ? super T> collector) {
        return boxed.collect(stateFactory, collector);
    }

    /**
     * @param func
     * @return
     * @see Observable#concatMap(Func1)
     */
    public final <R> Observable<R> concatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.concatMap(func);
    }

    /**
     * @param func
     * @return
     * @see Observable#concatMapDelayError(Func1)
     */
    public final <R> Observable<R> concatMapDelayError(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.concatMapDelayError(func);
    }

    /**
     * @param collectionSelector
     * @return
     * @see Observable#concatMapIterable(Func1)
     */
    public final <R> Observable<R> concatMapIterable(
            Func1<? super T, ? extends Iterable<? extends R>> collectionSelector) {
        return boxed.concatMapIterable(collectionSelector);
    }

    /**
     * @param t1
     * @return
     * @see Observable#concatWith(Observable)
     */
    public final Observable<T> concatWith(Observable<? extends T> t1) {
        return boxed.concatWith(t1);
    }

    /**
     * @param element
     * @return
     * @see Observable#contains(Object)
     */
    public final Observable<Boolean> contains(Object element) {
        return boxed.contains(element);
    }

    /**
     * @return
     * @see Observable#count()
     */
    public final Observable<Integer> count() {
        return boxed.count();
    }

    /**
     * @return
     * @see Observable#countLong()
     */
    public final Observable<Long> countLong() {
        return boxed.countLong();
    }

    /**
     * @param debounceSelector
     * @return
     * @see Observable#debounce(Func1)
     */
    public final <U> Observable<T> debounce(Func1<? super T, ? extends Observable<U>> debounceSelector) {
        return boxed.debounce(debounceSelector);
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @see Observable#debounce(long, TimeUnit)
     */
    public final Observable<T> debounce(long timeout, TimeUnit unit) {
        return boxed.debounce(timeout, unit);
    }

    /**
     * @param timeout
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#debounce(long, TimeUnit, Scheduler)
     */
    public final Observable<T> debounce(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.debounce(timeout, unit, scheduler);
    }

    /**
     * @param defaultValue
     * @return
     * @see Observable#defaultIfEmpty(Object)
     */
    public final Observable<T> defaultIfEmpty(T defaultValue) {
        return boxed.defaultIfEmpty(defaultValue);
    }

    /**
     * @param alternate
     * @return
     * @see Observable#switchIfEmpty(Observable)
     */
    public final Observable<T> switchIfEmpty(Observable<? extends T> alternate) {
        return boxed.switchIfEmpty(alternate);
    }

    /**
     * @param subscriptionDelay
     * @param itemDelay
     * @return
     * @see Observable#delay(Func0, Func1)
     */
    public final <U, V> Observable<T> delay(Func0<? extends Observable<U>> subscriptionDelay,
            Func1<? super T, ? extends Observable<V>> itemDelay) {
        return boxed.delay(subscriptionDelay, itemDelay);
    }

    /**
     * @param itemDelay
     * @return
     * @see Observable#delay(Func1)
     */
    public final <U> Observable<T> delay(Func1<? super T, ? extends Observable<U>> itemDelay) {
        return boxed.delay(itemDelay);
    }

    /**
     * @param delay
     * @param unit
     * @return
     * @see Observable#delay(long, TimeUnit)
     */
    public final Observable<T> delay(long delay, TimeUnit unit) {
        return boxed.delay(delay, unit);
    }

    /**
     * @param delay
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#delay(long, TimeUnit, Scheduler)
     */
    public final Observable<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delay(delay, unit, scheduler);
    }

    /**
     * @param delay
     * @param unit
     * @return
     * @see Observable#delaySubscription(long, TimeUnit)
     */
    public final Observable<T> delaySubscription(long delay, TimeUnit unit) {
        return boxed.delaySubscription(delay, unit);
    }

    /**
     * @param delay
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#delaySubscription(long, TimeUnit, Scheduler)
     */
    public final Observable<T> delaySubscription(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delaySubscription(delay, unit, scheduler);
    }

    /**
     * @param subscriptionDelay
     * @return
     * @see Observable#delaySubscription(Func0)
     */
    public final <U> Observable<T> delaySubscription(Func0<? extends Observable<U>> subscriptionDelay) {
        return boxed.delaySubscription(subscriptionDelay);
    }

    /**
     * @param other
     * @return
     * @see Observable#delaySubscription(Observable)
     */
    public final <U> Observable<T> delaySubscription(Observable<U> other) {
        return boxed.delaySubscription(other);
    }

    /**
     * @return
     * @see Observable#dematerialize()
     */
    public final <T2> Observable<T2> dematerialize() {
        return boxed.dematerialize();
    }

    /**
     * @return
     * @see Observable#distinct()
     */
    public final Observable<T> distinct() {
        return boxed.distinct();
    }

    /**
     * @param keySelector
     * @return
     * @see Observable#distinct(Func1)
     */
    public final <U> Observable<T> distinct(Func1<? super T, ? extends U> keySelector) {
        return boxed.distinct(keySelector);
    }

    /**
     * @return
     * @see Observable#distinctUntilChanged()
     */
    public final Observable<T> distinctUntilChanged() {
        return boxed.distinctUntilChanged();
    }

    /**
     * @param keySelector
     * @return
     * @see Observable#distinctUntilChanged(Func1)
     */
    public final <U> Observable<T> distinctUntilChanged(Func1<? super T, ? extends U> keySelector) {
        return boxed.distinctUntilChanged(keySelector);
    }

    /**
     * @param onCompleted
     * @return
     * @see Observable#doOnCompleted(Action0)
     */
    public final Observable<T> doOnCompleted(Action0 onCompleted) {
        return boxed.doOnCompleted(onCompleted);
    }

    /**
     * @param onNotification
     * @return
     * @see Observable#doOnEach(Action1)
     */
    public final Observable<T> doOnEach(Action1<Notification<? super T>> onNotification) {
        return boxed.doOnEach(onNotification);
    }

    /**
     * @param observer
     * @return
     * @see Observable#doOnEach(Observer)
     */
    public final Observable<T> doOnEach(Observer<? super T> observer) {
        return boxed.doOnEach(observer);
    }

    /**
     * @param onError
     * @return
     * @see Observable#doOnError(Action1)
     */
    public final Observable<T> doOnError(Action1<Throwable> onError) {
        return boxed.doOnError(onError);
    }

    /**
     * @param onNext
     * @return
     * @see Observable#doOnNext(Action1)
     */
    public final Observable<T> doOnNext(Action1<? super T> onNext) {
        return boxed.doOnNext(onNext);
    }

    /**
     * @param onRequest
     * @return
     * @see Observable#doOnRequest(Action1)
     */
    public final Observable<T> doOnRequest(Action1<Long> onRequest) {
        return boxed.doOnRequest(onRequest);
    }

    /**
     * @param subscribe
     * @return
     * @see Observable#doOnSubscribe(Action0)
     */
    public final Observable<T> doOnSubscribe(Action0 subscribe) {
        return boxed.doOnSubscribe(subscribe);
    }

    /**
     * @param onTerminate
     * @return
     * @see Observable#doOnTerminate(Action0)
     */
    public final Observable<T> doOnTerminate(Action0 onTerminate) {
        return boxed.doOnTerminate(onTerminate);
    }

    /**
     * @param unsubscribe
     * @return
     * @see Observable#doOnUnsubscribe(Action0)
     */
    public final Observable<T> doOnUnsubscribe(Action0 unsubscribe) {
        return boxed.doOnUnsubscribe(unsubscribe);
    }

    /**
     * @param mapper
     * @return
     * @see Observable#concatMapEager(Func1)
     */
    public final <R> Observable<R> concatMapEager(Func1<? super T, ? extends Observable<? extends R>> mapper) {
        return boxed.concatMapEager(mapper);
    }

    /**
     * @param mapper
     * @param capacityHint
     * @return
     * @see Observable#concatMapEager(Func1, int)
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
     * @see Observable#concatMapEager(Func1, int, int)
     */
    public final <R> Observable<R> concatMapEager(Func1<? super T, ? extends Observable<? extends R>> mapper,
            int capacityHint, int maxConcurrent) {
        return boxed.concatMapEager(mapper, capacityHint, maxConcurrent);
    }

    /**
     * @param index
     * @return
     * @see Observable#elementAt(int)
     */
    public final Observable<T> elementAt(int index) {
        return boxed.elementAt(index);
    }

    /**
     * @param index
     * @param defaultValue
     * @return
     * @see Observable#elementAtOrDefault(int, Object)
     */
    public final Observable<T> elementAtOrDefault(int index, T defaultValue) {
        return boxed.elementAtOrDefault(index, defaultValue);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#exists(Func1)
     */
    public final Observable<Boolean> exists(Func1<? super T, Boolean> predicate) {
        return boxed.exists(predicate);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#filter(Func1)
     */
    public final Observable<T> filter(Func1<? super T, Boolean> predicate) {
        return boxed.filter(predicate);
    }

    /**
     * @param action
     * @return
     * @deprecated
     * @see Observable#finallyDo(Action0)
     */
    public final Observable<T> finallyDo(Action0 action) {
        return boxed.finallyDo(action);
    }

    /**
     * @param action
     * @return
     * @see Observable#doAfterTerminate(Action0)
     */
    public final Observable<T> doAfterTerminate(Action0 action) {
        return boxed.doAfterTerminate(action);
    }

    /**
     * @return
     * @see Observable#first()
     */
    public final Observable<T> first() {
        return boxed.first();
    }

    /**
     * @param predicate
     * @return
     * @see Observable#first(Func1)
     */
    public final Observable<T> first(Func1<? super T, Boolean> predicate) {
        return boxed.first(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see Observable#firstOrDefault(Object)
     */
    public final Observable<T> firstOrDefault(T defaultValue) {
        return boxed.firstOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see Observable#firstOrDefault(Object, Func1)
     */
    public final Observable<T> firstOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.firstOrDefault(defaultValue, predicate);
    }

    /**
     * @param func
     * @return
     * @see Observable#flatMap(Func1)
     */
    public final <R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.flatMap(func);
    }

    /**
     * @param func
     * @param maxConcurrent
     * @return
     * @see Observable#flatMap(Func1, int)
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
     * @see Observable#flatMap(Func1, Func1, Func0)
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
     * @see Observable#flatMap(Func1, Func1, Func0, int)
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
     * @see Observable#flatMap(Func1, Func2)
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
     * @see Observable#flatMap(Func1, Func2, int)
     */
    public final <U, R> Observable<R> flatMap(Func1<? super T, ? extends Observable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector, int maxConcurrent) {
        return boxed.flatMap(collectionSelector, resultSelector, maxConcurrent);
    }

    /**
     * @param collectionSelector
     * @return
     * @see Observable#flatMapIterable(Func1)
     */
    public final <R> Observable<R> flatMapIterable(
            Func1<? super T, ? extends Iterable<? extends R>> collectionSelector) {
        return boxed.flatMapIterable(collectionSelector);
    }

    /**
     * @param collectionSelector
     * @param maxConcurrent
     * @return
     * @see Observable#flatMapIterable(Func1, int)
     */
    public final <R> Observable<R> flatMapIterable(Func1<? super T, ? extends Iterable<? extends R>> collectionSelector,
            int maxConcurrent) {
        return boxed.flatMapIterable(collectionSelector, maxConcurrent);
    }

    /**
     * @param collectionSelector
     * @param resultSelector
     * @return
     * @see Observable#flatMapIterable(Func1, Func2)
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
     * @see Observable#flatMapIterable(Func1, Func2, int)
     */
    public final <U, R> Observable<R> flatMapIterable(
            Func1<? super T, ? extends Iterable<? extends U>> collectionSelector,
            Func2<? super T, ? super U, ? extends R> resultSelector, int maxConcurrent) {
        return boxed.flatMapIterable(collectionSelector, resultSelector, maxConcurrent);
    }

    /**
     * @param onNext
     * @see Observable#forEach(Action1)
     */
    public final void forEach(Action1<? super T> onNext) {
        boxed.forEach(onNext);
    }

    /**
     * @param onNext
     * @param onError
     * @see Observable#forEach(Action1, Action1)
     */
    public final void forEach(Action1<? super T> onNext, Action1<Throwable> onError) {
        boxed.forEach(onNext, onError);
    }

    /**
     * @param onNext
     * @param onError
     * @param onComplete
     * @see Observable#forEach(Action1, Action1, Action0)
     */
    public final void forEach(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onComplete) {
        boxed.forEach(onNext, onError, onComplete);
    }

    /**
     * @param keySelector
     * @param elementSelector
     * @return
     * @see Observable#groupBy(Func1, Func1)
     */
    public final <K, R> Observable<GroupedObservable<K, R>> groupBy(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends R> elementSelector) {
        return boxed.groupBy(keySelector, elementSelector);
    }

    /**
     * @param keySelector
     * @return
     * @see Observable#groupBy(Func1)
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
     * @see Observable#groupJoin(Observable, Func1, Func1, Func2)
     */
    public final <T2, D1, D2, R> Observable<R> groupJoin(Observable<T2> right,
            Func1<? super T, ? extends Observable<D1>> leftDuration,
            Func1<? super T2, ? extends Observable<D2>> rightDuration,
            Func2<? super T, ? super Observable<T2>, ? extends R> resultSelector) {
        return boxed.groupJoin(right, leftDuration, rightDuration, resultSelector);
    }

    /**
     * @return
     * @see Observable#ignoreElements()
     */
    public final Observable<T> ignoreElements() {
        return boxed.ignoreElements();
    }

    /**
     * @return
     * @see Observable#isEmpty()
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
     * @see Observable#join(Observable, Func1, Func1, Func2)
     */
    public final <TRight, TLeftDuration, TRightDuration, R> Observable<R> join(Observable<TRight> right,
            Func1<T, Observable<TLeftDuration>> leftDurationSelector,
            Func1<TRight, Observable<TRightDuration>> rightDurationSelector, Func2<T, TRight, R> resultSelector) {
        return boxed.join(right, leftDurationSelector, rightDurationSelector, resultSelector);
    }

    /**
     * @return
     * @see Observable#last()
     */
    public final Observable<T> last() {
        return boxed.last();
    }

    /**
     * @param predicate
     * @return
     * @see Observable#last(Func1)
     */
    public final Observable<T> last(Func1<? super T, Boolean> predicate) {
        return boxed.last(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see Observable#lastOrDefault(Object)
     */
    public final Observable<T> lastOrDefault(T defaultValue) {
        return boxed.lastOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see Observable#lastOrDefault(Object, Func1)
     */
    public final Observable<T> lastOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.lastOrDefault(defaultValue, predicate);
    }

    /**
     * @param count
     * @return
     * @see Observable#limit(int)
     */
    public final Observable<T> limit(int count) {
        return boxed.limit(count);
    }

    /**
     * @param func
     * @return
     * @see Observable#map(Func1)
     */
    public final <R> Observable<R> map(Func1<? super T, ? extends R> func) {
        return boxed.map(func);
    }

    /**
     * @return
     * @see Observable#materialize()
     */
    public final Observable<Notification<T>> materialize() {
        return boxed.materialize();
    }

    /**
     * @param t1
     * @return
     * @see Observable#mergeWith(Observable)
     */
    public final Observable<T> mergeWith(Observable<? extends T> t1) {
        return boxed.mergeWith(t1);
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#observeOn(Scheduler)
     */
    public final Observable<T> observeOn(Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    /**
     * @param scheduler
     * @param bufferSize
     * @return
     * @see Observable#observeOn(Scheduler, int)
     */
    public final Observable<T> observeOn(Scheduler scheduler, int bufferSize) {
        return boxed.observeOn(scheduler, bufferSize);
    }

    /**
     * @param scheduler
     * @param delayError
     * @return
     * @see Observable#observeOn(Scheduler, boolean)
     */
    public final Observable<T> observeOn(Scheduler scheduler, boolean delayError) {
        return boxed.observeOn(scheduler, delayError);
    }

    /**
     * @param scheduler
     * @param delayError
     * @param bufferSize
     * @return
     * @see Observable#observeOn(Scheduler, boolean, int)
     */
    public final Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
        return boxed.observeOn(scheduler, delayError, bufferSize);
    }

    /**
     * @param klass
     * @return
     * @see Observable#ofType(Class)
     */
    public final <R> Observable<R> ofType(Class<R> klass) {
        return boxed.ofType(klass);
    }

    /**
     * @return
     * @see Observable#onBackpressureBuffer()
     */
    public final Observable<T> onBackpressureBuffer() {
        return boxed.onBackpressureBuffer();
    }

    /**
     * @param capacity
     * @return
     * @see Observable#onBackpressureBuffer(long)
     */
    public final Observable<T> onBackpressureBuffer(long capacity) {
        return boxed.onBackpressureBuffer(capacity);
    }

    /**
     * @param capacity
     * @param onOverflow
     * @return
     * @see Observable#onBackpressureBuffer(long, Action0)
     */
    public final Observable<T> onBackpressureBuffer(long capacity, Action0 onOverflow) {
        return boxed.onBackpressureBuffer(capacity, onOverflow);
    }

    /**
     * @param capacity
     * @param onOverflow
     * @param overflowStrategy
     * @return
     * @see Observable#onBackpressureBuffer(long, Action0, Strategy)
     */
    public final Observable<T> onBackpressureBuffer(long capacity, Action0 onOverflow, Strategy overflowStrategy) {
        return boxed.onBackpressureBuffer(capacity, onOverflow, overflowStrategy);
    }

    /**
     * @param onDrop
     * @return
     * @see Observable#onBackpressureDrop(Action1)
     */
    public final Observable<T> onBackpressureDrop(Action1<? super T> onDrop) {
        return boxed.onBackpressureDrop(onDrop);
    }

    /**
     * @return
     * @see Observable#onBackpressureDrop()
     */
    public final Observable<T> onBackpressureDrop() {
        return boxed.onBackpressureDrop();
    }

    /**
     * @return
     * @see Observable#onBackpressureLatest()
     */
    public final Observable<T> onBackpressureLatest() {
        return boxed.onBackpressureLatest();
    }

    /**
     * @param resumeFunction
     * @return
     * @see Observable#onErrorResumeNext(Func1)
     */
    public final Observable<T> onErrorResumeNext(Func1<Throwable, ? extends Observable<? extends T>> resumeFunction) {
        return boxed.onErrorResumeNext(resumeFunction);
    }

    /**
     * @param resumeSequence
     * @return
     * @see Observable#onErrorResumeNext(Observable)
     */
    public final Observable<T> onErrorResumeNext(Observable<? extends T> resumeSequence) {
        return boxed.onErrorResumeNext(resumeSequence);
    }

    /**
     * @param resumeFunction
     * @return
     * @see Observable#onErrorReturn(Func1)
     */
    public final Observable<T> onErrorReturn(Func1<Throwable, ? extends T> resumeFunction) {
        return boxed.onErrorReturn(resumeFunction);
    }

    /**
     * @param resumeSequence
     * @return
     * @see Observable#onExceptionResumeNext(Observable)
     */
    public final Observable<T> onExceptionResumeNext(Observable<? extends T> resumeSequence) {
        return boxed.onExceptionResumeNext(resumeSequence);
    }

    /**
     * @return
     * @see Observable#publish()
     */
    public final ConnectableObservable<T> publish() {
        return boxed.publish();
    }

    /**
     * @param selector
     * @return
     * @see Observable#publish(Func1)
     */
    public final <R> Observable<R> publish(Func1<? super Observable<T>, ? extends Observable<R>> selector) {
        return boxed.publish(selector);
    }

    /**
     * @param accumulator
     * @return
     * @see Observable#reduce(Func2)
     */
    public final Observable<T> reduce(Func2<T, T, T> accumulator) {
        return boxed.reduce(accumulator);
    }

    /**
     * @param initialValue
     * @param accumulator
     * @return
     * @see Observable#reduce(Object, Func2)
     */
    public final <R> Observable<R> reduce(R initialValue, Func2<R, ? super T, R> accumulator) {
        return boxed.reduce(initialValue, accumulator);
    }

    /**
     * @return
     * @see Observable#repeat()
     */
    public final Observable<T> repeat() {
        return boxed.repeat();
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#repeat(Scheduler)
     */
    public final Observable<T> repeat(Scheduler scheduler) {
        return boxed.repeat(scheduler);
    }

    /**
     * @param count
     * @return
     * @see Observable#repeat(long)
     */
    public final Observable<T> repeat(long count) {
        return boxed.repeat(count);
    }

    /**
     * @param count
     * @param scheduler
     * @return
     * @see Observable#repeat(long, Scheduler)
     */
    public final Observable<T> repeat(long count, Scheduler scheduler) {
        return boxed.repeat(count, scheduler);
    }

    /**
     * @param notificationHandler
     * @param scheduler
     * @return
     * @see Observable#repeatWhen(Func1, Scheduler)
     */
    public final Observable<T> repeatWhen(
            Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler,
            Scheduler scheduler) {
        return boxed.repeatWhen(notificationHandler, scheduler);
    }

    /**
     * @param notificationHandler
     * @return
     * @see Observable#repeatWhen(Func1)
     */
    public final Observable<T> repeatWhen(
            Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler) {
        return boxed.repeatWhen(notificationHandler);
    }

    /**
     * @return
     * @see Observable#replay()
     */
    public final ConnectableObservable<T> replay() {
        return boxed.replay();
    }

    /**
     * @param selector
     * @return
     * @see Observable#replay(Func1)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector) {
        return boxed.replay(selector);
    }

    /**
     * @param selector
     * @param bufferSize
     * @return
     * @see Observable#replay(Func1, int)
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
     * @see Observable#replay(Func1, int, long, TimeUnit)
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
     * @see Observable#replay(Func1, int, long, TimeUnit, Scheduler)
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
     * @see Observable#replay(Func1, int, Scheduler)
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
     * @see Observable#replay(Func1, long, TimeUnit)
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
     * @see Observable#replay(Func1, long, TimeUnit, Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector, long time,
            TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(selector, time, unit, scheduler);
    }

    /**
     * @param selector
     * @param scheduler
     * @return
     * @see Observable#replay(Func1, Scheduler)
     */
    public final <R> Observable<R> replay(Func1<? super Observable<T>, ? extends Observable<R>> selector,
            Scheduler scheduler) {
        return boxed.replay(selector, scheduler);
    }

    /**
     * @param bufferSize
     * @return
     * @see Observable#replay(int)
     */
    public final ConnectableObservable<T> replay(int bufferSize) {
        return boxed.replay(bufferSize);
    }

    /**
     * @param bufferSize
     * @param time
     * @param unit
     * @return
     * @see Observable#replay(int, long, TimeUnit)
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
     * @see Observable#replay(int, long, TimeUnit, Scheduler)
     */
    public final ConnectableObservable<T> replay(int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(bufferSize, time, unit, scheduler);
    }

    /**
     * @param bufferSize
     * @param scheduler
     * @return
     * @see Observable#replay(int, Scheduler)
     */
    public final ConnectableObservable<T> replay(int bufferSize, Scheduler scheduler) {
        return boxed.replay(bufferSize, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#replay(long, TimeUnit)
     */
    public final ConnectableObservable<T> replay(long time, TimeUnit unit) {
        return boxed.replay(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#replay(long, TimeUnit, Scheduler)
     */
    public final ConnectableObservable<T> replay(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.replay(time, unit, scheduler);
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#replay(Scheduler)
     */
    public final ConnectableObservable<T> replay(Scheduler scheduler) {
        return boxed.replay(scheduler);
    }

    /**
     * @return
     * @see Observable#retry()
     */
    public final Observable<T> retry() {
        return boxed.retry();
    }

    /**
     * @param count
     * @return
     * @see Observable#retry(long)
     */
    public final Observable<T> retry(long count) {
        return boxed.retry(count);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#retry(Func2)
     */
    public final Observable<T> retry(Func2<Integer, Throwable, Boolean> predicate) {
        return boxed.retry(predicate);
    }

    /**
     * @param notificationHandler
     * @return
     * @see Observable#retryWhen(Func1)
     */
    public final Observable<T> retryWhen(
            Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
        return boxed.retryWhen(notificationHandler);
    }

    /**
     * @param notificationHandler
     * @param scheduler
     * @return
     * @see Observable#retryWhen(Func1, Scheduler)
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
     * @see Observable#sample(long, TimeUnit)
     */
    public final Observable<T> sample(long period, TimeUnit unit) {
        return boxed.sample(period, unit);
    }

    /**
     * @param period
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#sample(long, TimeUnit, Scheduler)
     */
    public final Observable<T> sample(long period, TimeUnit unit, Scheduler scheduler) {
        return boxed.sample(period, unit, scheduler);
    }

    /**
     * @param sampler
     * @return
     * @see Observable#sample(Observable)
     */
    public final <U> Observable<T> sample(Observable<U> sampler) {
        return boxed.sample(sampler);
    }

    /**
     * @param accumulator
     * @return
     * @see Observable#scan(Func2)
     */
    public final Observable<T> scan(Func2<T, T, T> accumulator) {
        return boxed.scan(accumulator);
    }

    /**
     * @param initialValue
     * @param accumulator
     * @return
     * @see Observable#scan(Object, Func2)
     */
    public final <R> Observable<R> scan(R initialValue, Func2<R, ? super T, R> accumulator) {
        return boxed.scan(initialValue, accumulator);
    }

    /**
     * @return
     * @see Observable#serialize()
     */
    public final Observable<T> serialize() {
        return boxed.serialize();
    }

    /**
     * @return
     * @see Observable#share()
     */
    public final Observable<T> share() {
        return boxed.share();
    }

    /**
     * @return
     * @see Observable#single()
     */
    public final Observable<T> single() {
        return boxed.single();
    }

    /**
     * @param predicate
     * @return
     * @see Observable#single(Func1)
     */
    public final Observable<T> single(Func1<? super T, Boolean> predicate) {
        return boxed.single(predicate);
    }

    /**
     * @param defaultValue
     * @return
     * @see Observable#singleOrDefault(Object)
     */
    public final Observable<T> singleOrDefault(T defaultValue) {
        return boxed.singleOrDefault(defaultValue);
    }

    /**
     * @param defaultValue
     * @param predicate
     * @return
     * @see Observable#singleOrDefault(Object, Func1)
     */
    public final Observable<T> singleOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return boxed.singleOrDefault(defaultValue, predicate);
    }

    /**
     * @param count
     * @return
     * @see Observable#skip(int)
     */
    public final Observable<T> skip(int count) {
        return boxed.skip(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#skip(long, TimeUnit)
     */
    public final Observable<T> skip(long time, TimeUnit unit) {
        return boxed.skip(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#skip(long, TimeUnit, Scheduler)
     */
    public final Observable<T> skip(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skip(time, unit, scheduler);
    }

    /**
     * @param count
     * @return
     * @see Observable#skipLast(int)
     */
    public final Observable<T> skipLast(int count) {
        return boxed.skipLast(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#skipLast(long, TimeUnit)
     */
    public final Observable<T> skipLast(long time, TimeUnit unit) {
        return boxed.skipLast(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#skipLast(long, TimeUnit, Scheduler)
     */
    public final Observable<T> skipLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.skipLast(time, unit, scheduler);
    }

    /**
     * @param other
     * @return
     * @see Observable#skipUntil(Observable)
     */
    public final <U> Observable<T> skipUntil(Observable<U> other) {
        return boxed.skipUntil(other);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#skipWhile(Func1)
     */
    public final Observable<T> skipWhile(Func1<? super T, Boolean> predicate) {
        return boxed.skipWhile(predicate);
    }

    /**
     * @param values
     * @return
     * @see Observable#startWith(Observable)
     */
    public final Observable<T> startWith(Observable<T> values) {
        return boxed.startWith(values);
    }

    /**
     * @param values
     * @return
     * @see Observable#startWith(Iterable)
     */
    public final Observable<T> startWith(Iterable<T> values) {
        return boxed.startWith(values);
    }

    /**
     * @param t1
     * @return
     * @see Observable#startWith(Object)
     */
    public final Observable<T> startWith(T t1) {
        return boxed.startWith(t1);
    }

    /**
     * @param t1
     * @param t2
     * @return
     * @see Observable#startWith(Object, Object)
     */
    public final Observable<T> startWith(T t1, T t2) {
        return boxed.startWith(t1, t2);
    }

    /**
     * @param t1
     * @param t2
     * @param t3
     * @return
     * @see Observable#startWith(Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object, Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object, Object, Object, Object, Object)
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
     * @see Observable#startWith(Object, Object, Object, Object, Object, Object, Object, Object, Object)
     */
    public final Observable<T> startWith(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8, T t9) {
        return boxed.startWith(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    /**
     * @return
     * @see Observable#subscribe()
     */
    public final Subscription subscribe() {
        return boxed.subscribe();
    }

    /**
     * @param onNext
     * @return
     * @see Observable#subscribe(Action1)
     */
    public final Subscription subscribe(Action1<? super T> onNext) {
        return boxed.subscribe(onNext);
    }

    /**
     * @param onNext
     * @param onError
     * @return
     * @see Observable#subscribe(Action1, Action1)
     */
    public final Subscription subscribe(Action1<? super T> onNext, Action1<Throwable> onError) {
        return boxed.subscribe(onNext, onError);
    }

    /**
     * @param onNext
     * @param onError
     * @param onComplete
     * @return
     * @see Observable#subscribe(Action1, Action1, Action0)
     */
    public final Subscription subscribe(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onComplete) {
        return boxed.subscribe(onNext, onError, onComplete);
    }

    /**
     * @param observer
     * @return
     * @see Observable#subscribe(Observer)
     */
    public final Subscription subscribe(Observer<? super T> observer) {
        return boxed.subscribe(observer);
    }

    /**
     * @param subscriber
     * @return
     * @see Observable#unsafeSubscribe(rx.Subscriber)
     */
    public final Subscription unsafeSubscribe(rx.Subscriber<? super T> subscriber) {
        return boxed.unsafeSubscribe(subscriber);
    }

    /**
     * @param subscriber
     * @return
     * @see Observable#subscribe(rx.Subscriber)
     */
    public final Subscription subscribe(rx.Subscriber<? super T> subscriber) {
        return boxed.subscribe(subscriber);
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#subscribeOn(Scheduler)
     */
    public final Observable<T> subscribeOn(Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    /**
     * @param func
     * @return
     * @see Observable#switchMap(Func1)
     */
    public final <R> Observable<R> switchMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.switchMap(func);
    }

    /**
     * @param func
     * @return
     * @see Observable#switchMapDelayError(Func1)
     */
    public final <R> Observable<R> switchMapDelayError(Func1<? super T, ? extends Observable<? extends R>> func) {
        return boxed.switchMapDelayError(func);
    }

    /**
     * @param count
     * @return
     * @see Observable#take(int)
     */
    public final Observable<T> take(int count) {
        return boxed.take(count);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#take(long, TimeUnit)
     */
    public final Observable<T> take(long time, TimeUnit unit) {
        return boxed.take(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#take(long, TimeUnit, Scheduler)
     */
    public final Observable<T> take(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.take(time, unit, scheduler);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#takeFirst(Func1)
     */
    public final Observable<T> takeFirst(Func1<? super T, Boolean> predicate) {
        return boxed.takeFirst(predicate);
    }

    /**
     * @param count
     * @return
     * @see Observable#takeLast(int)
     */
    public final Observable<T> takeLast(int count) {
        return boxed.takeLast(count);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @return
     * @see Observable#takeLast(int, long, TimeUnit)
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
     * @see Observable#takeLast(int, long, TimeUnit, Scheduler)
     */
    public final Observable<T> takeLast(int count, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(count, time, unit, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#takeLast(long, TimeUnit)
     */
    public final Observable<T> takeLast(long time, TimeUnit unit) {
        return boxed.takeLast(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#takeLast(long, TimeUnit, Scheduler)
     */
    public final Observable<T> takeLast(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLast(time, unit, scheduler);
    }

    /**
     * @param count
     * @return
     * @see Observable#takeLastBuffer(int)
     */
    public final Observable<List<T>> takeLastBuffer(int count) {
        return boxed.takeLastBuffer(count);
    }

    /**
     * @param count
     * @param time
     * @param unit
     * @return
     * @see Observable#takeLastBuffer(int, long, TimeUnit)
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
     * @see Observable#takeLastBuffer(int, long, TimeUnit, Scheduler)
     */
    public final Observable<List<T>> takeLastBuffer(int count, long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLastBuffer(count, time, unit, scheduler);
    }

    /**
     * @param time
     * @param unit
     * @return
     * @see Observable#takeLastBuffer(long, TimeUnit)
     */
    public final Observable<List<T>> takeLastBuffer(long time, TimeUnit unit) {
        return boxed.takeLastBuffer(time, unit);
    }

    /**
     * @param time
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#takeLastBuffer(long, TimeUnit, Scheduler)
     */
    public final Observable<List<T>> takeLastBuffer(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.takeLastBuffer(time, unit, scheduler);
    }

    /**
     * @param other
     * @return
     * @see Observable#takeUntil(Observable)
     */
    public final <E> Observable<T> takeUntil(Observable<? extends E> other) {
        return boxed.takeUntil(other);
    }

    /**
     * @param predicate
     * @return
     * @see Observable#takeWhile(Func1)
     */
    public final Observable<T> takeWhile(Func1<? super T, Boolean> predicate) {
        return boxed.takeWhile(predicate);
    }

    /**
     * @param stopPredicate
     * @return
     * @see Observable#takeUntil(Func1)
     */
    public final Observable<T> takeUntil(Func1<? super T, Boolean> stopPredicate) {
        return boxed.takeUntil(stopPredicate);
    }

    /**
     * @param windowDuration
     * @param unit
     * @return
     * @see Observable#throttleFirst(long, TimeUnit)
     */
    public final Observable<T> throttleFirst(long windowDuration, TimeUnit unit) {
        return boxed.throttleFirst(windowDuration, unit);
    }

    /**
     * @param skipDuration
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#throttleFirst(long, TimeUnit, Scheduler)
     */
    public final Observable<T> throttleFirst(long skipDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleFirst(skipDuration, unit, scheduler);
    }

    /**
     * @param intervalDuration
     * @param unit
     * @return
     * @see Observable#throttleLast(long, TimeUnit)
     */
    public final Observable<T> throttleLast(long intervalDuration, TimeUnit unit) {
        return boxed.throttleLast(intervalDuration, unit);
    }

    /**
     * @param intervalDuration
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#throttleLast(long, TimeUnit, Scheduler)
     */
    public final Observable<T> throttleLast(long intervalDuration, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleLast(intervalDuration, unit, scheduler);
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @see Observable#throttleWithTimeout(long, TimeUnit)
     */
    public final Observable<T> throttleWithTimeout(long timeout, TimeUnit unit) {
        return boxed.throttleWithTimeout(timeout, unit);
    }

    /**
     * @param timeout
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#throttleWithTimeout(long, TimeUnit, Scheduler)
     */
    public final Observable<T> throttleWithTimeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.throttleWithTimeout(timeout, unit, scheduler);
    }

    /**
     * @return
     * @see Observable#timeInterval()
     */
    public final Observable<TimeInterval<T>> timeInterval() {
        return boxed.timeInterval();
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#timeInterval(Scheduler)
     */
    public final Observable<TimeInterval<T>> timeInterval(Scheduler scheduler) {
        return boxed.timeInterval(scheduler);
    }

    /**
     * @param firstTimeoutSelector
     * @param timeoutSelector
     * @return
     * @see Observable#timeout(Func0, Func1)
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
     * @see Observable#timeout(Func0, Func1, Observable)
     */
    public final <U, V> Observable<T> timeout(Func0<? extends Observable<U>> firstTimeoutSelector,
            Func1<? super T, ? extends Observable<V>> timeoutSelector, Observable<? extends T> other) {
        return boxed.timeout(firstTimeoutSelector, timeoutSelector, other);
    }

    /**
     * @param timeoutSelector
     * @return
     * @see Observable#timeout(Func1)
     */
    public final <V> Observable<T> timeout(Func1<? super T, ? extends Observable<V>> timeoutSelector) {
        return boxed.timeout(timeoutSelector);
    }

    /**
     * @param timeoutSelector
     * @param other
     * @return
     * @see Observable#timeout(Func1, Observable)
     */
    public final <V> Observable<T> timeout(Func1<? super T, ? extends Observable<V>> timeoutSelector,
            Observable<? extends T> other) {
        return boxed.timeout(timeoutSelector, other);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @return
     * @see Observable#timeout(long, TimeUnit)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit) {
        return boxed.timeout(timeout, timeUnit);
    }

    /**
     * @param timeout
     * @param timeUnit
     * @param other
     * @return
     * @see Observable#timeout(long, TimeUnit, Observable)
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
     * @see Observable#timeout(long, TimeUnit, Observable, Scheduler)
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
     * @see Observable#timeout(long, TimeUnit, Scheduler)
     */
    public final Observable<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, scheduler);
    }

    /**
     * @return
     * @see Observable#timestamp()
     */
    public final Observable<Timestamped<T>> timestamp() {
        return boxed.timestamp();
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#timestamp(Scheduler)
     */
    public final Observable<Timestamped<T>> timestamp(Scheduler scheduler) {
        return boxed.timestamp(scheduler);
    }

    /**
     * @return
     * @see Observable#toBlocking()
     */
    public final BlockingObservable<T> toBlocking() {
        return boxed.toBlocking();
    }

    /**
     * @return
     * @see Observable#toList()
     */
    public final Observable<List<T>> toList() {
        return boxed.toList();
    }

    /**
     * @param keySelector
     * @return
     * @see Observable#toMap(Func1)
     */
    public final <K> Observable<Map<K, T>> toMap(Func1<? super T, ? extends K> keySelector) {
        return boxed.toMap(keySelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @return
     * @see Observable#toMap(Func1, Func1)
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
     * @see Observable#toMap(Func1, Func1, Func0)
     */
    public final <K, V> Observable<Map<K, V>> toMap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, V>> mapFactory) {
        return boxed.toMap(keySelector, valueSelector, mapFactory);
    }

    /**
     * @param keySelector
     * @return
     * @see Observable#toMultimap(Func1)
     */
    public final <K> Observable<Map<K, Collection<T>>> toMultimap(Func1<? super T, ? extends K> keySelector) {
        return boxed.toMultimap(keySelector);
    }

    /**
     * @param keySelector
     * @param valueSelector
     * @return
     * @see Observable#toMultimap(Func1, Func1)
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
     * @see Observable#toMultimap(Func1, Func1, Func0)
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
     * @see Observable#toMultimap(Func1, Func1, Func0, Func1)
     */
    public final <K, V> Observable<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, Collection<V>>> mapFactory,
            Func1<? super K, ? extends Collection<V>> collectionFactory) {
        return boxed.toMultimap(keySelector, valueSelector, mapFactory, collectionFactory);
    }

    /**
     * @return
     * @see Observable#toSortedList()
     */
    public final Observable<List<T>> toSortedList() {
        return boxed.toSortedList();
    }

    /**
     * @param sortFunction
     * @return
     * @see Observable#toSortedList(Func2)
     */
    public final Observable<List<T>> toSortedList(Func2<? super T, ? super T, Integer> sortFunction) {
        return boxed.toSortedList(sortFunction);
    }

    /**
     * @param initialCapacity
     * @return
     * @see Observable#toSortedList(int)
     */
    public final Observable<List<T>> toSortedList(int initialCapacity) {
        return boxed.toSortedList(initialCapacity);
    }

    /**
     * @param sortFunction
     * @param initialCapacity
     * @return
     * @see Observable#toSortedList(Func2, int)
     */
    public final Observable<List<T>> toSortedList(Func2<? super T, ? super T, Integer> sortFunction,
            int initialCapacity) {
        return boxed.toSortedList(sortFunction, initialCapacity);
    }

    /**
     * @param scheduler
     * @return
     * @see Observable#unsubscribeOn(Scheduler)
     */
    public final Observable<T> unsubscribeOn(Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    /**
     * @param other
     * @param resultSelector
     * @return
     * @see Observable#withLatestFrom(Observable, Func2)
     */
    public final <U, R> Observable<R> withLatestFrom(Observable<? extends U> other,
            Func2<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.withLatestFrom(other, resultSelector);
    }

    /**
     * @param closingSelector
     * @return
     * @see Observable#window(Func0)
     */
    public final <TClosing> Observable<Observable<T>> window(
            Func0<? extends Observable<? extends TClosing>> closingSelector) {
        return boxed.window(closingSelector);
    }

    /**
     * @param count
     * @return
     * @see Observable#window(int)
     */
    public final Observable<Observable<T>> window(int count) {
        return boxed.window(count);
    }

    /**
     * @param count
     * @param skip
     * @return
     * @see Observable#window(int, int)
     */
    public final Observable<Observable<T>> window(int count, int skip) {
        return boxed.window(count, skip);
    }

    /**
     * @param timespan
     * @param timeshift
     * @param unit
     * @return
     * @see Observable#window(long, long, TimeUnit)
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
     * @see Observable#window(long, long, TimeUnit, Scheduler)
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
     * @see Observable#window(long, long, TimeUnit, int, Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, long timeshift, TimeUnit unit, int count,
            Scheduler scheduler) {
        return boxed.window(timespan, timeshift, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @return
     * @see Observable#window(long, TimeUnit)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit) {
        return boxed.window(timespan, unit);
    }

    /**
     * @param timespan
     * @param unit
     * @param count
     * @return
     * @see Observable#window(long, TimeUnit, int)
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
     * @see Observable#window(long, TimeUnit, int, Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit, int count, Scheduler scheduler) {
        return boxed.window(timespan, unit, count, scheduler);
    }

    /**
     * @param timespan
     * @param unit
     * @param scheduler
     * @return
     * @see Observable#window(long, TimeUnit, Scheduler)
     */
    public final Observable<Observable<T>> window(long timespan, TimeUnit unit, Scheduler scheduler) {
        return boxed.window(timespan, unit, scheduler);
    }

    /**
     * @param windowOpenings
     * @param closingSelector
     * @return
     * @see Observable#window(Observable, Func1)
     */
    public final <TOpening, TClosing> Observable<Observable<T>> window(Observable<? extends TOpening> windowOpenings,
            Func1<? super TOpening, ? extends Observable<? extends TClosing>> closingSelector) {
        return boxed.window(windowOpenings, closingSelector);
    }

    /**
     * @param boundary
     * @return
     * @see Observable#window(Observable)
     */
    public final <U> Observable<Observable<T>> window(Observable<U> boundary) {
        return boxed.window(boundary);
    }

    /**
     * @param other
     * @param zipFunction
     * @return
     * @see Observable#zipWith(Iterable, Func2)
     */
    public final <T2, R> Observable<R> zipWith(Iterable<? extends T2> other,
            Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return boxed.zipWith(other, zipFunction);
    }

    /**
     * @param other
     * @param zipFunction
     * @return
     * @see Observable#zipWith(Observable, Func2)
     */
    public final <T2, R> Observable<R> zipWith(Observable<? extends T2> other,
            Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return boxed.zipWith(other, zipFunction);
    }

}
