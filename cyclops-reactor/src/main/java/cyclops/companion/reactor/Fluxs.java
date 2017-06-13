package cyclops.companion.reactor;

import com.aol.cyclops.reactor.adapter.FluxReactiveSeq;
import com.aol.cyclops2.internal.stream.ReactiveStreamX;
import cyclops.monads.ReactorWitness;
import cyclops.monads.ReactorWitness.flux;
import com.aol.cyclops.reactor.hkt.FluxKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.StreamT;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;
import reactor.core.publisher.*;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Companion class for working with Reactor Flux types
 * 
 * @author johnmcclean
 *
 */
@UtilityClass
public class Fluxs {

    public static <T> Flux<T> raw(AnyM<flux,T> anyM){
        return ReactorWitness.flux(anyM);
    }
    public static <T,W extends WitnessType<W>> AnyM<W,Flux<T>> fromStream(AnyM<W,Stream<T>> anyM){
        return anyM.map(s->fluxFrom(ReactiveSeq.fromStream(s)));
    }
    public static <T> Flux<T> narrow(Flux<? extends T> observable) {
        return (Flux<T>)observable;
    }
    public static  <T> Flux<T> fluxFrom(ReactiveSeq<T> stream){

        return stream.visit(sync->Flux.fromStream(stream),rs->Flux.from(stream),async->Flux.from(stream));


    }
    public static <W extends WitnessType<W>,T> StreamT<W,T> fluxify(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        AnyM<W, ReactiveSeq<T>> flowableM = anyM.map(s -> {
            if (s instanceof FluxReactiveSeq) {
                return (FluxReactiveSeq)s;
            }
            if(s instanceof ReactiveSeq){
                return ((ReactiveSeq<T>)s).visit(sync->new FluxReactiveSeq<T>(Flux.fromStream(sync)),
                        rs->new FluxReactiveSeq<T>(Flux.from(rs)),
                        async ->new FluxReactiveSeq<T>(Flux.from(async)));
            }
            return new FluxReactiveSeq<T>(Flux.fromStream(s));
        });
        StreamT<W, T> res = StreamT.of(flowableM);
        return res;
    }



    public static <W extends WitnessType<W>,T,R> R nestedFlux(StreamT<W,T> nested, Function<? super AnyM<W,Flux<T>>,? extends R> mapper){
        return mapper.apply(nestedFlux(nested));
    }
    public static <W extends WitnessType<W>,T> AnyM<W,Flux<T>> nestedFlux(StreamT<W,T> nested){
        AnyM<W, Stream<T>> anyM = nested.unwrap();
        return anyM.map(s->{
            if(s instanceof FluxReactiveSeq){
                return ((FluxReactiveSeq)s).getFlux();
            }
            if(s instanceof ReactiveSeq){
                ReactiveSeq<T> r = (ReactiveSeq<T>)s;
                return r.visit(sync->Flux.fromStream(sync),rs->Flux.from((Publisher)s),
                        async->Flux.from(async));
            }
            if(s instanceof Publisher){
                return Flux.from((Publisher)s);
            }
            return Flux.fromStream(s);
        });
    }

    public static <W extends WitnessType<W>,T> StreamT<W,T> liftM(AnyM<W,Flux<T>> nested){
        AnyM<W, ReactiveSeq<T>> monad = nested.map(s -> new FluxReactiveSeq<T>(s));
        return StreamT.of(monad);
    }

    public static <T> ReactiveSeq<T> reactiveSeq(Flux<T> flux){
        return new FluxReactiveSeq<>(flux);
    }

    public static ReactiveSeq<Integer> range(int start, int end){
       return reactiveSeq(Flux.range(start,end));
    }
    public static <T> ReactiveSeq<T> of(T... data) {
        return reactiveSeq(Flux.just(data));
    }
    public static  <T> ReactiveSeq<T> of(T value){
        return reactiveSeq(Flux.just(value));
    }

