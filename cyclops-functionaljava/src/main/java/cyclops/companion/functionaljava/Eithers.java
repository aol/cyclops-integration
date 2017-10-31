package cyclops.companion.functionaljava;


import com.aol.cyclops.functionaljava.hkt.*;
import com.oath.cyclops.hkt.Higher;
import com.oath.cyclops.hkt.Higher2;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CompletableFutures;
import cyclops.companion.CompletableFutures.CompletableFutureKind;
import cyclops.companion.Monoids;
import cyclops.companion.Optionals;
import cyclops.companion.Optionals.OptionalKind;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Reader;
import cyclops.control.Try;
import cyclops.monads.*;
import cyclops.conversion.functionaljava.FromCyclopsReact;
import cyclops.conversion.functionaljava.ToCyclopsReact;
import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.types.Value;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.function.Monoid;
import cyclops.function.Reducer;
import cyclops.monads.FJWitness.either;
import cyclops.monads.FJWitness.list;
import cyclops.monads.FJWitness.nonEmptyList;
import cyclops.monads.FJWitness.option;
import cyclops.monads.Witness.*;
import cyclops.monads.transformers.EitherT;
import cyclops.reactive.ReactiveSeq;

import cyclops.typeclasses.*;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.comonad.ComonadByPure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.monad.*;
import fj.data.Either;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.aol.cyclops.functionaljava.hkt.EitherKind.widen;

