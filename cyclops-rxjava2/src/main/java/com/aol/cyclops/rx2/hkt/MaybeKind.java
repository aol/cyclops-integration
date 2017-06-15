package com.aol.cyclops.rx2.hkt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.async.Future;
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
 * MaybeKind is a Maybe and a Higher Kinded Type (MaybeKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Maybe
 */


public final class MaybeKind<T> implements Higher<MaybeKind.µ, T>, Publisher<T> {
    private MaybeKind(Maybe<T> boxed) {
        this.boxed = boxed;
    }

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
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
        return widen(Maybe.never());
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
        
        return new MaybeKind<T>(Maybe.fromPublisher(
                         completableMaybe));
    }
        
    
    /**
     * Convert the raw Higher Kinded Type for MaybeKind types into the MaybeKind type definition class
     * 
     * @param future HKT encoded list into a MaybeKind
     * @return MaybeKind
     */
    public static <T> MaybeKind<T> narrowK(final Higher<MaybeKind.µ, T> future) {
       return (MaybeKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Maybe into
     * 
     * @param completableMaybe Type Constructor to convert back into narrowed type
     * @return Maybe from Higher Kinded Type
     */
    public static <T> Maybe<T> narrow(final Higher<MaybeKind.µ, T> completableMaybe) {
      
            return ((MaybeKind<T>)completableMaybe).narrow();
           
       

    }


    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> amb(Iterable<? extends MaybeSource<? extends T1>> maybeSources) {
        return Maybe.amb(maybeSources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> ambArray(MaybeSource<? extends T1>[] sources) {
        return Maybe.ambArray(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    @BackpressureSupport(BackpressureKind.FULL)
    public static <T1> Flowable<T1> concat(Iterable<? extends MaybeSource<? extends T1>> maybeSources) {
        return Maybe.concat(maybeSources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Observable<T1> concat(ObservableSource<? extends MaybeSource<? extends T1>> sources) {
        return Maybe.concat(sources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(Publisher<? extends MaybeSource<? extends T1>> sources) {
        return Maybe.concat(sources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(Publisher<? extends MaybeSource<? extends T1>> sources, int prefetch) {
        return Maybe.concat(sources, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2) {
        return Maybe.concat(source1, source2);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2, MaybeSource<? extends T1> source3) {
        return Maybe.concat(source1, source2, source3);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2, MaybeSource<? extends T1> source3, MaybeSource<? extends T1> source4) {
        return Maybe.concat(source1, source2, source3, source4);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concatArray(MaybeSource<? extends T1>[] sources) {
        return Maybe.concatArray(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> create(MaybeOnSubscribe<T1> source) {
        return Maybe.create(source);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> defer(Callable<? extends MaybeSource<? extends T1>> maybeSupplier) {
        return Maybe.defer(maybeSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> error(Callable<? extends Throwable> errorSupplier) {
        return Maybe.error(errorSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> error(Throwable exception) {
        return Maybe.error(exception);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> fromCallable(Callable<? extends T1> callable) {
        return Maybe.fromCallable(callable);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> fromFuture(java.util.concurrent.Future<? extends T1> future) {
        return Maybe.fromFuture(future);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, long timeout, TimeUnit unit) {
        return Maybe.fromFuture(future, timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static <T1> Maybe<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, long timeout, TimeUnit unit, Scheduler scheduler) {
        return Maybe.fromFuture(future, timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static <T1> Maybe<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, Scheduler scheduler) {
        return Maybe.fromFuture(future, scheduler);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> fromPublisher(Publisher<? extends T1> publisher) {
        return Maybe.fromPublisher(publisher);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> fromObservable(ObservableSource<? extends T1> observableSource) {
        return Maybe.fromObservable(observableSource);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(Iterable<? extends MaybeSource<? extends T1>> maybeSources) {
        return Maybe.merge(maybeSources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(Publisher<? extends MaybeSource<? extends T1>> sources) {
        return Maybe.merge(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> merge(MaybeSource<? extends MaybeSource<? extends T1>> source) {
        return Maybe.merge(source);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2) {
        return Maybe.merge(source1, source2);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2, MaybeSource<? extends T1> source3) {
        return Maybe.merge(source1, source2, source3);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(MaybeSource<? extends T1> source1, MaybeSource<? extends T1> source2, MaybeSource<? extends T1> source3, MaybeSource<? extends T1> source4) {
        return Maybe.merge(source1, source2, source3, source4);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> never() {
        return Maybe.never();
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public static Maybe<Long> timer(long delay, TimeUnit unit) {
        return Maybe.timer(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static Maybe<Long> timer(long delay, TimeUnit unit, Scheduler scheduler) {
        return Maybe.timer(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<Boolean> equals(MaybeSource<? extends T1> first, MaybeSource<? extends T1> second) {
        return Maybe.equals(first, second);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> unsafeCreate(MaybeSource<T1> onSubscribe) {
        return Maybe.unsafeCreate(onSubscribe);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, U> Maybe<T1> using(Callable<U> resourceSupplier, Function<? super U, ? extends MaybeSource<? extends T1>> maybeFunction, Consumer<? super U> disposer) {
        return Maybe.using(resourceSupplier, maybeFunction, disposer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, U> Maybe<T1> using(Callable<U> resourceSupplier, Function<? super U, ? extends MaybeSource<? extends T1>> maybeFunction, Consumer<? super U> disposer, boolean eager) {
        return Maybe.using(resourceSupplier, maybeFunction, disposer, eager);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Maybe<T1> wrap(MaybeSource<T1> source) {
        return Maybe.wrap(source);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, R> Maybe<R> zip(Iterable<? extends MaybeSource<? extends T1>> maybeSources, Function<? super Object[], ? extends R> zipper) {
        return Maybe.zip(maybeSources, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, BiFunction<? super T1, ? super T2, ? extends R> zipper) {
        return Maybe.zip(source1, source2, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, Function3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, MaybeSource<? extends T5> source5, Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, source5, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, MaybeSource<? extends T5> source5, MaybeSource<? extends T6> source6, Function6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, source5, source6, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, MaybeSource<? extends T5> source5, MaybeSource<? extends T6> source6, MaybeSource<? extends T7> source7, Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, source5, source6, source7, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, MaybeSource<? extends T5> source5, MaybeSource<? extends T6> source6, MaybeSource<? extends T7> source7, MaybeSource<? extends T8> source8, Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, source5, source6, source7, source8, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Maybe<R> zip(MaybeSource<? extends T1> source1, MaybeSource<? extends T2> source2, MaybeSource<? extends T3> source3, MaybeSource<? extends T4> source4, MaybeSource<? extends T5> source5, MaybeSource<? extends T6> source6, MaybeSource<? extends T7> source7, MaybeSource<? extends T8> source8, MaybeSource<? extends T9> source9, Function9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> zipper) {
        return Maybe.zip(source1, source2, source3, source4, source5, source6, source7, source8, source9, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, R> Maybe<R> zipArray(Function<? super Object[], ? extends R> zipper, MaybeSource<? extends T1>[] sources) {
        return Maybe.zipArray(zipper, sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> ambWith(MaybeSource<? extends T> other) {
        return boxed.ambWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> hide() {
        return boxed.hide();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Maybe<R> compose(MaybeTransformer<? super T, ? extends R> transformer) {
        return boxed.compose(transformer);
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

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> concatWith(MaybeSource<? extends T> other) {
        return boxed.concatWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> delay(long time, TimeUnit unit) {
        return boxed.delay(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> delay(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.delay(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> delaySubscription(CompletableSource other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> delaySubscription(MaybeSource<U> other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> delaySubscription(ObservableSource<U> other) {
        return boxed.delaySubscription(other);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Maybe<T> delaySubscription(Publisher<U> other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public <U> Maybe<T> delaySubscription(long time, TimeUnit unit) {
        return boxed.delaySubscription(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <U> Maybe<T> delaySubscription(long time, TimeUnit unit, Scheduler scheduler) {
        return boxed.delaySubscription(time, unit, scheduler);
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
    public Maybe<T> doOnEvent(BiConsumer<? super T, ? super Throwable> onEvent) {
        return boxed.doOnEvent(onEvent);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnError(Consumer<? super Throwable> onError) {
        return boxed.doOnError(onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> doOnDispose(Action onDispose) {
        return boxed.doOnDispose(onDispose);
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
    public <R> Maybe<R> flatMapMaybe(Function<? super T, ? extends MaybeSource<? extends R>> mapper) {
        return boxed.flatMapMaybe(mapper);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Flowable<R> flatMapPublisher(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return boxed.flatMapPublisher(mapper);
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

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable flatMapCompletable(Function<? super T, ? extends CompletableSource> mapper) {
        return boxed.flatMapCompletable(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public T blockingGet() {
        return boxed.blockingGet();
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

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<Boolean> contains(Object value) {
        return boxed.contains(value);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<Boolean> contains(Object value, BiPredicate<Object, Object> comparer) {
        return boxed.contains(value, comparer);
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
    public Maybe<T> onErrorReturn(Function<Throwable, ? extends T> resumeFunction) {
        return boxed.onErrorReturn(resumeFunction);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorReturnItem(T value) {
        return boxed.onErrorReturnItem(value);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorResumeNext(Maybe<? extends T> resumeMaybeInCaseOfError) {
        return boxed.onErrorResumeNext(resumeMaybeInCaseOfError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> onErrorResumeNext(Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunctionInCaseOfError) {
        return boxed.onErrorResumeNext(resumeFunctionInCaseOfError);
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
    public Flowable<T> repeatWhen(Function<? super Flowable<Object>, ? extends Publisher<?>> handler) {
        return boxed.repeatWhen(handler);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> repeatUntil(BooleanSupplier stop) {
        return boxed.repeatUntil(stop);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry() {
        return boxed.retry();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(long times) {
        return boxed.retry(times);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(BiPredicate<? super Integer, ? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> retry(Predicate<? super Throwable> predicate) {
        return boxed.retry(predicate);
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
    public Disposable subscribe(BiConsumer<? super T, ? super Throwable> onCallback) {
        return boxed.subscribe(onCallback);
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

    @SchedulerSupport("none")
    public void subscribe(MaybeObserver<? super T> subscriber) {
        boxed.subscribe(subscriber);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E extends MaybeObserver<? super T>> E subscribeWith(E observer) {
        return boxed.subscribeWith(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> subscribeOn(Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> takeUntil(CompletableSource other) {
        return boxed.takeUntil(other);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <E> Maybe<T> takeUntil(Publisher<E> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E> Maybe<T> takeUntil(MaybeSource<? extends E> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> timeout(long timeout, TimeUnit unit) {
        return boxed.timeout(timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> timeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return boxed.timeout(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Maybe<T> timeout(long timeout, TimeUnit unit, Scheduler scheduler, MaybeSource<? extends T> other) {
        return boxed.timeout(timeout, unit, scheduler, other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Maybe<T> timeout(long timeout, TimeUnit unit, MaybeSource<? extends T> other) {
        return boxed.timeout(timeout, unit, other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> R to(Function<? super Maybe<T>, R> convert) {
        return boxed.to(convert);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Completable toCompletable() {
        return boxed.toCompletable();
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> toFlowable() {
        return boxed.toFlowable();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> toMaybe() {
        return boxed.toMaybe();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Observable<T> toObservable() {
        return boxed.toObservable();
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    @Experimental
    public Maybe<T> unsubscribeOn(Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Maybe<R> zipWith(MaybeSource<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
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
