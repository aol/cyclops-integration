package com.aol.cyclops.guava;

import com.aol.cyclops.guava.hkt.OptionalKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMValue;
import com.google.common.base.Optional;
import cyclops.control.Maybe;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by johnmcclean on 26/04/2017.
 */
public class Optionals {
    /**
     * <pre>
     * {@code
     * Guava.optional(Optional.of("hello world"))
    .map(String::toUpperCase)
    .toSequence()
    .toList()
     * }
     * //[HELLO WORLD]
     * </pre>
     *
     * @param optionM to construct AnyM from
     * @return AnyM
     */
    public static <T> AnyMValue<GuavaWitness.optional,T> anyM(Optional<T> optionM) {
        return AnyM.ofValue(optionM, GuavaWitness.optional.INSTANCE);
    }

    /**
     * Companion class for creating Type Class instances for working with Optionals
     * @author johnmcclean
     *
     */
    @UtilityClass
    public class Instances {


        /**
         *
         * Transform a option, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  OptionalKind<Integer> option = Optionals.functor()
         *                                      .map(i->i*2, OptionalKind.widen(Optional.some(1));
         *
         *  //[2]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Optionals
         * <pre>
         * {@code
         *   OptionalKind<Integer> option = Optionals.unit()
        .unit("hello")
        .then(h->Optionals.functor().map((String v) ->v.length(), h))
        .convert(OptionalKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Optionals
         */
        public static <T,R> Functor<OptionalKind.µ> functor(){
            BiFunction<OptionalKind<T>,Function<? super T, ? extends R>,OptionalKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * OptionalKind<String> option = Optionals.unit()
        .unit("hello")
        .convert(OptionalKind::narrowK);

        //Optional.some("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Optionals
         */
        public static <T> Pure<OptionalKind.µ> unit(){
            return General.<OptionalKind.µ,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.OptionalKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
         *
        Optionals.applicative()
        .ap(widen(Optional.some(l1(this::multiplyByTwo))),widen(Optional.some(1)));
         *
         * //[2]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * OptionalKind<Function<Integer,Integer>> optionFn =Optionals.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(OptionalKind::narrowK);

        OptionalKind<Integer> option = Optionals.unit()
        .unit("hello")
        .then(h->Optionals.functor().map((String v) ->v.length(), h))
        .then(h->Optionals.applicative().ap(optionFn, h))
        .convert(OptionalKind::narrowK);

        //Arrays.asOptional("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Optionals
         */
        public static <T,R> Applicative<OptionalKind.µ> applicative(){
            BiFunction<OptionalKind< Function<T, R>>,OptionalKind<T>,OptionalKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.OptionalKind.widen;
         * OptionalKind<Integer> option  = Optionals.monad()
        .flatMap(i->widen(OptionalX.range(0,i)), widen(Optional.some(1,2,3)))
        .convert(OptionalKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    OptionalKind<Integer> option = Optionals.unit()
        .unit("hello")
        .then(h->Optionals.monad().flatMap((String v) ->Optionals.unit().unit(v.length()), h))
        .convert(OptionalKind::narrowK);

        //Arrays.asOptional("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Optionals
         */
        public static <T,R> Monad<OptionalKind.µ> monad(){

            BiFunction<Higher<OptionalKind.µ,T>,Function<? super T, ? extends Higher<OptionalKind.µ,R>>,Higher<OptionalKind.µ,R>> flatMap = Instances::flatMap;
            return General.monad(applicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  OptionalKind<String> option = Optionals.unit()
        .unit("hello")
        .then(h->Optionals.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(OptionalKind::narrowK);

        //Arrays.asOptional("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<OptionalKind.µ> monadZero(){

            return General.monadZero(monad(), OptionalKind.absent());
        }
        /**
         * <pre>
         * {@code
         *  OptionalKind<Integer> option = Optionals.<Integer>monadPlus()
        .plus(OptionalKind.widen(Arrays.asOptional()), OptionalKind.widen(Arrays.asOptional(10)))
        .convert(OptionalKind::narrowK);
        //Arrays.asOptional(10))
         *
         * }
         * </pre>
         * @return Type class for combining Optionals by concatenation
         */
        public static <T> MonadPlus<OptionalKind.µ> monadPlus(){
            Monoid<Optional<T>> mn = Monoid.of(Optional.absent(), (a, b) -> a.isPresent() ? a : b);
            Monoid<OptionalKind<T>> m = Monoid.of(OptionalKind.widen(mn.zero()), (f, g)-> OptionalKind.widen(
                    mn.apply(OptionalKind.narrow(f), OptionalKind.narrow(g))));

            Monoid<Higher<OptionalKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<OptionalKind<Integer>> m = Monoid.of(OptionalKind.widen(Arrays.asOptional()), (a,b)->a.isEmpty() ? b : a);
        OptionalKind<Integer> option = Optionals.<Integer>monadPlus(m)
        .plus(OptionalKind.widen(Arrays.asOptional(5)), OptionalKind.widen(Arrays.asOptional(10)))
        .convert(OptionalKind::narrowK);
        //Arrays.asOptional(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Optionals
         * @return Type class for combining Optionals
         */
        public static <T> MonadPlus<OptionalKind.µ> monadPlus(Monoid<OptionalKind<T>> m){
            Monoid<Higher<OptionalKind.µ,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<OptionalKind.µ> traverse(){

            return General.traverseByTraverse(applicative(), Instances::traverseA);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Optionals.foldable()
        .foldLeft(0, (a,b)->a+b, OptionalKind.widen(Optional.some(2)));

        //2
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<OptionalKind.µ> foldable(){
            BiFunction<Monoid<T>,Higher<OptionalKind.µ,T>,T> foldRightFn =  (m, l)-> OptionalKind.narrow(l).or(m.zero());
            BiFunction<Monoid<T>,Higher<OptionalKind.µ,T>,T> foldLeftFn = (m, l)-> OptionalKind.narrow(l).or(m.zero());
            return General.foldable(foldRightFn, foldLeftFn);
        }
        public static <T> Comonad<OptionalKind.µ> comonad(){
            Function<? super Higher<OptionalKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(OptionalKind::narrow).get();
            return General.comonad(functor(), unit(), extractFn);
        }

        private <T> OptionalKind<T> of(T value){
            return OptionalKind.widen(Optional.of(value));
        }
        private static <T,R> OptionalKind<R> ap(OptionalKind<Function< T, R>> lt, OptionalKind<T> option){
            Maybe<R> mb = Guava.asMaybe(lt.narrow()).combine(Guava.asMaybe(option.narrow()),
                    (a,b)->a.apply(b));
            return OptionalKind.widen(mb);

        }
        private static <T,R> Higher<OptionalKind.µ,R> flatMap(Higher<OptionalKind.µ,T> lt, Function<? super T, ? extends  Higher<OptionalKind.µ,R>> fn){
            return OptionalKind.widen(OptionalKind.narrowOptional(lt).flatMap(in->fn.andThen(OptionalKind::narrowOptional).apply(in)));
        }
        private static <T,R> OptionalKind<R> map(OptionalKind<T> lt, Function<? super T, ? extends R> fn){

            return OptionalKind.widen(OptionalKind.narrow(lt).transform(t->fn.apply(t)));
        }


        private static <C2,T,R> Higher<C2, Higher<OptionalKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                                Higher<OptionalKind.µ, T> ds){
            Optional<T> opt = OptionalKind.narrow(ds);
            return opt.isPresent()?   applicative.map(OptionalKind::just, fn.apply(opt.get())) :
                    applicative.unit(OptionalKind.absent());
        }

    }

}
