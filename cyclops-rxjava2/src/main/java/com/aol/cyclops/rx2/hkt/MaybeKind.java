package com.aol.cyclops.rx2.hkt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.async.Future;
import cyclops.companion.rx2.Flowables;
import cyclops.companion.rx2.Maybes;
import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.maybe;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.StreamT;
import cyclops.monads.transformers.rx2.MaybeT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.reactivex.*;
import io.reactivex.annotations.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observers.TestObserver;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Simulates Higher Kinded Types for RxJava 2 Maybe's
 * 
 * MaybeKind is a Maybe and a Higher Kinded Type (maybe,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Maybe
 */


public final class MaybeKind<T> implements Higher<maybe, T>, Publisher<T> {
    private MaybeKind(Maybe<T> boxed) {
        this.boxed = boxed;
    }

    public <R> MaybeKind<R> fold(java.util.function.Function<? super Maybe<?  super T>,? extends Maybe<R>> op){
        return widen(op.apply(boxed));
    }
    public Active<maybe,T> allTypeclasses(){
        return Active.of(this, Maybes.Instances.definitions());
    }

    public static <T> Higher<maybe,T> widenK(final Maybe<T> completableList) {

        return new MaybeKind<>(
                completableList);
    }
    public <W2,R> Nested<maybe,W2,R> mapM(java.util.function.Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Maybes.mapM(boxed,fn,defs);
    }

    public <W extends WitnessType<W>> MaybeT<W, T> liftM(W witness) {
        return MaybeT.of(witness.adapter().unit(boxed));
    }
    
    /**
     * Construct a HKT encoded completed Maybe
     * 
     * @param value To encode inside a HKT encoded Maybe
     * @return Completed HKT encoded FMaybe
     */
    public static <T> MaybeKind<T> just(T value){
        
        return widen(Maybe.just(value));
    }
    public static <T> MaybeKind<T> empty(){
        return widen(Maybe.empty());
    }

    /**
     * Convert a Maybe to a simulated HigherKindedType that captures Maybe nature
     * and Maybe element data type separately. Recover via @see MaybeKind#narrow
     * 
     * If the supplied Maybe implements MaybeKind it is returned already, otherwise it
     * is wrapped into a Maybe implementation that does implement MaybeKind
     * 
     * @param completableMaybe Maybe to widen to a MaybeKind
     * @return MaybeKind encoding HKT info about Maybes
     */
    public static <T> MaybeKind<T> widen(final Maybe<T> completableMaybe) {
        
        return new MaybeKind<T>(
                         completableMaybe);
    }
    
    public static <T> MaybeKind<T> widen(final Publisher<T> completableMaybe) {
        
        return new MaybeKind<T>(Maybes.fromPublisher(
                         completableMaybe));
    }
        
    
    /**
     * Convert the raw Higher Kinded Type for MaybeKind types into the MaybeKind type definition class
     * 
     * @param future HKT encoded list into a MaybeKind
     * @return MaybeKind
     */
    public static <T> MaybeKind<T> narrowK(final Higher<maybe, T> future) {
       return (MaybeKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Maybe into
     * 
     * @param completableMaybe Type Constructor to convert back into narrowed type
     * @return Maybe from Higher Kinded Type
     */
    public static <T> Maybe<T> narrow(final Higher<maybe, T> completableMaybe) {
      
            return ((MaybeKind<T>)completableMaybe).narrow();
           
       

    }


    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> ambWith(MaybeSource<? extends T> other) {
        return boxed.ambWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingGet() {
        return boxed.blockingGet();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingGet(T defaultValue) {
        return boxed.blockingGet(defaultValue);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> cache() {
        return boxed.cache();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<U> cast(Class<? extends U> clazz) {
        return boxed.cast(clazz);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> compose(MaybeTransformer<? super T, ? extends R> transformer) {
        return boxed.compose(transformer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> concatMap(Function<? super T, ? extends MaybeSource<? extends R>> mapper) {
        return boxed.concatMap(mapper);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> concatWith(MaybeSource<? extends T> other) {
        return boxed.concatWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> contains(Object item) {
        return boxed.contains(item);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Long> count() {
        return boxed.count();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> defaultIfEmpty(T defaultItem) {
        return boxed.defaultIfEmpty(defaultItem);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> delay(long delay, TimeUnit unit) {
        return boxed.delay(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delay(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    public <U, V> Maybe<T> delay(Publisher<U> delayIndicator) {
        return boxed.delay(delayIndicator);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> delaySubscription(Publisher<U> subscriptionIndicator) {
        return boxed.delaySubscription(subscriptionIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> delaySubscription(long delay, TimeUnit unit) {
        return boxed.delaySubscription(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> delaySubscription(long delay, TimeUnit unit, Scheduler scheduler) {
        return boxed.delaySubscription(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doAfterSuccess(Consumer<? super T> onAfterSuccess) {
        return boxed.doAfterSuccess(onAfterSuccess);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doAfterTerminate(Action onAfterTerminate) {
        return boxed.doAfterTerminate(onAfterTerminate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doFinally(Action onFinally) {
        return boxed.doFinally(onFinally);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnDispose(Action onDispose) {
        return boxed.doOnDispose(onDispose);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnComplete(Action onComplete) {
        return boxed.doOnComplete(onComplete);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnError(Consumer<? super Throwable> onError) {
        return boxed.doOnError(onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnEvent(BiConsumer<? super T, ? super Throwable> onEvent) {
        return boxed.doOnEvent(onEvent);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnSubscribe(Consumer<? super Disposable> onSubscribe) {
        return boxed.doOnSubscribe(onSubscribe);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnSuccess(Consumer<? super T> onSuccess) {
        return boxed.doOnSuccess(onSuccess);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> flatMap(Function<? super T, ? extends MaybeSource<? extends R>> mapper) {
        return boxed.flatMap(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> flatMap(Function<? super T, ? extends MaybeSource<? extends R>> onSuccessMapper, Function<? super Throwable, ? extends MaybeSource<? extends R>> onErrorMapper, Callable<? extends MaybeSource<? extends R>> onCompleteSupplier) {
        return boxed.flatMap(onSuccessMapper, onErrorMapper, onCompleteSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Maybe<R> flatMap(Function<? super T, ? extends MaybeSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> resultSelector) {
        return boxed.flatMap(mapper, resultSelector);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Flowable<U> flattenAsFlowable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flattenAsFlowable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Observable<U> flattenAsObservable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flattenAsObservable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Observable<R> flatMapObservable(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return boxed.flatMapObservable(mapper);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapPublisher(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.flatMapPublisher(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> flatMapSingle(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.flatMapSingle(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> flatMapSingleElement(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        return boxed.flatMapSingleElement(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper) {
        return boxed.flatMapCompletable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> hide() {
        return boxed.hide();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable ignoreElement() {
        return boxed.ignoreElement();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> isEmpty() {
        return boxed.isEmpty();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> lift(MaybeOperator<? extends R, ? super T> lift) {
        return boxed.lift(lift);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
        return boxed.map(mapper);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> mergeWith(MaybeSource<? extends T> other) {
        return boxed.mergeWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> observeOn(Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<U> ofType(Class<U> clazz) {
        return boxed.ofType(clazz);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> R to(Function<? super Maybe<T>, R> convert) {
        return boxed.to(convert);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> toFlowable() {
        return boxed.toFlowable();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> toObservable() {
        return boxed.toObservable();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> toSingle(T defaultValue) {
        return boxed.toSingle(defaultValue);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> toSingle() {
        return boxed.toSingle();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorComplete() {
        return boxed.onErrorComplete();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorComplete(Predicate<? super Throwable> predicate) {
        return boxed.onErrorComplete(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorResumeNext(MaybeSource<? extends T> next) {
        return boxed.onErrorResumeNext(next);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorResumeNext(Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunction) {
        return boxed.onErrorResumeNext(resumeFunction);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorReturn(Function<? super Throwable, ? extends T> valueSupplier) {
        return boxed.onErrorReturn(valueSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorReturnItem(T item) {
        return boxed.onErrorReturnItem(item);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onExceptionResumeNext(MaybeSource<? extends T> next) {
        return boxed.onExceptionResumeNext(next);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onTerminateDetach() {
        return boxed.onTerminateDetach();
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> repeat() {
        return boxed.repeat();
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> repeat(long times) {
        return boxed.repeat(times);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> repeatUntil(BooleanSupplier stop) {
        return boxed.repeatUntil(stop);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> repeatWhen(Function<? super Flowable<Object>, ? extends Publisher<?>> handler) {
        return boxed.repeatWhen(handler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry() {
        return boxed.retry();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(BiPredicate<? super Integer, ? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(long count) {
        return boxed.retry(count);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(long times, Predicate<? super Throwable> predicate) {
        return boxed.retry(times, predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(Predicate<? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retryUntil(BooleanSupplier stop) {
        return boxed.retryUntil(stop);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retryWhen(Function<? super Flowable<Throwable>, ? extends Publisher<?>> handler) {
        return boxed.retryWhen(handler);
    }

    @SchedulerSupport("none")
    public Disposable subscribe() {
        return boxed.subscribe();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onSuccess) {
        return boxed.subscribe(onSuccess);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onSuccess, Consumer<? super Throwable> onError) {
        return boxed.subscribe(onSuccess, onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Disposable subscribe(Consumer<? super T> onSuccess, Consumer<? super Throwable> onError, Action onComplete) {
        return boxed.subscribe(onSuccess, onError, onComplete);
    }

    @SchedulerSupport("none")
    public void subscribe(MaybeObserver<? super T> observer) {
        boxed.subscribe(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> subscribeOn(Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E extends MaybeObserver<? super T>> E subscribeWith(E observer) {
        return boxed.subscribeWith(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> switchIfEmpty(MaybeSource<? extends T> other) {
        return boxed.switchIfEmpty(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> takeUntil(MaybeSource<U> other) {
        return boxed.takeUntil(other);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> takeUntil(Publisher<U> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> timeout(long timeout, TimeUnit timeUnit) {
        return boxed.timeout(timeout, timeUnit);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> timeout(long timeout, TimeUnit timeUnit, MaybeSource<? extends T> fallback) {
        return boxed.timeout(timeout, timeUnit, fallback);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler, MaybeSource<? extends T> fallback) {
        return boxed.timeout(timeout, timeUnit, scheduler, fallback);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return boxed.timeout(timeout, timeUnit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> timeout(MaybeSource<U> timeoutIndicator) {
        return boxed.timeout(timeoutIndicator);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> timeout(MaybeSource<U> timeoutIndicator, MaybeSource<? extends T> fallback) {
        return boxed.timeout(timeoutIndicator, fallback);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> timeout(Publisher<U> timeoutIndicator) {
        return boxed.timeout(timeoutIndicator);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> timeout(Publisher<U> timeoutIndicator, MaybeSource<? extends T> fallback) {
        return boxed.timeout(timeoutIndicator, fallback);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> unsubscribeOn(Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Maybe<R> zipWith(MaybeSource<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return boxed.zipWith(other, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public TestObserver<T> test() {
        return boxed.test();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public TestObserver<T> test(boolean cancelled) {
        return boxed.test(cancelled);
    }

    private final Maybe<T> boxed;

        /**
         * @return wrapped Maybe
         */
        public Maybe<T> narrow() {
            return boxed;
        }

        
        public Future<T> toFuture(){
            return Future.fromPublisher(boxed.toFlowable());
        }
        /**
         * @param s
         * @see Publisher#subscribe(Subscriber)
         */
        public void subscribe(Subscriber<? super T> s) {
            boxed.toFlowable().subscribe(s);
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
