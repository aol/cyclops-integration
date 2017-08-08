package cyclops.companion.functionaljava;

import com.aol.cyclops.functionaljava.hkt.*;
import cyclops.companion.CompletableFutures;
import cyclops.companion.CompletableFutures.CompletableFutureKind;
import cyclops.companion.Optionals;
import cyclops.companion.Optionals.OptionalKind;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.control.Xor;
import cyclops.conversion.functionaljava.FromJDK;
import cyclops.conversion.functionaljava.FromJooqLambda;
import cyclops.monads.*;
import cyclops.monads.FJWitness.list;
import cyclops.monads.FJWitness.nonEmptyList;
import cyclops.monads.FJWitness.option;
import cyclops.monads.FJWitness.stream;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;

import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.Witness.*;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.*;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import fj.F;
import fj.P2;
import fj.data.*;
import lombok.experimental.UtilityClass;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.aol.cyclops.functionaljava.hkt.StreamKind.widen;


public class Streams {

    public static  <W1,T> Coproduct<W1,stream,T> coproduct(Stream<T> list, InstanceDefinitions<W1> def1){
        return Coproduct.of(Xor.primary(StreamKind.widen(list)),def1, Instances.definitions());
    }
    public static  <W1,T> Coproduct<W1,stream,T> coproduct(InstanceDefinitions<W1> def1,T... values){
        return coproduct(Stream.stream(values),def1);
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,stream,T> xorM(Stream<T> type){
        return XorM.right(anyM(type));
    }
    public static <T> AnyMSeq<stream,T> anyM(Stream<T> option) {
        return AnyM.ofSeq(option, stream.INSTANCE);
    }
    public static  <T,R> Stream<R> tailRec(T initial, Function<? super T, ? extends Stream<? extends Either<T, R>>> fn) {
        Stream<Either<T, R>> next = Stream.stream(Either.left(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.bind(e -> e.either(s -> {
                        newValue[0]=true;
                        return (Stream<Either<T,R>>)fn.apply(s); },
                    p -> {
                        newValue[0]=false;
                        return Stream.stream(e);
                    }));
            if(!newValue[0])
                break;

        }

        return next.filter(Either::isRight).map(e->e.right()
                .iterator()
                .next());
    }
    public static  <T,R> Stream<R> tailRecXor(T initial, Function<? super T, ? extends Stream<? extends Xor<T, R>>> fn) {
        Stream<Xor<T, R>> next = Stream.arrayStream(Xor.secondary(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.bind(e -> e.visit(s -> {
                        newValue[0]=true;
                        return (Stream<Xor<T,R>>)fn.apply(s); },
                    p -> {
                        newValue[0]=false;
                        return Stream.arrayStream(e);
                    }));
            if(!newValue[0])
                break;

        }

        return next.filter(Xor::isPrimary).map(Xor::get);
    }

    public static <T,R> R foldRight(Stream<T> stream,R identity, BiFunction<? super T, ? super R, ? extends R> fn){
        return foldRightRec(stream,Eval.now(identity),(a,b)-> b.map(b2->fn.apply(a,b2))).get();
    }
    private static <T,R> Eval<R> foldRightRec(Stream<T> stream,Eval<R> identity, BiFunction<? super T, ? super Eval<R>, ? extends Eval<R>> fn){

        if(stream.isEmpty())
            return identity;
        else
            return identity.flatMap(i-> fn.apply(stream.head(), foldRightRec(stream.tail()._1(), identity, fn)));
    }
    /**
     * Perform a For Comprehension over a Stream, accepting 3 generating functions.
     * This results in a four level nested internal iteration over the provided Publishers.
     *
     *  <pre>
     * {@code
     *
     *   import static cyclops.Streams.forEach4;
     *
    forEach4(IntStream.range(1,10).boxed(),
    a-> Stream.iterate(a,i->i+1).limit(10),
    (a,b) -> Stream.<Integer>of(a+b),
    (a,b,c) -> Stream.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Stream
     * @param value2 Nested Stream
     * @param value3 Nested Stream
     * @param value4 Nested Stream
     * @param yieldingFunction  Generates a result per combination
     * @return Stream with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Stream<R> forEach4(Stream<? extends T1> value1,
                                                               Function<? super T1, ? extends Stream<R1>> value2,
                                                               BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
                                                               Fn3<? super T1, ? super R1, ? super R2, ? extends Stream<R3>> value4,
                                                               Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.bind(ina -> {
                Stream<R2> b = value3.apply(in,ina);
                return b.bind(inb -> {
                    Stream<R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Stream, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Publishers.
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops2.reactor.Streames.forEach4;
     *
     *  forEach4(IntStream.range(1,10).boxed(),
    a-> Stream.iterate(a,i->i+1).limit(10),
    (a,b) -> Stream.<Integer>just(a+b),
    (a,b,c) -> Stream.<Integer>just(a+b+c),
    (a,b,c,d) -> a+b+c+d <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Stream
     * @param value2 Nested Stream
     * @param value3 Nested Stream
     * @param value4 Nested Stream
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Stream with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Stream<R> forEach4(Stream<? extends T1> value1,
                                                                 Function<? super T1, ? extends Stream<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Stream<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.bind(ina -> {
                Stream<R2> b = value3.apply(in,ina);
                return b.bind(inb -> {
                    Stream<R3> c = value4.apply(in,ina,inb);
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    /**
     * Perform a For Comprehension over a Stream, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Publishers.
     *
     * <pre>
     * {@code
     *
     * import static Streams.forEach3;
     *
     * forEach(IntStream.range(1,10).boxed(),
    a-> Stream.iterate(a,i->i+1).limit(10),
    (a,b) -> Stream.<Integer>of(a+b),
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     *
     * @param value1 top level Stream
     * @param value2 Nested Stream
     * @param value3 Nested Stream
     * @param yieldingFunction Generates a result per combination
     * @return Stream with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Stream<R> forEach3(Stream<? extends T1> value1,
                                                         Function<? super T1, ? extends Stream<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.bind(ina -> {
                Stream<R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });


    }

    /**
     * Perform a For Comprehension over a Stream, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Publishers.
     * <pre>
     * {@code
     *
     * import static Streams.forEach;
     *
     * forEach(IntStream.range(1,10).boxed(),
    a-> Stream.iterate(a,i->i+1).limit(10),
    (a,b) -> Stream.<Integer>of(a+b),
    (a,b,c) ->a+b+c<10,
    Tuple::tuple)
    .toStreamX();
     * }
     * </pre>
     *
     * @param value1 top level Stream
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T1, T2, R1, R2, R> Stream<R> forEach3(Stream<? extends T1> value1,
                                                         Function<? super T1, ? extends Stream<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {


        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.bind(ina -> {
                Stream<R2> b = value3.apply(in,ina);
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });
    }

    /**
     * Perform a For Comprehension over a Stream, accepting an additonal generating function.
     * This results in a two level nested internal iteration over the provided Publishers.
     *
     * <pre>
     * {@code
     *
     *  import static Streams.forEach2;
     *  forEach(IntStream.range(1, 10).boxed(),
     *          i -> Stream.range(i, 10), Tuple::tuple)
    .forEach(System.out::println);

    //(1, 1)
    (1, 2)
    (1, 3)
    (1, 4)
    ...
     *
     * }</pre>
     *
     * @param value1 top level Stream
     * @param value2 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Stream<R> forEach2(Stream<? extends T> value1,
                                                Function<? super T, Stream<R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }

    /**
     *
     * <pre>
     * {@code
     *
     *   import static Streams.forEach2;
     *
     *   forEach(IntStream.range(1, 10).boxed(),
     *           i -> Stream.range(i, 10),
     *           (a,b) -> a>2 && b<10,
     *           Tuple::tuple)
    .forEach(System.out::println);

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
     * @param value1 top level Stream
     * @param value2 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Stream<R> forEach2(Stream<? extends T> value1,
                                                Function<? super T, ? extends Stream<R1>> value2,
                                                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return value1.bind(in -> {

            Stream<R1> a = value2.apply(in);
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });
    }
    public static <T> Active<stream,T> allTypeclasses(Stream<T> array){
        return Active.of(StreamKind.widen(array), Instances.definitions());
    }
    public static <T,W2,R> Nested<stream,W2,R> mapM(Stream<T> array, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Stream<Higher<W2, R>> e = array.map(i->fn.apply(i));
        StreamKind<Higher<W2, R>> lk = widen(e);
        return Nested.of(lk, Streams.Instances.definitions(), defs);
    }

    /**
     * Companion class for creating Type Class instances for working with Streams
     *
     */
    @UtilityClass
    public class Instances {
        public static InstanceDefinitions<stream> definitions() {
            return new InstanceDefinitions<stream>() {

                @Override
                public <T, R> Functor<stream> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<stream> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<stream> applicative() {
                    return Instances.zippingApplicative();
                }

                @Override
                public <T, R> Monad<stream> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<stream>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<stream>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<stream> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> Maybe<MonadPlus<stream>> monadPlus(Monoid<Higher<stream, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<stream> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<stream> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> Maybe<Comonad<stream>> comonad() {
                    return Maybe.none();
                }

                @Override
                public <T> Maybe<Unfoldable<stream>> unfoldable() {
                    return Maybe.just(Instances.unfoldable());
                }
            };
        }

        /**
         *
         * Transform a stream, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  StreamKind<Integer> stream = Streams.functor().map(i->i*2, StreamKind.widen(Arrays.asStream(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Streams
         * <pre>
         * {@code
         *   StreamKind<Integer> stream = Streams.unit()
        .unit("hello")
        .then(h->Streams.functor().map((String v) ->v.length(), h))
        .convert(StreamKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Streams
         */
        public static <T,R>Functor<stream> functor(){
            BiFunction<StreamKind<T>,Function<? super T, ? extends R>,StreamKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * StreamKind<String> stream = Streams.unit()
        .unit("hello")
        .convert(StreamKind::narrowK);

        //Arrays.asStream("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Streams
         */
        public static <T> Pure<stream> unit(){
            return General.<stream,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.StreamKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         * import static java.util.Arrays.asStream;
         *
        Streams.zippingApplicative()
        .ap(widen(asStream(l1(this::multiplyByTwo))),widen(asStream(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * StreamKind<Function<Integer,Integer>> streamFn =Streams.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(StreamKind::narrowK);

        StreamKind<Integer> stream = Streams.unit()
        .unit("hello")
        .then(h->Streams.functor().map((String v) ->v.length(), h))
        .then(h->Streams.zippingApplicative().ap(streamFn, h))
        .convert(StreamKind::narrowK);

        //Arrays.asStream("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Streams
         */
        public static <T,R> Applicative<stream> zippingApplicative(){
            BiFunction<StreamKind< Function<T, R>>,StreamKind<T>,StreamKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.StreamKind.widen;
         * StreamKind<Integer> stream  = Streams.monad()
        .flatMap(i->widen(StreamX.range(0,i)), widen(Arrays.asStream(1,2,3)))
        .convert(StreamKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    StreamKind<Integer> stream = Streams.unit()
        .unit("hello")
        .then(h->Streams.monad().flatMap((String v) ->Streams.unit().unit(v.length()), h))
        .convert(StreamKind::narrowK);

        //Arrays.asStream("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Streams
         */
        public static <T,R> Monad<stream> monad(){

            BiFunction<Higher<stream,T>,Function<? super T, ? extends Higher<stream,R>>,Higher<stream,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  StreamKind<String> stream = Streams.unit()
        .unit("hello")
        .then(h->Streams.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(StreamKind::narrowK);

        //Arrays.asStream("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<stream> monadZero(){

            return General.monadZero(monad(), StreamKind.widen(Stream.stream()));
        }
        /**
         * <pre>
         * {@code
         *  StreamKind<Integer> stream = Streams.<Integer>monadPlus()
        .plus(StreamKind.widen(Arrays.asStream()), StreamKind.widen(Arrays.asStream(10)))
        .convert(StreamKind::narrowK);
        //Arrays.asStream(10))
         *
         * }
         * </pre>
         * @return Type class for combining Streams by concatenation
         */
        public static <T> MonadPlus<stream> monadPlus(){
            Monoid<StreamKind<T>> m = Monoid.of(StreamKind.widen(Stream.stream()), Instances::concat);
            Monoid<Higher<stream,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<StreamKind<Integer>> m = Monoid.of(StreamKind.widen(Arrays.asStream()), (a,b)->a.isEmpty() ? b : a);
        StreamKind<Integer> stream = Streams.<Integer>monadPlus(m)
        .plus(StreamKind.widen(Arrays.asStream(5)), StreamKind.widen(Arrays.asStream(10)))
        .convert(StreamKind::narrowK);
        //Arrays.asStream(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Streams
         * @return Type class for combining Streams
         */
        public static <T> MonadPlus<stream> monadPlusK(Monoid<StreamKind<T>> m){
            Monoid<Higher<stream,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<stream> monadPlus(Monoid<Higher<stream,T>> m){
            Monoid<Higher<stream,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadRec<stream> monadRec(){
            return new MonadRec<stream>() {
                @Override
                public <T, R> Higher<stream, R> tailRec(T initial, Function<? super T, ? extends Higher<stream, ? extends Xor<T, R>>> fn) {
                    return widen(Streams.tailRecXor(initial,fn.andThen(StreamKind::narrow)));
                }
            };
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<stream> traverse(){

            BiFunction<Applicative<C2>,StreamKind<Higher<C2, T>>,Higher<C2, StreamKind<T>>> sequenceFn = (ap, stream) -> {

                Higher<C2,StreamKind<T>> identity = ap.unit(StreamKind.widen(Stream.stream()));

                BiFunction<Higher<C2,StreamKind<T>>,Higher<C2,T>,Higher<C2,StreamKind<T>>> combineToStream =
                        (acc,next) -> ap.apBiFn(ap.unit((a,b) -> StreamKind.widen(StreamKind.narrow(a).cons(b))), acc,next);

                BinaryOperator<Higher<C2,StreamKind<T>>> combineStreams = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> StreamKind.widen(StreamKind.narrow(l1).append(StreamKind.narrow(l2)))),a,b); ;

                return ReactiveSeq.fromIterable(StreamKind.narrow(stream))
                        .reduce(identity,
                                combineToStream,
                                combineStreams);


            };
            BiFunction<Applicative<C2>,Higher<stream,Higher<C2, T>>,Higher<C2, Higher<stream,T>>> sequenceNarrow  =
                    (a,b) -> StreamKind.widen2(sequenceFn.apply(a, StreamKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }


        /**
         *
         * <pre>
         * {@code
         * int sum  = Streams.foldable()
        .foldLeft(0, (a,b)->a+b, StreamKind.widen(Arrays.asStream(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<stream> foldable(){

            return new Foldable<stream>() {
                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<stream, T> ds) {
                    return StreamKind.narrow(ds).foldRight1((a,b)->monoid.apply(a,b),monoid.zero());
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<stream, T> ds) {
                    return StreamKind.narrow(ds).foldLeft((a,b)->monoid.apply(a,b),monoid.zero());
                }

                @Override
                public <T, R> R foldMap(Monoid<R> mb, Function<? super T, ? extends R> fn, Higher<stream, T> nestedA) {
                    return StreamKind.narrow(nestedA).foldLeft((a,b)->mb.apply(a,fn.apply(b)),mb.zero());
                }
            };

        }

        private static  <T> StreamKind<T> concat(StreamKind<T> l1, StreamKind<T> l2){
            return StreamKind.widen(l1.append(StreamKind.narrow(l2)));

        }
        private <T> StreamKind<T> of(T value){
            return StreamKind.widen(Stream.stream(value));
        }
        private static <T,R> StreamKind<R> ap(StreamKind<Function< T, R>> lt, StreamKind<T> stream){

            return StreamKind.widen(lt.zipWith(stream.narrow(),(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<stream,R> flatMap(Higher<stream,T> lt, Function<? super T, ? extends  Higher<stream,R>> fn){
            return StreamKind.widen(StreamKind.narrow(lt).bind(in->fn.andThen(StreamKind::narrow).apply(in)));
        }
        private static <T,R> StreamKind<R> map(StreamKind<T> lt, Function<? super T, ? extends R> fn){
            return StreamKind.widen(StreamKind.narrow(lt).map(in->fn.apply(in)));
        }
        public static Unfoldable<stream> unfoldable(){
            return new Unfoldable<stream>() {
                @Override
                public <R, T> Higher<stream, R> unfold(T b, Function<? super T, Optional<Tuple2<R, T>>> fn) {
                    F<? super T, Option<P2<R, T>>> f = FromJDK.f1(fn.andThen(FromJDK::option).andThen(o ->o.map(FromJooqLambda::tuple)));
                    return StreamKind.widen(Stream.unfold((F<T,Option<P2<R,T>>>)f,b));

                }
            };
        }
    }

    public static interface StreamNested {
        public static <T> Nested<stream,option,T> option(Stream<Option<T>> nested){
            Stream<OptionKind<T>> f = nested.map(OptionKind::widen);
            StreamKind<OptionKind<T>> x = widen(f);
            StreamKind<Higher<option,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(), Options.Instances.definitions());
        }
        public static <L,T> Nested<stream,Higher<FJWitness.either,L>,T> either(Stream<Either<L,T>> nested){
            Stream<EitherKind<L,T>> f = nested.map(EitherKind::widen);
            StreamKind<EitherKind<L,T>> x = widen(f);
            StreamKind<Higher<Higher<FJWitness.either,L>,T>> y = (StreamKind)x;

            return Nested.of(y,Instances.definitions(), Eithers.Instances.definitions());
        }
        public static <T> Nested<stream,nonEmptyList,T> nonEmptyList(Stream<NonEmptyList<T>> nested){
            Stream<NonEmptyListKind<T>> f = nested.map(NonEmptyListKind::widen);
            StreamKind<NonEmptyListKind<T>> x = widen(f);
            StreamKind<Higher<nonEmptyList,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),NonEmptyLists.Instances.definitions());
        }
        public static <T> Nested<stream,list,T> list(Stream<List<T>> nested){
            Stream<ListKind<T>> f = nested.map(ListKind::widen);
            StreamKind<ListKind<T>> x = widen(f);
            StreamKind<Higher<FJWitness.list,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Lists.Instances.definitions());
        }
        public static <T> Nested<stream,stream,T> stream(Stream<Stream<T>> nested){
            Stream<StreamKind<T>> f = nested.map(StreamKind::widen);
            StreamKind<StreamKind<T>> x = widen(f);
            StreamKind<Higher<stream,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Streams.Instances.definitions());
        }

        public static <T> Nested<stream,reactiveSeq,T> reactiveSeq(Stream<ReactiveSeq<T>> nested){
            StreamKind<ReactiveSeq<T>> x = widen(nested);
            StreamKind<Higher<reactiveSeq,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<stream,Witness.maybe,T> maybe(Stream<Maybe<T>> nested){
            StreamKind<Maybe<T>> x = widen(nested);
            StreamKind<Higher<Witness.maybe,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Maybe.Instances.definitions());
        }
        public static <T> Nested<stream,Witness.eval,T> eval(Stream<Eval<T>> nested){
            StreamKind<Eval<T>> x = widen(nested);
            StreamKind<Higher<Witness.eval,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<stream,Witness.future,T> future(Stream<cyclops.async.Future<T>> nested){
            StreamKind<cyclops.async.Future<T>> x = widen(nested);
            StreamKind<Higher<Witness.future,T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<stream,Higher<xor,S>, P> xor(Stream<Xor<S, P>> nested){
            StreamKind<Xor<S, P>> x = widen(nested);
            StreamKind<Higher<Higher<xor,S>, P>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Xor.Instances.definitions());
        }
        public static <S,T> Nested<stream,Higher<reader,S>, T> reader(Stream<Reader<S, T>> nested, S defaultValue){
            StreamKind<Reader<S, T>> x = widen(nested);
            StreamKind<Higher<Higher<reader,S>, T>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <S extends Throwable, P> Nested<stream,Higher<Witness.tryType,S>, P> cyclopsTry(Stream<cyclops.control.Try<P, S>> nested){
            StreamKind<cyclops.control.Try<P, S>> x = widen(nested);
            StreamKind<Higher<Higher<Witness.tryType,S>, P>> y = (StreamKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<stream,optional,T> javaOptional(Stream<Optional<T>> nested){
            Stream<OptionalKind<T>> f = nested.map(o -> OptionalKind.widen(o));
            StreamKind<OptionalKind<T>> x = StreamKind.widen(f);

            StreamKind<Higher<optional,T>> y = (StreamKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <T> Nested<stream,completableFuture,T> javaCompletableFuture(Stream<CompletableFuture<T>> nested){
            Stream<CompletableFutureKind<T>> f = nested.map(o -> CompletableFutureKind.widen(o));
            StreamKind<CompletableFutureKind<T>> x = StreamKind.widen(f);
            StreamKind<Higher<completableFuture,T>> y = (StreamKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<stream,Witness.stream,T> javaStream(Stream<java.util.stream.Stream<T>> nested){
            Stream<cyclops.companion.Streams.StreamKind<T>> f = nested.map(o -> cyclops.companion.Streams.StreamKind.widen(o));
            StreamKind<cyclops.companion.Streams.StreamKind<T>> x = StreamKind.widen(f);
            StreamKind<Higher<Witness.stream,T>> y = (StreamKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedStream{
        public static <T> Nested<reactiveSeq,stream,T> reactiveSeq(ReactiveSeq<Stream<T>> nested){
            ReactiveSeq<Higher<stream,T>> x = nested.map(StreamKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<maybe,stream,T> maybe(Maybe<Stream<T>> nested){
            Maybe<Higher<stream,T>> x = nested.map(StreamKind::widenK);

            return Nested.of(x,Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,stream,T> eval(Eval<Stream<T>> nested){
            Eval<Higher<stream,T>> x = nested.map(StreamKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.future,stream,T> future(cyclops.async.Future<Stream<T>> nested){
            cyclops.async.Future<Higher<stream,T>> x = nested.map(StreamKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<xor,S>,stream, P> xor(Xor<S, Stream<P>> nested){
            Xor<S, Higher<stream,P>> x = nested.map(StreamKind::widenK);

            return Nested.of(x,Xor.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,stream, T> reader(Reader<S, Stream<T>> nested,S defaultValue){

            Reader<S, Higher<stream, T>>  x = nested.map(StreamKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,stream, P> cyclopsTry(cyclops.control.Try<Stream<P>, S> nested){
            cyclops.control.Try<Higher<stream,P>, S> x = nested.map(StreamKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<optional,stream,T> javaOptional(Optional<Stream<T>> nested){
            Optional<Higher<stream,T>> x = nested.map(StreamKind::widenK);

            return  Nested.of(OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<completableFuture,stream,T> javaCompletableFuture(CompletableFuture<Stream<T>> nested){
            CompletableFuture<Higher<stream,T>> x = nested.thenApply(StreamKind::widenK);

            return Nested.of(CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,stream,T> javaStream(java.util.stream.Stream<Stream<T>> nested){
            java.util.stream.Stream<Higher<stream,T>> x = nested.map(StreamKind::widenK);

            return Nested.of(cyclops.companion.Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }

}
