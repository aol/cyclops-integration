package cyclops.companion.vavr;

import com.oath.cyclops.types.traversable.IterableX;
import io.vavr.Lazy;
import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.*;
import com.aol.cyclops.vavr.hkt.*;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.monads.*;
import cyclops.monads.VavrWitness.*;
import com.oath.cyclops.hkt.Higher;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.function.Monoid;
import cyclops.monads.Witness.*;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.*;
import com.aol.cyclops.vavr.hkt.EitherKind;
import com.aol.cyclops.vavr.hkt.ListKind;
import cyclops.companion.Monoids;
import cyclops.conversion.vavr.ToCyclopsReact;
import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.types.Value;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.collections.mutable.ListX;
import cyclops.function.Reducer;
import cyclops.monads.VavrWitness.either;
import cyclops.monads.transformers.EitherT;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.comonad.ComonadByPure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.monad.*;
import io.vavr.collection.List;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;


import static com.aol.cyclops.vavr.hkt.EitherKind.narrowK;
import static com.aol.cyclops.vavr.hkt.EitherKind.widen;

/**
 * Utility class for working with Eithers
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Eithers {


    public static  <W1,L,T> Coproduct<W1,Higher<either,L>,T> coproduct(Either<L,T> either, InstanceDefinitions<W1> def1){
        return Coproduct.of(cyclops.control.Either.right(widen(either)),def1,Instances.definitions());
    }
    public static  <W1,L,T> Coproduct<W1,Higher<either,L>,T> coproductRight(T right, InstanceDefinitions<W1> def1){
        return coproduct(Either.right(right),def1);
    }
    public static  <W1,L,T> Coproduct<W1,Higher<either,L>,T> coproductLeft(L left, InstanceDefinitions<W1> def1){
        return coproduct(Either.left(left),def1);
    }
    public static  <W1 extends WitnessType<W1>,L,T> XorM<W1,either,T> xorM(Either<L,T> type){
        return XorM.right(anyM(type));
    }
    public static  <W1 extends WitnessType<W1>,L,T> XorM<W1,either,T> xorMRight(T type){
        return XorM.right(anyM(Either.right(type)));
    }
    public static  <W1 extends WitnessType<W1>,L,T> XorM<W1,either,T> xorMLeft(L type){
        return XorM.right(anyM(Either.left(type)));
    }
    public static <L, R> Either<L, R> xor(cyclops.control.Either<L, R> value) {

        return value.visit(l -> Either.left(l), r -> Either.right(r));
    }
    public static <T> AnyMValue<either,T> anyM(Either<?,T> either) {
        return AnyM.ofValue(either, VavrWitness.either.INSTANCE);
    }

    public static <L, T, R> Either<L, R> tailRec(T initial, Function<? super T, ? extends Either<L, ? extends Either<T, R>>> fn) {
        Either<L,? extends Either<T, R>> next[] = new Either[1];
        next[0] = Either.right(Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].fold(__ -> false,p -> p.fold(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false));
        } while (cont);
        return next[0].map(Either::get);
    }
    public static <L, T, R> Either<L, R> tailRecEither(T initial, Function<? super T, ? extends Either<L, ? extends cyclops.control.Either<T, R>>> fn) {
        Either<L,? extends cyclops.control.Either<T, R>> next[] = new Either[1];
        next[0] = Either.right(cyclops.control.Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].fold(__ -> false,p -> p.visit(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false));
        } while (cont);
        return next[0].map(e->e.orElse(null));
    }




    /**
     * Perform a For Comprehension over a Either, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Eithers.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Eithers.forEach4;
     *
    forEach4(Either.just(1),
    a-> Either.just(a+1),
    (a,b) -> Either.<Integer>just(a+b),
    a                  (a,b,c) -> Either.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Either
     * @param value2 Nested Either
     * @param value3 Nested Either
     * @param value4 Nested Either
     * @param yieldingFunction Generates a result per combination
     * @return Either with a combined value generated by the yielding function
     */
    public static <L,T1, T2, T3, R1, R2, R3, R> Either<L,R> forEach4(Either<L,? extends T1> value1,
                                                                 Function<? super T1, ? extends Either<L,R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
                                                                 Function3<? super T1, ? super R1, ? super R2, ? extends Either<L,R3>> value4,
                                                                 Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Either<L,R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Either<L,R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }


    /**
     * Perform a For Comprehension over a Either, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Eithers.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Eithers.forEach3;
     *
    forEach3(Either.just(1),
    a-> Either.just(a+1),
    (a,b) -> Either.<Integer>just(a+b),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Either
     * @param value2 Nested Either
     * @param value3 Nested Either
     * @param yieldingFunction Generates a result per combination
     * @return Either with a combined value generated by the yielding function
     */
    public static <L,T1, T2, R1, R2, R> Either<L,R> forEach3(Either<L,? extends T1> value1,
                                                         Function<? super T1, ? extends Either<L,R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
                                                         Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Either<L,R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }



    /**
     * Perform a For Comprehension over a Either, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Eithers.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Eithers.forEach;
     *
    forEach(Either.just(1),
    a-> Either.just(a+1),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Either
     * @param value2 Nested Either
     * @param yieldingFunction Generates a result per combination
     * @return Either with a combined value generated by the yielding function
     */
    public static <L,T, R1, R> Either<L,R> forEach2(Either<L,? extends T> value1, Function<? super T, Either<L,R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });



    }






    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * By constrast with {@link Eithers#sequencePresent(IterableX)}, if any Eithers are empty the result
     * is an empty Either
     *
     * <pre>
     * {@code
     *
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();
     *
     *  Either<ListX<Integer>> opts = Eithers.sequence(ListX.of(just, none, Either.of(1)));
    //Either.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Maybe with a List of values
     */
    public static <L,T> Either<L,ListX<T>> sequence(final IterableX<Either<L,T>> opts) {
        java.util.stream.Stream<Either<L, T>> s = opts.stream();
        return sequence(s).map(r ->r.toListX());

    }
    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * Only successes are retained. By constrast with {@link Eithers#sequence(IterableX)} Either#empty types are
     * tolerated and ignored.
     *
     * <pre>
     * {@code
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();
     *
     * Either<ListX<Integer>> maybes = Eithers.sequencePresent(ListX.of(just, none, Either.of(1)));
    //Either.of(ListX.of(10, 1));
     * }
     * </pre>
     *
     * @param opts Eithers to Sequence
     * @return Either with a List of values
     */
    public static <L,T> Either<L,ListX<T>> sequencePresent(final IterableX<Either<L,T>> opts) {
      java.util.stream.Stream<Either<L, T>> s = opts.stream();
        return sequence(s.filter(Either::isRight)).map(r->r.toListX());
    }
    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * By constrast with {@link Eithers#sequencePresent(IterableX)} if any Either types are empty
     * the return type will be an empty Either
     *
     * <pre>
     * {@code
     *
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();
     *
     *  Either<ListX<Integer>> maybes = Eithers.sequence(ListX.of(just, none, Either.of(1)));
    //Either.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Either with a List of values
     */
    public static <L,T> Either<L,ReactiveSeq<T>> sequence(final java.util.stream.Stream<Either<L,T>> opts) {
        return AnyM.sequence(opts.map(Eithers::anyM), either.INSTANCE)
                .map(ReactiveSeq::fromStream)
                .to(VavrWitness::either);

    }
    /**
     * Accummulating operation using the supplied Reducer (@see cyclops2.Reducers). A typical use case is to accumulate into a Persistent Collection type.
     * Accumulates the present results, ignores empty Eithers.
     *
     * <pre>
     * {@code
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();

     * Either<PersistentSetX<Integer>> opts = Either.accumulateJust(ListX.of(just, none, Either.of(1)), Reducers.toPersistentSetX());
    //Either.of(PersistentSetX.of(10, 1)));
     *
     * }
     * </pre>
     *
     * @param eithers Eithers to accumulate
     * @param reducer Reducer to accumulate values with
     * @return Either with reduced value
     */
    public static <T, L,R> Either<L,R> accumulatePresent(final IterableX<Either<L,T>> eithers, final Reducer<R,T> reducer) {
        return sequencePresent(eithers).map(s -> s.mapReduce(reducer));
    }
    /**
     * Accumulate the results only from those Eithers which have a value present, using the supplied mapping function to
     * convert the data from each Either before reducing them using the supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();

     *  Either<String> opts = Either.accumulateJust(ListX.of(just, none, Either.of(1)), i -> "" + i,
    Monoids.stringConcat);
    //Either.of("101")
     *
     * }
     * </pre>
     *
     * @param eithers Eithers to accumulate
     * @param mapper Mapping function to be applied to the result of each Either
     * @param reducer Monoid to combine values from each Either
     * @return Either with reduced value
     */
    public static <T,L, R> Either<L,R> accumulatePresent(final IterableX<Either<L,T>> eithers, final Function<? super T, R> mapper,
                                                     final Monoid<R> reducer) {
        return sequencePresent(eithers).map(s -> s.map(mapper)
                .reduce(reducer));
    }
    /**
     * Accumulate the results only from those Eithers which have a value present, using the
     * supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Either<Integer> just = Either.of(10);
    Either<Integer> none = Either.empty();

     *  Either<String> opts = Either.accumulateJust(Monoids.stringConcat,ListX.of(just, none, Either.of(1)),
    );
    //Either.of("101")
     *
     * }
     * </pre>
     *
     * @param eitherals Eithers to accumulate
     * @param reducer Monoid to combine values from each Either
     * @return Either with reduced value
     */
    public static <L,T> Either<L,T> accumulatePresent(final Monoid<T> reducer, final CollectionX<Either<L,T>> eitherals) {
        return sequencePresent(eitherals).map(s -> s
                .reduce(reducer));
    }


    /**
     * Combine an Either with the provided Either using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Eithers.combine(Either.of(10),Either.of(20), this::add)
     *  //Either[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param f Either to combine with a value
     * @param v Either to combine
     * @param fn Combining function
     * @return Either combined with supplied value, or empty Either if no value present
     */
    public static <T1, T2, L,R> Either<L,R> zip(final Either<L,? extends T1> f, final Either<L,? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
      return narrow(FromCyclopsReact.either(ToCyclopsReact.either(f)
        .zip(ToCyclopsReact.either(v), fn)));
    }



    /**
     * Narrow covariant type parameter
     *
     * @param eitheral Either with covariant type parameter
     * @return Narrowed Either
     */
    public static <L,T> Either<L,T> narrow(final Either<L,? extends T> eitheral) {
        return (Either<L,T>) eitheral;
    }


    public static <L,R> Active<Higher<either,L>,R> allTypeclasses(Either<L,R> either){
        return Active.of(widen(either), Eithers.Instances.definitions());
    }
    public static <L,R,W2,R2> Nested<Higher<either,L>,W2,R2> mapM(Either<L,R> either, Function<? super R,? extends Higher<W2,R2>> fn, InstanceDefinitions<W2> defs){
        Either<L, Higher<W2, R2>> e = either.bimap(i -> i, i -> fn.apply(i));
        EitherKind<L, Higher<W2, R2>> ek = widen(e);
        return Nested.of(ek, Eithers.Instances.definitions(), defs);
    }
    public <L,R,W extends WitnessType<W>> EitherT<W, L, R> liftM(Either<L,R> either, W witness) {
        return EitherT.of(witness.adapter().unit(ToCyclopsReact.either(either)));
    }

    public static class Instances {

        public static <L> InstanceDefinitions<Higher<either, L>> definitions() {
            return new InstanceDefinitions<Higher<either, L>>() {
                @Override
                public <T, R> Functor<Higher<either, L>> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<Higher<either, L>> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<Higher<either, L>> applicative() {
                    return Instances.applicative();
                }

                @Override
                public <T, R> Monad<Higher<either, L>> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<Higher<either, L>>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<Higher<either, L>>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<Higher<either, L>> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> Maybe<MonadPlus<Higher<either, L>>> monadPlus(Monoid<Higher<Higher<either, L>, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<Higher<either, L>> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<Higher<either, L>> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> Maybe<Comonad<Higher<either, L>>> comonad() {
                    return Maybe.just(Instances.comonad());
                }

                @Override
                public <T> Maybe<Unfoldable<Higher<either, L>>> unfoldable() {
                    return Maybe.nothing();
                }
            };
        }

        public static <L> Functor<Higher<either, L>> functor() {
            return new Functor<Higher<either, L>>() {

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return widen(either.bimap(i -> i, r -> fn.apply(r)));
                }
            };
        }

        public static <L> Pure<Higher<either, L>> unit() {
            return new Pure<Higher<either, L>>() {

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return EitherKind.rightK(value);
                }
            };
        }

        public static <L> Applicative<Higher<either, L>> applicative() {
            return new Applicative<Higher<either, L>>() {


                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    Either<L, T> either = narrowK(apply);
                    Either<L, ? extends Function<T, R>> eitherFn = narrowK(fn);
                    return widen(Eithers.xor(ToCyclopsReact.either(eitherFn).zip(ToCyclopsReact.either(either), (a, b) -> a.apply(b))));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }

        public static <L> Monad<Higher<either, L>> monad() {
            return new Monad<Higher<either, L>>() {

                @Override
                public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return widen(either.flatMap(fn.andThen(EitherKind::narrowK)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn, apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }

        public static <L> Traverse<Higher<either, L>> traverse() {
            return new Traverse<Higher<either, L>>() {

                @Override
                public <C2, T, R> Higher<C2, Higher<Higher<either, L>, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> maybe = narrowK(ds);
                    return maybe.fold(left -> applicative.unit(EitherKind.<L, R>leftK(left)),
                            right -> applicative.map(m -> EitherKind.rightK(m), fn.apply(right)));
                }

                @Override
                public <C2, T> Higher<C2, Higher<Higher<either, L>, T>> sequenceA(Applicative<C2> applicative, Higher<Higher<either, L>, Higher<C2, T>> ds) {
                    return traverseA(applicative, Function.identity(), ds);
                }


                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn, apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }
        public static <L> MonadRec<Higher<either, L>> monadRec() {
            return new MonadRec<Higher<either, L>>() {
                @Override
                public <T, R> Higher<Higher<either, L>, R> tailRec(T initial, Function<? super T, ? extends Higher<Higher<either, L>, ? extends cyclops.control.Either<T, R>>> fn) {
                    return widen(Eithers.tailRecEither(initial,fn.andThen(EitherKind::narrowK)));
                }
            };
        }

        public static <L> Foldable<Higher<either, L>> foldable() {
            return new Foldable<Higher<either, L>>() {


                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return ToCyclopsReact.either(either).fold(monoid);
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return ToCyclopsReact.either(either).fold(monoid);
                }

                @Override
                public <T, R> R foldMap(Monoid<R> mb, Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> nestedA) {
                    return narrowK(nestedA).map(fn).fold(l->mb.zero(), r -> r);
                }
            };
        }

        public static <L> MonadZero<Higher<either, L>> monadZero() {
            return new MonadZero<Higher<either, L>>() {

                @Override
                public Higher<Higher<either, L>, ?> zero() {
                    return EitherKind.leftK(null);
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return widen(either.flatMap(fn.andThen(EitherKind::narrowK)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn, apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }

        public static <L> MonadPlus<Higher<either, L>> monadPlus() {
            Monoid m = Monoids.firstRightEither((Either) narrowK(Instances.<L>monadZero().zero()));

            return monadPlus(m);
        }

        public static <L, T> MonadPlus<Higher<either, L>> monadPlus(Monoid<Higher<Higher<either, L>, T>> m) {
            return new MonadPlus<Higher<either, L>>() {

                @Override
                public Monoid<Higher<Higher<either, L>, ?>> monoid() {
                    return (Monoid) m;
                }

                @Override
                public Higher<Higher<either, L>, ?> zero() {
                    return Instances.<L>monadZero().zero();
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return widen(either.flatMap(fn.andThen(EitherKind::narrowK)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn, apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }

        public static <L> Comonad<Higher<either, L>> comonad() {
            return new ComonadByPure<Higher<either, L>>() {


                @Override
                public <T> T extract(Higher<Higher<either, L>, T> ds) {
                    Either<L, T> either = narrowK(ds);
                    return either.get();
                }


                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn, ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }

        public static interface EitherNested {


            public static <L, T> Nested<Higher<either, L>, lazy, T> lazy(Either<L, Lazy<T>> type) {
                return Nested.of(widen(type.map(LazyKind::widen)), Instances.definitions(), Lazys.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, VavrWitness.tryType, T> eitherTry(Either<L, Try<T>> type) {
                return Nested.of(widen(type.map(TryKind::widen)), Instances.definitions(), Trys.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, VavrWitness.future, T> future(Either<L, Future<T>> type) {
                return Nested.of(widen(type.map(FutureKind::widen)), Instances.definitions(), Futures.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, Higher<either, L>, T> either(Either<L, Either<L, T>> nested) {
                return Nested.of(widen(nested.map(EitherKind::widen)), Instances.definitions(), Eithers.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, VavrWitness.queue, T> queue(Either<L, Queue<T>> nested) {
                return Nested.of(widen(nested.map(QueueKind::widen)), Instances.definitions(), Queues.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, VavrWitness.stream, T> stream(Either<L, Stream<T>> nested) {
                return Nested.of(widen(nested.map(StreamKind::widen)), Instances.definitions(), Streams.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, VavrWitness.list, T> list(Either<L, List<T>> nested) {
                return Nested.of(widen(nested.map(ListKind::widen)), Instances.definitions(), Lists.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, array, T> array(Either<L, Array<T>> nested) {
                return Nested.of(widen(nested.map(ArrayKind::widen)), Instances.definitions(), Arrays.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, vector, T> vector(Either<L, Vector<T>> nested) {
                return Nested.of(widen(nested.map(VectorKind::widen)), Instances.definitions(), Vectors.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, hashSet, T> set(Either<L, HashSet<T>> nested) {
                return Nested.of(widen(nested.map(HashSetKind::widen)), Instances.definitions(), HashSets.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, reactiveSeq, T> reactiveSeq(Either<L, ReactiveSeq<T>> nested) {
                EitherKind<L, ReactiveSeq<T>> x = widen(nested);
                EitherKind<L, Higher<reactiveSeq, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), ReactiveSeq.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, maybe, T> maybe(Either<L, Maybe<T>> nested) {
                EitherKind<L, Maybe<T>> x = widen(nested);
                EitherKind<L, Higher<maybe, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), Maybe.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, eval, T> eval(Either<L, Eval<T>> nested) {
                EitherKind<L, Eval<T>> x = widen(nested);
                EitherKind<L, Higher<eval, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), Eval.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, Witness.future, T> cyclopsFuture(Either<L, cyclops.async.Future<T>> nested) {
                EitherKind<L, cyclops.async.Future<T>> x = widen(nested);
                EitherKind<L, Higher<Witness.future, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), cyclops.async.Future.Instances.definitions());
            }

            public static <L, S, P> Nested<Higher<either, L>, Higher<Witness.either, S>, P> xor(Either<L, cyclops.control.Either<S, P>> nested) {
                EitherKind<L, cyclops.control.Either<S, P>> x = widen(nested);
                EitherKind<L, Higher<Higher<Witness.either, S>, P>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), cyclops.control.Either.Instances.definitions());
            }

            public static <L, S, T> Nested<Higher<either, L>, Higher<reader, S>, T> reader(Either<L, Reader<S, T>> nested, S defaultValue) {
                EitherKind<L, Reader<S, T>> x = widen(nested);
                EitherKind<L, Higher<Higher<reader, S>, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), Reader.Instances.definitions(defaultValue));
            }

            public static <L, S extends Throwable, P> Nested<Higher<either, L>, Higher<Witness.tryType, S>, P> cyclopsTry(Either<L, cyclops.control.Try<P, S>> nested) {
                EitherKind<L, cyclops.control.Try<P, S>> x = widen(nested);
                EitherKind<L, Higher<Higher<Witness.tryType, S>, P>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), cyclops.control.Try.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, optional, T> optional(Either<L, Optional<T>> nested) {
                EitherKind<L, Optional<T>> x = widen(nested);
                EitherKind<L, Higher<optional, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), Optionals.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, completableFuture, T> completableEither(Either<L, CompletableFuture<T>> nested) {
                EitherKind<L, CompletableFuture<T>> x = widen(nested);
                EitherKind<L, Higher<completableFuture, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
            }

            public static <L, T> Nested<Higher<either, L>, Witness.stream, T> javaStream(Either<L, java.util.stream.Stream<T>> nested) {
                EitherKind<L, java.util.stream.Stream<T>> x = widen(nested);
                EitherKind<L, Higher<Witness.stream, T>> y = (EitherKind) x;
                return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
            }




        }
        public static interface NestedEither {
            public static <L, T> Nested<reactiveSeq, Higher<either, L>, T> reactiveSeq(ReactiveSeq<Either<L, T>> nested) {
                ReactiveSeq<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);
                return Nested.of(x, ReactiveSeq.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<maybe, Higher<either, L>, T> maybe(Maybe<Either<L, T>> nested) {
                Maybe<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(x, Maybe.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<eval, Higher<either, L>, T> eval(Eval<Either<L, T>> nested) {
                Eval<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(x, Eval.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<Witness.future, Higher<either, L>, T> cyclopsFuture(cyclops.async.Future<Either<L, T>> nested) {
                cyclops.async.Future<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(x, cyclops.async.Future.Instances.definitions(), Instances.definitions());
            }

            public static <L, S, P> Nested<Higher<Witness.either, S>, Higher<either, L>, P> xor(cyclops.control.Either<S, Either<L, P>> nested) {
              cyclops.control.Either<S, Higher<Higher<either, L>, P>> x = nested.map(EitherKind::widenK);

                return Nested.of(x, cyclops.control.Either.Instances.definitions(), Instances.definitions());
            }

            public static <L, S, T> Nested<Higher<reader, S>, Higher<either, L>, T> reader(Reader<S, Either<L, T>> nested, S defaultValue) {

                Reader<S, Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(x, Reader.Instances.definitions(defaultValue), Instances.definitions());
            }

            public static <L, S extends Throwable, P> Nested<Higher<Witness.tryType, S>, Higher<either, L>, P> cyclopsTry(cyclops.control.Try<Either<L, P>, S> nested) {
                cyclops.control.Try<Higher<Higher<either, L>, P>, S> x = nested.map(EitherKind::widenK);

                return Nested.of(x, cyclops.control.Try.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<optional, Higher<either, L>, T> optional(Optional<Either<L, T>> nested) {
                Optional<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(Optionals.OptionalKind.widen(x), Optionals.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<completableFuture, Higher<either, L>, T> completableFuture(CompletableFuture<Either<L, T>> nested) {
                CompletableFuture<Higher<Higher<either, L>, T>> x = nested.thenApply(EitherKind::widenK);

                return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(), Instances.definitions());
            }

            public static <L, T> Nested<Witness.stream, Higher<either, L>, T> javaStream(java.util.stream.Stream<Either<L, T>> nested) {
                java.util.stream.Stream<Higher<Higher<either, L>, T>> x = nested.map(EitherKind::widenK);

                return Nested.of(cyclops.companion.Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(), Instances.definitions());
            }
        }
    }


    }
