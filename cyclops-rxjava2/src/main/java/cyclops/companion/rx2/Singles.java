package cyclops.companion.rx2;


import com.aol.cyclops.rx2.hkt.SingleKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.react.Status;
import com.aol.cyclops2.types.Value;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.collections.mutable.ListX;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.lazy.Either;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;

import cyclops.monads.Rx2Witness;
import cyclops.monads.Rx2Witness.single;
import cyclops.monads.WitnessType;

import cyclops.monads.transformers.rx2.SingleT;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.reactivex.Single;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;



import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    /**
     * Companion class for creating Type Class instances for working with Singles
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {


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
        public static <T,R>Functor<SingleKind.µ> functor(){
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
        public static <T> Pure<SingleKind.µ> unit(){
            return General.<SingleKind.µ,T>unit(Instances::of);
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
        public static <T,R> Applicative<SingleKind.µ> applicative(){
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
        public static <T,R> Monad<SingleKind.µ> monad(){

            BiFunction<Higher<SingleKind.µ,T>,Function<? super T, ? extends Higher<SingleKind.µ,R>>,Higher<SingleKind.µ,R>> flatMap = Instances::flatMap;
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
        public static <T,R> MonadZero<SingleKind.µ> monadZero(){

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
        public static <T> MonadPlus<SingleKind.µ> monadPlus(){


            Monoid<SingleKind<T>> m = Monoid.of(SingleKind.<T>widen(Single.never()),
                    (f,g)-> SingleKind.widen(Single.ambArray(f.narrow(),g.narrow())));

            Monoid<Higher<SingleKind.µ,T>> m2= (Monoid)m;
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
        public static <T> MonadPlus<SingleKind.µ> monadPlus(Monoid<SingleKind<T>> m){
            Monoid<Higher<SingleKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<SingleKind.µ> traverse(){

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
        public static <T> Foldable<SingleKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<SingleKind.µ,T>,T> foldRightFn =  (m, l)-> m.apply(m.zero(), SingleKind.narrow(l).blockingGet());
            BiFunction<Monoid<T>,Higher<SingleKind.µ,T>,T> foldLeftFn = (m, l)->  m.apply(m.zero(), SingleKind.narrow(l).blockingGet());
            return General.foldable(foldRightFn, foldLeftFn);
        }
        public static <T> Comonad<SingleKind.µ> comonad(){
            Function<? super Higher<SingleKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(SingleKind::narrow).blockingGet();
            return General.comonad(functor(), unit(), extractFn);
        }

        private static <T> SingleKind<T> of(T value){
            return SingleKind.widen(Single.just(value));
        }
        private static <T,R> SingleKind<R> ap(SingleKind<Function< T, R>> lt, SingleKind<T> list){


            return SingleKind.widen(Singles.combine(lt.narrow(),list.narrow(), (a, b)->a.apply(b)));

        }
        private static <T,R> Higher<SingleKind.µ,R> flatMap(Higher<SingleKind.µ,T> lt, Function<? super T, ? extends  Higher<SingleKind.µ,R>> fn){
            return SingleKind.widen(SingleKind.narrow(lt).flatMap(Functions.rxFunction(fn.andThen(SingleKind::narrow))));
        }
        private static <T,R> SingleKind<R> map(SingleKind<T> lt, Function<? super T, ? extends R> fn){
            return SingleKind.widen(lt.narrow().map(Functions.rxFunction(fn)));
        }


        private static <C2,T,R> Higher<C2, Higher<SingleKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                            Higher<SingleKind.µ, T> ds){
            Single<T> future = SingleKind.narrow(ds);
            return applicative.map(SingleKind::just, fn.apply(future.blockingGet()));
        }

    }

}
