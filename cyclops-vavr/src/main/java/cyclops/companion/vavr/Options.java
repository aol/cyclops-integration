package cyclops.companion.vavr;

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
import com.oath.cyclops.hkt.Higher;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.Witness.*;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.*;
import com.aol.cyclops.vavr.hkt.FutureKind;
import cyclops.conversion.vavr.ToCyclopsReact;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.option;
import com.aol.cyclops.vavr.hkt.OptionKind;
import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.types.Value;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.collections.mutable.ListX;
import cyclops.companion.Monoids;
import cyclops.function.Reducer;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.XorM;
import cyclops.monads.transformers.OptionalT;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;


import static com.aol.cyclops.vavr.hkt.OptionKind.narrowK;
import static com.aol.cyclops.vavr.hkt.OptionKind.widen;

/**
 * Utility class for working with JDK Optionals
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Options {
    public static  <W1,T> Coproduct<W1,option,T> coproduct(Option<T> type, InstanceDefinitions<W1> def1){
        return Coproduct.of(Xor.primary(widen(type)),def1, Instances.definitions());
    }
    public static  <W1,T> Coproduct<W1,option,T> coproduct(T value, InstanceDefinitions<W1> def1){
        return coproduct(Option.some(value),def1);
    }
    public static  <W1,T> Coproduct<W1,option,T> coproductNone(InstanceDefinitions<W1> def1){
        return coproduct(Option.none(),def1);
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,option,T> xorM(Option<T> type){
        return XorM.right(anyM(type));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,option,T> xorM(T type){
        return XorM.right(anyM(Option.some(type)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,option,T> xorMNone(){
        return XorM.right(anyM(Option.none()));
    }
    public static <T,W extends WitnessType<W>> OptionalT<W, T> liftM(Option<T> opt, W witness) {
        return OptionalT.of(witness.adapter().unit(opt.toJavaOptional()));
    }
    public static <T> AnyMValue<option,T> anyM(Option<T> option) {
        return AnyM.ofValue(option, VavrWitness.option.INSTANCE);
    }
    public static <L, T, R> Option<R> tailRec(T initial, Function<? super T, ? extends Option<? extends Either<T, R>>> fn) {
        Option<? extends Either<T, R>> next[] = new Option[1];
        next[0] = Option.some(Either.left(initial));
        boolean cont = true;
        do {
            cont = next[0].map(p -> p.fold(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false)).getOrElse(false);
        } while (cont);
        return next[0].map(Either::get);
    }
    public static <T, R> Option< R> tailRecXor(T initial, Function<? super T, ? extends Option<? extends Xor<T, R>>> fn) {
        Option<? extends Xor<T, R>> next[] = new Option[1];
        next[0] = Option.some(Xor.secondary(initial));
        boolean cont = true;
        do {
            cont = next[0].map(p -> p.visit(s -> {
                next[0] = fn.apply(s);
                return true;
            }, pr -> false)).getOrElse(false);
        } while (cont);
        return next[0].map(Xor::get);
    }

    /**
     * Perform a For Comprehension over a Option, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Options.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Options.forEach4;
     *
    forEach4(Option.just(1),
    a-> Option.just(a+1),
    (a,b) -> Option.<Integer>just(a+b),
    a                  (a,b,c) -> Option.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param value3 Nested Option
     * @param value4 Nested Option
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Option<R> forEach4(Option<? extends T1> value1,
                                                                 Function<? super T1, ? extends Option<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Option<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Option<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Option<R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     *
     * Perform a For Comprehension over a Option, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Options.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Options.forEach4;
     *
     *  forEach4(Option.just(1),
    a-> Option.just(a+1),
    (a,b) -> Option.<Integer>just(a+b),
    (a,b,c) -> Option.<Integer>just(a+b+c),
    (a,b,c,d) -> a+b+c+d <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param value3 Nested Option
     * @param value4 Nested Option
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Option<R> forEach4(Option<? extends T1> value1,
                                                                 Function<? super T1, ? extends Option<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Option<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Option<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Option<R3> c = value4.apply(in,ina,inb);
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Option, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Options.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Options.forEach3;
     *
    forEach3(Option.just(1),
    a-> Option.just(a+1),
    (a,b) -> Option.<Integer>just(a+b),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param value3 Nested Option
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Option<R> forEach3(Option<? extends T1> value1,
                                                         Function<? super T1, ? extends Option<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Option<R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }

    /**
     *
     * Perform a For Comprehension over a Option, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Options.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Options.forEach3;
     *
     *  forEach3(Option.just(1),
    a-> Option.just(a+1),
    (a,b) -> Option.<Integer>just(a+b),
    (a,b,c) -> a+b+c <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param value3 Nested Option
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Option<R> forEach3(Option<? extends T1> value1,
                                                         Function<? super T1, ? extends Option<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Option<R2> b = value3.apply(in,ina);
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });

    }

    /**
     * Perform a For Comprehension over a Option, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Options.
     *
     *  <pre>
     * {@code
     *
     *   import static com.oath.cyclops.reactor.Options.forEach;
     *
    forEach(Option.just(1),
    a-> Option.just(a+1),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T, R1, R> Option<R> forEach2(Option<? extends T> value1, Function<? super T, Option<R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });



    }

    /**
     *
     * Perform a For Comprehension over a Option, accepting a generating function.
     * This results in a two level nested internal iteration over the provided Options.
     *
     * <pre>
     * {@code
     *
     *  import static com.oath.cyclops.reactor.Options.forEach;
     *
     *  forEach(Option.just(1),
    a-> Option.just(a+1),
    (a,b) -> Option.<Integer>just(a+b),
    (a,b,c) -> a+b+c <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Option
     * @param value2 Nested Option
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Option with a combined value generated by the yielding function
     */
    public static <T, R1, R> Option<R> forEach2(Option<? extends T> value1, Function<? super T, ? extends Option<R1>> value2,
                                                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Option<R1> a = value2.apply(in);
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });




    }


    public static Option<Double> optional(OptionalDouble d){
        return d.isPresent() ? Option.of(d.getAsDouble()) : Option.none();
    }
    public static Option<Long> optional(OptionalLong l){
        return l.isPresent() ? Option.of(l.getAsLong()) : Option.none();
    }
    public static Option<Integer> optional(OptionalInt l){
        return l.isPresent() ? Option.of(l.getAsInt()) : Option.none();
    }

    /**
     * Sequence operation, take a Collection of Options and turn it into a Option with a Collection
     * By constrast with {@link Options#sequencePresent(CollectionX)}, if any Options are empty the result
     * is an empty Option
     *
     * <pre>
     * {@code
     *
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();
     *
     *  Option<ListX<Integer>> opts = Options.sequence(ListX.of(just, none, Option.of(1)));
    //Option.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Maybe with a List of values
     */
    public static <T> Option<ListX<T>> sequence(final CollectionX<Option<T>> opts) {
        return sequence(opts.stream()).map(s -> s.toListX());

    }
    /**
     * Sequence operation, take a Collection of Options and turn it into a Option with a Collection
     * Only successes are retained. By constrast with {@link Options#sequence(CollectionX)} Option#empty types are
     * tolerated and ignored.
     *
     * <pre>
     * {@code
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();
     *
     * Option<ListX<Integer>> maybes = Options.sequencePresent(ListX.of(just, none, Option.of(1)));
    //Option.of(ListX.of(10, 1));
     * }
     * </pre>
     *
     * @param opts Options to Sequence
     * @return Option with a List of values
     */
    public static <T> Option<ListX<T>> sequencePresent(final CollectionX<Option<T>> opts) {
        return sequence(opts.stream().filter(Option::isDefined)).map(s->s.toListX());
    }
    /**
     * Sequence operation, take a Collection of Options and turn it into a Option with a Collection
     * By constrast with {@link Options#sequencePresent(CollectionX)} if any Option types are empty
     * the return type will be an empty Option
     *
     * <pre>
     * {@code
     *
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();
     *
     *  Option<ListX<Integer>> maybes = Options.sequence(ListX.of(just, none, Option.of(1)));
    //Option.empty();
     *
     * }
     * </pre>
     *
     *
     * @param opts Maybes to Sequence
     * @return  Option with a List of values
     */
    public static <T> Option<ReactiveSeq<T>> sequence(final java.util.stream.Stream<Option<T>> opts) {
        return AnyM.sequence(opts.map(Options::anyM), option.INSTANCE)
                .map(ReactiveSeq::fromStream)
                .to(VavrWitness::option);

    }
    /**
     * Accummulating operation using the supplied Reducer (@see cyclops2.Reducers). A typical use case is to accumulate into a Persistent Collection type.
     * Accumulates the present results, ignores empty Options.
     *
     * <pre>
     * {@code
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();

     * Option<PersistentSetX<Integer>> opts = Option.accumulateJust(ListX.of(just, none, Option.of(1)), Reducers.toPersistentSetX());
    //Option.of(PersistentSetX.of(10, 1)));
     *
     * }
     * </pre>
     *
     * @param optionals Options to accumulate
     * @param reducer Reducer to accumulate values with
     * @return Option with reduced value
     */
    public static <T, R> Option<R> accumulatePresent(final CollectionX<Option<T>> optionals, final Reducer<R> reducer) {
        return sequencePresent(optionals).map(s -> s.mapReduce(reducer));
    }
    /**
     * Accumulate the results only from those Options which have a value present, using the supplied mapping function to
     * convert the data from each Option before reducing them using the supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();

     *  Option<String> opts = Option.accumulateJust(ListX.of(just, none, Option.of(1)), i -> "" + i,
    Monoids.stringConcat);
    //Option.of("101")
     *
     * }
     * </pre>
     *
     * @param optionals Options to accumulate
     * @param mapper Mapping function to be applied to the result of each Option
     * @param reducer Monoid to combine values from each Option
     * @return Option with reduced value
     */
    public static <T, R> Option<R> accumulatePresent(final CollectionX<Option<T>> optionals, final Function<? super T, R> mapper,
                                                     final Monoid<R> reducer) {
        return sequencePresent(optionals).map(s -> s.map(mapper)
                .reduce(reducer));
    }
    /**
     * Accumulate the results only from those Options which have a value present, using the
     * supplied Monoid (a combining BiFunction/BinaryOperator and identity element that takes two
     * input values of the same type and returns the combined result) {@see cyclops2.Monoids }.
     *
     * <pre>
     * {@code
     *  Option<Integer> just = Option.of(10);
    Option<Integer> none = Option.empty();

     *  Option<String> opts = Option.accumulateJust(Monoids.stringConcat,ListX.of(just, none, Option.of(1)),
    );
    //Option.of("101")
     *
     * }
     * </pre>
     *
     * @param optionals Options to accumulate
     * @param reducer Monoid to combine values from each Option
     * @return Option with reduced value
     */
    public static <T> Option<T> accumulatePresent(final Monoid<T> reducer, final CollectionX<Option<T>> optionals) {
        return sequencePresent(optionals).map(s -> s
                .reduce(reducer));
    }

    /**
     * Combine an Option with the provided value using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Options.combine(Option.of(10),Maybe.just(20), this::add)
     *  //Option[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Option to combine with a value
     * @param v Value to combine
     * @param fn Combining function
     * @return Option combined with supplied value
     */
    public static <T1, T2, R> Option<R> combine(final Option<? extends T1> f, final Value<? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.option(ToCyclopsReact.maybe(f)
                .combine(v, fn)));
    }
    /**
     * Combine an Option with the provided Option using the supplied BiFunction
     *
     * <pre>
     * {@code
     *  Options.combine(Option.of(10),Option.of(20), this::add)
     *  //Option[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param f Option to combine with a value
     * @param v Option to combine
     * @param fn Combining function
     * @return Option combined with supplied value, or empty Option if no value present
     */
    public static <T1, T2, R> Option<R> combine(final Option<? extends T1> f, final Option<? extends T2> v,
                                                final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return combine(f,ToCyclopsReact.maybe(v),fn);
    }

    /**
     * Combine an Option with the provided Iterable (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Options.zip(Option.of(10),Arrays.asList(20), this::add)
     *  //Option[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     * @param f Option to combine with first element in Iterable (if present)
     * @param v Iterable to combine
     * @param fn Combining function
     * @return Option combined with supplied Iterable, or empty Option if no value present
     */
    public static <T1, T2, R> Option<R> zip(final Option<? extends T1> f, final Iterable<? extends T2> v,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.option(ToCyclopsReact.maybe(f)
                .zip(v, fn)));
    }

    /**
     * Combine an Option with the provided Publisher (selecting one element if present) using the supplied BiFunction
     * <pre>
     * {@code
     *  Options.zip(Flux.just(10),Option.of(10), this::add)
     *  //Option[30]
     *
     *  private int add(int a, int b) {
    return a + b;
    }
     *
     * }
     * </pre>
     *
     * @param p Publisher to combine
     * @param f  Option to combine with
     * @param fn Combining function
     * @return Option combined with supplied Publisher, or empty Option if no value present
     */
    public static <T1, T2, R> Option<R> zip(final Publisher<? extends T2> p, final Option<? extends T1> f,
                                            final BiFunction<? super T1, ? super T2, ? extends R> fn) {
        return narrow(FromCyclopsReact.option(ToCyclopsReact.maybe(f)
                .zipP(p, fn)));
    }
    /**
     * Narrow covariant type parameter
     *
     * @param optional Option with covariant type parameter
     * @return Narrowed Option
     */
    public static <T> Option<T> narrow(final Option<? extends T> optional) {
        return (Option<T>) optional;
    }
    public static <T> Active<option,T> allTypeclasses(Option<T> option){
        return Active.of(widen(option), Options.Instances.definitions());
    }
    public static <T,W2,R> Nested<option,W2,R> mapM(Option<T> option, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Option<Higher<W2, R>> e = option.map(fn);
        OptionKind<Higher<W2, R>> lk = widen(e);
        return Nested.of(lk, Options.Instances.definitions(), defs);
    }
    /**
     * Companion class for creating Type Class instances for working with Options
     * @author johnmcclean
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<option> definitions() {
            return new InstanceDefinitions<option>() {

                @Override
                public <T, R> Functor<option> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<option> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<option> applicative() {
                    return Instances.applicative();
                }

                @Override
                public <T, R> Monad<option> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<option>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<option>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> MonadRec<option> monadRec() {
                    return Instances.monadRec();
                }

                @Override
                public <T> Maybe<MonadPlus<option>> monadPlus(Monoid<Higher<option, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Traverse<option> traverse() {
                    return Instances.traverse();
                }

                @Override
                public <T> Foldable<option> foldable() {
                    return Instances.foldable();
                }

                @Override
                public <T> Maybe<Comonad<option>> comonad() {
                    return Maybe.just(Instances.comonad());
                }

                @Override
                public <T> Maybe<Unfoldable<option>> unfoldable() {
                    return Maybe.none();
                }
            };
        }
        /**
         *
         * Transform a option, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  OptionKind<Integer> option = Options.functor().map(i->i*2, OptionKind.widen(Option.just(1));
         *
         *  //[2]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Options
         * <pre>
         * {@code
         *   OptionKind<Integer> option = Options.unit()
        .unit("hello")
        .then(h->Options.functor().map((String v) ->v.length(), h))
        .convert(OptionKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Options
         */
        public static <T,R>Functor<option> functor(){
            BiFunction<OptionKind<T>,Function<? super T, ? extends R>,OptionKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * OptionKind<String> option = Options.unit()
        .unit("hello")
        .convert(OptionKind::narrowK);

        //Option.just("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Options
         */
        public static <T> Pure<option> unit(){
            return General.<option,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.OptionKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         * import static java.util.Option.just;
         *
        Options.zippingApplicative()
        .ap(widen(asOption(l1(this::multiplyByTwo))),widen(asOption(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * OptionKind<Function<Integer,Integer>> optionFn =Options.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(OptionKind::narrowK);

        OptionKind<Integer> option = Options.unit()
        .unit("hello")
        .then(h->Options.functor().map((String v) ->v.length(), h))
        .then(h->Options.applicative().ap(optionFn, h))
        .convert(OptionKind::narrowK);

        //Option.just("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Options
         */
        public static <T,R> Applicative<option> applicative(){
            BiFunction<OptionKind< Function<T, R>>,OptionKind<T>,OptionKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.OptionKind.widen;
         * OptionKind<Integer> option  = Options.monad()
        .flatMap(i->widen(OptionX.range(0,i)), widen(Option.just(1,2,3)))
        .convert(OptionKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    OptionKind<Integer> option = Options.unit()
        .unit("hello")
        .then(h->Options.monad().flatMap((String v) ->Options.unit().unit(v.length()), h))
        .convert(OptionKind::narrowK);

        //Option.just("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Options
         */
        public static <T,R> Monad<option> monad(){

            BiFunction<Higher<option,T>,Function<? super T, ? extends Higher<option,R>>,Higher<option,R>> flatMap = Instances::flatMap;
            return General.monad(applicative(), flatMap);
        }
        public static <T,R> MonadRec<option> monadRec(){
            return new MonadRec<option>() {
                @Override
                public <T, R> Higher<option, R> tailRec(T initial, Function<? super T, ? extends Higher<option, ? extends Xor<T, R>>> fn) {
                    return widen(Options.tailRecXor(initial,fn.andThen(OptionKind::narrowK)));
                }
            };
        }
        /**
         *
         * <pre>
         * {@code
         *  OptionKind<String> option = Options.unit()
        .unit("hello")
        .then(h->Options.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(OptionKind::narrowK);

        //Option.just("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<option> monadZero(){

            return General.monadZero(monad(), OptionKind.none());
        }
        /**
         * <pre>
         * {@code
         *  OptionKind<Integer> option = Options.<Integer>monadPlus()
        .plus(OptionKind.widen(Option.just()), OptionKind.widen(Option.just(10)))
        .convert(OptionKind::narrowK);
        //Option.just(10))
         *
         * }
         * </pre>
         * @return Type class for combining Options by concatenation
         */
        public static <T> MonadPlus<option> monadPlus(){
            Monoid<OptionKind<T>> m = Monoid.of( OptionKind.ofOptional(Monoids.<T>firstPresentOptional().zero()),
                    (a,b)-> OptionKind.ofOptional(Monoids.<T>firstPresentOptional().apply(a.toJavaOptional(),b.toJavaOptional())));
            Monoid<Higher<option,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<OptionKind<Integer>> m = Monoid.of(OptionKind.widen(Option.just()), (a,b)->a.isEmpty() ? b : a);
        OptionKind<Integer> option = Options.<Integer>monadPlus(m)
        .plus(OptionKind.widen(Option.just(5)), OptionKind.widen(Option.just(10)))
        .convert(OptionKind::narrowK);
        //Option[5]
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Options
         * @return Type class for combining Options
         */
        public static <T> MonadPlus<option> monadPlus(Monoid<Higher<option,T>> m){
            Monoid<Higher<option,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<option> monadPlusK(Monoid<OptionKind<T>> m){
            Monoid<Higher<option,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<option> traverse(){

            return General.traverseByTraverse(applicative(), Instances::traverseA);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Options.foldable()
        .foldLeft(0, (a,b)->a+b, OptionKind.widen(Option.just(1)));

        //1
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<option> foldable(){
            return new Foldable<option>() {
                @Override
                public <T> T foldRight(Monoid<T> monoid, Higher<option, T> ds) {
                    return narrowK(ds).getOrElse(monoid.zero());
                }

                @Override
                public <T> T foldLeft(Monoid<T> monoid, Higher<option, T> ds) {
                    return narrowK(ds).getOrElse(monoid.zero());
                }

                @Override
                public <T, R> R foldMap(Monoid<R> mb, Function<? super T, ? extends R> fn, Higher<option, T> nestedA) {
                    return narrowK(nestedA).<R>map(fn).getOrElse(mb.zero());
                }
            };
        }
        public static <T> Comonad<option> comonad(){
            Function<? super Higher<option, T>, ? extends T> extractFn = maybe -> maybe.convert(OptionKind::narrow).get();
            return General.comonad(functor(), unit(), extractFn);
        }

        private <T> OptionKind<T> of(T value){
            return widen(Option.of(value));
        }
        private static <T,R> OptionKind<R> ap(OptionKind<Function< T, R>> lt, OptionKind<T> option){
            return widen(FromCyclopsReact.option(ToCyclopsReact.maybe(lt).combine(ToCyclopsReact.maybe(option), (a, b)->a.apply(b))));

        }
        private static <T,R> Higher<option,R> flatMap(Higher<option,T> lt, Function<? super T, ? extends  Higher<option,R>> fn){
            return widen(OptionKind.narrow(lt).flatMap(fn.andThen(OptionKind::narrow)));
        }
        private static <T,R> OptionKind<R> map(OptionKind<T> lt, Function<? super T, ? extends R> fn){
            return widen(OptionKind.narrow(lt).map(fn));
        }


        private static <C2,T,R> Higher<C2, Higher<option, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                              Higher<option, T> ds){

            Option<T> option = OptionKind.narrow(ds);
            Higher<C2, OptionKind<R>> res = ToCyclopsReact.maybe(option).visit(some-> applicative.map(m-> OptionKind.of(m), fn.apply(some)),
                    ()->applicative.unit(widen(OptionKind.<R>none())));

            return OptionKind.widen2(res);
        }

    }
    public static interface OptionNested{


        public static <T> Nested<option,lazy,T> lazy(Option<Lazy<T>> type){
            return Nested.of(widen(type.map(LazyKind::widen)),Instances.definitions(),Lazys.Instances.definitions());
        }
        public static <T> Nested<option,tryType,T> optionTry(Option<Try<T>> type){
            return Nested.of(widen(type.map(TryKind::widen)),Instances.definitions(),Trys.Instances.definitions());
        }
        public static <T> Nested<option,VavrWitness.future,T> future(Option<Future<T>> type){
            return Nested.of(widen(type.map(FutureKind::widen)),Instances.definitions(),Futures.Instances.definitions());
        }
        public static <T> Nested<option,option,T> option(Option<Option<T>> nested){
            return Nested.of(widen(nested.map(OptionKind::widen)),Instances.definitions(),Options.Instances.definitions());
        }
        public static <L, R> Nested<option,Higher<VavrWitness.either,L>, R> either(Option<Either<L, R>> nested){
            return Nested.of(widen(nested.map(EitherKind::widen)),Instances.definitions(),Eithers.Instances.definitions());
        }
        public static <T> Nested<option,VavrWitness.queue,T> queue(Option<Queue<T>> nested){
            return Nested.of(widen(nested.map(QueueKind::widen)), Instances.definitions(),Queues.Instances.definitions());
        }
        public static <T> Nested<option,VavrWitness.stream,T> stream(Option<Stream<T>> nested){
            return Nested.of(widen(nested.map(StreamKind::widen)),Instances.definitions(),Streams.Instances.definitions());
        }
        public static <T> Nested<option,VavrWitness.list,T> list(Option<List<T>> nested){
            return Nested.of(widen(nested.map(ListKind::widen)), Instances.definitions(),Lists.Instances.definitions());
        }
        public static <T> Nested<option,array,T> array(Option<Array<T>> nested){
            return Nested.of(widen(nested.map(ArrayKind::widen)),Instances.definitions(),Arrays.Instances.definitions());
        }
        public static <T> Nested<option,vector,T> vector(Option<Vector<T>> nested){
            return Nested.of(widen(nested.map(VectorKind::widen)),Instances.definitions(),Vectors.Instances.definitions());
        }
        public static <T> Nested<option,hashSet,T> set(Option<HashSet<T>> nested){
            return Nested.of(widen(nested.map(HashSetKind::widen)),Instances.definitions(), HashSets.Instances.definitions());
        }

        public static <T> Nested<option,reactiveSeq,T> reactiveSeq(Option<ReactiveSeq<T>> nested){
            OptionKind<ReactiveSeq<T>> x = widen(nested);
            OptionKind<Higher<reactiveSeq,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),ReactiveSeq.Instances.definitions());
        }

        public static <T> Nested<option,maybe,T> maybe(Option<Maybe<T>> nested){
            OptionKind<Maybe<T>> x = widen(nested);
            OptionKind<Higher<maybe,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),Maybe.Instances.definitions());
        }
        public static <T> Nested<option,eval,T> eval(Option<Eval<T>> nested){
            OptionKind<Eval<T>> x = widen(nested);
            OptionKind<Higher<eval,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),Eval.Instances.definitions());
        }
        public static <T> Nested<option,Witness.future,T> cyclopsFuture(Option<cyclops.async.Future<T>> nested){
            OptionKind<cyclops.async.Future<T>> x = widen(nested);
            OptionKind<Higher<Witness.future,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.async.Future.Instances.definitions());
        }
        public static <S, P> Nested<option,Higher<xor,S>, P> xor(Option<Xor<S, P>> nested){
            OptionKind<Xor<S, P>> x = widen(nested);
            OptionKind<Higher<Higher<xor,S>, P>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),Xor.Instances.definitions());
        }
        public static <S,T> Nested<option,Higher<reader,S>, T> reader(Option<Reader<S, T>> nested,S defaultValue){
            OptionKind<Reader<S, T>> x = widen(nested);
            OptionKind<Higher<Higher<reader,S>, T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),Reader.Instances.definitions(defaultValue));
        }
        public static <S extends Throwable, P> Nested<option,Higher<Witness.tryType,S>, P> cyclopsTry(Option<cyclops.control.Try<P, S>> nested){
            OptionKind<cyclops.control.Try<P, S>> x = widen(nested);
            OptionKind<Higher<Higher<Witness.tryType,S>, P>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(),cyclops.control.Try.Instances.definitions());
        }
        public static <T> Nested<option,optional,T> optional(Option<Optional<T>> nested){
            OptionKind<Optional<T>> x = widen(nested);
            OptionKind<Higher<optional,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(), Optionals.Instances.definitions());
        }
        public static <T> Nested<option,completableFuture,T> completableOption(Option<CompletableFuture<T>> nested){
            OptionKind<CompletableFuture<T>> x = widen(nested);
            OptionKind<Higher<completableFuture,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(), CompletableFutures.Instances.definitions());
        }
        public static <T> Nested<option,Witness.stream,T> javaStream(Option<java.util.stream.Stream<T>> nested){
            OptionKind<java.util.stream.Stream<T>> x = widen(nested);
            OptionKind<Higher<Witness.stream,T>> y = (OptionKind)x;
            return Nested.of(y,Instances.definitions(), cyclops.companion.Streams.Instances.definitions());
        }


    }
    public static interface NestedOption{
        public static <T> Nested<reactiveSeq,option,T> reactiveSeq(ReactiveSeq<Option<T>> nested){
            ReactiveSeq<Higher<option,T>> x = nested.map(OptionKind::widenK);
            return Nested.of(x,ReactiveSeq.Instances.definitions(),Instances.definitions());
        }

        public static <T> Nested<maybe,option,T> maybe(Maybe<Option<T>> nested){
            Maybe<Higher<option,T>> x = nested.map(OptionKind::widenK);

            return Nested.of(x,Maybe.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<eval,option,T> eval(Eval<Option<T>> nested){
            Eval<Higher<option,T>> x = nested.map(OptionKind::widenK);

            return Nested.of(x,Eval.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.future,option,T> cyclopsFuture(cyclops.async.Future<Option<T>> nested){
            cyclops.async.Future<Higher<option,T>> x = nested.map(OptionKind::widenK);

            return Nested.of(x,cyclops.async.Future.Instances.definitions(),Instances.definitions());
        }
        public static <S, P> Nested<Higher<xor,S>,option, P> xor(Xor<S, Option<P>> nested){
            Xor<S, Higher<option,P>> x = nested.map(OptionKind::widenK);

            return Nested.of(x,Xor.Instances.definitions(),Instances.definitions());
        }
        public static <S,T> Nested<Higher<reader,S>,option, T> reader(Reader<S, Option<T>> nested,S defaultValue){

            Reader<S, Higher<option, T>>  x = nested.map(OptionKind::widenK);

            return Nested.of(x,Reader.Instances.definitions(defaultValue),Instances.definitions());
        }
        public static <S extends Throwable, P> Nested<Higher<Witness.tryType,S>,option, P> cyclopsTry(cyclops.control.Try<Option<P>, S> nested){
            cyclops.control.Try<Higher<option,P>, S> x = nested.map(OptionKind::widenK);

            return Nested.of(x,cyclops.control.Try.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<optional,option,T> optional(Optional<Option<T>> nested){
            Optional<Higher<option,T>> x = nested.map(OptionKind::widenK);

            return  Nested.of(Optionals.OptionalKind.widen(x), Optionals.Instances.definitions(), Instances.definitions());
        }
        public static <T> Nested<completableFuture,option,T> completableOption(CompletableFuture<Option<T>> nested){
            CompletableFuture<Higher<option,T>> x = nested.thenApply(OptionKind::widenK);

            return Nested.of(CompletableFutures.CompletableFutureKind.widen(x), CompletableFutures.Instances.definitions(),Instances.definitions());
        }
        public static <T> Nested<Witness.stream,option,T> javaStream(java.util.stream.Stream<Option<T>> nested){
            java.util.stream.Stream<Higher<option,T>> x = nested.map(OptionKind::widenK);

            return Nested.of(cyclops.companion.Streams.StreamKind.widen(x), cyclops.companion.Streams.Instances.definitions(),Instances.definitions());
        }
    }




}
