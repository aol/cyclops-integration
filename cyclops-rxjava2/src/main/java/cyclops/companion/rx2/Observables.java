package cyclops.companion.rx2;

import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aol.cyclops.rx2.adapter.ObservableReactiveSeq;
import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.obsvervable;
import com.aol.cyclops.rx2.hkt.ObservableKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.StreamT;
import cyclops.stream.ReactiveSeq;


import cyclops.stream.Spouts;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;



/**
 * Companion class for working with RxJava Observable types
 * 
 * @author johnmcclean
 *
 */
@UtilityClass
public class Observables {

    public static <T,W extends WitnessType<W>> AnyM<W,Observable<T>> fromStream(AnyM<W,Stream<T>> anyM){
        return anyM.map(s->fromStream(s));
    }
    public static <T> Observable<T> raw(AnyM<obsvervable,T> anyM){
        return Rx2Witness.observable(anyM);
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
                async->Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> rxSubscriber) throws Exception {

                        stream.forEach(rxSubscriber::onNext,rxSubscriber::onError,rxSubscriber::onComplete);
                    }

        }));


    }
    public static  <T> Observable<T> fromStream(Stream<T> s){

        if(s instanceof  ReactiveSeq) {
            ReactiveSeq<T> stream = (ReactiveSeq<T>)s;
            return stream.visit(sync -> Observable.fromIterable(stream),
                    rs -> observable(stream),
                    async -> Observable.create(new ObservableOnSubscribe<T>() {
                        @Override
                        public void subscribe(ObservableEmitter<T> rxSubscriber) throws Exception {

                            stream.forEach(rxSubscriber::onNext,rxSubscriber::onError,rxSubscriber::onComplete);
                        }
                    }));
        }
        return Observable.fromIterable(ReactiveSeq.fromStream(s));
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
        return observable.toFlowable(BackpressureStrategy.BUFFER);
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
        return Flowable.fromPublisher(publisher).toObservable();
    }



    


  
    public static <T> ReactiveSeq<T> empty() {
        return reactiveSeq(Observable.empty());
    }

    public static <T> ReactiveSeq<T> error(Throwable exception) {
        return reactiveSeq(Observable.error(exception));
    }


  
  

   
    public static <T> ReactiveSeq<T> from(Iterable<? extends T> iterable) {
        return reactiveSeq(Observable.fromIterable(iterable));
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
        return reactiveSeq(Observable.fromArray(array));
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
        return reactiveSeq(Observable.timer(delay,unit,scheduler));
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
    public static <T> AnyMSeq<obsvervable,T> anyM(Observable<T> obs) {
        return AnyM.ofSeq(reactiveSeq(obs), obsvervable.INSTANCE);
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
                                                                     Fn3<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                                                                     Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


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
                                                                     Fn3<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                                                                     Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                     Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

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
                                                             Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

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
                                                             Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                             Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

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

    /**
     * Companion class for creating Type Class instances for working with Observables
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {


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
        public static <T,R>Functor<ObservableKind.µ> functor(){
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
        public static <T> Pure<ObservableKind.µ> unit(){
            return General.<ObservableKind.µ,T>unit(Instances::of);
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
        public static <T,R> Applicative<ObservableKind.µ> zippingApplicative(){
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
        public static <T,R> Monad<ObservableKind.µ> monad(){

            BiFunction<Higher<ObservableKind.µ,T>,Function<? super T, ? extends Higher<ObservableKind.µ,R>>,Higher<ObservableKind.µ,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
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
        public static <T,R> MonadZero<ObservableKind.µ> monadZero(){
            BiFunction<Higher<ObservableKind.µ,T>,Predicate<? super T>,Higher<ObservableKind.µ,T>> filter = Instances::filter;
            Supplier<Higher<ObservableKind.µ, T>> zero = ()-> ObservableKind.widen(Observable.empty());
            return General.<ObservableKind.µ,T,R>monadZero(monad(), zero,filter);
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
        public static <T> MonadPlus<ObservableKind.µ> monadPlus(){
            Monoid<ObservableKind<T>> m = Monoid.of(ObservableKind.widen(Observable.<T>empty()), Instances::concat);
            Monoid<Higher<ObservableKind.µ,T>> m2= (Monoid)m;
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
        public static <T> MonadPlus<ObservableKind.µ> monadPlus(Monoid<ObservableKind<T>> m){
            Monoid<Higher<ObservableKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<ObservableKind.µ> traverse(){
            BiFunction<Applicative<C2>,ObservableKind<Higher<C2, T>>,Higher<C2, ObservableKind<T>>> sequenceFn = (ap, observable) -> {

                Higher<C2,ObservableKind<T>> identity = ap.unit(ObservableKind.widen(Observable.empty()));

                BiFunction<Higher<C2,ObservableKind<T>>,Higher<C2,T>,Higher<C2,ObservableKind<T>>> combineToObservable =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> ObservableKind.widen(Observable.concat(ObservableKind.narrow(a),Observable.just(b)))),acc,next);

                BinaryOperator<Higher<C2,ObservableKind<T>>> combineObservables = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return ObservableKind.widen(Observable.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromPublisher(observable).reduce(identity,
                        combineToObservable,
                        combineObservables);


            };
            BiFunction<Applicative<C2>,Higher<ObservableKind.µ,Higher<C2, T>>,Higher<C2, Higher<ObservableKind.µ,T>>> sequenceNarrow  =
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
        public static <T> Foldable<ObservableKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<ObservableKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromPublisher(ObservableKind.narrowK(l)).foldRight(m);
            BiFunction<Monoid<T>,Higher<ObservableKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromPublisher(ObservableKind.narrowK(l)).reduce(m);
            return General.foldable(foldRightFn, foldLeftFn);
        }

        private static  <T> ObservableKind<T> concat(ObservableKind<T> l1, ObservableKind<T> l2){
            return ObservableKind.widen(Observable.concat(l1.narrow(),l2.narrow()));
        }
        private <T> ObservableKind<T> of(T value){
            return ObservableKind.widen(Observable.just(value));
        }
        private static <T,R> ObservableKind<R> ap(ObservableKind<Function< T, R>> lt, ObservableKind<T> observable){
            return ObservableKind.widen(lt.zipWith(observable.narrow(),(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<ObservableKind.µ,R> flatMap(Higher<ObservableKind.µ,T> lt, Function<? super T, ? extends  Higher<ObservableKind.µ,R>> fn){
            io.reactivex.functions.Function<? super T, ? extends  Observable<R>> f = t->fn.andThen(ObservableKind::narrow).apply(t);

            return ObservableKind.widen(ObservableKind.narrowK(lt)
                    .flatMap(f));
        }
        private static <T,R> ObservableKind<R> map(ObservableKind<T> lt, Function<? super T, ? extends R> fn){
            return ObservableKind.widen(lt.map(in->fn.apply(in)));
        }
        private static <T> ObservableKind<T> filter(Higher<ObservableKind.µ,T> lt, Predicate<? super T> fn){
            return ObservableKind.widen(ObservableKind.narrow(lt).filter(in->fn.test(in)));
        }
    }

}
