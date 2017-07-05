package cyclops.companion.vavr;

import cyclops.monads.VavrWitness.list;
import cyclops.monads.VavrWitness.stream;
import cyclops.monads.VavrWitness.tryType;
import io.vavr.Lazy;
import io.vavr.collection.*;
import io.vavr.control.*;
import com.aol.cyclops.vavr.hkt.*;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.control.Xor;
import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.monads.*;
import cyclops.monads.VavrWitness.*;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.Witness.*;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.*;
import com.aol.cyclops.vavr.hkt.ArrayKind;
import com.aol.cyclops.vavr.hkt.ListKind;
import com.aol.cyclops2.react.Status;
import cyclops.conversion.vavr.ToCyclopsReact;
import cyclops.monads.VavrWitness.future;
import com.aol.cyclops.vavr.hkt.FutureKind;
import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.types.Value;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.collections.mutable.ListX;
import cyclops.companion.Monoids;
import cyclops.function.Reducer;
import cyclops.monads.transformers.FutureT;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.vavr.collection.Array;
import io.vavr.concurrent.Future;

import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


import static com.aol.cyclops.vavr.hkt.FutureKind.widen;

/**
 * Utilty methods for working with JDK CompletableFutures
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Futures {


    public static  <W1,T> Coproduct<W1,future,T> coproduct(Future<T> type, InstanceDefinitions<W1> def1){
        return Coproduct.of(Xor.primary(widen(type)),def1, Instances.definitions());
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,future,T> xorM(Future<T> type){
        return XorM.right(anyM(type));
    }
    public static <T> void subscribe(final Subscriber<? super T> sub, Future<T> f){
         asPublisher(f).subscribe(sub);
    }
    public static <T> Publisher<T> asPublisher(Future<T> f){
        return ToCyclopsReact.future(f);
    }
    public static <T> AnyMValue<future,T> anyM(Future<T> future) {
        return AnyM.ofValue(future, VavrWitness.future.INSTANCE);
    }

    /**
     * Lifts a vavr Future into a cyclops FutureT monad transformer (involves an observables conversion to
     * cyclops Future types)
     *
     */
    public static <T,W extends WitnessType<W>> FutureT<W, T> liftM(Future<T> opt, W witness) {
        return FutureT.of(witness.adapter().unit(ToCyclopsReact.future(opt)));
    }



    /**
     * Select the first Future to complete
     *
     * @see CompletableFuture#anyOf(CompletableFuture...)
     * @param fts FutureWs to race
     * @return First Future to complete
     */
    public static <T> Future<T> anyOf(Future<T>... fts) {
        return FromCyclopsReact.future(cyclops.async.Future.anyOf(ToCyclopsReact.futures(fts)));

    }
    /**
     * Wait until all the provided Future's to complete
     *
     * @see CompletableFuture#allOf(CompletableFuture...)
     *
     * @param fts FutureWs to  wait on
     * @return Future that completes when all the provided Futures Complete. Empty Future result, or holds an Exception
     *         from a provided Future that failed.
     */
    public static <T> Future<T> allOf(Future<T>... fts) {

        return FromCyclopsReact.future(cyclops.async.Future.allOf(ToCyclopsReact.futures(fts)));
    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Future<ListX<Integer>> strings = Future.quorum(status -> status.getCompleted() >0, Future.ofSupplier(()->1),Future.future(),Future.future());


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
    public static <T> Future<ListX<T>> quorum(Predicate<Status<T>> breakout, Consumer<Throwable> errorHandler, Future<T>... fts) {

        return FromCyclopsReact.future(cyclops.async.Future.quorum(breakout,errorHandler,ToCyclopsReact.futures(fts)));


    }
    /**
     * Block until a Quorum of results have returned as determined by the provided Predicate
     *
     * <pre>
     * {@code
     *
     * Future<ListX<Integer>> strings = Future.quorum(status -> status.getCompleted() >0, Future.ofSupplier(()->1),Future.future(),Future.future());


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
     * @return Future which will be populated with a Quorum of results
     */
    @SafeVarargs
    public static <T> Future<ListX<T>> quorum(Predicate<Status<T>> breakout, Future<T>... fts) {

        return FromCyclopsReact.future(cyclops.async.Future.quorum(breakout,ToCyclopsReact.futures(fts)));


    }
    /**
     * Select the first Future to return with a successful result
     *
     * <pre>
     * {@code
     * Future<Integer> ft = Future.future();
    Future<Integer> result = Future.firstSuccess(Future.ofSupplier(()->1),ft);

    ft.complete(10);
    result.get() //1
     * }
     * </pre>
     *
     * @param fts Futures to race
     * @return First Future to return with a result
     */
    @SafeVarargs
    public static <T> Future<T> firstSuccess(Future<T>... fts) {
        return FromCyclopsReact.future(cyclops.async.Future.firstSuccess(ToCyclopsReact.futures(fts)));

    }

    /**
     * Perform a For Comprehension over a Future, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Futures.
     *
     *  <pre>
     * {@code
     *
     *   import static com.aol.cyclops2.reactor.Futures.forEach4;
     *
    forEach4(Future.just(1),
    a-> Future.just(a+1),
    (a,b) -> Future.<Integer>just(a+b),
    a                  (a,b,c) -> Future.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param value3 Nested Future
     * @param value4 Nested Future
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Future<R> forEach4(Future<? extends T1> value1,
                                                                 Function<? super T1, ? extends Future<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Future<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Future<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Future<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Future<R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     *
     * Perform a For Comprehension over a Future, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Futures.
     *
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops2.reactor.Futures.forEach4;
     *
     *  forEach4(Future.just(1),
    a-> Future.just(a+1),
    (a,b) -> Future.<Integer>just(a+b),
    (a,b,c) -> Future.<Integer>just(a+b+c),
    (a,b,c,d) -> a+b+c+d <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param value3 Nested Future
     * @param value4 Nested Future
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Future<R> forEach4(Future<? extends T1> value1,
                                                                 Function<? super T1, ? extends Future<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Future<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Future<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Future<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Future<R3> c = value4.apply(in,ina,inb);
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Future, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Futures.
     *
     *  <pre>
     * {@code
     *
     *   import static com.aol.cyclops2.reactor.Futures.forEach3;
     *
    forEach3(Future.just(1),
    a-> Future.just(a+1),
    (a,b) -> Future.<Integer>just(a+b),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param value3 Nested Future
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Future<R> forEach3(Future<? extends T1> value1,
                                                         Function<? super T1, ? extends Future<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Future<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Future<R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }

    /**
     *
     * Perform a For Comprehension over a Future, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Futures.
     *
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops2.reactor.Futures.forEach3;
     *
     *  forEach3(Future.just(1),
    a-> Future.just(a+1),
    (a,b) -> Future.<Integer>just(a+b),
    (a,b,c) -> a+b+c <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param value3 Nested Future
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Future<R> forEach3(Future<? extends T1> value1,
                                                         Function<? super T1, ? extends Future<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Future<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Future<R2> b = value3.apply(in,ina);
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });

    }

    /**
     * Perform a For Comprehension over a Future, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Futures.
     *
     *  <pre>
     * {@code
     *
     *   import static com.aol.cyclops2.reactor.Futures.forEach;
     *
    forEach(Future.just(1),
    a-> Future.just(a+1),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T, R1, R> Future<R> forEach2(Future<? extends T> value1, Function<? super T, Future<R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });



    }

    /**
     *
     * Perform a For Comprehension over a Future, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Futures.
     *
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops2.reactor.Futures.forEach;
     *
     *  forEach(Future.just(1),
    a-> Future.just(a+1),
    (a,b) -> Future.<Integer>just(a+b),
    (a,b,c) -> a+b+c <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Future
     * @param value2 Nested Future
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Future with a combined value generated by the yielding function
     */
    public static <T, R1, R> Future<R> forEach2(Future<? extends T> value1, Function<? super T, ? extends Future<R1>> value2,
                                                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Future<R1> a = value2.apply(in);
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });




    }


    /**
     * Sequence operation, take a Collection of Futures and turn it into a Future with a Collection
     * By constrast with {@link Futures#sequencePresent(CollectionX)}, if any Futures are empty the result
     * is an empty Future
     *
     * <pre>
     * {@code
     *
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();
     *
     *  Future<ListX<Integer>> opts = Futures.sequence(ListX.of(just, none, Future.of(1)));
    //Future.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Maybe with a List of values
     */
    public static <T> Future<ListX<T>> sequence(final CollectionX<Future<T>> opts) {
        return sequence(opts.stream()).map(s -> s.toListX());

    }
    /**
     * Sequence operation, take a Collection of Futures and turn it into a Future with a Collection
     * Only successes are retained. By constrast with {@link Futures#sequence(CollectionX)} Future#empty types are
     * tolerated and ignored.
     *
     * <pre>
     * {@code
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();
     *
     * Future<ListX<Integer>> maybes = Futures.sequencePresent(ListX.of(just, none, Future.of(1)));
    //Future.of(ListX.of(10, 1));
     * }
     * </pre>
     *
     * @param opts Futures to Sequence
     * @return Future with a List of values
     */
    public static <T> Future<ListX<T>> sequencePresent(final CollectionX<Future<T>> opts) {
        return sequence(opts.stream().filter(Future::isCompleted)).map(s->s.toListX());
    }
    /**
     * Sequence operation, take a Collection of Futures and turn it into a Future with a Collection
     * By constrast with {@link Futures#sequencePresent(CollectionX)} if any Future types are empty
     * the return type will be an empty Future
     *
     * <pre>
     * {@code
     *
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();
     *
     *  Future<ListX<Integer>> maybes = Futures.sequence(ListX.of(just, none, Future.of(1)));
    //Future.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Future with a List of values
     */
    public static <T> Future<ReactiveSeq<T>> sequence(final java.util.stream.Stream<Future<T>> opts) {
        return AnyM.sequence(opts.map(Futures::anyM), future.INSTANCE)
                .map(ReactiveSeq::fromStream)
                .to(VavrWitness::future);

    }
    /**
     * Accummulating operation using the supplied Reducer (@see cyclops2.Reducers). A typical use case is to accumulate into a Persistent Collection type.
     * Accumulates the present results, ignores empty Futures.
     *
     * <pre>
     * {@code
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();

     * Future<PersistentSetX<Integer>> opts = Future.accumulateJust(ListX.of(just, none, Future.of(1)), Reducers.toPersistentSetX());
    //Future.of(PersistentSetX.of(10, 1)));
     *
     * }
     * </pre>
     *
     * @param futureals Futures to accumulate
     * @param reducer Reducer to accumulate values with
     * @return Future with reduced value
     */
    public static <T, R> Future<R> accumulatePresent(final CollectionX<Future<T>> futureals, final Reducer<R> reducer) {
        return sequencePresent(futureals).map(s -> s.mapReduce(reducer));
    }
    /**
     * Accumulate the results only from those Futures which have a value present, using the supplied mapping function to
     * convert the data from each Future before reducing them using the supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();

     *  Future<String> opts = Future.accumulateJust(ListX.of(just, none, Future.of(1)), i -> "" + i,
    Monoids.stringConcat);
    //Future.of("101")
     *
     * }
     * </pre>
     *
     * @param futureals Futures to accumulate
     * @param mapper Mapping function to be applied to the result of each Future
     * @param reducer Monoid to combine values from each Future
     * @return Future with reduced value
     */
    public static <T, R> Future<R> accumulatePresent(final CollectionX<Future<T>> futureals, final Function<? super T, R> mapper,
                                                     final Monoid<R> reducer) {
        return sequencePresent(futureals).map(s -> s.map(mapper)
                .reduce(reducer));
    }
    /**
     * Accumulate the results only from those Futures which have a value present, using the
     * supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Future<Integer> just = Future.of(10);
    Future<Integer> none = Future.empty();

     *  Future<String> opts = Future.accumulateJust(Monoids.stringConcat,ListX.of(just, none, Future.of(1)),
    );
    //Future.of("101")
     *
     * }
     * </pre>
     *
     * @param futureals Futures to accumulate
     * @param reducer Monoid to combine values from each Future
     * @return Future with reduced value
     */
    public static <T> Future<T> accumulatePresent(final Monoid<T> reducer, final CollectionX<Future<T>> futureals) {
        return sequencePresent(futureals).map(s -> s
                .reduce(reducer));
    }

    /**
     * Combine an Future with the provided value using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Futures.combine(Future.of(10),Maybe.just(20), this::add)
     *  //Future[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Future to combine with a value
     * @param v Value to combine
     * @param fn Combining function
     * @return Future combined with supplied value
     */
    public static <T1, T2, R> Future<R> combine(final Future<? extends T1> f, final Value<? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.future(ToCyclopsReact.future(f)
                .combine(v, fn)));
    }
    /**
     * Combine an Future with the provided Future using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Futures.combine(Future.of(10),Future.of(20), this::add)
     *  //Future[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param f Future to combine with a value
     * @param v Future to combine
     * @param fn Combining function
     * @return Future combined with supplied value, or empty Future if no value present
     */
    public static <T1, T2, R> Future<R> combine(final Future<? extends T1> f, final Future<? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return combine(f,ToCyclopsReact.future(v),fn);
    }

    /**
     * Combine an Future with the provided Iterable (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Futures.zip(Future.of(10),Arrays.asList(20), this::add)
     *  //Future[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Future to combine with first element in Iterable (if present)
     * @param v Iterable to combine
     * @param fn Combining function
     * @return Future combined with supplied Iterable, or empty Future if no value present
     */
    public static <T1, T2, R> Future<R> zip(final Future<? extends T1> f, final Iterable<? extends T2> v,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.future(ToCyclopsReact.future(f)
                .zip(v, fn)));
    }

    /**
     * Combine an Future with the provided Publisher (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Futures.zip(Flux.just(10),Future.of(10), this::add)
     *  //Future[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param p Publisher to combine
     * @param f  Future to combine with
     * @param fn Combining function
     * @return Future combined with supplied Publisher, or empty Future if no value present
     */
    public static <T1, T2, R> Future<R> zip(final Publisher<? extends T2> p, final Future<? extends T1> f,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.future(ToCyclopsReact.future(f)
                .zipP(p, fn)));
    }
    /**
     * Narrow covariant type parameter
     *
     * @param futureal Future with covariant type parameter
     * @return Narrowed Future
     */
    public static <T> Future<T> narrow(final Future<? extends T> futureal) {
        return (Future<T>) futureal;
    }

    public static <T> Active<future,T> allTypeclasses(Future<T> future){
        return Active.of(widen(future), Futures.Instances.definitions());
    }
    public static <T,W2,R> Nested<future,W2,R> mapM(Future<T> future, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Future<Higher<W2, R>> e = future.map(fn);
        FutureKind<Higher<W2, R>> lk = widen(e);
        return Nested.of(lk, Futures.Instances.definitions(), defs);
    }

    /**
     * Companion class for creating Type Class instances for working with Futures
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {

        public static  InstanceDefinitions<future> definitions() {
            return new InstanceDefinitions<future>() {

                @Override
                public <T, R> Functor<future> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<future> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<future> applicative() {
                    return Instances.applicative();
                }

                @Override
                public <T, R> Monad<future> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<future>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<future>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> Maybe<MonadPlus<future>> monadPlus(Monoid<Higher<future, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Maybe<Traverse<future>> traverse() {
                    return Maybe.just(Instances.traverse());
                }

                @Override
                public <T> Maybe<Foldable<future>> foldable() {
                    return Maybe.just(Instances.foldable());
                }

                @Override
                public <T> Maybe<Comonad<future>> comonad() {
                    return Maybe.just(Instances.comonad());
                }

                @Override
                public <T> Maybe<Unfoldable<future>> unfoldable() {
                    return Maybe.none();
                }
            };
        }

        /**
         *
         * Transform a Future, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  FutureKind<Integer> future = Futures.functor().map(i->i*2, FutureKind.widen(Future.successful(1));
         *
         *  //[2]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Futures
         * <pre>
         * {@code
         *   FutureKind<Integer> ft = Futures.unit()
        .unit("hello")
        .then(h->Futures.functor().map((String v) ->v.length(), h))
        .convert(FutureKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Futures
         */
        public static <T,R>Functor<future> functor(){
            BiFunction<FutureKind<T>,Function<? super T, ? extends R>,FutureKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * FutureKind<String> ft = Futures.unit()
        .unit("hello")
        .convert(FutureKind::narrowK);

        //Arrays.asFuture("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Futures
         */
        public static <T> Pure<future> unit(){
            return General.<future,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FutureKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        Futures.applicative()
        .ap(widen(Future.successful(l1(this::multiplyByTwo))),widen(asFuture(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * FutureKind<Function<Integer,Integer>> ftFn =Futures.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(FutureKind::narrowK);

        FutureKind<Integer> ft = Futures.unit()
        .unit("hello")
        .then(h->Futures.functor().map((String v) ->v.length(), h))
        .then(h->Futures.applicative().ap(ftFn, h))
        .convert(FutureKind::narrowK);

        //Arrays.asFuture("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Futures
         */
        public static <T,R> Applicative<future> applicative(){
            BiFunction<FutureKind< Function<T, R>>,FutureKind<T>,FutureKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FutureKind.widen;
         * FutureKind<Integer> ft  = Futures.monad()
        .flatMap(i->widen(Future.successful(i), widen(Future.successful(3))
        .convert(FutureKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    FutureKind<Integer> ft = Futures.unit()
        .unit("hello")
        .then(h->Futures.monad().flatMap((String v) ->Futures.unit().unit(v.length()), h))
        .convert(FutureKind::narrowK);

        //Arrays.asFuture("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Futures
         */
        public static <T,R> Monad<future> monad(){

            BiFunction<Higher<future,T>,Function<? super T, ? extends Higher<future,R>>,Higher<future,R>> flatMap = Instances::flatMap;
            return General.monad(applicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  FutureKind<String> ft = Futures.unit()
        .unit("hello")
        .then(h->Futures.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(FutureKind::narrowK);

        //Arrays.asFuture("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<future> monadZero(){

            return General.monadZero(monad(), FutureKind.promise());
        }
        /**
         * <pre>
         * {@code
         *  FutureKind<Integer> ft = Futures.<Integer>monadPlus()
        .plus(FutureKind.widen(Arrays.asFuture()), FutureKind.widen(Future.successful((10)))
        .convert(FutureKind::narrowK);
        //Future(10)
         *
         * }
         * </pre>
         * @return Type class for combining Futures by concatenation
         */
        public static <T> MonadPlus<future> monadPlus(){
            Monoid<cyclops.async.Future<T>> mn = Monoids.firstSuccessfulFuture();
            Monoid<FutureKind<T>> m = Monoid.of(widen(mn.zero()), (f, g)-> widen(
                    mn.apply(ToCyclopsReact.future(f), ToCyclopsReact.future(g))));

            Monoid<Higher<future,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<FutureKind<Integer>> m = Monoid.of(FutureKind.widen(Future.failed(e), (a,b)->a.isEmpty() ? b : a);
        FutureKind<Integer> ft = Futures.<Integer>monadPlus(m)
        .plus(FutureKind.widen(Future.successful(5), FutureKind.widen(Future.successful(10))
        .convert(FutureKind::narrowK);
        //Future(5)
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Futures
         * @return Type class for combining Futures
         */
        public static <T> MonadPlus<future> monadPlus(Monoid<Higher<future, T>> m){
            Monoid<Higher<future,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<future> monadPlusK(Monoid<FutureKind<T>> m){
            Monoid<Higher<future,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<future> traverse(){

            return General.traverseByTraverse(applicative(), Instances::traverseA);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Futures.foldable()
        .foldLeft(0, (a,b)->a+b, FutureKind.widen(Future.successful(4));

        //4
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<future> foldable(){
            BiFunction<Monoid<T>,Higher<future,T>,T> foldRightFn =  (m, l)-> m.apply(m.zero(), FutureKind.narrow(l).get());
            BiFunction<Monoid<T>,Higher<future,T>,T> foldLeftFn = (m, l)->  m.apply(m.zero(), FutureKind.narrow(l).get());
            return General.foldable(foldRightFn, foldLeftFn);
        }
        public static <T> Comonad<future> comonad(){
            Function<? super Higher<future, T>, ? extends T> extractFn = maybe -> maybe.convert(FutureKind::narrow).get();
            return General.comonad(functor(), unit(), extractFn);
        }

        private <T> FutureKind<T> of(T value){
            return widen(Future.successful(value));
        }
        private static <T,R> FutureKind<R> ap(FutureKind<Function< T, R>> lt, FutureKind<T> list){
            return widen(ToCyclopsReact.future(lt).combine(ToCyclopsReact.future(list), (a, b)->a.apply(b)));

        }
        private static <T,R> Higher<future,R> flatMap(Higher<future,T> lt, Function<? super T, ? extends  Higher<future,R>> fn){
            return widen(FutureKind.narrow(lt).flatMap(fn.andThen(FutureKind::narrowK)));
        }
        private static <T,R> FutureKind<R> map(FutureKind<T> lt, Function<? super T, ? extends R> fn){
            return widen(lt.map(fn));
        }


        private static <C2,T,R> Higher<C2, Higher<future, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                              Higher<future, T> ds){
            Future<T> future = FutureKind.narrow(ds);
            return applicative.map(FutureKind::successful, fn.apply(future.get()));
        }

    }

    public static interface FutureNested{


        public static <T> Nested<future,option,T> option(Future<Option<T>> type){
            return Nested.of(widen(type.map(OptionKind::widen)),Instances.definitions(),Options.Instances.definitions());
        }
        public static <T> Nested<future,tryType,T> futureTry(Future<Try<T>> type){
            return Nested.of(widen(type.map(TryKind::widen)),Instances.definitions(),Trys.Instances.definitions());
        }
        public static <T> Nested<future,future,T> future(Future<Future<T>> type){
            return Nested.of(widen(type.map(FutureKind::widen)),Instances.definitions(),Futures.Instances.definitions());
        }
        public static <T> Nested<future,lazy,T> lazy(Future<Lazy<T>> nested){
            return Nested.of(widen(nested.map(LazyKind::widen)),Instances.definitions(),Lazys.Instances.definitions());
        }
        public static <L, R> Nested<future,Higher<VavrWitness.either,L>, R> either(Future<Either<L, R>> nested){
            return Nested.of(widen(nested.map(EitherKind::widen)),Instances.definitions(),Eithers.Instances.definitions());
        }
        public static <T> Nested<future,VavrWitness.queue,T> queue(Future<Queue<T>> nested){
            return Nested.of(widen(nested.map(QueueKind::widen)), Instances.definitions(),Queues.Instances.definitions());
        }
        public static <T> Nested<future,stream,T> stream(Future<Stream<T>> nested){
            return Nested.of(widen(nested.map(StreamKind::widen)),Instances.definitions(),Streams.Instances.definitions());
        }
        public static <T> Nested<future,list,T> list(Future<List<T>> nested){
            return Nested.of(widen(nested.map(ListKind::widen)), Instances.definitions(),Lists.Instances.definitions());
        }
        public static <T> Nested<future,array,T> array(Future<Array<T>> nested){
            return Nested.of(widen(nested.map(ArrayKind::widen)),Instances.definitions(),Arrays.Instances.definitions());
        }
        public static <T> Nested<future,vector,T> vector(Future<Vector<T>> nested){
            return Nested.of(widen(nested.map(VectorKind::widen)),Instances.definitions(),Vectors.Instances.definitions());
        }
        public static <T> Nested<future,hashSet,T> set(Future<HashSet<T>> nested){
            return Nested.of(widen(nested.map(HashSetKind::widen)),Instances.definitions(), HashSets.Instances.definitions());
        }

        public static <T> Nested<future,reactiveSeq,T> reactiveSeq(Future<ReactiveSeq<T>> nested){
            FutureKind<ReactiveSeq<T>> x = widen(nested);
            FutureKind<Higher<reactiveSeq,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<future,maybe,T> maybe(Future<Maybe<T>> nested){
            FutureKind<Maybe<T>> x = widen(nested);
            FutureKind<Higher<maybe,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),Maybe.Instances.definitions());
        }
        public static <T> Nested<future,eval,T> eval(Future<Eval<T>> nested){
            FutureKind<Eval<T>> x = widen(nested);
            FutureKind<Higher<eval,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<future,Witness.future,T> cyclopsFuture(Future<cyclops.async.Future<T>> nested){
            FutureKind<cyclops.async.Future<T>> x = widen(nested);
            FutureKind<Higher<Witness.future,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<future,Higher<xor,S>, P> xor(Future<Xor<S, P>> nested){
            FutureKind<Xor<S, P>> x = widen(nested);
            FutureKind<Higher<Higher<xor,S>, P>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),Xor.Instances.definitions());
        }
        public static <S,T> Nested<future,Higher<reader,S>, T> reader(Future<Reader<S, T>> nested){
            FutureKind<Reader<S, T>> x = widen(nested);
            FutureKind<Higher<Higher<reader,S>, T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<future,Higher<Witness.tryType,S>, P> cyclopsTry(Future<cyclops.control.Try<P, S>> nested){
            FutureKind<cyclops.control.Try<P, S>> x = widen(nested);
            FutureKind<Higher<Higher<Witness.tryType,S>, P>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<future,optional,T> optional(Future<Optional<T>> nested){
            FutureKind<Optional<T>> x = widen(nested);
            FutureKind<Higher<optional,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(), Optionals.Instances.definitions());
        }
        public static <T> Nested<future,completableFuture,T> completableFuture(Future<CompletableFuture<T>> nested){
            FutureKind<CompletableFuture<T>> x = widen(nested);
            FutureKind<Higher<completableFuture,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<future,Witness.stream,T> javaStream(Future<java.util.stream.Stream<T>> nested){
            FutureKind<java.util.stream.Stream<T>> x = widen(nested);
            FutureKind<Higher<Witness.stream,T>> y = (FutureKind)x;
            return Nested.of(y,Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }




    }
    public static interface NestedFuture{
        public static <T> Nested<reactiveSeq,future,T> reactiveSeq(ReactiveSeq<Future<T>> nested){
            ReactiveSeq<Higher<future,T>> x = nested.map(FutureKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<maybe,future,T> maybe(Maybe<Future<T>> nested){
            Maybe<Higher<future,T>> x = nested.map(FutureKind::widenK);

            return Nested.of(x,Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,future,T> eval(Eval<Future<T>> nested){
            Eval<Higher<future,T>> x = nested.map(FutureKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.future,future,T> cyclopsFuture(cyclops.async.Future<Future<T>> nested){
            cyclops.async.Future<Higher<future,T>> x = nested.map(FutureKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<xor,S>,future, P> xor(Xor<S, Future<P>> nested){
            Xor<S, Higher<future,P>> x = nested.map(FutureKind::widenK);

            return Nested.of(x,Xor.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,future, T> reader(Reader<S, Future<T>> nested){

            Reader<S, Higher<future, T>>  x = nested.map(FutureKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,future, P> cyclopsTry(cyclops.control.Try<Future<P>, S> nested){
            cyclops.control.Try<Higher<future,P>, S> x = nested.map(FutureKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<optional,future,T> optional(Optional<Future<T>> nested){
            Optional<Higher<future,T>> x = nested.map(FutureKind::widenK);

            return  Nested.of(Optionals.OptionalKind.widen(x), Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<completableFuture,future,T> completableFuture(CompletableFuture<Future<T>> nested){
            CompletableFuture<Higher<future,T>> x = nested.thenApply(FutureKind::widenK);

            return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,future,T> javaStream(java.util.stream.Stream<Future<T>> nested){
            java.util.stream.Stream<Higher<future,T>> x = nested.map(FutureKind::widenK);

            return Nested.of(cyclops.companion.Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }


}
