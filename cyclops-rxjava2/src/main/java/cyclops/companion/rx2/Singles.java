package cyclops.companion.rx2;


import com.aol.cyclops.rx2.hkt.FlowableKind;
import com.aol.cyclops.rx2.hkt.MaybeKind;
import com.aol.cyclops.rx2.hkt.ObservableKind;
import com.aol.cyclops.rx2.hkt.SingleKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.react.Status;
import com.aol.cyclops2.types.MonadicValue;
import com.aol.cyclops2.types.Value;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals;
import cyclops.companion.Streams;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.control.Xor;
import cyclops.control.lazy.Either;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;

import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.flowable;
import cyclops.monads.Rx2Witness.maybe;
import cyclops.monads.Rx2Witness.observable;
import cyclops.monads.Rx2Witness.single;
import cyclops.monads.Witness;
import cyclops.monads.Witness.*;
import cyclops.monads.WitnessType;

import cyclops.monads.transformers.rx2.SingleT;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.reactivex.Flowable;
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

import static com.aol.cyclops.rx2.hkt.SingleKind.widen;

/**
 * Companion class for working with Reactor Single types
 * 
 * @author johnmcclean
 *
 */
@UtilityClass
public class Singles {

    public static <T> Single<T> raw(AnyM<single,T> anyM){
        return Rx2Witness.single(anyM);
    }

    public static <T> Single<T> fromValue(MonadicValue<T> future){
        return Single.fromPublisher(future);
    }

    public static <W extends WitnessType<W>,T> SingleT<W,T> liftM(AnyM<W,Single<T>> nested){
        return SingleT.of(nested);
    }

    public static <T> Future[] futures(Single<T>... futures){

        Future[] array = new Future[futures.length];
        for(int i=0;i<array.length;i++){
            array[i]=future(futures[i]);
        }
        return array;
    }
    public static <T> Future<T> future(Single<T> future){
        return Future.fromPublisher(future.toFlowable());
    }

    public static <R> Either<Throwable,R> either(Single<R> either){
        return Either.fromFuture(future(either));

    }

    public static <T> Maybe<T> maybe(Single<T> opt){
        return Maybe.fromFuture(future(opt));
    }
    public static <T> Eval<T> eval(Single<T> opt){
        return Eval.fromFuture(future(opt));
    }
    
    /**
     * Construct an AnyM type from a Single. This allows the Single to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     * 
     * <pre>
     * {@code 
     *    
     *    AnyMSeq<Integer> single = Fluxs.anyM(Single.just(1,2,3));
     *    AnyMSeq<Integer> transformedSingle = myGenericOperation(single);
     *    
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     * 
     * @param single To wrap inside an AnyM
     * @return AnyMSeq wrapping a Single
     */
    public static <T> AnyMValue<single,T> anyM(Single<T> single) {
        return AnyM.ofValue(single, Rx2Witness.single.INSTANCE);
    }



