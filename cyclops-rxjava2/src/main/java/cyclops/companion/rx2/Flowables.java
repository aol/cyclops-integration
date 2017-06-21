package cyclops.companion.rx2;

import com.aol.cyclops.rx2.adapter.FlowableReactiveSeq;
import com.aol.cyclops.rx2.hkt.FlowableKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;

import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.flowable;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.StreamT;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

import java.time.Duration;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Companion class for working with Reactor Flowable types
 * 
 * @author johnmcclean
 *
 */
@UtilityClass
public class Flowables {

    public static <T> Flowable<T> raw(AnyM<flowable,T> anyM){
        return flowable(anyM);
    }
    public static <T,W extends WitnessType<W>> AnyM<W,Flowable<T>> fromStream(AnyM<W,Stream<T>> anyM){
        return anyM.map(s->flowableFrom(ReactiveSeq.fromStream(s)));
    }
    public static <T> Flowable<T> narrow(Flowable<? extends T> observable) {
        return (Flowable<T>)observable;
    }
    public static  <T> Flowable<T> flowableFrom(ReactiveSeq<T> stream){

        return stream.visit(sync->Flowable.fromIterable(stream),
                            rs->Flowable.fromPublisher(stream),
                            async-> Observables.fromStream(stream).toFlowable(BackpressureStrategy.BUFFER));


    }

    public static <W extends WitnessType<W>,T> StreamT<W,T> flowablify(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        AnyM<W, ReactiveSeq<T>> flowableM = anyM.map(s -> {
            if (s instanceof FlowableReactiveSeq) {
                return (FlowableReactiveSeq)s;
            }
            if(s instanceof ReactiveSeq){
            return ((ReactiveSeq<T>)s).visit(sync->new FlowableReactiveSeq<T>(Flowable.fromIterable(sync)),
                            rs->new FlowableReactiveSeq<T>(Flowable.fromPublisher(rs)),
                            async ->new FlowableReactiveSeq<T>(Observables.fromStream(async).toFlowable(BackpressureStrategy.BUFFER)));
            }
             return new FlowableReactiveSeq<T>(Flowable.fromIterable(ReactiveSeq.fromStream(s)));
        });
        StreamT<W, T> res = StreamT.of(flowableM);
        return res;
    }

    public static <W extends WitnessType<W>,T,R> R nestedFlowable(StreamT<W,T> nested, Function<? super AnyM<W,Flowable<T>>,? extends R> mapper){
        return mapper.apply(nestedFlowable(nested));
    }
    public static <W extends WitnessType<W>,T> AnyM<W,Flowable<T>> nestedFlowable(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        return anyM.map(s->{
            if(s instanceof FlowableReactiveSeq){
                return ((FlowableReactiveSeq)s).getFlowable();
            }
            if(s instanceof ReactiveSeq){
                ReactiveSeq<T> r = (ReactiveSeq<T>)s;
                return r.visit(sync->Flowable.fromIterable(sync),rs->Flowable.fromPublisher((Publisher)s),
                        async->Flowable.fromPublisher(async));
            }
            if(s instanceof Publisher){
                return Flowable.<T>fromPublisher((Publisher)s);
            }
            return Flowable.fromIterable(ReactiveSeq.fromStream(s));
        });
    }

    public static <W extends WitnessType<W>,T> StreamT<W,T> liftM(AnyM<W,Flowable<T>> nested){
        AnyM<W, ReactiveSeq<T>> monad = nested.map(s -> new FlowableReactiveSeq<T>(s));
        return StreamT.of(monad);
    }

    public static <T> ReactiveSeq<T> reactiveSeq(Flowable<T> flowable){
        return new FlowableReactiveSeq<>(flowable);
    }

    public static <T> ReactiveSeq<T> reactiveSeq(Publisher<T> flowable){
        return new FlowableReactiveSeq<>(Flowable.fromPublisher(flowable));
    }

    public static ReactiveSeq<Integer> range(int start, int end){
       return reactiveSeq(Flowable.range(start,end));
    }
    public static <T> ReactiveSeq<T> of(T... data) {
        return reactiveSeq(Flowable.fromArray(data));
    }
    public static  <T> ReactiveSeq<T> of(T value){
        return reactiveSeq(Flowable.just(value));
    }

    public static <T> ReactiveSeq<T> ofNullable(T nullable){
        if(nullable==null){
            return empty();
        }
        return of(nullable);
    }



    public static <T> ReactiveSeq<T> empty() {
        return reactiveSeq(Flowable.empty());
    }


    public static <T> ReactiveSeq<T> error(Throwable error) {
        return reactiveSeq(Flowable.error(error));
    }