    public static <T> ReactiveSeq<T> ofNullable(T nullable){
        if(nullable==null){
            return empty();
        }
        return of(nullable);
    }
    public static <T> ReactiveSeq<T> create(Consumer<? super FluxSink<T>> emitter) {
        return reactiveSeq(Flux.create(emitter));
    }


    public static <T> ReactiveSeq<T> create(Consumer<? super FluxSink<T>> emitter, FluxSink.OverflowStrategy backpressure) {
        return reactiveSeq(Flux.create(emitter,backpressure));
    }


    public static <T> ReactiveSeq<T> defer(Supplier<? extends Publisher<T>> supplier) {
        return reactiveSeq(Flux.defer(supplier));
    }

    public static <T> ReactiveSeq<T> empty() {
        return reactiveSeq(Flux.empty());
    }


    public static <T> ReactiveSeq<T> error(Throwable error) {
        return reactiveSeq(Flux.error(error));
    }


    public static <O> ReactiveSeq<O> error(Throwable throwable, boolean whenRequested) {
        return reactiveSeq(Flux.error(throwable,whenRequested));
    }


    @SafeVarargs
    public static <I> ReactiveSeq<I> firstEmitting(Publisher<? extends I>... sources) {
        return reactiveSeq(Flux.firstEmitting(sources));
    }


    public static <I> ReactiveSeq<I> firstEmitting(Iterable<? extends Publisher<? extends I>> sources) {
        return reactiveSeq(Flux.firstEmitting(sources));
    }


    public static <T> ReactiveSeq<T> from(Publisher<? extends T> source) {
       return reactiveSeq(Flux.from(source));
    }


    public static <T> ReactiveSeq<T> fromIterable(Iterable<? extends T> it) {
        return reactiveSeq(Flux.fromIterable(it));
    }


    public static <T> ReactiveSeq<T> fromStream(Stream<? extends T> s) {
        return reactiveSeq(Flux.fromStream(s));
    }


    public static <T> ReactiveSeq<T> generate(Consumer<SynchronousSink<T>> generator) {
        return reactiveSeq(Flux.generate(generator));
    }


