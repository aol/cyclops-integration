package cyclops.companion.rx;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aol.cyclops.rx.adapter.ObservableReactiveSeq;
import cyclops.companion.CompletableFutures;
import cyclops.companion.CompletableFutures.CompletableFutureKind;
import cyclops.companion.Optionals;
import cyclops.companion.Optionals.OptionalKind;
import cyclops.companion.Streams;
import cyclops.companion.Streams.StreamKind;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.control.Either;
import cyclops.monads.*;
import cyclops.monads.RxWitness.observable;
import com.aol.cyclops.rx.hkt.ObservableKind;
import com.oath.cyclops.hkt.Higher;
import com.oath.cyclops.types.anyM.AnyMSeq;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.function.Monoid;
import cyclops.monads.Witness.*;
import cyclops.monads.transformers.StreamT;
import cyclops.reactive.ReactiveSeq;


import cyclops.stream.Spouts;
import cyclops.typeclasses.*;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;
import cyclops.data.tuple.Tuple2;
import org.reactivestreams.Publisher;
import rx.*;
import rx.Observable;
import rx.functions.*;
import rx.internal.operators.*;
import rx.observables.AsyncOnSubscribe;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

import static com.aol.cyclops.rx.hkt.ObservableKind.widen;

/**
 * Companion class for working with RxJava Observable types
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Observables {

    public static  <W1,T> Coproduct<W1,observable,T> coproduct(Observable<T> list, InstanceDefinitions<W1> def1){
        return Coproduct.of(Either.right(ObservableKind.widen(list)),def1, Instances.definitions());
    }
    public static  <W1,T> Coproduct<W1,observable,T> coproduct(InstanceDefinitions<W1> def1,T... values){
        return coproduct(Observable.from(values),def1);
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,observable,T> xorM(Observable<T> type){
        return XorM.right(anyM(type));
    }

    public static <T,W extends WitnessType<W>> AnyM<W,Observable<T>> fromStream(AnyM<W,Stream<T>> anyM){
        return anyM.map(s->fromStream(s));
    }
    public static  <T,R> Observable<R> tailRec(T initial, Function<? super T, ? extends Observable<? extends Either<T, R>>> fn) {
        Observable<Either<T, R>> next = Observable.just(Either.left(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.flatMap(e -> e.visit(s -> {
                        newValue[0]=true;
                        return fn.apply(s); },
                    p -> {
                        newValue[0]=false;
                        return Observable.just(e);
                    }));
            if(!newValue[0])
                break;

        }

        return next.filter(Either::isPrimary).map(Either::get);
    }
    public static <T> Observable<T> raw(AnyM<observable,T> anyM){
        return RxWitness.observable(anyM);
    }
    public static <T> Observable<T> narrow(Observable<? extends T> observable) {
        return (Observable<T>)observable;
    }
    public static <T> ReactiveSeq<T> reactiveSeq(Observable<T> observable) {
        return new ObservableReactiveSeq<>(observable);
    }
    public static  <T> Observable<T> observableFrom(ReactiveSeq<T> stream){
        return stream.visit(sync->fromStream(stream),
                rs->observable(stream),
                async->Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final rx.Subscriber<? super T> rxSubscriber) {
                rxSubscriber.onStart();
                stream.forEach(rxSubscriber::onNext,rxSubscriber::onError,rxSubscriber::onCompleted);
            }
        }).onBackpressureBuffer(Long.MAX_VALUE));


    }
    public static  <T> Observable<T> fromStream(Stream<T> s){

        if(s instanceof  ReactiveSeq) {
            ReactiveSeq<T> stream = (ReactiveSeq<T>)s;

            return stream.visit(sync -> Observable.from(stream),
                    rs -> observable(stream),
                    async -> Observable.create(new Observable.OnSubscribe<T>() {
                        @Override
                        public void call(final rx.Subscriber<? super T> rxSubscriber) {
                            rxSubscriber.onStart();
                            stream.forEach(rxSubscriber::onNext, rxSubscriber::onError, rxSubscriber::onCompleted);
                        }
                    }).onBackpressureBuffer(Long.MAX_VALUE));
        }
        return Observable.from(ReactiveSeq.fromStream(s));
    }
    public static <W extends WitnessType<W>,T> StreamT<W,T> observablify(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        AnyM<W, ReactiveSeq<T>> fluxM = anyM.map(s -> {
            if (s instanceof ObservableReactiveSeq) {
                return (ObservableReactiveSeq)s;
            }
            if(s instanceof ReactiveSeq){
                return new ObservableReactiveSeq<T>(Observables.observableFrom((ReactiveSeq<T>) s));
            }
            if (s instanceof Publisher) {
                return new ObservableReactiveSeq<T>(Observables.observable((Publisher) s));
            }
            return new ObservableReactiveSeq<T>(fromStream(s));
        });
        StreamT<W, T> res = StreamT.of(fluxM);
        return res;
    }

    public static <W extends WitnessType<W>,T,R> R nestedObservable(StreamT<W,T> nested, Function<? super AnyM<W,Observable<T>>,? extends R> mapper){
        return mapper.apply(nestedObservable(nested));
    }
    public static <W extends WitnessType<W>,T> AnyM<W,Observable<T>> nestedObservable(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        return anyM.map((Stream<T> s)->{
            if(s instanceof ObservableReactiveSeq){
                return ((ObservableReactiveSeq)s).getObservable();
            }
            if(s instanceof ReactiveSeq){
                return Observables.observableFrom((ReactiveSeq<T>) s);
            }
            if (s instanceof Publisher) {
                return Observables.observable((Publisher) s);
            }
            return Observables.fromStream(s);
        });
    }

    public static <W extends WitnessType<W>,T> StreamT<W,T> liftM(AnyM<W,Observable<T>> nested){

        AnyM<W, ReactiveSeq<T>> monad = nested.map(s -> new ObservableReactiveSeq<T>(s));
        return StreamT.of(monad);
    }
    /**
     * Convert an Observable to a reactive-streams Publisher
     *
     * @param observable To convert
     * @return reactive-streams Publisher
     */
    public static <T> Publisher<T> publisher(Observable<T> observable) {
        return RxReactiveStreams.toPublisher(observable);
    }

    /**
     * Convert an Observable to a cyclops-react ReactiveSeq
     *
     * @param observable To conver
     * @return ReactiveSeq
     */
    public static <T> ReactiveSeq<T> connectToReactiveSeq(Observable<T> observable) {
        return Spouts.async(s->{
           observable.subscribe(s::onNext,e->{
               s.onError(e);
               s.onComplete();
           },s::onComplete);

        });

    }


    /**
     * Convert a Publisher to an observable
     *
     * @param publisher To convert
     * @return Observable
     */
    public static <T> Observable<T> observable(Publisher<T> publisher) {
        return RxReactiveStreams.toObservable(publisher);
    }




    public static <T> ReactiveSeq<T> create(Observable.OnSubscribe<T> f) {
        return reactiveSeq(Observable.create(f));
    }


    public static <S, T> ReactiveSeq<T> create(SyncOnSubscribe<S, T> syncOnSubscribe) {
        return reactiveSeq(Observable.create(syncOnSubscribe));
    }


    public static <S, T> ReactiveSeq<T> create(AsyncOnSubscribe<S, T> asyncOnSubscribe) {
        return reactiveSeq(Observable.create(asyncOnSubscribe));
    }





    public static <T> ReactiveSeq<T> amb(Iterable<? extends Observable<? extends T>> sources) {
        return create(OnSubscribeAmb.amb(sources));
    }




    public static <T> ReactiveSeq<T> concat(Observable<? extends Observable<? extends T>> observables) {
        return reactiveSeq(Observable.concat(observables));
    }




    public static <T> ReactiveSeq<T> concatDelayError(Observable<? extends Observable<? extends T>> sources) {
        return reactiveSeq(Observable.concatDelayError(sources));
    }


    public static <T> ReactiveSeq<T> defer(Supplier<Observable<T>> observableFactory) {
        return reactiveSeq(Observable.defer(()->observableFactory.get()));
    }


    public static <T> ReactiveSeq<T> empty() {
        return reactiveSeq(Observable.empty());
    }

    public static <T> ReactiveSeq<T> error(Throwable exception) {
        return reactiveSeq(Observable.error(exception));
    }






    public static <T> ReactiveSeq<T> from(Iterable<? extends T> iterable) {
        return reactiveSeq(Observable.from(iterable));
    }


    public static <T> ReactiveSeq<T> from(T... params) {
        T[] array = params;
        int n = array.length;
        if (n == 0) {
            return empty();
        } else
        if (n == 1) {
            return just(array[0]);
        }
        return create(new OnSubscribeFromArray<T>(array));
    }


    public static ReactiveSeq<Long> interval(long interval, TimeUnit unit) {
        return interval(interval, interval, unit, Schedulers.computation());
    }


    public static ReactiveSeq<Long> interval(long interval, TimeUnit unit, Scheduler scheduler) {
        return interval(interval, interval, unit, scheduler);
    }


    public static ReactiveSeq<Long> interval(long initialDelay, long period, TimeUnit unit) {
        return interval(initialDelay, period, unit, Schedulers.computation());
    }


    public static ReactiveSeq<Long> interval(long initialDelay, long period, TimeUnit unit, Scheduler scheduler) {
        return reactiveSeq(Observable.interval(initialDelay,period,unit,scheduler));
    }


    public static <T> ReactiveSeq<T> just(final T value) {
        return reactiveSeq(Observable.just(value));
    }
    @SafeVarargs
    public static <T> ReactiveSeq<T> just(final T... values) {
        T[] array = values;
        return reactiveSeq(Observable.from(array));
    }
    public static <T> ReactiveSeq<T> of(final T value) {
        return just(value);
    }
    @SafeVarargs
    public static <T> ReactiveSeq<T> of(final T... values) {
        return just(values);
    }


    public static <T> ReactiveSeq<T> merge(Iterable<? extends Observable<? extends T>> sequences) {
        return merge(from(sequences));
    }


    public static <T> ReactiveSeq<T> merge(Iterable<? extends Observable<? extends T>> sequences, int maxConcurrent) {
        return merge(from(sequences), maxConcurrent);
    }

    public static <T> ReactiveSeq<T> merge(Observable<? extends Observable<? extends T>> source) {
        return reactiveSeq(Observable.merge(source));
    }


    public static <T> ReactiveSeq<T> merge(Observable<? extends Observable<? extends T>> source, int maxConcurrent) {
       return reactiveSeq(Observable.merge(source,maxConcurrent));
    }

    public static <T> ReactiveSeq<T> mergeDelayError(Observable<? extends Observable<? extends T>> source) {
        return reactiveSeq(Observable.mergeDelayError(source));
    }

    public static <T> ReactiveSeq<T> mergeDelayError(Observable<? extends Observable<? extends T>> source, int maxConcurrent) {
        return reactiveSeq(Observable.mergeDelayError(source,maxConcurrent));
    }

    public static <T> ReactiveSeq<T> mergeDelayError(Iterable<? extends Observable<? extends T>> sequences) {
        return mergeDelayError(from(sequences));
    }

    public static <T> ReactiveSeq<T> mergeDelayError(Iterable<? extends Observable<? extends T>> sequences, int maxConcurrent) {
        return mergeDelayError(from(sequences), maxConcurrent);
    }



    public static <T> ReactiveSeq<T> never() {
        return reactiveSeq(Observable.never());
    }

    public static ReactiveSeq<Integer> range(int start, int count) {
       return reactiveSeq(Observable.range(start,count));
    }


    public static ReactiveSeq<Integer> range(int start, int count, Scheduler scheduler) {
        return reactiveSeq(Observable.range(start,count,scheduler));
    }


    public static <T> ReactiveSeq<T> switchOnNext(Observable<? extends Observable<? extends T>> sequenceOfSequences) {
        return reactiveSeq(Observable.switchOnNext(sequenceOfSequences));
    }


    public static <T> ReactiveSeq<T> switchOnNextDelayError(Observable<? extends Observable<? extends T>> sequenceOfSequences) {
        return reactiveSeq(Observable.switchOnNext(sequenceOfSequences));
    }


    public static ReactiveSeq<Long> timer(long initialDelay, long period, TimeUnit unit) {
        return interval(initialDelay, period, unit, Schedulers.computation());
    }


    public static ReactiveSeq<Long> timer(long delay, TimeUnit unit) {
        return timer(delay, unit, Schedulers.computation());
    }


    public static ReactiveSeq<Long> timer(long delay, TimeUnit unit, Scheduler scheduler) {
        return create(new OnSubscribeTimerOnce(delay, unit, scheduler));
    }




    /**
     * Construct an AnyM type from an Observable. This allows the Observable to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     *
     * <pre>
     * {@code
     *
     *    AnyMSeq<Integer> obs = Observables.anyM(Observable.just(1,2,3));
     *    AnyMSeq<Integer> transformedObs = myGenericOperation(obs);
     *
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     *
     * @param obs Observable to wrap inside an AnyM
     * @return AnyMSeq wrapping an Observable
     */
    public static <T> AnyMSeq<observable,T> anyM(Observable<T> obs) {
        return AnyM.ofSeq(reactiveSeq(obs), observable.INSTANCE);
    }

    /**
     * Perform a For Comprehension over a Observable, accepting 3 generating functions.
     * This results in a four level nested internal iteration over the provided Observables.
     *
     *  <pre>
     * {@code
     *
     *   import static com.aol.cyclops.reactor.Observables.forEach4;
     *
    forEach4(Observable.range(1,10),
    a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
    (a,b) -> Maybe.<Integer>of(a+b),
    (a,b,c) -> Mono.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param value3 Nested Observable
     * @param value4 Nested Observable
     * @param yieldingFunction  Generates a result per combination
     * @return Observable with an element per combination of nested Observables generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Observable<R> forEach4(Observable<? extends T1> value1,
                                                                     Function<? super T1, ? extends Observable<R1>> value2,
                                                                     BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                                                                     Function3<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                                                                     Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Observable<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Observable<R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });


    }

    /**
     * Perform a For Comprehension over a Observable, accepting 3 generating functions.
     * This results in a four level nested internal iteration over the provided Observables.
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops.reactor.Observables.forEach4;
     *
     *  forEach4(Observable.range(1,10),
    a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
    (a,b) -> Maybe.<Integer>just(a+b),
    (a,b,c) -> Mono.<Integer>just(a+b+c),
    (a,b,c,d) -> a+b+c+d <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param value3 Nested Observable
     * @param value4 Nested Observable
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Observable with an element per combination of nested Observables generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Observable<R> forEach4(Observable<? extends T1> value1,
                                                                     Function<? super T1, ? extends Observable<R1>> value2,
                                                                     BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                                                                     Function3<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                                                                     Function4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                     Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Observable<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Observable<R3> c = value4.apply(in,ina,inb);
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    /**
     * Perform a For Comprehension over a Observable, accepting 2 generating functions.
     * This results in a three level nested internal iteration over the provided Observables.
     *
     * <pre>
     * {@code
     *
     * import static com.aol.cyclops.reactor.Observables.forEach;
     *
     * forEach(Observable.range(1,10),
    a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
    (a,b) -> Maybe.<Integer>of(a+b),
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param value3 Nested Observable
     * @param yieldingFunction Generates a result per combination
     * @return Observable with an element per combination of nested Observables generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Observable<R> forEach3(Observable<? extends T1> value1,
                                                             Function<? super T1, ? extends Observable<R1>> value2,
                                                             BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                                                             Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Observable<R2> b = value3.apply(in, ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }
    /**
     * Perform a For Comprehension over a Observable, accepting 2 generating functions.
     * This results in a three level nested internal iteration over the provided Observables.
     * <pre>
     * {@code
     *
     * import static com.aol.cyclops.reactor.Observables.forEach;
     *
     * forEach(Observable.range(1,10),
    a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
    (a,b) -> Maybe.<Integer>of(a+b),
    (a,b,c) ->a+b+c<10,
    Tuple::tuple).toListX();
     * }
     * </pre>
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param value3 Nested Observable
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T1, T2, R1, R2, R> Observable<R> forEach3(Observable<? extends T1> value1,
                                                             Function<? super T1, ? extends Observable<R1>> value2,
                                                             BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                                                             Function3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                             Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Observable<R2> b = value3.apply(in,ina);
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });

    }

    /**
     * Perform a For Comprehension over a Observable, accepting an additonal generating function.
     * This results in a two level nested internal iteration over the provided Observables.
     *
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops.reactor.Observables.forEach;
     *  forEach(Observable.range(1, 10), i -> Observable.range(i, 10), Tuple::tuple)
    .subscribe(System.out::println);

    //(1, 1)
    (1, 2)
    (1, 3)
    (1, 4)
    ...
     *
     * }</pre>
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Observable<R> forEach(Observable<? extends T> value1, Function<? super T, Observable<R1>> value2,
                                                   BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }

    /**
     *
     * <pre>
     * {@code
     *
     *   import static com.aol.cyclops.reactor.Observables.forEach;
     *
     *   forEach(Observable.range(1, 10), i -> Observable.range(i, 10),(a,b) -> a>2 && b<10,Tuple::tuple)
    .subscribe(System.out::println);

    //(3, 3)
    (3, 4)
    (3, 5)
    (3, 6)
    (3, 7)
    (3, 8)
    (3, 9)
    ...

     *
     * }</pre>
     *
     *
     * @param value1 top level Observable
     * @param value2 Nested Observable
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Observable<R> forEach(Observable<? extends T> value1,
                                                   Function<? super T, ? extends Observable<R1>> value2,
                                                   BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                   BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Observable<R1> a = value2.apply(in);
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }
    public static <T> Active<observable,T> allTypeclasses(Observable<T> type){
        return Active.of(widen(type), Observables.Instances.definitions());
    }
    public static <T,W2,R> Nested<observable,W2,R> mapM(Observable<T> type, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Observable<Higher<W2, R>> e = type.map(x->fn.apply(x));
        ObservableKind<Higher<W2, R>> lk = ObservableKind.widen(e);
        return Nested.of(lk, Observables.Instances.definitions(), defs);
    }
    /**
     * Companion class for creating Type Class instances for working with Observables
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<observable> definitions() {
            return new InstanceDefinitions<observable>() {


                @Override
                public <T, R> Functor<observable> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<observable> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<observable> applicative() {
                    return Instances.zippingApplicative();
                }

                @Override
                public <T, R> Monad<observable> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<observable>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<observable>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<observable> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> Maybe<MonadPlus<observable>> monadPlus(Monoid<Higher<observable, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<observable> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<observable> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> Maybe<Comonad<observable>> comonad() {
                    return Maybe.nothing();
                }

                @Override
                public <T> Maybe<Unfoldable<observable>> unfoldable() {
                    return Maybe.just(Instances.unfoldable());
                }
            };
        }
        /**
         *
         * Transform a observable, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  ObservableKind<Integer> observable = Observables.functor().map(i->i*2, ObservableKind.widen(Observable.of(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Observables
         * <pre>
         * {@code
         *   ObservableKind<Integer> observable = Observables.unit()
        .unit("hello")
        .then(h->Observables.functor().map((String v) ->v.length(), h))
        .convert(ObservableKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Observables
         */
        public static <T,R>Functor<observable> functor(){
            BiFunction<ObservableKind<T>,Function<? super T, ? extends R>,ObservableKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * ObservableKind<String> observable = Observables.unit()
        .unit("hello")
        .convert(ObservableKind::narrowK);

        //Observable.of("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Observables
         */
        public static <T> Pure<observable> unit(){
            return General.<observable,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.ObservableKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        Observables.zippingApplicative()
        .ap(widen(Observable.of(l1(this::multiplyByTwo))),widen(Observable.of(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * ObservableKind<Function<Integer,Integer>> observableFn =Observables.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(ObservableKind::narrowK);

        ObservableKind<Integer> observable = Observables.unit()
        .unit("hello")
        .then(h->Observables.functor().map((String v) ->v.length(), h))
        .then(h->Observables.zippingApplicative().ap(observableFn, h))
        .convert(ObservableKind::narrowK);

        //Observable.of("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Observables
         */
        public static <T,R> Applicative<observable> zippingApplicative(){
            BiFunction<ObservableKind< Function<T, R>>,ObservableKind<T>,ObservableKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.ObservableKind.widen;
         * ObservableKind<Integer> observable  = Observables.monad()
        .flatMap(i->widen(ObservableX.range(0,i)), widen(Observable.of(1,2,3)))
        .convert(ObservableKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    ObservableKind<Integer> observable = Observables.unit()
        .unit("hello")
        .then(h->Observables.monad().flatMap((String v) ->Observables.unit().unit(v.length()), h))
        .convert(ObservableKind::narrowK);

        //Observable.of("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Observables
         */
        public static <T,R> Monad<observable> monad(){

            BiFunction<Higher<observable,T>,Function<? super T, ? extends Higher<observable,R>>,Higher<observable,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        public static <T,R> MonadRec<observable> monadRec(){
            return new MonadRec<observable>() {
                @Override
                public <T, R> Higher<observable, R> tailRec(T initial, Function<? super T, ? extends Higher<observable, ? extends Either<T, R>>> fn) {
                    return widen(Observables.tailRec(initial,fn.andThen(ObservableKind::narrowK).andThen(a->a.narrow())));
                }
            };
        }
        /**
         *
         *
         * <pre>
         * {@code
         *  ObservableKind<String> observable = Observables.unit()
        .unit("hello")
        .then(h->Observables.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(ObservableKind::narrowK);

        //Observable.of("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<observable> monadZero(){
            BiFunction<Higher<observable,T>,Predicate<? super T>,Higher<observable,T>> filter = Instances::filter;
            Supplier<Higher<observable, T>> zero = ()-> widen(Observable.empty());
            return General.<observable,T,R>monadZero(monad(), zero,filter);
        }
        /**
         * <pre>
         * {@code
         *  ObservableKind<Integer> observable = Observables.<Integer>monadPlus()
        .plus(ObservableKind.widen(Observable.of()), ObservableKind.widen(Observable.of(10)))
        .convert(ObservableKind::narrowK);
        //Observable.of(10))
         *
         * }
         * </pre>
         * @return Type class for combining Observables by concatenation
         */
        public static <T> MonadPlus<observable> monadPlus(){
            Monoid<ObservableKind<T>> m = Monoid.of(widen(Observable.<T>empty()), Instances::concat);
            Monoid<Higher<observable,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<ObservableKind<Integer>> m = Monoid.of(ObservableKind.widen(Observable.of()), (a,b)->a.isEmpty() ? b : a);
        ObservableKind<Integer> observable = Observables.<Integer>monadPlus(m)
        .plus(ObservableKind.widen(Observable.of(5)), ObservableKind.widen(Observable.of(10)))
        .convert(ObservableKind::narrowK);
        //Observable.of(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Observables
         * @return Type class for combining Observables
         */
        public static <T> MonadPlus<observable> monadPlusK(Monoid<ObservableKind<T>> m){
            Monoid<Higher<observable,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<observable> monadPlus(Monoid<Higher<observable,T>> m){
            Monoid<Higher<observable,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<observable> traverse(){
            BiFunction<Applicative<C2>,ObservableKind<Higher<C2, T>>,Higher<C2, ObservableKind<T>>> sequenceFn = (ap, observable) -> {

                Higher<C2,ObservableKind<T>> identity = ap.unit(widen(Observable.empty()));

                BiFunction<Higher<C2,ObservableKind<T>>,Higher<C2,T>,Higher<C2,ObservableKind<T>>> combineToObservable =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> widen(Observable.concat(ObservableKind.narrow(a),Observable.just(b)))),acc,next);

                BinaryOperator<Higher<C2,ObservableKind<T>>> combineObservables = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return widen(Observable.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromPublisher(observable).reduce(identity,
                        combineToObservable,
                        combineObservables);


            };
            BiFunction<Applicative<C2>,Higher<observable,Higher<C2, T>>,Higher<C2, Higher<observable,T>>> sequenceNarrow  =
                    (a,b) -> ObservableKind.widen2(sequenceFn.apply(a, ObservableKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Observables.foldable()
        .foldLeft(0, (a,b)->a+b, ObservableKind.widen(Observable.of(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<observable> foldable(){
            return new Foldable<observable>() {
                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<observable, T> ds) {
                    return ReactiveSeq.fromPublisher(ObservableKind.narrowK(ds)).foldRight(monoid);
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<observable, T> ds) {
                    return ObservableKind.narrowK(ds)
                            .narrow()
                            .reduce(monoid.zero(), (a, b) -> monoid.apply(a, b))
                            .toBlocking()
                            .single();
                }

                @Override
                public <T, R> R foldMap(Monoid<R> monoid, Function<? super T, ? extends R> fn, Higher<observable, T> ds) {
                    return ObservableKind.narrowK(ds)
                            .narrow()
                            .reduce(monoid.zero(), (a, b) -> monoid.apply(a, fn.apply(b)))
                            .toBlocking()
                            .single();
                }
            };

        }

        private static  <T> ObservableKind<T> concat(ObservableKind<T> l1, ObservableKind<T> l2){
            return widen(Observable.concat(l1.narrow(),l2.narrow()));
        }
        private <T> ObservableKind<T> of(T value){
            return widen(Observable.just(value));
        }
        private static <T,R> ObservableKind<R> ap(ObservableKind<Function< T, R>> lt, ObservableKind<T> observable){
            return widen(lt.zipWith(observable.narrow(),(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<observable,R> flatMap(Higher<observable,T> lt, Function<? super T, ? extends  Higher<observable,R>> fn){
            Func1<? super T, ? extends  Observable<R>> f = t->fn.andThen(ObservableKind::narrow).apply(t);

            return widen(ObservableKind.narrowK(lt)
                    .flatMap(f));
        }
        private static <T,R> ObservableKind<R> map(ObservableKind<T> lt, Function<? super T, ? extends R> fn){
            return widen(lt.map(in->fn.apply(in)));
        }
        private static <T> ObservableKind<T> filter(Higher<observable,T> lt, Predicate<? super T> fn){
            return widen(ObservableKind.narrow(lt).filter(in->fn.test(in)));
        }
        public static Unfoldable<observable> unfoldable(){
            return new Unfoldable<observable>() {
                @Override
                public <R, T> Higher<observable, R> unfold(T b, Function<? super T, Optional<Tuple2<R, T>>> fn) {
                    return widen(Observables.fromStream(ReactiveSeq.unfold(b,fn)));
                }
            };
        }
    }

    public static interface ObservableNested {

        public static <T> Nested<observable,observable,T> observable(Observable<Observable<T>> nested){
            Observable<ObservableKind<T>> f = nested.map(ObservableKind::widen);
            ObservableKind<ObservableKind<T>> x = widen(f);
            ObservableKind<Higher<observable,T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(), Observables.Instances.definitions());
        }

        public static <T> Nested<observable,reactiveSeq,T> reactiveSeq(Observable<ReactiveSeq<T>> nested){
            ObservableKind<ReactiveSeq<T>> x = widen(nested);
            ObservableKind<Higher<reactiveSeq,T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<observable,maybe,T> maybe(Observable<Maybe<T>> nested){
            ObservableKind<Maybe<T>> x = widen(nested);
            ObservableKind<Higher<maybe,T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),Maybe.Instances.definitions());
        }
        public static <T> Nested<observable,eval,T> eval(Observable<Eval<T>> nested){
            ObservableKind<Eval<T>> x = widen(nested);
            ObservableKind<Higher<eval,T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<observable,future,T> future(Observable<cyclops.async.Future<T>> nested){
            ObservableKind<cyclops.async.Future<T>> x = widen(nested);
            ObservableKind<Higher<future,T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<observable,Higher<xor,S>, P> xor(Observable<Either<S, P>> nested){
            ObservableKind<Either<S, P>> x = widen(nested);
            ObservableKind<Higher<Higher<xor,S>, P>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),Either.Instances.definitions());
        }
        public static <S,T> Nested<observable,Higher<reader,S>, T> reader(Observable<Reader<S, T>> nested, S defaultValue){
            ObservableKind<Reader<S, T>> x = widen(nested);
            ObservableKind<Higher<Higher<reader,S>, T>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <S extends Throwable, P> Nested<observable,Higher<Witness.tryType,S>, P> cyclopsTry(Observable<cyclops.control.Try<P, S>> nested){
            ObservableKind<cyclops.control.Try<P, S>> x = widen(nested);
            ObservableKind<Higher<Higher<Witness.tryType,S>, P>> y = (ObservableKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<observable,optional,T> javaOptional(Observable<Optional<T>> nested){
            Observable<OptionalKind<T>> f = nested.map(o -> OptionalKind.widen(o));
            ObservableKind<OptionalKind<T>> x = ObservableKind.widen(f);

            ObservableKind<Higher<optional,T>> y = (ObservableKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <T> Nested<observable,completableFuture,T> javaCompletableFuture(Observable<CompletableFuture<T>> nested){
            Observable<CompletableFutureKind<T>> f = nested.map(o -> CompletableFutureKind.widen(o));
            ObservableKind<CompletableFutureKind<T>> x = ObservableKind.widen(f);
            ObservableKind<Higher<completableFuture,T>> y = (ObservableKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<observable,Witness.stream,T> javaStream(Observable<java.util.stream.Stream<T>> nested){
            Observable<StreamKind<T>> f = nested.map(o -> StreamKind.widen(o));
            ObservableKind<StreamKind<T>> x = ObservableKind.widen(f);
            ObservableKind<Higher<Witness.stream,T>> y = (ObservableKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedObservable{
        public static <T> Nested<reactiveSeq,observable,T> reactiveSeq(ReactiveSeq<Observable<T>> nested){
            ReactiveSeq<Higher<observable,T>> x = nested.map(ObservableKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<maybe,observable,T> maybe(Maybe<Observable<T>> nested){
            Maybe<Higher<observable,T>> x = nested.map(ObservableKind::widenK);

            return Nested.of(x,Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,observable,T> eval(Eval<Observable<T>> nested){
            Eval<Higher<observable,T>> x = nested.map(ObservableKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<future,observable,T> future(cyclops.async.Future<Observable<T>> nested){
            cyclops.async.Future<Higher<observable,T>> x = nested.map(ObservableKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<xor,S>,observable, P> xor(Either<S, Observable<P>> nested){
            Either<S, Higher<observable,P>> x = nested.map(ObservableKind::widenK);

            return Nested.of(x,Either.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,observable, T> reader(Reader<S, Observable<T>> nested, S defaultValue){

            Reader<S, Higher<observable, T>>  x = nested.map(ObservableKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,observable, P> cyclopsTry(cyclops.control.Try<Observable<P>, S> nested){
            cyclops.control.Try<Higher<observable,P>, S> x = nested.map(ObservableKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<optional,observable,T> javaOptional(Optional<Observable<T>> nested){
            Optional<Higher<observable,T>> x = nested.map(ObservableKind::widenK);

            return  Nested.of(OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<completableFuture,observable,T> javaCompletableFuture(CompletableFuture<Observable<T>> nested){
            CompletableFuture<Higher<observable,T>> x = nested.thenApply(ObservableKind::widenK);

            return Nested.of(CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,observable,T> javaStream(java.util.stream.Stream<Observable<T>> nested){
            java.util.stream.Stream<Higher<observable,T>> x = nested.map(ObservableKind::widenK);

            return Nested.of(StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }



}