/**
 * Utility class for working with Eithers
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Eithers {

    public static <L, T, R> Either<L, R> tailRec(T initial, Function<? super T, ? extends Either<L, ? extends Either<T, R>>> fn) {
        Either<L,? extends Either<T, R>> next[] = new Either[1];
        next[0] = Either.right(Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].either(__ -> false,p -> p.either(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false));
        } while (cont);
        return next[0].right().map(e->e.right()
                                       .iterator()
                                       .next());
    }
    public static <L, T, R> Either<L, R> tailRecEither(T initial, Function<? super T, ? extends Either<L, ? extends cyclops.control.Either<T, R>>> fn) {
        Either<L,? extends cyclops.control.Either<T, R>> next[] = new Either[1];
        next[0] = Either.right(cyclops.control.Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].either(__ -> false,p -> p.visit(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false));
        } while (cont);
        return next[0].right().map(e->e.orElse(null));
    }

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
    public static  <W1 extends WitnessType<W1>,L,T> XorM<W1,either,T> xorMRight(T right){
        return XorM.right(anyM(Either.right(right)));
    }
    public static  <W1 extends WitnessType<W1>,L,T> XorM<W1,either,T> xorMLeft(L left){
        return XorM.right(anyM(Either.left(left)));
    }
    public static <L, R> Either<L, R> xor(cyclops.control.Either<L, R> value) {

        return value.visit(l -> Either.left(l), r -> Either.right(r));
    }

    public static <T> AnyMValue<either,T> anyM(Either<?,T> either) {
        return AnyM.ofValue(either, FJWitness.either.INSTANCE);
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

        return value1.right().bind(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.right().bind(ina -> {
                Either<L,R2> b = value3.apply(in,ina);
                return b.right().bind(inb -> {
                    Either<L,R3> c = value4.apply(in,ina,inb);
                    return c.right().map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
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

        return value1.right().bind(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.right().bind(ina -> {
                Either<L,R2> b = value3.apply(in,ina);
                return b.right().map(in2 -> yieldingFunction.apply(in, ina, in2));
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

        return value1.right().bind(in -> {

            Either<L,R1> a = value2.apply(in);
            return a.right().map(in2 -> yieldingFunction.apply(in,  in2));
        });



    }






    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * By constrast with {@link Eithers#sequencePresent(CollectionX)}, if any Eithers are empty the result
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
    public static <L,T> Either<L,ListX<T>> sequence(final CollectionX<Either<L,T>> opts) {
        return sequence(opts.stream()).right().map(s -> s.toListX());

    }
    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * Only successes are retained. By constrast with {@link Eithers#sequence(CollectionX)} Either#empty types are
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
    public static <L,T> Either<L,ListX<T>> sequencePresent(final CollectionX<Either<L,T>> opts) {
        return sequence(opts.stream().filter(Either::isRight)).right().map(s->s.toListX());
    }
    /**
     * Sequence operation, take a Collection of Eithers and turn it into a Either with a Collection
     * By constrast with {@link Eithers#sequencePresent(CollectionX)} if any Either types are empty
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
    public static <L,T> Either<L,ReactiveSeq<T>> sequence(final Stream<Either<L,T>> opts) {
        return AnyM.sequence(opts.map(Eithers::anyM), either.INSTANCE)
                .map(ReactiveSeq::fromStream)
                .to(FJWitness::either);

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
    public static <T, L,R> Either<L,R> accumulatePresent(final CollectionX<Either<L,T>> eithers, final Reducer<R,T> reducer) {
        return sequencePresent(eithers).right().map(s -> s.mapReduce(reducer));
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
    public static <T,L, R> Either<L,R> accumulatePresent(final CollectionX<Either<L,T>> eithers, final Function<? super T, R> mapper,
                                                     final Monoid<R> reducer) {
        return sequencePresent(eithers).right().map(s -> s.map(mapper)
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
        return sequencePresent(eitherals).right().map(s -> s
                .reduce(reducer));
    }

    /**
     * Combine an Either with the provided value using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Eithers.combine(Either.of(10),Maybe.just(20), this::add)
     *  //Either[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Either to combine with a value
     * @param v Value to combine
     * @param fn Combining function
     * @return Either combined with supplied value
     */
    public static <T1, T2, L,R> Either<L,R> combine(final Either<L,? extends T1> f, final Value<? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.either(ToCyclopsReact.xor(f)
                .combine(v, fn)));
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
    public static <T1, T2, L,R> Either<L,R> combine(final Either<L,? extends T1> f, final Either<L,? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return combine(f,ToCyclopsReact.xor(v),fn);
    }

    /**
     * Combine an Either with the provided Iterable (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Eithers.zip(Either.of(10),Arrays.asList(20), this::add)
     *  //Either[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Either to combine with first element in Iterable (if present)
     * @param v Iterable to combine
     * @param fn Combining function
     * @return Either combined with supplied Iterable, or empty Either if no value present
     */
    public static <T1, T2, L,R> Either<L,R> zip(final Either<L,? extends T1> f, final Iterable<? extends T2> v,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.either(ToCyclopsReact.xor(f)
                .zip(v, fn)));
    }

    /**
     * Combine an Either with the provided Publisher (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Eithers.zip(Flux.just(10),Either.of(10), this::add)
     *  //Either[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param p Publisher to combine
     * @param f  Either to combine with
     * @param fn Combining function
     * @return Either combined with supplied Publisher, or empty Either if no value present
     */
    public static <T1, T2, L,R> Either<L,R> zip(final Publisher<? extends T2> p, final Either<L,? extends T1> f,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.either(ToCyclopsReact.xor(f)
                .zipP(p, fn)));
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
    public static <L,R,W2,R2> Nested<Higher<either,L>,W2,R2> mapM(Either<L,R> either,Function<? super R,? extends Higher<W2,R2>> fn, InstanceDefinitions<W2> defs){
        Either<L, Higher<W2, R2>> e = either.bimap(i -> i, i -> fn.apply(i));
        EitherKind<L, Higher<W2, R2>> ek = widen(e);
        return Nested.of(ek, Eithers.Instances.definitions(), defs);
    }
    public <L,R,W extends WitnessType<W>> EitherT<W, L, R> liftM(Either<L,R> either,W witness) {
        return EitherT.of(witness.adapter().unit(ToCyclopsReact.xor(either)));
    }

    public static class Instances {

        public static <L> InstanceDefinitions<Higher<either, L>> definitions(){
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
                    fj.data.Either<L,T> either = EitherKind.narrowK(ds);
                    return widen(either.bimap(i->i,r->fn.apply(r)));
                }
            };
        }
        public static <L> Pure<Higher<either, L>> unit() {
            return new Pure<Higher<either, L>>() {

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return EitherKind.right(value);
                }
            };
        }
        public static <L> Applicative<Higher<either, L>> applicative() {
            return new Applicative<Higher<either, L>>() {


                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    Either<L,T>  either = EitherKind.narrowK(apply);
                    fj.data.Either<L, ? extends Function<T, R>> eitherFn = EitherKind.narrowK(fn);
                    return widen(Eithers.xor(ToCyclopsReact.xor(eitherFn).zip(ToCyclopsReact.xor(either),(a,b)->a.apply(b))));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
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
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return widen(either.right().bind(i->fn.andThen(EitherKind::narrowK).apply(i)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn,apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
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
                    Either<L,T> maybe = EitherKind.narrowK(ds);
                    return maybe.either(left->  applicative.unit(EitherKind.<L,R>left(left)),
                            right->applicative.map(m->EitherKind.right(m), fn.apply(right)));
                }

                @Override
                public <C2, T> Higher<C2, Higher<Higher<either, L>, T>> sequenceA(Applicative<C2> applicative, Higher<Higher<either, L>, Higher<C2, T>> ds) {
                    return traverseA(applicative,Function.identity(),ds);
                }



                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn,apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
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
                public <T, R> Higher<Higher<either, L>, R> tailRec(T initial, Function<? super T, ? extends Higher<Higher<either, L>, ? extends Either<T, R>>> fn) {
                    return widen(Eithers.tailRecEither(initial,fn.andThen(EitherKind::narrowK)));
                }
            };
        }
        public static <L> Foldable<Higher<either, L>> foldable() {
            return new Foldable<Higher<either, L>>() {


                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<Higher<either, L>, T> ds) {
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return ToCyclopsReact.xor(either).fold(monoid);
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<Higher<either, L>, T> ds) {
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return ToCyclopsReact.xor(either).fold(monoid);
                }

                @Override
                public <T, R> R foldMap(Monoid<R> mb, Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return EitherKind.narrowK(ds).right().map(a->fn.apply(a)).either(l->mb.zero(),r->mb.apply(mb.zero(),r));
                }
            };
        }
        public static <L> MonadZero<Higher<either, L>> monadZero() {
            return new MonadZero<Higher<either, L>>() {

                @Override
                public Higher<Higher<either, L>, ?> zero() {
                    return EitherKind.left(null);
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return widen(either.right().bind(i->fn.andThen(EitherKind::narrowK).apply(i)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn,apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }
        public static <L> MonadPlus<Higher<either, L>> monadPlus() {
            Monoid m = Monoids.firstRightEither((Either)EitherKind.narrowK(Instances.<L>monadZero().zero()));

            return monadPlus(m);
        }
        public static <L,T> MonadPlus<Higher<either, L>> monadPlus(Monoid<Higher<Higher<either, L>, T>> m) {
            return new MonadPlus<Higher<either, L>>() {

                @Override
                public Monoid<Higher<Higher<either, L>, ?>> monoid() {
                    return (Monoid)m;
                }

                @Override
                public Higher<Higher<either, L>, ?> zero() {
                    return Instances.<L>monadZero().zero();
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> flatMap(Function<? super T, ? extends Higher<Higher<either, L>, R>> fn, Higher<Higher<either, L>, T> ds) {
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return widen(either.right().bind(i->fn.andThen(EitherKind::narrowK).apply(i)));
                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> ap(Higher<Higher<either, L>, ? extends Function<T, R>> fn, Higher<Higher<either, L>, T> apply) {
                    return Instances.<L>applicative().ap(fn,apply);

                }

                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
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
                    Either<L,T> either = EitherKind.narrowK(ds);
                    return either.right().value();
                }


                @Override
                public <T, R> Higher<Higher<either, L>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<either, L>, T> ds) {
                    return Instances.<L>functor().map(fn,ds);
                }

                @Override
                public <T> Higher<Higher<either, L>, T> unit(T value) {
                    return Instances.<L>unit().unit(value);
                }
            };
        }
    }

    public static interface EitherNested {
        public static <L1,L2,T> Nested<Higher<either,L1>,Higher<either,L2>,T> either(Either<L1,Either<L2,T>> nested){
            Either<L1,EitherKind<L2,T>> f = nested.bimap(i->i,EitherKind::widen);
            EitherKind<L1,EitherKind<L2,T>> x = widen(f);
            EitherKind<L1,Higher<Higher<either,L2>,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(), Eithers.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,nonEmptyList,T> nonEmptyList(Either<L,NonEmptyList<T>> nested){
            Either<L,NonEmptyListKind<T>> f = nested.bimap(i->i,NonEmptyListKind::widen);
            EitherKind<L,NonEmptyListKind<T>> x = widen(f);
            EitherKind<L,Higher<nonEmptyList,T>> y = (EitherKind)x;

            return Nested.of(y,Instances.definitions(), NonEmptyLists.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,option,T> option(Either<L,Option<T>> nested){
            Either<L,OptionKind<T>> f = nested.bimap(i->i,OptionKind::widen);
            EitherKind<L,OptionKind<T>> x = widen(f);
            EitherKind<L,Higher<option,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),Options.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,FJWitness.stream,T> stream(Either<L,fj.data.Stream<T>> nested){
            Either<L,StreamKind<T>> f = nested.bimap(i->i,StreamKind::widen);
            EitherKind<L,StreamKind<T>> x = widen(f);
            EitherKind<L,Higher<FJWitness.stream,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.companion.functionaljava.Streams.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,list,T> list(Either<L,List<T>> nested){
            Either<L,ListKind<T>> f = nested.bimap(i->i,ListKind::widen);
            EitherKind<L,ListKind<T>> x = widen(f);
            EitherKind<L,Higher<list,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),Lists.Instances.definitions());
        }

        public static <L,T> Nested<Higher<either,L>,reactiveSeq,T> reactiveSeq(Either<L,ReactiveSeq<T>> nested){
            EitherKind<L,ReactiveSeq<T>> x = widen(nested);
            EitherKind<L,Higher<reactiveSeq,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <L,T> Nested<Higher<either,L>,Witness.maybe,T> maybe(Either<L,Maybe<T>> nested){
            EitherKind<L,Maybe<T>> x = widen(nested);
            EitherKind<L,Higher<Witness.maybe,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),Maybe.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,Witness.eval,T> eval(Either<L,Eval<T>> nested){
            EitherKind<L,Eval<T>> x = widen(nested);
            EitherKind<L,Higher<Witness.eval,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,Witness.future,T> future(Either<L,cyclops.async.Future<T>> nested){
            EitherKind<L,cyclops.async.Future<T>> x = widen(nested);
            EitherKind<L,Higher<Witness.future,T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <L,S, P> Nested<Higher<either,L>,Higher<Witness.either,S>, P> xor(Either<L,Either<S, P>> nested){
            EitherKind<L,Either<S, P>> x = widen(nested);
            EitherKind<L,Higher<Higher<xor,S>, P>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Either.Instances.definitions());
        }
        public static <L,S,T> Nested<Higher<either,L>,Higher<reader,S>, T> reader(Either<L,Reader<S, T>> nested,S defaultValue){
            EitherKind<L,Reader<S, T>> x = widen(nested);
            EitherKind<L,Higher<Higher<reader,S>, T>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <L,S extends Throwable, P> Nested<Higher<either,L>,Higher<tryType,S>, P> cyclopsTry(Either<L,Try<P, S>> nested){
            EitherKind<L,Try<P, S>> x = widen(nested);
            EitherKind<L,Higher<Higher<tryType,S>, P>> y = (EitherKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,optional,T> javaOptional(Either<L,Optional<T>> nested){
            Either<L,OptionalKind<T>> f = nested.bimap(i->i,o -> OptionalKind.widen(o));
            EitherKind<L,OptionalKind<T>> x = widen(f);

            EitherKind<L,Higher<optional,T>> y = (EitherKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Optionals.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,completableFuture,T> javaCompletableFuture(Either<L,CompletableFuture<T>> nested){
            Either<L,CompletableFutureKind<T>> f = nested.bimap(i->i,o -> CompletableFutureKind.widen(o));
            EitherKind<L,CompletableFutureKind<T>> x = widen(f);
            EitherKind<L,Higher<completableFuture,T>> y = (EitherKind)x;
            return Nested.of(y, Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <L,T> Nested<Higher<either,L>,Witness.stream,T> javaStream(Either<L,java.util.stream.Stream<T>> nested){
            Either<L,cyclops.companion.Streams.StreamKind<T>> f = nested.bimap(i->i,o -> cyclops.companion.Streams.StreamKind.widen(o));
            EitherKind<L,cyclops.companion.Streams.StreamKind<T>> x = widen(f);
            EitherKind<L,Higher<Witness.stream,T>> y = (EitherKind)x;
            return Nested.of(y, Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }

    }

    public static interface NestedEither{
        public static <L,T> Nested<reactiveSeq,Higher<either,L>,T> reactiveSeq(ReactiveSeq<Either<L,T>> nested){
            ReactiveSeq<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <L,T> Nested<maybe,Higher<either,L>,T> maybe(Maybe<Either<L,T>> nested){
            Maybe<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);

            return Nested.of(x,Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <L,T> Nested<eval,Higher<either,L>,T> eval(Eval<Either<L,T>> nested){
            Eval<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <L,T> Nested<Witness.future,Higher<either,L>,T> future(cyclops.async.Future<Either<L,T>> nested){
            cyclops.async.Future<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <L,S, P> Nested<Higher<xor,S>,Higher<either,L>, P> xor(Either<S, Either<L,P>> nested){
            Either<S, Higher<Higher<either,L>,P>> x = nested.map(EitherKind::widenK);

            return Nested.of(x,Either.Instances.definitions(),Instances.definitions());
        }
        public static <L,S,T> Nested<Higher<reader,S>,Higher<either,L>, T> reader(Reader<S, Either<L,T>> nested, S defaultValue){

            Reader<S, Higher<Higher<either,L>, T>>  x = nested.map(EitherKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <L,S extends Throwable, P> Nested<Higher<tryType,S>,Higher<either,L>, P> cyclopsTry(Try<Either<L,P>, S> nested){
            cyclops.control.Try<Higher<Higher<either,L>,P>, S> x = nested.map(EitherKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <L,T> Nested<optional,Higher<either,L>,T> javaEitheral(Optional<Either<L,T>> nested){
            Optional<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);

            return  Nested.of(OptionalKind.widen(x), cyclops.companion.Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <L,T> Nested<completableFuture,Higher<either,L>,T> javaCompletableFuture(CompletableFuture<Either<L,T>> nested){
            CompletableFuture<Higher<Higher<either,L>,T>> x = nested.thenApply(EitherKind::widenK);

            return Nested.of(CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <L,T> Nested<Witness.stream,Higher<either,L>,T> javaStream(java.util.stream.Stream<Either<L,T>> nested){
            java.util.stream.Stream<Higher<Higher<either,L>,T>> x = nested.map(EitherKind::widenK);

            return Nested.of(cyclops.companion.Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }



}
