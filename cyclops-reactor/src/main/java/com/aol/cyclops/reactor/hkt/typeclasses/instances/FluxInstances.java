package com.aol.cyclops.reactor.hkt.typeclasses.instances;


import com.aol.cyclops.reactor.hkt.FluxKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

import java.util.function.*;

/**
 * Companion class for creating Type Class instances for working with Fluxs
 * @author johnmcclean
 *
 */
@UtilityClass
public class FluxInstances {

   
    /**
     * 
     * Transform a flux, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  FluxKind<Integer> flux = Fluxs.functor().map(i->i*2, FluxKind.widen(Flux.of(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Fluxs
     * <pre>
     * {@code 
     *   FluxKind<Integer> flux = Fluxs.unit()
                                       .unit("hello")
                                       .then(h->Fluxs.functor().map((String v) ->v.length(), h))
                                       .convert(FluxKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Fluxs
     */
    public static <T,R>Functor<FluxKind.µ> functor(){
        BiFunction<FluxKind<T>,Function<? super T, ? extends R>,FluxKind<R>> map = FluxInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FluxKind<String> flux = Fluxs.unit()
                                     .unit("hello")
                                     .convert(FluxKind::narrowK);
        
        //Flux.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Fluxs
     */
    public static <T> Pure<FluxKind.µ> unit(){
        return General.<FluxKind.µ,T>unit(FluxInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluxKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * 
       Fluxs.zippingApplicative()
            .ap(widen(Flux.of(l1(this::multiplyByTwo))),widen(Flux.of(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * FluxKind<Function<Integer,Integer>> fluxFn =Fluxs.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FluxKind::narrowK);
        
        FluxKind<Integer> flux = Fluxs.unit()
                                      .unit("hello")
                                      .then(h->Fluxs.functor().map((String v) ->v.length(), h))
                                      .then(h->Fluxs.zippingApplicative().ap(fluxFn, h))
                                      .convert(FluxKind::narrowK);
        
        //Flux.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Fluxs
     */
    public static <T,R> Applicative<FluxKind.µ> zippingApplicative(){
        BiFunction<FluxKind< Function<T, R>>,FluxKind<T>,FluxKind<R>> ap = FluxInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluxKind.widen;
     * FluxKind<Integer> flux  = Fluxs.monad()
                                      .flatMap(i->widen(FluxX.range(0,i)), widen(Flux.of(1,2,3)))
                                      .convert(FluxKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FluxKind<Integer> flux = Fluxs.unit()
                                        .unit("hello")
                                        .then(h->Fluxs.monad().flatMap((String v) ->Fluxs.unit().unit(v.length()), h))
                                        .convert(FluxKind::narrowK);
        
        //Flux.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Fluxs
     */
    public static <T,R> Monad<FluxKind.µ> monad(){
  
        BiFunction<Higher<FluxKind.µ,T>,Function<? super T, ? extends Higher<FluxKind.µ,R>>,Higher<FluxKind.µ,R>> flatMap = FluxInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FluxKind<String> flux = Fluxs.unit()
                                         .unit("hello")
                                         .then(h->Fluxs.monadZero().filter((String t)->t.startsWith("he"), h))
                                         .convert(FluxKind::narrowK);
        
       //Flux.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FluxKind.µ> monadZero(){
        BiFunction<Higher<FluxKind.µ,T>,Predicate<? super T>,Higher<FluxKind.µ,T>> filter = FluxInstances::filter;
        Supplier<Higher<FluxKind.µ, T>> zero = ()-> FluxKind.widen(Flux.empty());
        return General.<FluxKind.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  FluxKind<Integer> flux = Fluxs.<Integer>monadPlus()
                                      .plus(FluxKind.widen(Flux.of()), FluxKind.widen(Flux.of(10)))
                                      .convert(FluxKind::narrowK);
        //Flux.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Fluxs by concatenation
     */
    public static <T> MonadPlus<FluxKind.µ> monadPlus(){
        Monoid<FluxKind<T>> m = Monoid.of(FluxKind.widen(Flux.<T>empty()), FluxInstances::concat);
        Monoid<Higher<FluxKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FluxKind<Integer>> m = Monoid.of(FluxKind.widen(Flux.of()), (a,b)->a.isEmpty() ? b : a);
        FluxKind<Integer> flux = Fluxs.<Integer>monadPlus(m)
                                      .plus(FluxKind.widen(Flux.of(5)), FluxKind.widen(Flux.of(10)))
                                      .convert(FluxKind::narrowK);
        //Flux.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Fluxs
     * @return Type class for combining Fluxs
     */
    public static <T> MonadPlus<FluxKind.µ> monadPlus(Monoid<FluxKind<T>> m){
        Monoid<Higher<FluxKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FluxKind.µ> traverse(){
        BiFunction<Applicative<C2>,FluxKind<Higher<C2, T>>,Higher<C2, FluxKind<T>>> sequenceFn = (ap, flux) -> {
        
            Higher<C2,FluxKind<T>> identity = ap.unit(FluxKind.widen(Flux.empty()));

            BiFunction<Higher<C2,FluxKind<T>>,Higher<C2,T>,Higher<C2,FluxKind<T>>> combineToFlux =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> FluxKind.widen(Flux.concat(a,Flux.just(b)))),acc,next);

            BinaryOperator<Higher<C2,FluxKind<T>>> combineFluxs = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return FluxKind.widen(Flux.concat(l1.narrow(),l2.narrow()));}),a,b); ;

            return ReactiveSeq.fromPublisher(flux).reduce(identity,
                                                            combineToFlux,
                                                            combineFluxs);  

   
        };
        BiFunction<Applicative<C2>,Higher<FluxKind.µ,Higher<C2, T>>,Higher<C2, Higher<FluxKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> FluxKind.widen2(sequenceFn.apply(a, FluxKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Fluxs.foldable()
                        .foldLeft(0, (a,b)->a+b, FluxKind.widen(Flux.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<FluxKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<FluxKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromPublisher(FluxKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<FluxKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromPublisher(FluxKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> FluxKind<T> concat(FluxKind<T> l1, FluxKind<T> l2){
        return FluxKind.widen(Flux.concat(l1,l2));
    }
    private <T> FluxKind<T> of(T value){
        return FluxKind.widen(Flux.just(value));
    }
    private static <T,R> FluxKind<R> ap(FluxKind<Function< T, R>> lt, FluxKind<T> flux){
       return FluxKind.widen(lt.zipWith(flux,(a, b)->a.apply(b)));
    }
    private static <T,R> Higher<FluxKind.µ,R> flatMap(Higher<FluxKind.µ,T> lt, Function<? super T, ? extends  Higher<FluxKind.µ,R>> fn){
        return FluxKind.widen(FluxKind.narrowK(lt).flatMap(fn.andThen(FluxKind::narrowK)));
    }
    private static <T,R> FluxKind<R> map(FluxKind<T> lt, Function<? super T, ? extends R> fn){
        return FluxKind.widen(lt.map(fn));
    }
    private static <T> FluxKind<T> filter(Higher<FluxKind.µ,T> lt, Predicate<? super T> fn){
        return FluxKind.widen(FluxKind.narrow(lt).filter(fn));
    }
}