    public static <T, S> ReactiveSeq<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator) {
        return reactiveSeq(Flux.generate(stateSupplier,generator));
    }


    public static <T, S> ReactiveSeq<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator, Consumer<? super S> stateConsumer) {
        return reactiveSeq(Flux.generate(stateSupplier,generator,stateConsumer));
    }


    public static ReactiveSeq<Long> interval(Duration period) {
        return reactiveSeq(Flux.interval(period));
    }


    public static ReactiveSeq<Long> interval(Duration delay, Duration period) {
        return reactiveSeq(Flux.interval(delay,period));
    }


    public static ReactiveSeq<Long> intervalMillis(long period) {
        return reactiveSeq(Flux.intervalMillis(period));
    }
    @SafeVarargs
    public static <T> ReactiveSeq<T> just(T... data) {
        return reactiveSeq(Flux.just(data));
    }


    public static <T> ReactiveSeq<T> just(T data) {
        return reactiveSeq(Flux.just(data));
    }


    /**
     * Construct an AnyM type from a Flux. This allows the Flux to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     * 
     * <pre>
     * {@code 
     *    
     *    AnyMSeq<Integer> flux = Fluxs.anyM(Flux.just(1,2,3));
     *    AnyMSeq<Integer> transformedFlux = myGenericOperation(flux);
     *    
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     * 
     * @param flux To wrap inside an AnyM
     * @return AnyMSeq wrapping a flux
     */
    public static <T> AnyMSeq<flux,T> anyM(Flux<T> flux) {
        return AnyM.ofSeq(reactiveSeq(flux), ReactorWitness.flux.INSTANCE);
    }

    public static <T> Flux<T> flux(AnyM<flux,T> flux) {

        FluxReactiveSeq<T> fluxSeq = flux.unwrap();
        return fluxSeq.getFlux();
    }

    /**
     * Perform a For Comprehension over a Flux, accepting 3 generating functions. 
     * This results in a four level nested internal iteration over the provided Publishers.
     * 
     *  <pre>
      * {@code
      *    
      *   import static cyclops.companion.reactor.Fluxs.forEach4;
      *    
          forEach4(Flux.range(1,10), 
                  a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                  (a,b) -> Maybe.<Integer>of(a+b),
                  (a,b,c) -> Mono.<Integer>just(a+b+c),
                  Tuple::tuple)
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Flux
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param value4 Nested publisher
     * @param yieldingFunction  Generates a result per combination
     * @return Flux with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Flux<R> forEach4(Flux<? extends T1> value1,
                                                               Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.flatMap(ina -> {
                Flux<R2> b = Flux.from(value3.apply(in,ina));
                return b.flatMap(inb -> {
                    Flux<R3> c = Flux.from(value4.apply(in,ina,inb));
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });


    }

    /**
     * Perform a For Comprehension over a Flux, accepting 3 generating functions. 
     * This results in a four level nested internal iteration over the provided Publishers. 
     * <pre>
     * {@code
     * 
     *  import static cyclops.companion.reactor.Fluxs.forEach4;
     *   
     *  forEach4(Flux.range(1,10), 
                            a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                            (a,b) -> Maybe.<Integer>just(a+b),
                            (a,b,c) -> Mono.<Integer>just(a+b+c),
                            (a,b,c,d) -> a+b+c+d <100,
                            Tuple::tuple);
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Flux
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param value4 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Flux with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Flux<R> forEach4(Flux<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.flatMap(ina -> {
                Flux<R2> b = Flux.from(value3.apply(in,ina));
                return b.flatMap(inb -> {
                    Flux<R3> c = Flux.from(value4.apply(in,ina,inb));
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    /**
     * Perform a For Comprehension over a Flux, accepting 2 generating functions. 
     * This results in a three level nested internal iteration over the provided Publishers. 
     * 
     * <pre>
     * {@code 
     * 
     * import static cyclops.companion.reactor.Fluxs.forEach;
     * 
     * forEach(Flux.range(1,10), 
                            a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                            (a,b) -> Maybe.<Integer>of(a+b),
                            Tuple::tuple);
     * 
     * }
     * </pre>
     * 
     * 
     * @param value1 top level Flux
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return Flux with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Flux<R> forEach3(Flux<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.flatMap(ina -> {
                Flux<R2> b = Flux.from(value3.apply(in, ina));
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }
        /**
         * Perform a For Comprehension over a Flux, accepting 2 generating functions.
         * This results in a three level nested internal iteration over the provided Publishers.
         * <pre>
         * {@code
         *
         * import static cyclops.companion.reactor.Fluxs.forEach;
         *
         * forEach(Flux.range(1,10),
                       a-> ReactiveSeq.iterate(a,i->i+1).limit(10),
                       (a,b) -> Maybe.<Integer>of(a+b),
                       (a,b,c) ->a+b+c<10,
                       Tuple::tuple).toListX();
         * }
         * </pre>
         *
         * @param value1 top level Flux
         * @param value2 Nested publisher
         * @param value3 Nested publisher
         * @param filterFunction A filtering function, keeps values where the predicate holds
         * @param yieldingFunction Generates a result per combination
         * @return
         */
    public static <T1, T2, R1, R2, R> Flux<R> forEach3(Flux<? extends T1> value1,
            Function<? super T1, ? extends Publisher<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Publisher<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
            Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.flatMap(ina -> {
                Flux<R2> b = Flux.from(value3.apply(in,ina));
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });

    }

    /**
     * Perform a For Comprehension over a Flux, accepting an additonal generating function. 
     * This results in a two level nested internal iteration over the provided Publishers. 
     * 
     * <pre>
     * {@code 
     * 
     *  import static cyclops.companion.reactor.Fluxs.forEach;
     *  forEach(Flux.range(1, 10), i -> Flux.range(i, 10), Tuple::tuple)
              .subscribe(System.out::println);
              
       //(1, 1)
         (1, 2)
         (1, 3)
         (1, 4)
         ...
     * 
     * }</pre>
     * 
     * @param value1 top level Flux
     * @param value2 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Flux<R> forEach(Flux<? extends T> value1, Function<? super T, Flux<R1>> value2,
            BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }

    /**
     * 
     * <pre>
     * {@code 
     * 
     *   import static cyclops.companion.reactor.Fluxs.forEach;
     * 
     *   forEach(Flux.range(1, 10), i -> Flux.range(i, 10),(a,b) -> a>2 && b<10,Tuple::tuple)
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
     * @param value1 top level Flux
     * @param value2 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Flux<R> forEach(Flux<? extends T> value1,
            Function<? super T, ? extends Publisher<R1>> value2,
            BiFunction<? super T, ? super R1, Boolean> filterFunction,
            BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Flux<R1> a = Flux.from(value2.apply(in));
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }


    /**
     * Companion class for creating Type Class instances for working with Fluxs
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {


        /**
         *
         * Transform a flux, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  FluxKind<Integer> flux = Fluxs.functor().map(i->i*2, FluxKind.widen(Flux.of(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Fluxs
         * <pre>
         * {@code
         *   FluxKind<Integer> flux = Fluxs.unit()
        .unit("hello")
        .then(h->Fluxs.functor().map((String v) ->v.length(), h))
        .convert(FluxKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Fluxs
         */
        public static <T,R>Functor<FluxKind.µ> functor(){
            BiFunction<FluxKind<T>,Function<? super T, ? extends R>,FluxKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * FluxKind<String> flux = Fluxs.unit()
        .unit("hello")
        .convert(FluxKind::narrowK);

        //Flux.of("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Fluxs
         */
        public static <T> Pure<FluxKind.µ> unit(){
            Function<T, Higher<FluxKind.µ, T>> unitRef = Instances::of;
            return General.<FluxKind.µ,T>unit(unitRef);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FluxKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        Fluxs.zippingApplicative()
        .ap(widen(Flux.of(l1(this::multiplyByTwo))),widen(Flux.of(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * FluxKind<Function<Integer,Integer>> fluxFn =Fluxs.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(FluxKind::narrowK);

        FluxKind<Integer> flux = Fluxs.unit()
        .unit("hello")
        .then(h->Fluxs.functor().map((String v) ->v.length(), h))
        .then(h->Fluxs.zippingApplicative().ap(fluxFn, h))
        .convert(FluxKind::narrowK);

        //Flux.of("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Fluxs
         */
        public static <T,R> Applicative<FluxKind.µ> zippingApplicative(){
            BiFunction<FluxKind< Function<T, R>>,FluxKind<T>,FluxKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FluxKind.widen;
         * FluxKind<Integer> flux  = Fluxs.monad()
        .flatMap(i->widen(FluxX.range(0,i)), widen(Flux.of(1,2,3)))
        .convert(FluxKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    FluxKind<Integer> flux = Fluxs.unit()
        .unit("hello")
        .then(h->Fluxs.monad().flatMap((String v) ->Fluxs.unit().unit(v.length()), h))
        .convert(FluxKind::narrowK);

        //Flux.of("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Fluxs
         */
        public static <T,R> Monad<FluxKind.µ> monad(){

            BiFunction<Higher<FluxKind.µ,T>,Function<? super T, ? extends Higher<FluxKind.µ,R>>,Higher<FluxKind.µ,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  FluxKind<String> flux = Fluxs.unit()
        .unit("hello")
        .then(h->Fluxs.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(FluxKind::narrowK);

        //Flux.of("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<FluxKind.µ> monadZero(){
            BiFunction<Higher<FluxKind.µ,T>,Predicate<? super T>,Higher<FluxKind.µ,T>> filter = Instances::filter;
            Supplier<Higher<FluxKind.µ, T>> zero = ()-> FluxKind.widen(Flux.empty());
            return General.<FluxKind.µ,T,R>monadZero(monad(), zero,filter);
        }
        /**
         * <pre>
         * {@code
         *  FluxKind<Integer> flux = Fluxs.<Integer>monadPlus()
        .plus(FluxKind.widen(Flux.of()), FluxKind.widen(Flux.of(10)))
        .convert(FluxKind::narrowK);
        //Flux.of(10))
         *
         * }
         * </pre>
         * @return Type class for combining Fluxs by concatenation
         */
        public static <T> MonadPlus<FluxKind.µ> monadPlus(){
            Monoid<FluxKind<T>> m = Monoid.of(FluxKind.widen(Flux.<T>empty()), Instances::concat);
            Monoid<Higher<FluxKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<FluxKind<Integer>> m = Monoid.of(FluxKind.widen(Flux.of()), (a,b)->a.isEmpty() ? b : a);
        FluxKind<Integer> flux = Fluxs.<Integer>monadPlus(m)
        .plus(FluxKind.widen(Flux.of(5)), FluxKind.widen(Flux.of(10)))
        .convert(FluxKind::narrowK);
        //Flux.of(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Fluxs
         * @return Type class for combining Fluxs
         */
        public static <T> MonadPlus<FluxKind.µ> monadPlus(Monoid<FluxKind<T>> m){
            Monoid<Higher<FluxKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<FluxKind.µ> traverse(){
            BiFunction<Applicative<C2>,FluxKind<Higher<C2, T>>,Higher<C2, FluxKind<T>>> sequenceFn = (ap, flux) -> {

                Higher<C2,FluxKind<T>> identity = ap.unit(FluxKind.widen(Flux.empty()));

                BiFunction<Higher<C2,FluxKind<T>>,Higher<C2,T>,Higher<C2,FluxKind<T>>> combineToFlux =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> FluxKind.widen(Flux.concat(a,Flux.just(b)))),acc,next);

                BinaryOperator<Higher<C2,FluxKind<T>>> combineFluxs = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return FluxKind.widen(Flux.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromPublisher(flux).reduce(identity,
                        combineToFlux,
                        combineFluxs);


            };
            BiFunction<Applicative<C2>,Higher<FluxKind.µ,Higher<C2, T>>,Higher<C2, Higher<FluxKind.µ,T>>> sequenceNarrow  =
                    (a,b) -> FluxKind.widen2(sequenceFn.apply(a, FluxKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Fluxs.foldable()
        .foldLeft(0, (a,b)->a+b, FluxKind.widen(Flux.of(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<FluxKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<FluxKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromPublisher(FluxKind.narrow(l)).foldRight(m);
            BiFunction<Monoid<T>,Higher<FluxKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromPublisher(FluxKind.narrow(l)).reduce(m);
            return General.foldable(foldRightFn, foldLeftFn);
        }

        private static  <T> FluxKind<T> concat(FluxKind<T> l1, FluxKind<T> l2){
            return FluxKind.widen(Flux.concat(l1,l2));
        }
        private static <T> FluxKind<T> of(T value){
            return FluxKind.widen(Flux.just(value));
        }
        private static <T,R> FluxKind<R> ap(FluxKind<Function< T, R>> lt, FluxKind<T> flux){
            return FluxKind.widen(lt.zipWith(flux,(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<FluxKind.µ,R> flatMap(Higher<FluxKind.µ,T> lt, Function<? super T, ? extends  Higher<FluxKind.µ,R>> fn){
            return FluxKind.widen(FluxKind.narrowK(lt).flatMap(fn.andThen(FluxKind::narrowK)));
        }
        private static <T,R> FluxKind<R> map(FluxKind<T> lt, Function<? super T, ? extends R> fn){
            return FluxKind.widen(lt.map(fn));
        }
        private static <T> FluxKind<T> filter(Higher<FluxKind.µ,T> lt, Predicate<? super T> fn){
            return FluxKind.widen(FluxKind.narrow(lt).filter(fn));
        }
    }


}
