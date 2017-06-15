package com.aol.cyclops.rx2.hkt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.async.Future;
import io.reactivex.*;
import io.reactivex.annotations.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.logging.Level;

/**
 * Simulates Higher Kinded Types for RxJava 2 Single's
 * 
 * SingleKind is a Single and a Higher Kinded Type (SingleKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Single
 */


public final class SingleKind<T> implements Higher<SingleKind.µ, T>, Publisher<T> {
    private SingleKind(Single<T> boxed) {
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
     * Construct a HKT encoded completed Single
     * 
     * @param value To encode inside a HKT encoded Single
     * @return Completed HKT encoded FSingle
     */
    public static <T> SingleKind<T> just(T value){
        
        return widen(Single.just(value));
    }
    public static <T> SingleKind<T> empty(){
        return widen(Single.never());
    }

    /**
     * Convert a Single to a simulated HigherKindedType that captures Single nature
     * and Single element data type separately. Recover via @see SingleKind#narrow
     * 
     * If the supplied Single implements SingleKind it is returned already, otherwise it
     * is wrapped into a Single implementation that does implement SingleKind
     * 
     * @param completableSingle Single to widen to a SingleKind
     * @return SingleKind encoding HKT info about Singles
     */
    public static <T> SingleKind<T> widen(final Single<T> completableSingle) {
        
        return new SingleKind<T>(
                         completableSingle);
    }
    
    public static <T> SingleKind<T> widen(final Publisher<T> completableSingle) {
        
        return new SingleKind<T>(Single.fromPublisher(
                         completableSingle));
    }
        
    
    /**
     * Convert the raw Higher Kinded Type for SingleKind types into the SingleKind type definition class
     * 
     * @param future HKT encoded list into a SingleKind
     * @return SingleKind
     */
    public static <T> SingleKind<T> narrowK(final Higher<SingleKind.µ, T> future) {
       return (SingleKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Single into
     * 
     * @param completableSingle Type Constructor to convert back into narrowed type
     * @return Single from Higher Kinded Type
     */
    public static <T> Single<T> narrow(final Higher<SingleKind.µ, T> completableSingle) {
      
            return ((SingleKind<T>)completableSingle).narrow();
           
       

    }


    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> amb(Iterable<? extends SingleSource<? extends T1>> singleSources) {
        return Single.amb(singleSources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> ambArray(SingleSource<? extends T1>[] sources) {
        return Single.ambArray(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    @BackpressureSupport(BackpressureKind.FULL)
    public static <T1> Flowable<T1> concat(Iterable<? extends SingleSource<? extends T1>> singleSources) {
        return Single.concat(singleSources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Observable<T1> concat(ObservableSource<? extends SingleSource<? extends T1>> sources) {
        return Single.concat(sources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(Publisher<? extends SingleSource<? extends T1>> sources) {
        return Single.concat(sources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(Publisher<? extends SingleSource<? extends T1>> sources, int prefetch) {
        return Single.concat(sources, prefetch);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2) {
        return Single.concat(source1, source2);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2, SingleSource<? extends T1> source3) {
        return Single.concat(source1, source2, source3);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concat(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2, SingleSource<? extends T1> source3, SingleSource<? extends T1> source4) {
        return Single.concat(source1, source2, source3, source4);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> concatArray(SingleSource<? extends T1>[] sources) {
        return Single.concatArray(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> create(SingleOnSubscribe<T1> source) {
        return Single.create(source);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> defer(Callable<? extends SingleSource<? extends T1>> singleSupplier) {
        return Single.defer(singleSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> error(Callable<? extends Throwable> errorSupplier) {
        return Single.error(errorSupplier);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> error(Throwable exception) {
        return Single.error(exception);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> fromCallable(Callable<? extends T1> callable) {
        return Single.fromCallable(callable);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> fromFuture(java.util.concurrent.Future<? extends T1> future) {
        return Single.fromFuture(future);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, long timeout, TimeUnit unit) {
        return Single.fromFuture(future, timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static <T1> Single<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, long timeout, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return Single.fromFuture(future, timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static <T1> Single<T1> fromFuture(java.util.concurrent.Future<? extends T1> future, io.reactivex.Scheduler scheduler) {
        return Single.fromFuture(future, scheduler);
    }

    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> fromPublisher(Publisher<? extends T1> publisher) {
        return Single.fromPublisher(publisher);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> fromObservable(ObservableSource<? extends T1> observableSource) {
        return Single.fromObservable(observableSource);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(Iterable<? extends SingleSource<? extends T1>> singleSources) {
        return Single.merge(singleSources);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(Publisher<? extends SingleSource<? extends T1>> sources) {
        return Single.merge(sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> merge(SingleSource<? extends SingleSource<? extends T1>> source) {
        return Single.merge(source);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2) {
        return Single.merge(source1, source2);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2, SingleSource<? extends T1> source3) {
        return Single.merge(source1, source2, source3);
    }

    @CheckReturnValue
    @BackpressureSupport(BackpressureKind.FULL)
    @SchedulerSupport("none")
    public static <T1> Flowable<T1> merge(SingleSource<? extends T1> source1, SingleSource<? extends T1> source2, SingleSource<? extends T1> source3, SingleSource<? extends T1> source4) {
        return Single.merge(source1, source2, source3, source4);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> never() {
        return Single.never();
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public static Single<Long> timer(long delay, TimeUnit unit) {
        return Single.timer(delay, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public static Single<Long> timer(long delay, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return Single.timer(delay, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<Boolean> equals(SingleSource<? extends T1> first, SingleSource<? extends T1> second) {
        return Single.equals(first, second);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> unsafeCreate(SingleSource<T1> onSubscribe) {
        return Single.unsafeCreate(onSubscribe);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, U> Single<T1> using(Callable<U> resourceSupplier, io.reactivex.functions.Function<? super U, ? extends SingleSource<? extends T1>> singleFunction, Consumer<? super U> disposer) {
        return Single.using(resourceSupplier, singleFunction, disposer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, U> Single<T1> using(Callable<U> resourceSupplier, Function<? super U, ? extends SingleSource<? extends T1>> singleFunction, Consumer<? super U> disposer, boolean eager) {
        return Single.using(resourceSupplier, singleFunction, disposer, eager);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1> Single<T1> wrap(SingleSource<T1> source) {
        return Single.wrap(source);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, R> Single<R> zip(Iterable<? extends SingleSource<? extends T1>> singleSources, Function<? super Object[], ? extends R> zipper) {
        return Single.zip(singleSources, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, BiFunction<? super T1, ? super T2, ? extends R> zipper) {
        return Single.zip(source1, source2, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, Function3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, SingleSource<? extends T5> source5, Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, source5, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, SingleSource<? extends T5> source5, SingleSource<? extends T6> source6, Function6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, source5, source6, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, SingleSource<? extends T5> source5, SingleSource<? extends T6> source6, SingleSource<? extends T7> source7, Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, source5, source6, source7, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, SingleSource<? extends T5> source5, SingleSource<? extends T6> source6, SingleSource<? extends T7> source7, SingleSource<? extends T8> source8, Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, source5, source6, source7, source8, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Single<R> zip(SingleSource<? extends T1> source1, SingleSource<? extends T2> source2, SingleSource<? extends T3> source3, SingleSource<? extends T4> source4, SingleSource<? extends T5> source5, SingleSource<? extends T6> source6, SingleSource<? extends T7> source7, SingleSource<? extends T8> source8, SingleSource<? extends T9> source9, Function9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> zipper) {
        return Single.zip(source1, source2, source3, source4, source5, source6, source7, source8, source9, zipper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T1, R> Single<R> zipArray(Function<? super Object[], ? extends R> zipper, SingleSource<? extends T1>[] sources) {
        return Single.zipArray(zipper, sources);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> ambWith(SingleSource<? extends T> other) {
        return boxed.ambWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> hide() {
        return boxed.hide();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> compose(SingleTransformer<? super T, ? extends R> transformer) {
        return boxed.compose(transformer);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> cache() {
        return boxed.cache();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<U> cast(Class<? extends U> clazz) {
        return boxed.cast(clazz);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> concatWith(SingleSource<? extends T> other) {
        return boxed.concatWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Single<T> delay(long time, TimeUnit unit) {
        return boxed.delay(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Single<T> delay(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.delay(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> delaySubscription(CompletableSource other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<T> delaySubscription(SingleSource<U> other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<T> delaySubscription(ObservableSource<U> other) {
        return boxed.delaySubscription(other);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <U> Single<T> delaySubscription(Publisher<U> other) {
        return boxed.delaySubscription(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public <U> Single<T> delaySubscription(long time, TimeUnit unit) {
        return boxed.delaySubscription(time, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public <U> Single<T> delaySubscription(long time, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.delaySubscription(time, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doAfterSuccess(Consumer<? super T> onAfterSuccess) {
        return boxed.doAfterSuccess(onAfterSuccess);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doAfterTerminate(Action onAfterTerminate) {
        return boxed.doAfterTerminate(onAfterTerminate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doFinally(Action onFinally) {
        return boxed.doFinally(onFinally);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doOnSubscribe(Consumer<? super Disposable> onSubscribe) {
        return boxed.doOnSubscribe(onSubscribe);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doOnSuccess(Consumer<? super T> onSuccess) {
        return boxed.doOnSuccess(onSuccess);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doOnEvent(BiConsumer<? super T, ? super Throwable> onEvent) {
        return boxed.doOnEvent(onEvent);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doOnError(Consumer<? super Throwable> onError) {
        return boxed.doOnError(onError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> doOnDispose(Action onDispose) {
        return boxed.doOnDispose(onDispose);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Maybe<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> flatMap(Function<? super T, ? extends SingleSource<? extends R>> mapper) {
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
    public <R> Single<R> lift(SingleOperator<? extends R, ? super T> lift) {
        return boxed.lift(lift);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        return boxed.map(mapper);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> contains(Object value) {
        return boxed.contains(value);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<Boolean> contains(Object value, BiPredicate<Object, Object> comparer) {
        return boxed.contains(value, comparer);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public Flowable<T> mergeWith(SingleSource<? extends T> other) {
        return boxed.mergeWith(other);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Single<T> observeOn(io.reactivex.Scheduler scheduler) {
        return boxed.observeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> onErrorReturn(Function<Throwable, ? extends T> resumeFunction) {
        return boxed.onErrorReturn(resumeFunction);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> onErrorReturnItem(T value) {
        return boxed.onErrorReturnItem(value);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> onErrorResumeNext(Single<? extends T> resumeSingleInCaseOfError) {
        return boxed.onErrorResumeNext(resumeSingleInCaseOfError);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> onErrorResumeNext(Function<? super Throwable, ? extends SingleSource<? extends T>> resumeFunctionInCaseOfError) {
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
    public Single<T> retry() {
        return boxed.retry();
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> retry(long times) {
        return boxed.retry(times);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> retry(BiPredicate<? super Integer, ? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> retry(Predicate<? super Throwable> predicate) {
        return boxed.retry(predicate);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> retryWhen(Function<? super Flowable<Throwable>, ? extends Publisher<?>> handler) {
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
    public void subscribe(SingleObserver<? super T> subscriber) {
        boxed.subscribe(subscriber);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E extends SingleObserver<? super T>> E subscribeWith(E observer) {
        return boxed.subscribeWith(observer);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Single<T> subscribeOn(io.reactivex.Scheduler scheduler) {
        return boxed.subscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public Single<T> takeUntil(CompletableSource other) {
        return boxed.takeUntil(other);
    }

    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @SchedulerSupport("none")
    public <E> Single<T> takeUntil(Publisher<E> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <E> Single<T> takeUntil(SingleSource<? extends E> other) {
        return boxed.takeUntil(other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Single<T> timeout(long timeout, TimeUnit unit) {
        return boxed.timeout(timeout, unit);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Single<T> timeout(long timeout, TimeUnit unit, io.reactivex.Scheduler scheduler) {
        return boxed.timeout(timeout, unit, scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("custom")
    public Single<T> timeout(long timeout, TimeUnit unit, io.reactivex.Scheduler scheduler, SingleSource<? extends T> other) {
        return boxed.timeout(timeout, unit, scheduler, other);
    }

    @CheckReturnValue
    @SchedulerSupport("io.reactivex:computation")
    public Single<T> timeout(long timeout, TimeUnit unit, SingleSource<? extends T> other) {
        return boxed.timeout(timeout, unit, other);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <R> R to(Function<? super Single<T>, R> convert) {
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
    public Single<T> unsubscribeOn(io.reactivex.Scheduler scheduler) {
        return boxed.unsubscribeOn(scheduler);
    }

    @CheckReturnValue
    @SchedulerSupport("none")
    public <U, R> Single<R> zipWith(SingleSource<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
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

    private final Single<T> boxed;

        /**
         * @return wrapped Single
         */
        public Single<T> narrow() {
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
