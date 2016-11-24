package com.aol.cyclops.hkt.instances.cyclops;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Monoids;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.comonad.Comonad;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;

import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Maybes
 * @author johnmcclean
 *
 */
@UtilityClass
public class MaybeInstances {

    

    /**
     * 
     * Transform a maybe, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  MaybeType<Integer> maybe = Maybes.functor().map(i->i*2, MaybeType.widen(Maybe.just(1));
     *  
     *  //[2]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Maybes
     * <pre>
     * {@code 
     *   MaybeType<Integer> maybe = Maybes.unit()
                                       .unit("hello")
                                       .then(h->Maybes.functor().map((String v) ->v.length(), h))
                                       .convert(MaybeType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Maybes
     */
    public static <T,R>Functor<MaybeType.µ> functor(){
        BiFunction<MaybeType<T>,Function<? super T, ? extends R>,MaybeType<R>> map = MaybeInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * MaybeType<String> maybe = Maybes.unit()
                                     .unit("hello")
                                     .convert(MaybeType::narrowK);
        
        //Maybe.just("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Maybes
     */
    public static <T> Unit<MaybeType.µ> unit(){
        return General.<MaybeType.µ,T>unit(MaybeInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MaybeType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Maybe.just;
     * 
       Maybes.zippingApplicative()
            .ap(widen(asMaybe(l1(this::multiplyByTwo))),widen(asMaybe(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * MaybeType<Function<Integer,Integer>> maybeFn =Maybes.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(MaybeType::narrowK);
        
        MaybeType<Integer> maybe = Maybes.unit()
                                      .unit("hello")
                                      .then(h->Maybes.functor().map((String v) ->v.length(), h))
                                      .then(h->Maybes.applicative().ap(maybeFn, h))
                                      .convert(MaybeType::narrowK);
        
        //Maybe.just("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Maybes
     */
    public static <T,R> Applicative<MaybeType.µ> applicative(){
        BiFunction<MaybeType< Function<T, R>>,MaybeType<T>,MaybeType<R>> ap = MaybeInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MaybeType.widen;
     * MaybeType<Integer> maybe  = Maybes.monad()
                                      .flatMap(i->widen(MaybeX.range(0,i)), widen(Maybe.just(1,2,3)))
                                      .convert(MaybeType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    MaybeType<Integer> maybe = Maybes.unit()
                                        .unit("hello")
                                        .then(h->Maybes.monad().flatMap((String v) ->Maybes.unit().unit(v.length()), h))
                                        .convert(MaybeType::narrowK);
        
        //Maybe.just("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Maybes
     */
    public static <T,R> Monad<MaybeType.µ> monad(){
  
        BiFunction<Higher<MaybeType.µ,T>,Function<? super T, ? extends Higher<MaybeType.µ,R>>,Higher<MaybeType.µ,R>> flatMap = MaybeInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  MaybeType<String> maybe = Maybes.unit()
                                     .unit("hello")
                                     .then(h->Maybes.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(MaybeType::narrowK);
        
       //Maybe.just("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<MaybeType.µ> monadZero(){
        
        return General.monadZero(monad(), MaybeType.none());
    }
    /**
     * <pre>
     * {@code 
     *  MaybeType<Integer> maybe = Maybes.<Integer>monadPlus()
                                      .plus(MaybeType.widen(Maybe.just()), MaybeType.widen(Maybe.just(10)))
                                      .convert(MaybeType::narrowK);
        //Maybe.just(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Maybes by concatenation
     */
    public static <T> MonadPlus<MaybeType.µ> monadPlus(){
        Monoid<Maybe<T>> mn = Monoids.firstPresentMaybe();
        Monoid<MaybeType<T>> m = Monoid.of(MaybeType.widen(mn.zero()), (f,g)-> MaybeType.widen(
                                                                                mn.apply(MaybeType.narrow(f), MaybeType.narrow(g))));
                
        Monoid<Higher<MaybeType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<MaybeType<Integer>> m = Monoid.of(MaybeType.widen(Maybe.just()), (a,b)->a.isEmpty() ? b : a);
        MaybeType<Integer> maybe = Maybes.<Integer>monadPlus(m)
                                      .plus(MaybeType.widen(Maybe.just(5)), MaybeType.widen(Maybe.just(10)))
                                      .convert(MaybeType::narrowK);
        //Maybe[5]
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Maybes
     * @return Type class for combining Maybes
     */
    public static <T> MonadPlus<MaybeType.µ> monadPlus(Monoid<MaybeType<T>> m){
        Monoid<Higher<MaybeType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<MaybeType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), MaybeInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Maybes.foldable()
                           .foldLeft(0, (a,b)->a+b, MaybeType.widen(Maybe.just(1)));
        
        //1
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<MaybeType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<MaybeType.µ,T>,T> foldRightFn =  (m,l)-> MaybeType.narrow(l).orElse(m.zero());
        BiFunction<Monoid<T>,Higher<MaybeType.µ,T>,T> foldLeftFn = (m,l)-> MaybeType.narrow(l).orElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    
    public static <T> Comonad<MaybeType.µ> comonad(){
        Function<? super Higher<MaybeType.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(MaybeType::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
  
    
    private <T> MaybeType<T> of(T value){
        return MaybeType.widen(Maybe.of(value));
    }
    private static <T,R> MaybeType<R> ap(MaybeType<Function< T, R>> lt,  MaybeType<T> maybe){
        return MaybeType.widen(lt.combine(maybe, (a,b)->a.apply(b)).toMaybe());
        
    }
    private static <T,R> Higher<MaybeType.µ,R> flatMap( Higher<MaybeType.µ,T> lt, Function<? super T, ? extends  Higher<MaybeType.µ,R>> fn){
        return MaybeType.widen(MaybeType.narrow(lt).flatMap(fn.andThen(MaybeType::narrow)));
    }
    private static <T,R> MaybeType<R> map(MaybeType<T> lt, Function<? super T, ? extends R> fn){
        return MaybeType.widen(MaybeType.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<MaybeType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<MaybeType.µ, T> ds){
       
        Maybe<T> maybe = MaybeType.narrow(ds);
         Higher<C2, MaybeType<R>> res = maybe.visit(some-> applicative.map(m->MaybeType.of(m), fn.apply(some)),
                                                    ()->applicative.unit(MaybeType.widen(MaybeType.<R>none())));
        
        return MaybeType.widen2(res);
    }
   
}