    public static <T> ReactiveSeq<T> from(Publisher<? extends T> source) {
       return reactiveSeq(Flowable.fromPublisher(source));
    }


    public static <T> ReactiveSeq<T> fromIterable(Iterable<? extends T> it) {
        return reactiveSeq(Flowable.fromIterable(it));
    }


    public static <T> ReactiveSeq<T> fromStream(Stream<? extends T> s) {
        return reactiveSeq(flowableFrom(ReactiveSeq.fromStream((Stream<T>)s)));
    }








    @SafeVarargs
    public static <T> ReactiveSeq<T> just(T... data) {
        return reactiveSeq(Flowable.fromArray(data));
    }


    public static <T> ReactiveSeq<T> just(T data) {
        return reactiveSeq(Flowable.just(data));
    }


    /**
     * Construct an AnyM type from a Flowable. This allows the Flowable to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     * 
     * <pre>
     * {@code 
     *    
     *    AnyMSeq<Integer> flowable = Flowables.anyM(Flowable.just(1,2,3));
     *    AnyMSeq<Integer> transformedFlowable = myGenericOperation(flowable);
     *    
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     * 
     * @param flowable To wrap inside an AnyM
     * @return AnyMSeq wrapping a flowable
     */
    public static <T> AnyMSeq<flowable,T> anyM(Flowable<T> flowable) {
        return AnyM.ofSeq(reactiveSeq(flowable), Rx2Witness.flowable.INSTANCE);
    }

    public static <T> Flowable<T> flowable(AnyM<flowable,T> flowable) {

        FlowableReactiveSeq<T> flowableSeq = flowable.unwrap();
        return flowableSeq.getFlowable();
    }

    /**
     * Perform a For Comprehension over a Flowable, accepting 3 generating functions. 
     * This results in a four level nested internal iteration over the provided Publishers.
     * 
     *  <pre>
      * {@code
      *    
      *   import static cyclops.companion.reactor.Flowables.forEach4;
      *    
          forEach4(Flowable.range(1,10), 
                  a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                  (a,b) -> Maybe.<Integer>of(a+b),
                  (a,b,c) -> Mono.<Integer>just(a+b+c),
                  Tuple::tuple)
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Flowable
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param value4 Nested publisher
     * @param yieldingFunction  Generates a result per combination
     * @return Flowable with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Flowable<R> forEach4(Flowable<? extends T1> value1,
                                                               Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.flatMap(ina -> {
                Flowable<R2> b = Flowable.fromPublisher(value3.apply(in,ina));
                return b.flatMap(inb -> {
                    Flowable<R3> c = Flowable.fromPublisher(value4.apply(in,ina,inb));
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });


    }

    /**
     * Perform a For Comprehension over a Flowable, accepting 3 generating functions. 
     * This results in a four level nested internal iteration over the provided Publishers. 
     * <pre>
     * {@code
     * 
     *  import static cyclops.companion.reactor.Flowables.forEach4;
     *   
     *  forEach4(Flowable.range(1,10), 
                            a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                            (a,b) -> Maybe.<Integer>just(a+b),
                            (a,b,c) -> Mono.<Integer>just(a+b+c),
                            (a,b,c,d) -> a+b+c+d <100,
                            Tuple::tuple);
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Flowable
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param value4 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Flowable with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Flowable<R> forEach4(Flowable<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.flatMap(ina -> {
                Flowable<R2> b = Flowable.fromPublisher(value3.apply(in,ina));
                return b.flatMap(inb -> {
                    Flowable<R3> c = Flowable.fromPublisher(value4.apply(in,ina,inb));
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    /**
     * Perform a For Comprehension over a Flowable, accepting 2 generating functions. 
     * This results in a three level nested internal iteration over the provided Publishers. 
     * 
     * <pre>
     * {@code 
     * 
     * import static cyclops.companion.reactor.Flowables.forEach;
     * 
     * forEach(Flowable.range(1,10), 
                            a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                            (a,b) -> Maybe.<Integer>of(a+b),
                            Tuple::tuple);
     * 
     * }
     * </pre>
     * 
     * 
     * @param value1 top level Flowable
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return Flowable with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Flowable<R> forEach3(Flowable<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.flatMap(ina -> {
                Flowable<R2> b = Flowable.fromPublisher(value3.apply(in, ina));
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }
        /**
         * Perform a For Comprehension over a Flowable, accepting 2 generating functions.
         * This results in a three level nested internal iteration over the provided Publishers.
         * <pre>
         * {@code
         *
         * import static cyclops.companion.reactor.Flowables.forEach;
         *
         * forEach(Flowable.range(1,10),
                       a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                       (a,b) -> Maybe.<Integer>of(a+b),
                       (a,b,c) ->a+b+c<10,
                       Tuple::tuple).toListX();
         * }
         * </pre>
         *
         * @param value1 top level Flowable
         * @param value2 Nested publisher
         * @param value3 Nested publisher
         * @param filterFunction A filtering function, keeps values where the predicate holds
         * @param yieldingFunction Generates a result per combination
         * @return
         */
    public static <T1, T2, R1, R2, R> Flowable<R> forEach3(Flowable<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
            Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.flatMap(ina -> {
                Flowable<R2> b = Flowable.fromPublisher(value3.apply(in,ina));
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });

    }

