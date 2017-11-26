package cyclops.companion.rx2;

import com.oath.cyclops.rx2.adapter.FlowableReactiveSeq;
import com.oath.cyclops.rx2.hkt.FlowableKind;
import com.oath.cyclops.rx2.hkt.MaybeKind;
import com.oath.cyclops.rx2.hkt.ObservableKind;
import com.oath.cyclops.rx2.hkt.SingleKind;
import com.oath.cyclops.hkt.Higher;
import com.oath.cyclops.types.anyM.AnyMSeq;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals;
import cyclops.companion.Streams;
import cyclops.control.Eval;
import cyclops.control.Option;
import cyclops.control.Reader;
import cyclops.control.Either;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.function.Monoid;
import cyclops.monads.*;

import cyclops.monads.Rx2Witness.flowable;
import cyclops.monads.Rx2Witness.maybe;
import cyclops.monads.Rx2Witness.observable;
import cyclops.monads.Rx2Witness.single;
import cyclops.monads.Witness.eval;
import cyclops.monads.Witness.reactiveSeq;
import cyclops.monads.transformers.StreamT;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.*;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.reactivex.*;
import lombok.experimental.UtilityClass;
import cyclops.data.tuple.Tuple2;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Stream;

import static com.oath.cyclops.rx2.hkt.FlowableKind.widen;


