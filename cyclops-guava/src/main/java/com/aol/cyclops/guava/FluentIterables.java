package com.aol.cyclops.guava;

import com.aol.cyclops.guava.hkt.FluentIterableKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.google.common.collect.FluentIterable;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;

import java.util.function.*;

/**
 * Created by johnmcclean on 26/04/2017.
 */
public class FluentIterables {

    /**
     * <pre>
     * {@code
     * Guava.anyM(FluentIterable.of(new String[]{"hello world"}))
    .map(String::toUpperCase)
    .flatMap(i->AnyMonads.anyM(java.util.stream.Stream.of(i)))
    .toSequence()
    .toList()
     * }
     *  //[HELLO WORLD]
     * </pre>
     *
     * @param streamM to construct AnyM from
     * @return AnyM
     */
    public static <T> AnyMSeq<GuavaWitness.fluentIterable,T> anyM(FluentIterable<T> streamM) {
        return AnyM.ofSeq(streamM, GuavaWitness.fluentIterable.INSTANCE);
    }

    /**
     * Companion class for creating Type Class instances for working with FluentIterables
     * @author johnmcclean
     *
     */
    @UtilityClass
    public class Instances {


        /**
         *
         * Transform a flux, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  FluentIterableKind<Integer> flux = FluentIterables.functor().map(i->i*2, FluentIterableKind.widen(FluentIterable.of(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with FluentIterables
         * <pre>
         * {@code
         *   FluentIterableKind<Integer> flux = FluentIterables.unit()
        .unit("hello")
        .then(h->FluentIterables.functor().map((String v) ->v.length(), h))
        .convert(FluentIterableKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for FluentIterables
         */
        public static <T,R>Functor<FluentIterableKind.µ> functor(){
            BiFunction<FluentIterableKind<T>,Function<? super T, ? extends R>,FluentIterableKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * FluentIterableKind<String> flux = FluentIterables.unit()
        .unit("hello")
        .convert(FluentIterableKind::narrowK);

        //FluentIterable.of("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for FluentIterables
         */
        public static <T> Pure<FluentIterableKind.µ> unit(){
            return General.<FluentIterableKind.µ,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FluentIterableKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        FluentIterables.zippingApplicative()
        .ap(widen(FluentIterable.of(l1(this::multiplyByTwo))),widen(FluentIterable.of(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * FluentIterableKind<Function<Integer,Integer>> fluxFn =FluentIterables.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(FluentIterableKind::narrowK);

        FluentIterableKind<Integer> flux = FluentIterables.unit()
        .unit("hello")
        .then(h->FluentIterables.functor().map((String v) ->v.length(), h))
        .then(h->FluentIterables.zippingApplicative().ap(fluxFn, h))
        .convert(FluentIterableKind::narrowK);

        //FluentIterable.of("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for FluentIterables
         */
        public static <T,R> Applicative<FluentIterableKind.µ> zippingApplicative(){
            BiFunction<FluentIterableKind< Function<T, R>>,FluentIterableKind<T>,FluentIterableKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.FluentIterableKind.widen;
         * FluentIterableKind<Integer> flux  = FluentIterables.monad()
        .flatMap(i->widen(FluentIterableX.range(0,i)), widen(FluentIterable.of(1,2,3)))
        .convert(FluentIterableKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    FluentIterableKind<Integer> flux = FluentIterables.unit()
        .unit("hello")
        .then(h->FluentIterables.monad().flatMap((String v) ->FluentIterables.unit().unit(v.length()), h))
        .convert(FluentIterableKind::narrowK);

        //FluentIterable.of("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for FluentIterables
         */
        public static <T,R> Monad<FluentIterableKind.µ> monad(){

            BiFunction<Higher<FluentIterableKind.µ,T>,Function<? super T, ? extends Higher<FluentIterableKind.µ,R>>,Higher<FluentIterableKind.µ,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  FluentIterableKind<String> flux = FluentIterables.unit()
        .unit("hello")
        .then(h->FluentIterables.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(FluentIterableKind::narrowK);

        //FluentIterable.of("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<FluentIterableKind.µ> monadZero(){
            BiFunction<Higher<FluentIterableKind.µ,T>,Predicate<? super T>,Higher<FluentIterableKind.µ,T>> filter = Instances::filter;
            Supplier<Higher<FluentIterableKind.µ, T>> zero = ()-> FluentIterableKind.widen(FluentIterable.of());
            return General.<FluentIterableKind.µ,T,R>monadZero(monad(), zero,filter);
        }
        /**
         * <pre>
         * {@code
         *  FluentIterableKind<Integer> flux = FluentIterables.<Integer>monadPlus()
        .plus(FluentIterableKind.widen(FluentIterable.of()), FluentIterableKind.widen(FluentIterable.of(10)))
        .convert(FluentIterableKind::narrowK);
        //FluentIterable.of(10))
         *
         * }
         * </pre>
         * @return Type class for combining FluentIterables by concatenation
         */
        public static <T> MonadPlus<FluentIterableKind.µ> monadPlus(){
            Monoid<FluentIterableKind<T>> m = Monoid.of(FluentIterableKind.widen(FluentIterable.<T>of()), Instances::concat);
            Monoid<Higher<FluentIterableKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<FluentIterableKind<Integer>> m = Monoid.of(FluentIterableKind.widen(FluentIterable.of()), (a,b)->a.isEmpty() ? b : a);
        FluentIterableKind<Integer> flux = FluentIterables.<Integer>monadPlus(m)
        .plus(FluentIterableKind.widen(FluentIterable.of(5)), FluentIterableKind.widen(FluentIterable.of(10)))
        .convert(FluentIterableKind::narrowK);
        //FluentIterable.of(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining FluentIterables
         * @return Type class for combining FluentIterables
         */
        public static <T> MonadPlus<FluentIterableKind.µ> monadPlus(Monoid<FluentIterableKind<T>> m){
            Monoid<Higher<FluentIterableKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<FluentIterableKind.µ> traverse(){
            BiFunction<Applicative<C2>,FluentIterableKind<Higher<C2, T>>,Higher<C2, FluentIterableKind<T>>> sequenceFn = (ap, flux) -> {

                Higher<C2,FluentIterableKind<T>> identity = ap.unit(FluentIterableKind.widen(FluentIterable.of()));

                BiFunction<Higher<C2,FluentIterableKind<T>>,Higher<C2,T>,Higher<C2,FluentIterableKind<T>>> combineToFluentIterable =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> FluentIterableKind.widen(FluentIterable.concat(a,FluentIterable.of(b)))),acc,next);

                BinaryOperator<Higher<C2,FluentIterableKind<T>>> combineFluentIterables = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return FluentIterableKind.widen(FluentIterable.concat(l1.narrow(),l2.narrow()));}),a,b); ;

                return ReactiveSeq.fromIterable(flux).reduce(identity,
                        combineToFluentIterable,
                        combineFluentIterables);


            };
            BiFunction<Applicative<C2>,Higher<FluentIterableKind.µ,Higher<C2, T>>,Higher<C2, Higher<FluentIterableKind.µ,T>>> sequenceNarrow  =
                    (a,b) -> FluentIterableKind.widen2(sequenceFn.apply(a, FluentIterableKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = FluentIterables.foldable()
        .foldLeft(0, (a,b)->a+b, FluentIterableKind.widen(FluentIterable.of(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<FluentIterableKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<FluentIterableKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromPublisher(FluentIterableKind.narrowK(l)).foldRight(m);
            BiFunction<Monoid<T>,Higher<FluentIterableKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromPublisher(FluentIterableKind.narrowK(l)).reduce(m);
            return General.foldable(foldRightFn, foldLeftFn);
        }

        private static  <T> FluentIterableKind<T> concat(FluentIterableKind<T> l1, FluentIterableKind<T> l2){
            return FluentIterableKind.widen(FluentIterable.concat(l1,l2));
        }
        private <T> FluentIterableKind<T> of(T value){
            return FluentIterableKind.widen(FluentIterable.of(value));
        }
        private static <T,R> FluentIterableKind<R> ap(FluentIterableKind<Function< T, R>> lt, FluentIterableKind<T> flux){
            return FluentIterableKind.widen(lt.toReactiveSeq().zip(flux,(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<FluentIterableKind.µ,R> flatMap(Higher<FluentIterableKind.µ,T> lt, Function<? super T, ? extends  Higher<FluentIterableKind.µ,R>> fn){
            return FluentIterableKind.widen(FluentIterableKind.narrowK(lt).transformAndConcat(i->fn.andThen(FluentIterableKind::narrowK).apply(i)));
        }
        private static <T,R> FluentIterableKind<R> map(FluentIterableKind<T> lt, Function<? super T, ? extends R> fn){
            return FluentIterableKind.widen(lt.transform(i->fn.apply(i)));
        }
        private static <T> FluentIterableKind<T> filter(Higher<FluentIterableKind.µ,T> lt, Predicate<? super T> fn){
            return FluentIterableKind.widen(FluentIterableKind.narrow(lt).filter(i->fn.test(i)));
        }
    }

}