    /**
     * Perform a For Comprehension over a Flowable, accepting an additonal generating function. 
     * This results in a two level nested internal iteration over the provided Publishers. 
     * 
     * <pre>
     * {@code 
     * 
     *  import static cyclops.companion.reactor.Flowables.forEach;
     *  forEach(Flowable.range(1, 10), i -> Flowable.range(i, 10), Tuple::tuple)
              .subscribe(System.out::println);
              
       //(1, 1)
         (1, 2)
         (1, 3)
         (1, 4)
         ...
     * 
     * }</pre>
     * 
     * @param value1 top level Flowable
     * @param value2 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Flowable<R> forEach(Flowable<? extends T> value1, Function<? super T, Flowable<R1>> value2,
            BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }

    /**
     * 
     * <pre>
     * {@code 
     * 
     *   import static cyclops.companion.reactor.Flowables.forEach;
     * 
     *   forEach(Flowable.range(1, 10), i -> Flowable.range(i, 10),(a,b) -> a>2 && b<10,Tuple::tuple)
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
     * @param value1 top level Flowable
     * @param value2 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Flowable<R> forEach(Flowable<? extends T> value1,
            Function<? super T, ? extends Publisher<R1>> value2,
            BiFunction<? super T, ? super R1, Boolean> filterFunction,
            BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flowable<R1> a = Flowable.fromPublisher(value2.apply(in));
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }


    /**
     * Companion class for creating Type Class instances for working with Flowables
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {


        /**
         *
         * Transform a flowable, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  FlowableKind<Integer> flowable = Flowables.functor().map(i->i*2, FlowableKind.widen(Flowable.of(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Flowables
         * <pre>
         * {@code
         *   FlowableKind<Integer> flowable = Flowables.unit()
        .unit("hello")
        .then(h->Flowables.functor().map((String v) ->v.length(), h))
        .convert(FlowableKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Flowables
         */
        public static <T,R>Functor<FlowableKind.µ> functor(){
            BiFunction<FlowableKind<T>,Function<? super T, ? extends R>,FlowableKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * FlowableKind<String> flowable = Flowables.unit()
        .unit("hello")
        .convert(FlowableKind::narrowK);

        //Flowable.of("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Flowables
         */
        public static <T> Pure<FlowableKind.µ> unit(){
            Function<T, Higher<FlowableKind.µ, T>> unitRef = Instances::of;
            return General.<FlowableKind.µ,T>unit(unitRef);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FlowableKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        Flowables.zippingApplicative()
        .ap(widen(Flowable.of(l1(this::multiplyByTwo))),widen(Flowable.of(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * FlowableKind<Function<Integer,Integer>> flowableFn =Flowables.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(FlowableKind::narrowK);

        FlowableKind<Integer> flowable = Flowables.unit()
        .unit("hello")
        .then(h->Flowables.functor().map((String v) ->v.length(), h))
        .then(h->Flowables.zippingApplicative().ap(flowableFn, h))
        .convert(FlowableKind::narrowK);

        //Flowable.of("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Flowables
         */
        public static <T,R> Applicative<FlowableKind.µ> zippingApplicative(){
            BiFunction<FlowableKind< Function<T, R>>,FlowableKind<T>,FlowableKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FlowableKind.widen;
         * FlowableKind<Integer> flowable  = Flowables.monad()
        .flatMap(i->widen(FlowableX.range(0,i)), widen(Flowable.of(1,2,3)))
        .convert(FlowableKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    FlowableKind<Integer> flowable = Flowables.unit()
        .unit("hello")
        .then(h->Flowables.monad().flatMap((String v) ->Flowables.unit().unit(v.length()), h))
        .convert(FlowableKind::narrowK);

        //Flowable.of("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Flowables
         */
        public static <T,R> Monad<FlowableKind.µ> monad(){

            BiFunction<Higher<FlowableKind.µ,T>,Function<? super T, ? extends Higher<FlowableKind.µ,R>>,Higher<FlowableKind.µ,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  FlowableKind<String> flowable = Flowables.unit()
        .unit("hello")
        .then(h->Flowables.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(FlowableKind::narrowK);

        //Flowable.of("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<FlowableKind.µ> monadZero(){
            BiFunction<Higher<FlowableKind.µ,T>,Predicate<? super T>,Higher<FlowableKind.µ,T>> filter = Instances::filter;
            Supplier<Higher<FlowableKind.µ, T>> zero = ()-> FlowableKind.widen(Flowable.empty());
            return General.<FlowableKind.µ,T,R>monadZero(monad(), zero,filter);
        }
        /**
         * <pre>
         * {@code
         *  FlowableKind<Integer> flowable = Flowables.<Integer>monadPlus()
        .plus(FlowableKind.widen(Flowable.of()), FlowableKind.widen(Flowable.of(10)))
        .convert(FlowableKind::narrowK);
        //Flowable.of(10))
         *
         * }
         * </pre>
         * @return Type class for combining Flowables by concatenation
         */
        public static <T> MonadPlus<FlowableKind.µ> monadPlus(){
            Monoid<FlowableKind<T>> m = Monoid.of(FlowableKind.widen(Flowable.<T>empty()), Instances::concat);
            Monoid<Higher<FlowableKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<FlowableKind<Integer>> m = Monoid.of(FlowableKind.widen(Flowable.of()), (a,b)->a.isEmpty() ? b : a);
        FlowableKind<Integer> flowable = Flowables.<Integer>monadPlus(m)
        .plus(FlowableKind.widen(Flowable.of(5)), FlowableKind.widen(Flowable.of(10)))
        .convert(FlowableKind::narrowK);
        //Flowable.of(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Flowables
         * @return Type class for combining Flowables
         */
        public static <T> MonadPlus<FlowableKind.µ> monadPlus(Monoid<FlowableKind<T>> m){
            Monoid<Higher<FlowableKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<FlowableKind.µ> traverse(){
            BiFunction<Applicative<C2>,FlowableKind<Higher<C2, T>>,Higher<C2, FlowableKind<T>>> sequenceFn = (ap, flowable) -> {

                Higher<C2,FlowableKind<T>> identity = ap.unit(FlowableKind.widen(Flowable.empty()));

                BiFunction<Higher<C2,FlowableKind<T>>,Higher<C2,T>,Higher<C2,FlowableKind<T>>> combineToFlowable =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> FlowableKind.widen(Flowable.concat(a,Flowable.just(b)))),acc,next);

                BinaryOperator<Higher<C2,FlowableKind<T>>> combineFlowables = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return FlowableKind.widen(Flowable.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromPublisher(flowable).reduce(identity,
                        combineToFlowable,
                        combineFlowables);


            };
            BiFunction<Applicative<C2>,Higher<FlowableKind.µ,Higher<C2, T>>,Higher<C2, Higher<FlowableKind.µ,T>>> sequenceNarrow  =
                    (a,b) -> FlowableKind.widen2(sequenceFn.apply(a, FlowableKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Flowables.foldable()
        .foldLeft(0, (a,b)->a+b, FlowableKind.widen(Flowable.of(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<FlowableKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<FlowableKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromPublisher(FlowableKind.narrow(l)).foldRight(m);
            BiFunction<Monoid<T>,Higher<FlowableKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromPublisher(FlowableKind.narrow(l)).reduce(m);
            return General.foldable(foldRightFn, foldLeftFn);
        }

        private static  <T> FlowableKind<T> concat(FlowableKind<T> l1, FlowableKind<T> l2){
            return FlowableKind.widen(Flowable.concat(l1,l2));
        }
        private static <T> FlowableKind<T> of(T value){
            return FlowableKind.widen(Flowable.just(value));
        }
        private static <T,R> FlowableKind<R> ap(FlowableKind<Function< T, R>> lt, FlowableKind<T> flowable){
            return FlowableKind.widen(lt.zipWith(flowable,(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<FlowableKind.µ,R> flatMap(Higher<FlowableKind.µ,T> lt, Function<? super T, ? extends  Higher<FlowableKind.µ,R>> fn){
            return FlowableKind.widen(FlowableKind.narrowK(lt).flatMap(Functions.rxFunction(fn.andThen(FlowableKind::narrowK))));
        }
        private static <T,R> FlowableKind<R> map(FlowableKind<T> lt, Function<? super T, ? extends R> fn){
            return FlowableKind.widen(lt.map(Functions.rxFunction(fn)));
        }
        private static <T> FlowableKind<T> filter(Higher<FlowableKind.µ,T> lt, Predicate<? super T> fn){
            return FlowableKind.widen(FlowableKind.narrow(lt).filter(Functions.rxPredicate(fn)));
        }
    }


}