/**
 * Companion class for working with Reactor Flowable types
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Flowables {

    public static  <W1,T> Coproduct<W1,flowable,T> coproduct(Flowable<T> list, InstanceDefinitions<W1> def1){
        return Coproduct.of(Either.right(FlowableKind.widen(list)),def1, Instances.definitions());
    }
    public static  <W1,T> Coproduct<W1,flowable,T> coproduct(InstanceDefinitions<W1> def1,T... values){
        return coproduct(Flowable.fromArray(values),def1);
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,flowable,T> xorM(Flowable<T> type){
        return XorM.right(anyM(type));
    }

    public static  <T,R> Flowable<R> tailRec(T initial, Function<? super T, ? extends Flowable<? extends Either<T, R>>> fn) {
        Flowable<Either<T, R>> next = Flowable.just(Either.left(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.flatMap(e -> e.visit(s -> {
                        newValue[0]=true;
                        return fn.apply(s); },
                    p -> {
                        newValue[0]=false;
                        return Flowable.just(e);
                    }));
            if(!newValue[0])
                break;

        }

        return next.filter(Either::isRight).map(e->e.orElse(null));
    }

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
            Function3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


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
            Function3<? super T1, ? super R1, ? super R2, ? extends Publisher<R3>> value4,
            Function4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
            Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

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
            Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

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
            Function3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
            Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

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
    public static <T> Active<flowable,T> allTypeclasses(Flowable<T> type){
        return Active.of(FlowableKind.widen(type), Flowables.Instances.definitions());
    }
    public static <T,W2,R> Nested<flowable,W2,R> mapM(Flowable<T> type, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Flowable<Higher<W2, R>> e = type.map(x->fn.apply(x));
        FlowableKind<Higher<W2, R>> lk = FlowableKind.widen(e);
        return Nested.of(lk, Flowables.Instances.definitions(), defs);
    }

    /**
     * Companion class for creating Type Class instances for working with Flowables
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<flowable> definitions() {
            return new InstanceDefinitions<flowable>() {


                @Override
                public <T, R> Functor<flowable> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<flowable> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<flowable> applicative() {
                    return Instances.zippingApplicative();
                }

                @Override
                public <T, R> Monad<flowable> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> cyclops.control.Maybe<MonadZero<flowable>> monadZero() {
                    return cyclops.control.Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<flowable>> monadPlus() {
                    return cyclops.control.Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<flowable> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<flowable>> monadPlus(Monoid<Higher<flowable, T>> m) {
                    return cyclops.control.Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<flowable> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<flowable> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> cyclops.control.Maybe<Comonad<flowable>> comonad() {
                    return cyclops.control.Maybe.nothing();
                }

                @Override
                public <T> cyclops.control.Maybe<Unfoldable<flowable>> unfoldable() {
                    return cyclops.control.Maybe.just(Instances.unfoldable());
                }
            };
        }

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
        public static <T,R>Functor<flowable> functor(){
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
        public static <T> Pure<flowable> unit(){
            Function<T, Higher<flowable, T>> unitRef = Instances::of;
            return General.<flowable,T>unit(unitRef);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.oath.cyclops.hkt.jdk.FlowableKind.widen;
         * import static com.oath.cyclops.util.function.Lambda.l1;
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
        public static <T,R> Applicative<flowable> zippingApplicative(){
            BiFunction<FlowableKind< Function<T, R>>,FlowableKind<T>,FlowableKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.oath.cyclops.hkt.jdk.FlowableKind.widen;
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
        public static <T,R> Monad<flowable> monad(){

            BiFunction<Higher<flowable,T>,Function<? super T, ? extends Higher<flowable,R>>,Higher<flowable,R>> flatMap = Instances::flatMap;
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
        public static <T,R> MonadZero<flowable> monadZero(){
            BiFunction<Higher<flowable,T>,Predicate<? super T>,Higher<flowable,T>> filter = Instances::filter;
            Supplier<Higher<flowable, T>> zero = ()-> widen(Flowable.empty());
            return General.<flowable,T,R>monadZero(monad(), zero,filter);
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
        public static <T> MonadPlus<flowable> monadPlus(){
            Monoid<FlowableKind<T>> m = Monoid.of(widen(Flowable.<T>empty()), Instances::concat);
            Monoid<Higher<flowable,T>> m2= (Monoid)m;
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
        public static <T> MonadPlus<flowable> monadPlusK(Monoid<FlowableKind<T>> m){
            Monoid<Higher<flowable,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<flowable> monadPlus(Monoid<Higher<flowable,T>> m){
            Monoid<Higher<flowable,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadRec<flowable> monadRec(){
            return new MonadRec<flowable>() {
                @Override
                public <T, R> Higher<flowable, R> tailRec(T initial, Function<? super T, ? extends Higher<flowable, ? extends Either<T, R>>> fn) {
                    return widen(Flowables.tailRec(initial,fn.andThen(FlowableKind::narrowK).andThen(f->f.narrow())));
                }
            };
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<flowable> traverse(){
            BiFunction<Applicative<C2>,FlowableKind<Higher<C2, T>>,Higher<C2, FlowableKind<T>>> sequenceFn = (ap, flowable) -> {

                Higher<C2,FlowableKind<T>> identity = ap.unit(widen(Flowable.empty()));

                BiFunction<Higher<C2,FlowableKind<T>>,Higher<C2,T>,Higher<C2,FlowableKind<T>>> combineToFlowable =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> widen(Flowable.concat(a,Flowable.just(b)))),acc,next);

                BinaryOperator<Higher<C2,FlowableKind<T>>> combineFlowables = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return widen(Flowable.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromPublisher(flowable).reduce(identity,
                        combineToFlowable,
                        combineFlowables);


            };
            BiFunction<Applicative<C2>,Higher<flowable,Higher<C2, T>>,Higher<C2, Higher<flowable,T>>> sequenceNarrow  =
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
        public static <T> Foldable<flowable> foldable(){
            return new Foldable<flowable>() {
                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<flowable, T> ds) {
                    return ReactiveSeq.fromPublisher(FlowableKind.narrow(ds)).reduce(monoid);
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<flowable, T> ds) {
                    return FlowableKind.narrowK(ds)
                            .narrow()
                            .reduce(monoid.zero(), (a, b) -> monoid.apply(a, b))
                            .blockingGet();
                }

                @Override
                public <T, R> R foldMap(Monoid<R> monoid, Function<? super T, ? extends R> fn, Higher<flowable, T> nestedA) {
                    return FlowableKind.narrowK(nestedA)
                            .narrow()
                            .reduce(monoid.zero(), (a, b) -> monoid.apply(a, fn.apply(b)))
                            .blockingGet();
                }
            };

        }

        private static  <T> FlowableKind<T> concat(FlowableKind<T> l1, FlowableKind<T> l2){
            return widen(Flowable.concat(l1,l2));
        }
        private static <T> FlowableKind<T> of(T value){
            return widen(Flowable.just(value));
        }
        private static <T,R> FlowableKind<R> ap(FlowableKind<Function< T, R>> lt, FlowableKind<T> flowable){
            return widen(lt.zipWith(flowable,(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<flowable,R> flatMap(Higher<flowable,T> lt, Function<? super T, ? extends  Higher<flowable,R>> fn){
            return widen(FlowableKind.narrowK(lt).flatMap(Functions.rxFunction(fn.andThen(FlowableKind::narrowK))));
        }
        private static <T,R> FlowableKind<R> map(FlowableKind<T> lt, Function<? super T, ? extends R> fn){
            return widen(lt.map(Functions.rxFunction(fn)));
        }
        private static <T> FlowableKind<T> filter(Higher<flowable,T> lt, Predicate<? super T> fn){
            return widen(FlowableKind.narrow(lt).filter(Functions.rxPredicate(fn)));
        }
        public static Unfoldable<flowable> unfoldable() {
            return new Unfoldable<flowable>() {

                @Override
                public <R, T> Higher<flowable, R> unfold(T b, Function<? super T, Option<Tuple2<R, T>>> fn) {
                    return widen(Flowables.fromStream(ReactiveSeq.unfold(b, fn)));
                }
            };
        }
    }

    public static interface FlowableNested {

        public static <T> Nested<flowable,flowable,T> flowable(Flowable<Flowable<T>> nested){
            Flowable<FlowableKind<T>> f = nested.map(FlowableKind::widen);
            FlowableKind<FlowableKind<T>> x = widen(f);
            FlowableKind<Higher<flowable,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(), Flowables.Instances.definitions());
        }
        public static <T> Nested<flowable,observable,T> observable(Flowable<Observable<T>> nested){
            Flowable<ObservableKind<T>> f = nested.map(ObservableKind::widen);
            FlowableKind<ObservableKind<T>> x = widen(f);
            FlowableKind<Higher<observable,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(), Observables.Instances.definitions());
        }

        public static <T> Nested<flowable,maybe,T> maybe(Flowable<Maybe<T>> nested){
            Flowable<MaybeKind<T>> f = nested.map(MaybeKind::widen);
            FlowableKind<MaybeKind<T>> x = widen(f);
            FlowableKind<Higher<maybe,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(), Maybes.Instances.definitions());
        }
        public static <T> Nested<flowable,single,T> single(Flowable<Single<T>> nested){
            Flowable<SingleKind<T>> f = nested.map(SingleKind::widen);
            FlowableKind<SingleKind<T>> x = widen(f);
            FlowableKind<Higher<single,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(), Singles.Instances.definitions());
        }
        public static <T> Nested<flowable,reactiveSeq,T> reactiveSeq(Flowable<ReactiveSeq<T>> nested){
            FlowableKind<ReactiveSeq<T>> x = widen(nested);
            FlowableKind<Higher<reactiveSeq,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<flowable,Witness.maybe,T> cyclopsMaybe(Flowable<cyclops.control.Maybe<T>> nested){
            FlowableKind<cyclops.control.Maybe<T>> x = widen(nested);
            FlowableKind<Higher<Witness.maybe,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(), cyclops.control.Maybe.Instances.definitions());
        }
        public static <T> Nested<flowable,eval,T> eval(Flowable<Eval<T>> nested){
            FlowableKind<Eval<T>> x = widen(nested);
            FlowableKind<Higher<eval,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<flowable,Witness.future,T> future(Flowable<cyclops.async.Future<T>> nested){
            FlowableKind<cyclops.async.Future<T>> x = widen(nested);
            FlowableKind<Higher<Witness.future,T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<flowable,Higher<Witness.either,S>, P> xor(Flowable<Either<S, P>> nested){
            FlowableKind<Either<S, P>> x = widen(nested);
            FlowableKind<Higher<Higher<Witness.either,S>, P>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),Either.Instances.definitions());
        }
        public static <S,T> Nested<flowable,Higher<Witness.reader,S>, T> reader(Flowable<Reader<S, T>> nested, S defaultValue){
            FlowableKind<Reader<S, T>> x = widen(nested);
            FlowableKind<Higher<Higher<Witness.reader,S>, T>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <S extends Throwable, P> Nested<flowable,Higher<Witness.tryType,S>, P> cyclopsTry(Flowable<cyclops.control.Try<P, S>> nested){
            FlowableKind<cyclops.control.Try<P, S>> x = widen(nested);
            FlowableKind<Higher<Higher<Witness.tryType,S>, P>> y = (FlowableKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<flowable,Witness.optional,T> javaOptional(Flowable<Optional<T>> nested){
            Flowable<Optionals.OptionalKind<T>> f = nested.map(o -> Optionals.OptionalKind.widen(o));
            FlowableKind<Optionals.OptionalKind<T>> x = FlowableKind.widen(f);

            FlowableKind<Higher<Witness.optional,T>> y = (FlowableKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <T> Nested<flowable,Witness.completableFuture,T> javaCompletableFuture(Flowable<CompletableFuture<T>> nested){
            Flowable<CompletableFutures.CompletableFutureKind<T>> f = nested.map(o -> CompletableFutures.CompletableFutureKind.widen(o));
            FlowableKind<CompletableFutures.CompletableFutureKind<T>> x = FlowableKind.widen(f);
            FlowableKind<Higher<Witness.completableFuture,T>> y = (FlowableKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<flowable,Witness.stream,T> javaStream(Flowable<java.util.stream.Stream<T>> nested){
            Flowable<Streams.StreamKind<T>> f = nested.map(o -> Streams.StreamKind.widen(o));
            FlowableKind<Streams.StreamKind<T>> x = FlowableKind.widen(f);
            FlowableKind<Higher<Witness.stream,T>> y = (FlowableKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedFlowable{
        public static <T> Nested<reactiveSeq,flowable,T> reactiveSeq(ReactiveSeq<Flowable<T>> nested){
            ReactiveSeq<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<Witness.maybe,flowable,T> maybe(cyclops.control.Maybe<Flowable<T>> nested){
            cyclops.control.Maybe<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);

            return Nested.of(x, cyclops.control.Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,flowable,T> eval(Eval<Flowable<T>> nested){
            Eval<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.future,flowable,T> future(cyclops.async.Future<Flowable<T>> nested){
            cyclops.async.Future<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<Witness.either,S>,flowable, P> xor(Either<S, Flowable<P>> nested){
            Either<S, Higher<flowable,P>> x = nested.map(FlowableKind::widenK);

            return Nested.of(x,Either.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<Witness.reader,S>,flowable, T> reader(Reader<S, Flowable<T>> nested, S defaultValue){

            Reader<S, Higher<flowable, T>>  x = nested.map(FlowableKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,flowable, P> cyclopsTry(cyclops.control.Try<Flowable<P>, S> nested){
            cyclops.control.Try<Higher<flowable,P>, S> x = nested.map(FlowableKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.optional,flowable,T> javaOptional(Optional<Flowable<T>> nested){
            Optional<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);

            return  Nested.of(Optionals.OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<Witness.completableFuture,flowable,T> javaCompletableFuture(CompletableFuture<Flowable<T>> nested){
            CompletableFuture<Higher<flowable,T>> x = nested.thenApply(FlowableKind::widenK);

            return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,flowable,T> javaStream(java.util.stream.Stream<Flowable<T>> nested){
            java.util.stream.Stream<Higher<flowable,T>> x = nested.map(FlowableKind::widenK);

            return Nested.of(Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }


}
