package cyclops.companion.rx2;


import com.oath.cyclops.rx2.hkt.FlowableKind;
import com.oath.cyclops.rx2.hkt.MaybeKind;
import com.oath.cyclops.rx2.hkt.ObservableKind;
import com.oath.cyclops.rx2.hkt.SingleKind;
import com.oath.cyclops.hkt.Higher;
import com.oath.cyclops.react.Status;
import com.oath.cyclops.types.MonadicValue;
import com.oath.cyclops.types.Value;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals.OptionalKind;
import cyclops.companion.Streams.StreamKind;
import cyclops.control.Eval;

import cyclops.control.LazyEither;
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
import cyclops.monads.Witness.*;
import cyclops.monads.transformers.rx2.MaybeT;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.*;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oath.cyclops.rx2.hkt.MaybeKind.widen;

/**
 * Companion class for working with RxJava 2 Maybe types
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Maybes {

    public static  <W1,T> Coproduct<W1,maybe,T> coproduct(Maybe<T> list, InstanceDefinitions<W1> def1){
        return Coproduct.of(Either.right(MaybeKind.widen(list)),def1, Instances.definitions());
    }
    public static  <W1,T> Coproduct<W1,maybe,T> coproductNone(InstanceDefinitions<W1> def1){
        return coproduct(Maybe.never(),def1);
    }
    public static  <W1,T> Coproduct<W1,maybe,T> coproductJust(T value,InstanceDefinitions<W1> def1){
        return coproduct(Maybe.just(value),def1);
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,maybe,T> xorM(Maybe<T> type){
        return XorM.right(anyM(type));
    }


    public static <T, R> Maybe< R> tailRec(T initial, Function<? super T, ? extends Maybe<? extends Either<T, R>>> fn) {
        Maybe<? extends Either<T, R>> next[] = new Maybe[1];
        next[0] = Maybe.just(Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].map(p -> p.visit(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false)).blockingGet(false);
        } while (cont);
        return next[0].map(e->e.orElse(null));
    }

    public static <T> Maybe<T> fromPublisher(Publisher<T> maybe){
        return Single.fromPublisher(maybe).toMaybe();
    }

    public static <T> Maybe<T> raw(AnyM<maybe,T> anyM){
        return Rx2Witness.maybe(anyM);
    }


    public static <W extends WitnessType<W>,T> MaybeT<W,T> liftM(AnyM<W,Maybe<T>> nested){
        return MaybeT.of(nested);
    }

    public static <T> Future[] futures(Maybe<T>... futures){

        Future[] array = new Future[futures.length];
        for(int i=0;i<array.length;i++){
            array[i]=future(futures[i]);
        }
        return array;
    }
    public static <T> cyclops.control.Maybe<T> toMaybe(Maybe<T> future){
        return cyclops.control.Maybe.fromPublisher(future.toFlowable());
    }
    public static <T> Maybe<T> fromMaybe(cyclops.control.Maybe<T> future){
        return Single.fromPublisher(future).toMaybe();

    }
    public static <T> Maybe<T> fromValue(MonadicValue<T> future){
        return Single.fromPublisher(future).toMaybe();

    }
    public static <T> Future<T> future(Maybe<T> future){
        return Future.fromPublisher(future.toFlowable());
    }

    public static <R> LazyEither<Throwable,R> either(Maybe<R> either){
        return LazyEither.fromFuture(future(either));

    }

    public static <T> cyclops.control.Maybe<T> maybe(Maybe<T> opt){
        return cyclops.control.Maybe.fromFuture(future(opt));
    }
    public static <T> Eval<T> eval(Maybe<T> opt){
        return Eval.fromFuture(future(opt));
    }

    /**
     * Construct an AnyM type from a Maybe. This allows the Maybe to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     *
     * <pre>
     * {@code
     *
     *    AnyMSeq<Integer> maybe = Fluxs.anyM(Maybe.just(1,2,3));
     *    AnyMSeq<Integer> transformedMaybe = myGenericOperation(maybe);
     *
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     *
     * @param maybe To wrap inside an AnyM
     * @return AnyMSeq wrapping a Maybe
     */
    public static <T> AnyMValue<maybe,T> anyM(Maybe<T> maybe) {
        return AnyM.ofValue(maybe, Rx2Witness.maybe.INSTANCE);
    }



    /**
     * Select the first Maybe to complete
     *
     * @see CompletableFuture#anyOf(CompletableFuture...)
     * @param fts Maybes to race
     * @return First Maybe to complete
     */
    public static <T> Maybe<T> anyOf(Maybe<T>... fts) {


        return Single.fromPublisher(Future.anyOf(futures(fts))).toMaybe();

    }
    /**
     * Wait until all the provided Future's to complete
     *
     * @see CompletableFuture#allOf(CompletableFuture...)
     *
     * @param fts Maybes to  wait on
     * @return Maybe that completes when all the provided Futures Complete. Empty Future result, or holds an Exception
     *         from a provided Future that failed.
     */
    public static <T> Maybe<T> allOf(Maybe<T>... fts) {

        return Single.fromPublisher(Future.allOf(futures(fts))).toMaybe();
    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Maybe<ListX<Integer>> strings = Maybes.quorum(status -> status.getCompleted() >0, Maybe.deferred(()->1),Maybe.empty(),Maybe.empty());


    strings.get().size()
    //1
     *
     * }
     * </pre>
     *
     *
     * @param breakout Predicate that determines whether the block should be
     *            continued or removed
     * @param fts FutureWs to  wait on results from
     * @param errorHandler Consumer to handle any exceptions thrown
     * @return Future which will be populated with a Quorum of results
     */
    @SafeVarargs
    public static <T> Maybe<ListX<T>> quorum(Predicate<Status<T>> breakout, Consumer<Throwable> errorHandler, Maybe<T>... fts) {

        return Single.fromPublisher(Future.quorum(breakout,errorHandler,futures(fts))).toMaybe();


    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Maybe<ListX<Integer>> strings = Maybes.quorum(status -> status.getCompleted() >0, Maybe.deferred(()->1),Maybe.empty(),Maybe.empty());


    strings.get().size()
    //1
     *
     * }
     * </pre>
     *
     *
     * @param breakout Predicate that determines whether the block should be
     *            continued or removed
     * @param fts Maybes to  wait on results from
     * @return Maybe which will be populated with a Quorum of results
     */
    @SafeVarargs
    public static <T> Maybe<ListX<T>> quorum(Predicate<Status<T>> breakout, Maybe<T>... fts) {

        return Single.fromPublisher(Future.quorum(breakout,futures(fts))).toMaybe();


    }
    /**
     * Select the first Future to return with a successful result
     *
     * <pre>
     * {@code
     * Maybe<Integer> ft = Maybe.empty();
      Maybe<Integer> result = Maybes.firstSuccess(Maybe.deferred(()->1),ft);

    ft.complete(10);
    result.get() //1
     * }
     * </pre>
     *
     * @param fts Maybes to race
     * @return First Maybe to return with a result
     */
    @SafeVarargs
    public static <T> Maybe<T> firstSuccess(Maybe<T>... fts) {
        return Single.fromPublisher(Future.firstSuccess(futures(fts))).toMaybe();

    }

    /**
     * Perform a For Comprehension over a Maybe, accepting 3 generating functions.
     * This results in a four level nested internal iteration over the provided Maybes.
     *
     *  <pre>
     * {@code
     *
     *   import static cyclops.companion.reactor.Maybes.forEach4;
     *
          forEach4(Maybe.just(1),
                  a-> Maybe.just(a+1),
                  (a,b) -> Maybe.<Integer>just(a+b),
                  (a,b,c) -> Maybe.<Integer>just(a+b+c),
                  Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Maybe
     * @param value2 Nested Maybe
     * @param value3 Nested Maybe
     * @param value4 Nested Maybe
     * @param yieldingFunction Generates a result per combination
     * @return Maybe with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Maybe<R> forEach4(Maybe<? extends T1> value1,
            Function<? super T1, ? extends Maybe<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Maybe<R2>> value3,
            Function3<? super T1, ? super R1, ? super R2, ? extends Maybe<R3>> value4,
            Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        Maybe<? extends R> res = value1.flatMap(in -> {

            Maybe<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Maybe<R2> b = value3.apply(in, ina);
                return b.flatMap(inb -> {
                    Maybe<R3> c = value4.apply(in, ina, inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
        return  narrow(res);
    }


    /**
     * Perform a For Comprehension over a Maybe, accepting 2 generating functions.
     * This results in a three level nested internal iteration over the provided Maybes.
     *
     *  <pre>
     * {@code
     *
     *   import static cyclops.companion.reactor.Maybes.forEach3;
     *
          forEach3(Maybe.just(1),
                  a-> Maybe.just(a+1),
                  (a,b) -> Maybe.<Integer>just(a+b),
                  Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Maybe
     * @param value2 Nested Maybe
     * @param value3 Nested Maybe
     * @param yieldingFunction Generates a result per combination
     * @return Maybe with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Maybe<R> forEach3(Maybe<? extends T1> value1,
            Function<? super T1, ? extends Maybe<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Maybe<R2>> value3,
            Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {


        Maybe<? extends R> res = value1.flatMap(in -> {

            Maybe<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Maybe<R2> b = value3.apply(in, ina);


                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));


            });

        });
        return narrow(res);

    }



    /**
     * Perform a For Comprehension over a Maybe, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Maybes.
     *
     *  <pre>
     * {@code
     *
     *   import static cyclops.companion.reactor.Maybes.forEach;
     *
          forEach(Maybe.just(1),
                  a-> Maybe.just(a+1),
                  Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Maybe
     * @param value2 Nested Maybe
     * @param yieldingFunction Generates a result per combination
     * @return Maybe with a combined value generated by the yielding function
     */
    public static <T, R1, R> Maybe<R> forEach(Maybe<? extends T> value1,
                                             Function<? super T, Maybe<R1>> value2,
                                             BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        Maybe<R> res = value1.flatMap(in -> {

            Maybe<R1> a = value2.apply(in);
            return a.map(ina -> yieldingFunction.apply(in, ina));


        });


        return narrow(res);

    }



    /**
     * Lazily combine this Maybe with the supplied value via the supplied BiFunction
     *
     * @param maybe Maybe to combine with another value
     * @param app Value to combine with supplied maybe
     * @param fn Combiner function
     * @return Combined Maybe
     */
    public static <T1, T2, R> Maybe<R> combine(Maybe<? extends T1> maybe, Value<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Single.fromPublisher(Future.fromPublisher(maybe.toFlowable())
                                .combine(app, fn)).toMaybe());
    }

    /**
     * Lazily combine this Maybe with the supplied Maybe via the supplied BiFunction
     *
     * @param maybe Maybe to combine with another value
     * @param app Maybe to combine with supplied maybe
     * @param fn Combiner function
     * @return Combined Maybe
     */
    public static <T1, T2, R> Maybe<R> combine(Maybe<? extends T1> maybe, Maybe<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Single.fromPublisher(Future.fromPublisher(maybe.toFlowable())
                                .combine(Future.fromPublisher(app.toFlowable()), fn)).toMaybe());
    }

    /**
     * Combine the provided Maybe with the first element (if present) in the provided Iterable using the provided BiFunction
     *
     * @param maybe Maybe to combine with an Iterable
     * @param app Iterable to combine with a Maybe
     * @param fn Combining function
     * @return Combined Maybe
     */
    public static <T1, T2, R> Maybe<R> zip(Maybe<? extends T1> maybe, Iterable<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(Single.fromPublisher(Future.fromPublisher(maybe.toFlowable())
                                .zip(app, fn)).toMaybe());
    }

    /**
     * Combine the provided Maybe with the first element (if present) in the provided Publisher using the provided BiFunction
     *
     * @param maybe  Maybe to combine with a Publisher
     * @param fn Publisher to combine with a Maybe
     * @param app Combining function
     * @return Combined Maybe
     */
    public static <T1, T2, R> Maybe<R> zip(Maybe<? extends T1> maybe, BiFunction<? super T1, ? super T2, ? extends R> fn,
            Publisher<? extends T2> app) {
        Maybe<R> res = narrow(Single.fromPublisher(Future.fromPublisher(maybe.toFlowable()).zipP(app, fn)).toMaybe());
        return res;
    }


    /**
     * Construct a Maybe from Iterable by taking the first value from Iterable
     *
     * @param t Iterable to populate Maybe from
     * @return Maybe containing first element from Iterable (or empty Maybe)
     */
    public static <T> Maybe<T> fromIterable(Iterable<T> t) {
        return narrow(Single.fromPublisher(Future.fromIterable(t)).toMaybe());
    }

    /**
     * Get an Iterator for the value (if any) in the provided Maybe
     *
     * @param pub Maybe to get Iterator for
     * @return Iterator over Maybe value
     */
    public static <T> Iterator<T> iterator(Maybe<T> pub) {
        return pub.toFlowable().blockingIterable().iterator();

    }

    public static <R> Maybe<R> narrow(Maybe<? extends R> apply) {
        return (Maybe<R>)apply;
    }

    public static <T> Active<maybe,T> allTypeclasses(Maybe<T> type){
        return Active.of(widen(type), Maybes.Instances.definitions());
    }
    public static <T,W2,R> Nested<maybe,W2,R> mapM(Maybe<T> type, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Maybe<Higher<W2, R>> e = type.map(x->fn.apply(x));
        MaybeKind<Higher<W2, R>> lk = MaybeKind.widen(e);
        return Nested.of(lk, Maybes.Instances.definitions(), defs);
    }
    /**
     * Companion class for creating Type Class instances for working with Maybes
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<maybe> definitions() {
            return new InstanceDefinitions<maybe>() {

                @Override
                public <T, R> Functor<maybe> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<maybe> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<maybe> applicative() {
                    return Instances.applicative();
                }

                @Override
                public <T, R> Monad<maybe> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> cyclops.control.Maybe<MonadZero<maybe>> monadZero() {
                    return cyclops.control.Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<maybe>> monadPlus() {
                    return cyclops.control.Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<maybe> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<maybe>> monadPlus(Monoid<Higher<maybe, T>> m) {
                    return cyclops.control.Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<maybe> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<maybe> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> cyclops.control.Maybe<Comonad<maybe>> comonad() {
                    return cyclops.control.Maybe.just(Instances.comonad());
                }

                @Override
                public <T> cyclops.control.Maybe<Unfoldable<maybe>> unfoldable() {
                    return cyclops.control.Maybe.nothing();
                }
            };
        }
        /**
         *
         * Transform a Maybe, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  MaybeKind<Integer> future = Maybes.functor().map(i->i*2, MaybeKind.widen(Maybe.just(3));
         *
         *  //[6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Maybes
         * <pre>
         * {@code
         *   MaybeKind<Integer> ft = Maybes.unit()
        .unit("hello")
        .then(h->Maybes.functor().map((String v) ->v.length(), h))
        .convert(MaybeKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Maybes
         */
        public static <T,R>Functor<maybe> functor(){
            BiFunction<MaybeKind<T>,Function<? super T, ? extends R>,MaybeKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * MaybeKind<String> ft = Maybes.unit()
        .unit("hello")
        .convert(MaybeKind::narrowK);

        //Maybe["hello"]
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Maybes
         */
        public static <T> Pure<maybe> unit(){
            return General.<maybe,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.oath.cyclops.hkt.jdk.MaybeKind.widen;
         * import static com.oath.cyclops.util.function.Lambda.l1;
         * import static java.util.Arrays.asMaybe;
         *
        Maybes.applicative()
        .ap(widen(asMaybe(l1(this::multiplyByTwo))),widen(Maybe.just(3)));
         *
         * //[6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * MaybeKind<Function<Integer,Integer>> ftFn =Maybes.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(MaybeKind::narrowK);

        MaybeKind<Integer> ft = Maybes.unit()
        .unit("hello")
        .then(h->Maybes.functor().map((String v) ->v.length(), h))
        .then(h->Maybes.applicative().ap(ftFn, h))
        .convert(MaybeKind::narrowK);

        //Maybe.just("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Maybes
         */
        public static <T,R> Applicative<maybe> applicative(){
            BiFunction<MaybeKind< Function<T, R>>,MaybeKind<T>,MaybeKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.oath.cyclops.hkt.jdk.MaybeKind.widen;
         * MaybeKind<Integer> ft  = Maybes.monad()
        .flatMap(i->widen(Maybe.just(i)), widen(Maybe.just(3)))
        .convert(MaybeKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    MaybeKind<Integer> ft = Maybes.unit()
        .unit("hello")
        .then(h->Maybes.monad().flatMap((String v) ->Maybes.unit().unit(v.length()), h))
        .convert(MaybeKind::narrowK);

        //Maybe.just("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Maybes
         */
        public static <T,R> Monad<maybe> monad(){

            BiFunction<Higher<maybe,T>,Function<? super T, ? extends Higher<maybe,R>>,Higher<maybe,R>> flatMap = Instances::flatMap;
            return General.monad(applicative(), flatMap);
        }
        public static <T> MonadRec<maybe> monadRec(){
            return new MonadRec<maybe>() {
                @Override
                public <T, R> Higher<maybe, R> tailRec(T initial, Function<? super T, ? extends Higher<maybe, ? extends Either<T, R>>> fn) {
                    return MaybeKind.widen(Maybes.tailRec(initial,fn.andThen(MaybeKind::narrowK).andThen(m->m.narrow())));
                };
            };
        }
        /**
         *
         * <pre>
         * {@code
         *  MaybeKind<String> ft = Maybes.unit()
        .unit("hello")
        .then(h->Maybes.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(MaybeKind::narrowK);

        //Maybe.just("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<maybe> monadZero(){

            return General.monadZero(monad(), MaybeKind.empty());
        }
        /**
         * Combines Maybes by selecting the first result returned
         *
         * <pre>
         * {@code
         *  MaybeKind<Integer> ft = Maybes.<Integer>monadPlus()
        .plus(MaybeKind.widen(Maybe.empty()), MaybeKind.widen(Maybe.just(10)))
        .convert(MaybeKind::narrowK);
        //Maybe.empty()
         *
         * }
         * </pre>
         * @return Type class for combining Maybes by concatenation
         */
        public static <T> MonadPlus<maybe> monadPlus(){


            Monoid<MaybeKind<T>> m = Monoid.of(MaybeKind.<T>widen(Maybe.empty()),
                    (f,g)-> widen(Maybe.ambArray(f.narrow(),g.narrow())));

            Monoid<Higher<maybe,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<MaybeKind<Integer>> m = Monoid.of(MaybeKind.widen(Arrays.asMaybe()), (a,b)->a.isEmpty() ? b : a);
        MaybeKind<Integer> ft = Maybes.<Integer>monadPlus(m)
        .plus(MaybeKind.widen(Arrays.asMaybe(5)), MaybeKind.widen(Arrays.asMaybe(10)))
        .convert(MaybeKind::narrowK);
        //Arrays.asMaybe(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Maybes
         * @return Type class for combining Maybes
         */
        public static <T> MonadPlus<maybe> monadPlusK(Monoid<MaybeKind<T>> m){
            Monoid<Higher<maybe,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<maybe> monadPlus(Monoid<Higher<maybe,T>> m){
            Monoid<Higher<maybe,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<maybe> traverse(){

            return General.traverseByTraverse(applicative(), Instances::traverseA);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Maybes.foldable()
        .foldLeft(0, (a,b)->a+b, MaybeKind.widen(Arrays.asMaybe(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<maybe> foldable(){
            return new Foldable<maybe>() {
                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<maybe, T> ds) {
                    return monoid.apply(monoid.zero(),MaybeKind.narrow(ds).blockingGet());
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<maybe, T> ds) {
                    return monoid.apply(monoid.zero(),MaybeKind.narrow(ds).blockingGet());
                }

                @Override
                public <T, R> R foldMap(Monoid<R> mb, Function<? super T, ? extends R> fn, Higher<maybe, T> nestedA) {
                    return mb.apply(mb.zero(),MaybeKind.narrow(nestedA).map(a->fn.apply(a)).blockingGet());
                }
            };

        }
        public static <T> Comonad<maybe> comonad(){
            Function<? super Higher<maybe, T>, ? extends T> extractFn = maybe -> maybe.convert(MaybeKind::narrow).blockingGet();
            return General.comonad(functor(), unit(), extractFn);
        }

        private static <T> MaybeKind<T> of(T value){
            return widen(Maybe.just(value));
        }
        private static <T,R> MaybeKind<R> ap(MaybeKind<Function< T, R>> lt, MaybeKind<T> list){


            return widen(Maybes.combine(lt.narrow(),list.narrow(), (a, b)->a.apply(b)));

        }
        private static <T,R> Higher<maybe,R> flatMap(Higher<maybe,T> lt, Function<? super T, ? extends  Higher<maybe,R>> fn){
            return widen(MaybeKind.narrow(lt).flatMap(Functions.rxFunction(fn.andThen(MaybeKind::narrow))));
        }
        private static <T,R> MaybeKind<R> map(MaybeKind<T> lt, Function<? super T, ? extends R> fn){
            return widen(lt.narrow().map(Functions.rxFunction(fn)));
        }


        private static <C2,T,R> Higher<C2, Higher<maybe, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                            Higher<maybe, T> ds){
            Maybe<T> future = MaybeKind.narrow(ds);
            return applicative.map(MaybeKind::just, fn.apply(future.blockingGet()));
        }

    }
    public static interface MaybeNested {

        public static <T> Nested<maybe,observable,T> observable(Maybe<Observable<T>> nested){
            Maybe<ObservableKind<T>> f = nested.map(ObservableKind::widen);
            MaybeKind<ObservableKind<T>> x = widen(f);
            MaybeKind<Higher<observable,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(), Observables.Instances.definitions());
        }
        public static <T> Nested<maybe,flowable,T> flowable(Maybe<Flowable<T>> nested){
            Maybe<FlowableKind<T>> f = nested.map(FlowableKind::widen);
            MaybeKind<FlowableKind<T>> x = widen(f);
            MaybeKind<Higher<flowable,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(), Flowables.Instances.definitions());
        }

        public static <T> Nested<maybe,maybe,T> maybe(Maybe<Maybe<T>> nested){
            Maybe<MaybeKind<T>> f = nested.map(MaybeKind::widen);
            MaybeKind<MaybeKind<T>> x = widen(f);
            MaybeKind<Higher<maybe,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(), Maybes.Instances.definitions());
        }
        public static <T> Nested<maybe,single,T> single(Maybe<Single<T>> nested){
            Maybe<SingleKind<T>> f = nested.map(SingleKind::widen);
            MaybeKind<SingleKind<T>> x = widen(f);
            MaybeKind<Higher<single,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(), Singles.Instances.definitions());
        }
        public static <T> Nested<maybe,reactiveSeq,T> reactiveSeq(Maybe<ReactiveSeq<T>> nested){
            MaybeKind<ReactiveSeq<T>> x = widen(nested);
            MaybeKind<Higher<reactiveSeq,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<maybe,Witness.maybe,T> cyclopsMaybe(Maybe<cyclops.control.Maybe<T>> nested){
            MaybeKind<cyclops.control.Maybe<T>> x = widen(nested);
            MaybeKind<Higher<Witness.maybe,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(), cyclops.control.Maybe.Instances.definitions());
        }
        public static <T> Nested<maybe,eval,T> eval(Maybe<Eval<T>> nested){
            MaybeKind<Eval<T>> x = widen(nested);
            MaybeKind<Higher<eval,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<maybe,future,T> future(Maybe<cyclops.async.Future<T>> nested){
            MaybeKind<cyclops.async.Future<T>> x = widen(nested);
            MaybeKind<Higher<future,T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<maybe,Higher<Witness.either,S>, P> xor(Maybe<Either<S, P>> nested){
            MaybeKind<Either<S, P>> x = widen(nested);
            MaybeKind<Higher<Higher<Witness.either,S>, P>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),Either.Instances.definitions());
        }
        public static <S,T> Nested<maybe,Higher<reader,S>, T> reader(Maybe<Reader<S, T>> nested, S defaultValue){
            MaybeKind<Reader<S, T>> x = widen(nested);
            MaybeKind<Higher<Higher<reader,S>, T>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <S extends Throwable, P> Nested<maybe,Higher<tryType,S>, P> cyclopsTry(Maybe<cyclops.control.Try<P, S>> nested){
            MaybeKind<cyclops.control.Try<P, S>> x = widen(nested);
            MaybeKind<Higher<Higher<tryType,S>, P>> y = (MaybeKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<maybe,optional,T> javaOptional(Maybe<Optional<T>> nested){
            Maybe<OptionalKind<T>> f = nested.map(o -> OptionalKind.widen(o));
            MaybeKind<OptionalKind<T>> x = MaybeKind.widen(f);

            MaybeKind<Higher<optional,T>> y = (MaybeKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <T> Nested<maybe,completableFuture,T> javaCompletableFuture(Maybe<CompletableFuture<T>> nested){
            Maybe<CompletableFutures.CompletableFutureKind<T>> f = nested.map(o -> CompletableFutures.CompletableFutureKind.widen(o));
            MaybeKind<CompletableFutures.CompletableFutureKind<T>> x = MaybeKind.widen(f);
            MaybeKind<Higher<completableFuture,T>> y = (MaybeKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<maybe,Witness.stream,T> javaStream(Maybe<java.util.stream.Stream<T>> nested){
            Maybe<StreamKind<T>> f = nested.map(o -> StreamKind.widen(o));
            MaybeKind<StreamKind<T>> x = MaybeKind.widen(f);
            MaybeKind<Higher<Witness.stream,T>> y = (MaybeKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedMaybe{
        public static <T> Nested<reactiveSeq,maybe,T> reactiveSeq(ReactiveSeq<Maybe<T>> nested){
            ReactiveSeq<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<Witness.maybe,maybe,T> maybe(cyclops.control.Maybe<Maybe<T>> nested){
            cyclops.control.Maybe<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);

            return Nested.of(x, cyclops.control.Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,maybe,T> eval(Eval<Maybe<T>> nested){
            Eval<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<future,maybe,T> future(cyclops.async.Future<Maybe<T>> nested){
            cyclops.async.Future<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<Witness.either,S>,maybe, P> xor(Either<S, Maybe<P>> nested){
            Either<S, Higher<maybe,P>> x = nested.map(MaybeKind::widenK);

            return Nested.of(x,Either.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,maybe, T> reader(Reader<S, Maybe<T>> nested,S defaultValue){

            Reader<S, Higher<maybe, T>>  x = nested.map(MaybeKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<tryType,S>,maybe, P> cyclopsTry(cyclops.control.Try<Maybe<P>, S> nested){
            cyclops.control.Try<Higher<maybe,P>, S> x = nested.map(MaybeKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<optional,maybe,T> javaOptional(Optional<Maybe<T>> nested){
            Optional<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);

            return  Nested.of(OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<completableFuture,maybe,T> javaCompletableFuture(CompletableFuture<Maybe<T>> nested){
            CompletableFuture<Higher<maybe,T>> x = nested.thenApply(MaybeKind::widenK);

            return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,maybe,T> javaStream(java.util.stream.Stream<Maybe<T>> nested){
            java.util.stream.Stream<Higher<maybe,T>> x = nested.map(MaybeKind::widenK);

            return Nested.of(StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }

}
