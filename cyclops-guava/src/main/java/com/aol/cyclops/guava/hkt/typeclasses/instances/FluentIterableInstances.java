package com.aol.cyclops.guava.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.guava.hkt.FluentIterableType;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;
import com.google.common.collect.FluentIterable;

import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with FluentIterables
 * @author johnmcclean
 *
 */
@UtilityClass
public class FluentIterableInstances {

   
    /**
     * 
     * Transform a flux, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  FluentIterableType<Integer> flux = FluentIterables.functor().map(i->i*2, FluentIterableType.widen(FluentIterable.of(1,2,3));
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
     *   FluentIterableType<Integer> flux = FluentIterables.unit()
                                       .unit("hello")
                                       .then(h->FluentIterables.functor().map((String v) ->v.length(), h))
                                       .convert(FluentIterableType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for FluentIterables
     */
    public static <T,R>Functor<FluentIterableType.µ> functor(){
        BiFunction<FluentIterableType<T>,Function<? super T, ? extends R>,FluentIterableType<R>> map = FluentIterableInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FluentIterableType<String> flux = FluentIterables.unit()
                                     .unit("hello")
                                     .convert(FluentIterableType::narrowK);
        
        //FluentIterable.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for FluentIterables
     */
    public static <T> Unit<FluentIterableType.µ> unit(){
        return General.<FluentIterableType.µ,T>unit(FluentIterableInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluentIterableType.widen;
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
     * FluentIterableType<Function<Integer,Integer>> fluxFn =FluentIterables.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FluentIterableType::narrowK);
        
        FluentIterableType<Integer> flux = FluentIterables.unit()
                                      .unit("hello")
                                      .then(h->FluentIterables.functor().map((String v) ->v.length(), h))
                                      .then(h->FluentIterables.zippingApplicative().ap(fluxFn, h))
                                      .convert(FluentIterableType::narrowK);
        
        //FluentIterable.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for FluentIterables
     */
    public static <T,R> Applicative<FluentIterableType.µ> zippingApplicative(){
        BiFunction<FluentIterableType< Function<T, R>>,FluentIterableType<T>,FluentIterableType<R>> ap = FluentIterableInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluentIterableType.widen;
     * FluentIterableType<Integer> flux  = FluentIterables.monad()
                                      .flatMap(i->widen(FluentIterableX.range(0,i)), widen(FluentIterable.of(1,2,3)))
                                      .convert(FluentIterableType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FluentIterableType<Integer> flux = FluentIterables.unit()
                                        .unit("hello")
                                        .then(h->FluentIterables.monad().flatMap((String v) ->FluentIterables.unit().unit(v.length()), h))
                                        .convert(FluentIterableType::narrowK);
        
        //FluentIterable.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for FluentIterables
     */
    public static <T,R> Monad<FluentIterableType.µ> monad(){
  
        BiFunction<Higher<FluentIterableType.µ,T>,Function<? super T, ? extends Higher<FluentIterableType.µ,R>>,Higher<FluentIterableType.µ,R>> flatMap = FluentIterableInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FluentIterableType<String> flux = FluentIterables.unit()
                                         .unit("hello")
                                         .then(h->FluentIterables.monadZero().filter((String t)->t.startsWith("he"), h))
                                         .convert(FluentIterableType::narrowK);
        
       //FluentIterable.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FluentIterableType.µ> monadZero(){
        BiFunction<Higher<FluentIterableType.µ,T>,Predicate<? super T>,Higher<FluentIterableType.µ,T>> filter = FluentIterableInstances::filter;
        Supplier<Higher<FluentIterableType.µ, T>> zero = ()->FluentIterableType.widen(FluentIterable.of());
        return General.<FluentIterableType.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  FluentIterableType<Integer> flux = FluentIterables.<Integer>monadPlus()
                                      .plus(FluentIterableType.widen(FluentIterable.of()), FluentIterableType.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableType::narrowK);
        //FluentIterable.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining FluentIterables by concatenation
     */
    public static <T> MonadPlus<FluentIterableType.µ> monadPlus(){
        Monoid<FluentIterableType<T>> m = Monoid.of(FluentIterableType.widen(FluentIterable.<T>of()), FluentIterableInstances::concat);
        Monoid<Higher<FluentIterableType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FluentIterableType<Integer>> m = Monoid.of(FluentIterableType.widen(FluentIterable.of()), (a,b)->a.isEmpty() ? b : a);
        FluentIterableType<Integer> flux = FluentIterables.<Integer>monadPlus(m)
                                      .plus(FluentIterableType.widen(FluentIterable.of(5)), FluentIterableType.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableType::narrowK);
        //FluentIterable.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining FluentIterables
     * @return Type class for combining FluentIterables
     */
    public static <T> MonadPlus<FluentIterableType.µ> monadPlus(Monoid<FluentIterableType<T>> m){
        Monoid<Higher<FluentIterableType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FluentIterableType.µ> traverse(){
        BiFunction<Applicative<C2>,FluentIterableType<Higher<C2, T>>,Higher<C2, FluentIterableType<T>>> sequenceFn = (ap,flux) -> {
        
            Higher<C2,FluentIterableType<T>> identity = ap.unit(FluentIterableType.widen(FluentIterable.of()));

            BiFunction<Higher<C2,FluentIterableType<T>>,Higher<C2,T>,Higher<C2,FluentIterableType<T>>> combineToFluentIterable =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> FluentIterableType.widen(FluentIterable.concat(a,FluentIterable.of(b)))),acc,next);

            BinaryOperator<Higher<C2,FluentIterableType<T>>> combineFluentIterables = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { return FluentIterableType.widen(FluentIterable.concat(l1.narrow(),l2.narrow()));}),a,b); ;  

            return ReactiveSeq.fromPublisher(flux).reduce(identity,
                                                            combineToFluentIterable,
                                                            combineFluentIterables);  

   
        };
        BiFunction<Applicative<C2>,Higher<FluentIterableType.µ,Higher<C2, T>>,Higher<C2, Higher<FluentIterableType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> FluentIterableType.widen2(sequenceFn.apply(a, FluentIterableType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = FluentIterables.foldable()
                        .foldLeft(0, (a,b)->a+b, FluentIterableType.widen(FluentIterable.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<FluentIterableType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<FluentIterableType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromPublisher(FluentIterableType.narrowK(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<FluentIterableType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromPublisher(FluentIterableType.narrowK(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> FluentIterableType<T> concat(FluentIterableType<T> l1, FluentIterableType<T> l2){
        return FluentIterableType.widen(FluentIterable.concat(l1,l2));
    }
    private <T> FluentIterableType<T> of(T value){
        return FluentIterableType.widen(FluentIterable.of(value));
    }
    private static <T,R> FluentIterableType<R> ap(FluentIterableType<Function< T, R>> lt,  FluentIterableType<T> flux){
       return FluentIterableType.widen(lt.toReactiveSeq().zip(flux,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<FluentIterableType.µ,R> flatMap( Higher<FluentIterableType.µ,T> lt, Function<? super T, ? extends  Higher<FluentIterableType.µ,R>> fn){
        return FluentIterableType.widen(FluentIterableType.narrowK(lt).transformAndConcat(i->fn.andThen(FluentIterableType::narrowK).apply(i)));
    }
    private static <T,R> FluentIterableType<R> map(FluentIterableType<T> lt, Function<? super T, ? extends R> fn){
        return FluentIterableType.widen(lt.transform(i->fn.apply(i)));
    }
    private static <T> FluentIterableType<T> filter(Higher<FluentIterableType.µ,T> lt, Predicate<? super T> fn){
        return FluentIterableType.widen(FluentIterableType.narrow(lt).filter(i->fn.test(i)));
    }
}