    /**
     * Select the first Single to complete
     *
     * @see CompletableFuture#anyOf(CompletableFuture...)
     * @param fts Singles to race
     * @return First Single to complete
     */
    public static <T> Single<T> anyOf(Single<T>... fts) {
        return Single.fromPublisher(Future.anyOf(futures(fts)));

    }
    /**
     * Wait until all the provided Future's to complete
     *
     * @see CompletableFuture#allOf(CompletableFuture...)
     *
     * @param fts Singles to  wait on
     * @return Single that completes when all the provided Futures Complete. Empty Future result, or holds an Exception
     *         from a provided Future that failed.
     */
    public static <T> Single<T> allOf(Single<T>... fts) {

        return Single.fromPublisher(Future.allOf(futures(fts)));
    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Single<ListX<Integer>> strings = Singles.quorum(status -> status.getCompleted() >0, Single.deferred(()->1),Single.empty(),Single.empty());


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
    public static <T> Single<ListX<T>> quorum(Predicate<Status<T>> breakout, Consumer<Throwable> errorHandler, Single<T>... fts) {

        return Single.fromPublisher(Future.quorum(breakout,errorHandler,futures(fts)));


    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Single<ListX<Integer>> strings = Singles.quorum(status -> status.getCompleted() >0, Single.deferred(()->1),Single.empty(),Single.empty());


    strings.get().size()
    //1
     *
     * }
     * </pre>
     *
     *
     * @param breakout Predicate that determines whether the block should be
     *            continued or removed
     * @param fts Singles to  wait on results from
     * @return Single which will be populated with a Quorum of results
     */
    @SafeVarargs
    public static <T> Single<ListX<T>> quorum(Predicate<Status<T>> breakout, Single<T>... fts) {

        return Single.fromPublisher(Future.quorum(breakout,futures(fts)));


    }
    /**
     * Select the first Future to return with a successful result
     *
     * <pre>
     * {@code
     * Single<Integer> ft = Single.empty();
      Single<Integer> result = Singles.firstSuccess(Single.deferred(()->1),ft);

    ft.complete(10);
    result.get() //1
     * }
     * </pre>
     *
     * @param fts Singles to race
     * @return First Single to return with a result
     */
    @SafeVarargs
    public static <T> Single<T> firstSuccess(Single<T>... fts) {
        return Single.fromPublisher(Future.firstSuccess(futures(fts)));

    }

    /**
     * Perform a For Comprehension over a Single, accepting 3 generating functions. 
     * This results in a four level nested internal iteration over the provided Singles.
     * 
     *  <pre>
     * {@code
     *    
     *   import static cyclops.companion.reactor.Singles.forEach4;
     *    
          forEach4(Single.just(1), 
                  a-> Single.just(a+1),
                  (a,b) -> Single.<Integer>just(a+b),
                  (a,b,c) -> Single.<Integer>just(a+b+c),
                  Tuple::tuple)
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Single
     * @param value2 Nested Single
     * @param value3 Nested Single
     * @param value4 Nested Single
     * @param yieldingFunction Generates a result per combination
     * @return Single with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Single<R> forEach4(Single<? extends T1> value1,
            Function<? super T1, ? extends Single<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Single<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends Single<R3>> value4,
            Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        Single<? extends R> res = value1.flatMap(in -> {

            Single<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Single<R2> b = value3.apply(in, ina);
                return b.flatMap(inb -> {
                    Single<R3> c = value4.apply(in, ina, inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
        return  narrow(res);
    }


    /**
     * Perform a For Comprehension over a Single, accepting 2 generating functions. 
     * This results in a three level nested internal iteration over the provided Singles.
     * 
     *  <pre>
     * {@code
     *    
     *   import static cyclops.companion.reactor.Singles.forEach3;
     *    
          forEach3(Single.just(1), 
                  a-> Single.just(a+1),
                  (a,b) -> Single.<Integer>just(a+b),
                  Tuple::tuple)
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Single
     * @param value2 Nested Single
     * @param value3 Nested Single
     * @param yieldingFunction Generates a result per combination
     * @return Single with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Single<R> forEach3(Single<? extends T1> value1,
            Function<? super T1, ? extends Single<R1>> value2,
            BiFunction<? super T1, ? super R1, ? extends Single<R2>> value3,
            Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {


        Single<? extends R> res = value1.flatMap(in -> {

            Single<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Single<R2> b = value3.apply(in, ina);


                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));


            });

        });
        return narrow(res);

    }



    /**
     * Perform a For Comprehension over a Single, accepting a generating function. 
     * This results in a two level nested internal iteration over the provided Singles.
     * 
     *  <pre>
     * {@code
     *    
     *   import static cyclops.companion.reactor.Singles.forEach;
     *    
          forEach(Single.just(1), 
                  a-> Single.just(a+1),
                  Tuple::tuple)
     * 
     * }
     * </pre>
     * 
     * @param value1 top level Single
     * @param value2 Nested Single
     * @param yieldingFunction Generates a result per combination
     * @return Single with a combined value generated by the yielding function
     */
    public static <T, R1, R> Single<R> forEach(Single<? extends T> value1,
                                             Function<? super T, Single<R1>> value2,
                                             BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        Single<R> res = value1.flatMap(in -> {

            Single<R1> a = value2.apply(in);
            return a.map(ina -> yieldingFunction.apply(in, ina));


        });


        return narrow(res);

    }



    /**
     * Lazily combine this Single with the supplied value via the supplied BiFunction
     * 
     * @param single Single to combine with another value
     * @param app Value to combine with supplied single
     * @param fn Combiner function
     * @return Combined Single
     */
    public static <T1, T2, R> Single<R> combine(Single<? extends T1> single, Value<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return Single.fromPublisher(Future.fromPublisher(single.toFlowable())
                                .combine(app, fn));
    }

    /**
     * Lazily combine this Single with the supplied Single via the supplied BiFunction
     * 
     * @param single Single to combine with another value
     * @param app Single to combine with supplied single
     * @param fn Combiner function
     * @return Combined Single
     */
    public static <T1, T2, R> Single<R> combine(Single<? extends T1> single, Single<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return Single.fromPublisher(Future.fromPublisher(single.toFlowable())
                                .combine(Future.fromPublisher(app.toFlowable()), fn));
    }

    /**
     * Combine the provided Single with the first element (if present) in the provided Iterable using the provided BiFunction
     * 
     * @param single Single to combine with an Iterable
     * @param app Iterable to combine with a Single
     * @param fn Combining function
     * @return Combined Single
     */
    public static <T1, T2, R> Single<R> zip(Single<? extends T1> single, Iterable<? extends T2> app,
            BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return Single.fromPublisher(Future.fromPublisher(single.toFlowable())
                                .zip(app, fn));
    }

    /**
     * Combine the provided Single with the first element (if present) in the provided Publisher using the provided BiFunction
     * 
     * @param single  Single to combine with a Publisher
     * @param fn Publisher to combine with a Single
     * @param app Combining function
     * @return Combined Single
     */
    public static <T1, T2, R> Single<R> zip(Single<? extends T1> single, BiFunction<? super T1, ? super T2, ? extends R> fn,
            Publisher<? extends T2> app) {
        Single<R> res = Single.fromPublisher(Future.fromPublisher(single.toFlowable()).zipP(app, fn));
        return res;
    }

    /**
     * Test if value is equal to the value inside this Single
     * 
     * @param single Single to test
     * @param test Value to test
     * @return true if equal
     */
    public static <T> boolean test(Single<T> single, T test) {
        return Future.fromPublisher(single.toFlowable())
                      .test(test);
    }

    /**
     * Construct a Single from Iterable by taking the first value from Iterable
     * 
     * @param t Iterable to populate Single from
     * @return Single containing first element from Iterable (or empty Single)
     */
    public static <T> Single<T> fromIterable(Iterable<T> t) {
        return Single.fromPublisher(Future.fromIterable(t));
    }

    /**
     * Get an Iterator for the value (if any) in the provided Single
     * 
     * @param pub Single to get Iterator for
     * @return Iterator over Single value
     */
    public static <T> Iterator<T> iterator(Single<T> pub) {
        return pub.toFlowable().blockingIterable().iterator();

    }

    public static <R> Single<R> narrow(Single<? extends R> apply) {
        return (Single<R>)apply;
    }
    public static <T> Active<single,T> allTypeclasses(Single<T> type){
        return Active.of(widen(type), Singles.Instances.definitions());
    }
    public static <T,W2,R> Nested<single,W2,R> mapM(Single<T> type, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Single<Higher<W2, R>> e = type.map(x->fn.apply(x));
        SingleKind<Higher<W2, R>> lk = SingleKind.widen(e);
        return Nested.of(lk, Singles.Instances.definitions(), defs);
    }
    /**
     * Companion class for creating Type Class instances for working with Singles
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<single> definitions() {
            return new InstanceDefinitions<single>() {


                @Override
                public <T, R> Functor<single> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<single> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<single> applicative() {
                    return Instances.applicative();
                }

                @Override
                public <T, R> Monad<single> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> cyclops.control.Maybe<MonadZero<single>> monadZero() {
                    return cyclops.control.Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<single>> monadPlus() {
                    return cyclops.control.Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> cyclops.control.Maybe<MonadPlus<single>> monadPlus(Monoid<Higher<single, T>> m) {
                    return cyclops.control.Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> cyclops.control.Maybe<Traverse<single>> traverse() {
                    return cyclops.control.Maybe.just(Instances.traverse());
                }

                @Override
                public <T> cyclops.control.Maybe<Foldable<single>> foldable() {
                    return cyclops.control.Maybe.just(Instances.foldable());
                }

                @Override
                public <T> cyclops.control.Maybe<Comonad<single>> comonad() {
                    return cyclops.control.Maybe.just(Instances.comonad());
                }

                @Override
                public <T> cyclops.control.Maybe<Unfoldable<single>> unfoldable() {
                    return cyclops.control.Maybe.none();
                }
            };
        }

        /**
         *
         * Transform a Single, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  SingleKind<Integer> future = Singles.functor().map(i->i*2, SingleKind.widen(Single.just(3));
         *
         *  //[6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Singles
         * <pre>
         * {@code
         *   SingleKind<Integer> ft = Singles.unit()
        .unit("hello")
        .then(h->Singles.functor().map((String v) ->v.length(), h))
        .convert(SingleKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Singles
         */
        public static <T,R>Functor<single> functor(){
            BiFunction<SingleKind<T>,Function<? super T, ? extends R>,SingleKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * SingleKind<String> ft = Singles.unit()
        .unit("hello")
        .convert(SingleKind::narrowK);

        //Single["hello"]
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Singles
         */
        public static <T> Pure<single> unit(){
            return General.<single,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.SingleKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         * import static java.util.Arrays.asSingle;
         *
        Singles.applicative()
        .ap(widen(asSingle(l1(this::multiplyByTwo))),widen(Single.just(3)));
         *
         * //[6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * SingleKind<Function<Integer,Integer>> ftFn =Singles.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(SingleKind::narrowK);

        SingleKind<Integer> ft = Singles.unit()
        .unit("hello")
        .then(h->Singles.functor().map((String v) ->v.length(), h))
        .then(h->Singles.applicative().ap(ftFn, h))
        .convert(SingleKind::narrowK);

        //Single.just("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Singles
         */
        public static <T,R> Applicative<single> applicative(){
            BiFunction<SingleKind< Function<T, R>>,SingleKind<T>,SingleKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.SingleKind.widen;
         * SingleKind<Integer> ft  = Singles.monad()
        .flatMap(i->widen(Single.just(i)), widen(Single.just(3)))
        .convert(SingleKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    SingleKind<Integer> ft = Singles.unit()
        .unit("hello")
        .then(h->Singles.monad().flatMap((String v) ->Singles.unit().unit(v.length()), h))
        .convert(SingleKind::narrowK);

        //Single.just("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Singles
         */
        public static <T,R> Monad<single> monad(){

            BiFunction<Higher<single,T>,Function<? super T, ? extends Higher<single,R>>,Higher<single,R>> flatMap = Instances::flatMap;
            return General.monad(applicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  SingleKind<String> ft = Singles.unit()
        .unit("hello")
        .then(h->Singles.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(SingleKind::narrowK);

        //Single.just("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<single> monadZero(){

            return General.monadZero(monad(), SingleKind.empty());
        }
        /**
         * Combines Singles by selecting the first result returned
         *
         * <pre>
         * {@code
         *  SingleKind<Integer> ft = Singles.<Integer>monadPlus()
        .plus(SingleKind.widen(Single.empty()), SingleKind.widen(Single.just(10)))
        .convert(SingleKind::narrowK);
        //Single.empty()
         *
         * }
         * </pre>
         * @return Type class for combining Singles by concatenation
         */
        public static <T> MonadPlus<single> monadPlus(){


            Monoid<SingleKind<T>> m = Monoid.of(SingleKind.<T>widen(Single.never()),
                    (f,g)-> SingleKind.widen(Single.ambArray(f.narrow(),g.narrow())));

            Monoid<Higher<single,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<SingleKind<Integer>> m = Monoid.of(SingleKind.widen(Arrays.asSingle()), (a,b)->a.isEmpty() ? b : a);
        SingleKind<Integer> ft = Singles.<Integer>monadPlus(m)
        .plus(SingleKind.widen(Arrays.asSingle(5)), SingleKind.widen(Arrays.asSingle(10)))
        .convert(SingleKind::narrowK);
        //Arrays.asSingle(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Singles
         * @return Type class for combining Singles
         */
        public static <T> MonadPlus<single> monadPlusK(Monoid<SingleKind<T>> m){
            Monoid<Higher<single,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<single> monadPlus(Monoid<Higher<single,T>> m){
            Monoid<Higher<single,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<single> traverse(){

            return General.traverseByTraverse(applicative(), Instances::traverseA);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Singles.foldable()
        .foldLeft(0, (a,b)->a+b, SingleKind.widen(Arrays.asSingle(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<single> foldable(){
            BiFunction<Monoid<T>,Higher<single,T>,T> foldRightFn =  (m, l)-> m.apply(m.zero(), SingleKind.narrow(l).blockingGet());
            BiFunction<Monoid<T>,Higher<single,T>,T> foldLeftFn = (m, l)->  m.apply(m.zero(), SingleKind.narrow(l).blockingGet());
            return General.foldable(foldRightFn, foldLeftFn);
        }
        public static <T> Comonad<single> comonad(){
            Function<? super Higher<single, T>, ? extends T> extractFn = maybe -> maybe.convert(SingleKind::narrow).blockingGet();
            return General.comonad(functor(), unit(), extractFn);
        }

        private static <T> SingleKind<T> of(T value){
            return SingleKind.widen(Single.just(value));
        }
        private static <T,R> SingleKind<R> ap(SingleKind<Function< T, R>> lt, SingleKind<T> list){


            return SingleKind.widen(Singles.combine(lt.narrow(),list.narrow(), (a, b)->a.apply(b)));

        }
        private static <T,R> Higher<single,R> flatMap(Higher<single,T> lt, Function<? super T, ? extends  Higher<single,R>> fn){
            return SingleKind.widen(SingleKind.narrow(lt).flatMap(Functions.rxFunction(fn.andThen(SingleKind::narrow))));
        }
        private static <T,R> SingleKind<R> map(SingleKind<T> lt, Function<? super T, ? extends R> fn){
            return SingleKind.widen(lt.narrow().map(Functions.rxFunction(fn)));
        }


        private static <C2,T,R> Higher<C2, Higher<single, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                            Higher<single, T> ds){
            Single<T> future = SingleKind.narrow(ds);
            return applicative.map(SingleKind::just, fn.apply(future.blockingGet()));
        }

    }
    public static interface SingleNested {

        public static <T> Nested<single,observable,T> observable(Single<Observable<T>> nested){
            Single<ObservableKind<T>> f = nested.map(ObservableKind::widen);
            SingleKind<ObservableKind<T>> x = widen(f);
            SingleKind<Higher<observable,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(), Observables.Instances.definitions());
        }
        public static <T> Nested<single,flowable,T> flowable(Single<Flowable<T>> nested){
            Single<FlowableKind<T>> f = nested.map(FlowableKind::widen);
            SingleKind<FlowableKind<T>> x = widen(f);
            SingleKind<Higher<flowable,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(), Flowables.Instances.definitions());
        }

        public static <T> Nested<single,maybe,T> maybe(Single<Maybe<T>> nested){
            Single<MaybeKind<T>> f = nested.map(MaybeKind::widen);
            SingleKind<MaybeKind<T>> x = widen(f);
            SingleKind<Higher<maybe,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(), Maybes.Instances.definitions());
        }
        public static <T> Nested<single,single,T> single(Single<Single<T>> nested){
            Single<SingleKind<T>> f = nested.map(SingleKind::widen);
            SingleKind<SingleKind<T>> x = widen(f);
            SingleKind<Higher<single,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(), Singles.Instances.definitions());
        }
        public static <T> Nested<single,reactiveSeq,T> reactiveSeq(Single<ReactiveSeq<T>> nested){
            SingleKind<ReactiveSeq<T>> x = widen(nested);
            SingleKind<Higher<reactiveSeq,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<single,Witness.maybe,T> cyclopsMaybe(Single<cyclops.control.Maybe<T>> nested){
            SingleKind<cyclops.control.Maybe<T>> x = widen(nested);
            SingleKind<Higher<Witness.maybe,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(), cyclops.control.Maybe.Instances.definitions());
        }
        public static <T> Nested<single,eval,T> eval(Single<Eval<T>> nested){
            SingleKind<Eval<T>> x = widen(nested);
            SingleKind<Higher<eval,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<single,future,T> future(Single<cyclops.async.Future<T>> nested){
            SingleKind<cyclops.async.Future<T>> x = widen(nested);
            SingleKind<Higher<future,T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<single,Higher<xor,S>, P> xor(Single<Xor<S, P>> nested){
            SingleKind<Xor<S, P>> x = widen(nested);
            SingleKind<Higher<Higher<xor,S>, P>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),Xor.Instances.definitions());
        }
        public static <S,T> Nested<single,Higher<reader,S>, T> reader(Single<Reader<S, T>> nested){
            SingleKind<Reader<S, T>> x = widen(nested);
            SingleKind<Higher<Higher<reader,S>, T>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<single,Higher<Witness.tryType,S>, P> cyclopsTry(Single<cyclops.control.Try<P, S>> nested){
            SingleKind<cyclops.control.Try<P, S>> x = widen(nested);
            SingleKind<Higher<Higher<Witness.tryType,S>, P>> y = (SingleKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<single,Witness.optional,T> javaOptional(Single<Optional<T>> nested){
            Single<Optionals.OptionalKind<T>> f = nested.map(o -> Optionals.OptionalKind.widen(o));
            SingleKind<Optionals.OptionalKind<T>> x = SingleKind.widen(f);

            SingleKind<Higher<Witness.optional,T>> y = (SingleKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <T> Nested<single,Witness.completableFuture,T> javaCompletableFuture(Single<CompletableFuture<T>> nested){
            Single<CompletableFutures.CompletableFutureKind<T>> f = nested.map(o -> CompletableFutures.CompletableFutureKind.widen(o));
            SingleKind<CompletableFutures.CompletableFutureKind<T>> x = SingleKind.widen(f);
            SingleKind<Higher<Witness.completableFuture,T>> y = (SingleKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<single,Witness.stream,T> javaStream(Single<java.util.stream.Stream<T>> nested){
            Single<Streams.StreamKind<T>> f = nested.map(o -> Streams.StreamKind.widen(o));
            SingleKind<Streams.StreamKind<T>> x = SingleKind.widen(f);
            SingleKind<Higher<Witness.stream,T>> y = (SingleKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedSingle{
        public static <T> Nested<reactiveSeq,single,T> reactiveSeq(ReactiveSeq<Single<T>> nested){
            ReactiveSeq<Higher<single,T>> x = nested.map(SingleKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<Witness.maybe,single,T> maybe(cyclops.control.Maybe<Single<T>> nested){
            cyclops.control.Maybe<Higher<single,T>> x = nested.map(SingleKind::widenK);

            return Nested.of(x, cyclops.control.Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,single,T> eval(Eval<Single<T>> nested){
            Eval<Higher<single,T>> x = nested.map(SingleKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<future,single,T> future(cyclops.async.Future<Single<T>> nested){
            cyclops.async.Future<Higher<single,T>> x = nested.map(SingleKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<xor,S>,single, P> xor(Xor<S, Single<P>> nested){
            Xor<S, Higher<single,P>> x = nested.map(SingleKind::widenK);

            return Nested.of(x,Xor.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,single, T> reader(Reader<S, Single<T>> nested){

            Reader<S, Higher<single, T>>  x = nested.map(SingleKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,single, P> cyclopsTry(cyclops.control.Try<Single<P>, S> nested){
            cyclops.control.Try<Higher<single,P>, S> x = nested.map(SingleKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.optional,single,T> javaOptional(Optional<Single<T>> nested){
            Optional<Higher<single,T>> x = nested.map(SingleKind::widenK);

            return  Nested.of(Optionals.OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<Witness.completableFuture,single,T> javaCompletableFuture(CompletableFuture<Single<T>> nested){
            CompletableFuture<Higher<single,T>> x = nested.thenApply(SingleKind::widenK);

            return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,single,T> javaStream(java.util.stream.Stream<Single<T>> nested){
            java.util.stream.Stream<Higher<single,T>> x = nested.map(SingleKind::widenK);

            return Nested.of(Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }

}
