package com.aol.cyclops.reactor.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
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
import com.aol.cyclops.reactor.hkt.FluxType;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

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
     *  FluxType<Integer> flux = Fluxs.functor().map(i->i*2, FluxType.widen(Flux.of(1,2,3));
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
     *   FluxType<Integer> flux = Fluxs.unit()
                                       .unit("hello")
                                       .then(h->Fluxs.functor().map((String v) ->v.length(), h))
                                       .convert(FluxType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Fluxs
     */
    public static <T,R>Functor<FluxType.µ> functor(){
        BiFunction<FluxType<T>,Function<? super T, ? extends R>,FluxType<R>> map = FluxInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FluxType<String> flux = Fluxs.unit()
                                     .unit("hello")
                                     .convert(FluxType::narrowK);
        
        //Flux.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Fluxs
     */
    public static <T> Unit<FluxType.µ> unit(){
        return General.<FluxType.µ,T>unit(FluxInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluxType.widen;
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
     * FluxType<Function<Integer,Integer>> fluxFn =Fluxs.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FluxType::narrowK);
        
        FluxType<Integer> flux = Fluxs.unit()
                                      .unit("hello")
                                      .then(h->Fluxs.functor().map((String v) ->v.length(), h))
                                      .then(h->Fluxs.zippingApplicative().ap(fluxFn, h))
                                      .convert(FluxType::narrowK);
        
        //Flux.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Fluxs
     */
    public static <T,R> Applicative<FluxType.µ> zippingApplicative(){
        BiFunction<FluxType< Function<T, R>>,FluxType<T>,FluxType<R>> ap = FluxInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FluxType.widen;
     * FluxType<Integer> flux  = Fluxs.monad()
                                      .flatMap(i->widen(FluxX.range(0,i)), widen(Flux.of(1,2,3)))
                                      .convert(FluxType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FluxType<Integer> flux = Fluxs.unit()
                                        .unit("hello")
                                        .then(h->Fluxs.monad().flatMap((String v) ->Fluxs.unit().unit(v.length()), h))
                                        .convert(FluxType::narrowK);
        
        //Flux.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Fluxs
     */
    public static <T,R> Monad<FluxType.µ> monad(){
  
        BiFunction<Higher<FluxType.µ,T>,Function<? super T, ? extends Higher<FluxType.µ,R>>,Higher<FluxType.µ,R>> flatMap = FluxInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FluxType<String> flux = Fluxs.unit()
                                         .unit("hello")
                                         .then(h->Fluxs.monadZero().filter((String t)->t.startsWith("he"), h))
                                         .convert(FluxType::narrowK);
        
       //Flux.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FluxType.µ> monadZero(){
        BiFunction<Higher<FluxType.µ,T>,Predicate<? super T>,Higher<FluxType.µ,T>> filter = FluxInstances::filter;
        Supplier<Higher<FluxType.µ, T>> zero = ()->FluxType.widen(Flux.empty());
        return General.<FluxType.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  FluxType<Integer> flux = Fluxs.<Integer>monadPlus()
                                      .plus(FluxType.widen(Flux.of()), FluxType.widen(Flux.of(10)))
                                      .convert(FluxType::narrowK);
        //Flux.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Fluxs by concatenation
     */
    public static <T> MonadPlus<FluxType.µ> monadPlus(){
        Monoid<FluxType<T>> m = Monoid.of(FluxType.widen(Flux.<T>empty()), FluxInstances::concat);
        Monoid<Higher<FluxType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FluxType<Integer>> m = Monoid.of(FluxType.widen(Flux.of()), (a,b)->a.isEmpty() ? b : a);
        FluxType<Integer> flux = Fluxs.<Integer>monadPlus(m)
                                      .plus(FluxType.widen(Flux.of(5)), FluxType.widen(Flux.of(10)))
                                      .convert(FluxType::narrowK);
        //Flux.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Fluxs
     * @return Type class for combining Fluxs
     */
    public static <T> MonadPlus<FluxType.µ> monadPlus(Monoid<FluxType<T>> m){
        Monoid<Higher<FluxType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FluxType.µ> traverse(){
        BiFunction<Applicative<C2>,FluxType<Higher<C2, T>>,Higher<C2, FluxType<T>>> sequenceFn = (ap,flux) -> {
        
            Higher<C2,FluxType<T>> identity = ap.unit(FluxType.widen(Flux.empty()));

            BiFunction<Higher<C2,FluxType<T>>,Higher<C2,T>,Higher<C2,FluxType<T>>> combineToFlux =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> FluxType.widen(Flux.concat(a,Flux.just(b)))),acc,next);

            BinaryOperator<Higher<C2,FluxType<T>>> combineFluxs = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { return FluxType.widen(Flux.concat(l1.narrow(),l2.narrow()));}),a,b); ;  

            return ReactiveSeq.fromPublisher(flux).reduce(identity,
                                                            combineToFlux,
                                                            combineFluxs);  

   
        };
        BiFunction<Applicative<C2>,Higher<FluxType.µ,Higher<C2, T>>,Higher<C2, Higher<FluxType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> FluxType.widen2(sequenceFn.apply(a, FluxType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Fluxs.foldable()
                        .foldLeft(0, (a,b)->a+b, FluxType.widen(Flux.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<FluxType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<FluxType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromPublisher(FluxType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<FluxType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromPublisher(FluxType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> FluxType<T> concat(FluxType<T> l1, FluxType<T> l2){
        return FluxType.widen(Flux.concat(l1,l2));
    }
    private <T> FluxType<T> of(T value){
        return FluxType.widen(Flux.just(value));
    }
    private static <T,R> FluxType<R> ap(FluxType<Function< T, R>> lt,  FluxType<T> flux){
       return FluxType.widen(lt.zipWith(flux,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<FluxType.µ,R> flatMap( Higher<FluxType.µ,T> lt, Function<? super T, ? extends  Higher<FluxType.µ,R>> fn){
        return FluxType.widen(FluxType.narrowK(lt).flatMap(fn.andThen(FluxType::narrowK)));
    }
    private static <T,R> FluxType<R> map(FluxType<T> lt, Function<? super T, ? extends R> fn){
        return FluxType.widen(lt.map(fn));
    }
    private static <T> FluxType<T> filter(Higher<FluxType.µ,T> lt, Predicate<? super T> fn){
        return FluxType.widen(FluxType.narrow(lt).filter(fn));
    }
}
