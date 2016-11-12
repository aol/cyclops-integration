package com.aol.cyclops.hkt.instances.cyclops;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Monoids;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.FutureType;
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
 * Companion class for creating Type Class instances for working with FutureWs
 * @author johnmcclean
 *
 */
@UtilityClass
public class FutureWs {

    
    /**
     * 
     * Transform a future, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  FutureType<Integer> future = FutureWs.functor().map(i->i*2, FutureType.widen(FutureW.ofResult(2));
     *  
     *  //[4]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with FutureWs
     * <pre>
     * {@code 
     *   FutureType<Integer> future = FutureWs.unit()
                                       .unit("hello")
                                       .then(h->FutureWs.functor().map((String v) ->v.length(), h))
                                       .convert(FutureType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for FutureWs
     */
    public static <T,R>Functor<FutureType.µ> functor(){
        BiFunction<FutureType<T>,Function<? super T, ? extends R>,FutureType<R>> map = FutureWs::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FutureType<String> future = FutureWs.unit()
                                     .unit("hello")
                                     .convert(FutureType::narrowK);
        
        //FutureW("hello")
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for FutureWs
     */
    public static Unit<FutureType.µ> unit(){
        return General.unit(FutureWs::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asFutureW;
     * 
       FutureWs.zippingApplicative()
            .ap(widen(asFutureW(l1(this::multiplyByTwo))),widen(asFutureW(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * FutureType<Function<Integer,Integer>> futureFn =FutureWs.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FutureType::narrowK);
        
        FutureType<Integer> future = FutureWs.unit()
                                      .unit("hello")
                                      .then(h->FutureWs.functor().map((String v) ->v.length(), h))
                                      .then(h->FutureWs.applicative().ap(futureFn, h))
                                      .convert(FutureType::narrowK);
        
        //FutureW("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for FutureWs
     */
    public static <T,R> Applicative<FutureType.µ> applicative(){
        BiFunction<FutureType< Function<T, R>>,FutureType<T>,FutureType<R>> ap = FutureWs::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureType.widen;
     * FutureType<Integer> future  = FutureWs.monad()
                                      .flatMap(i->widen(FutureW.ofResult(0)), widen(FutureW.ofResult(2)))
                                      .convert(FutureType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FutureType<Integer> future = FutureWs.unit()
                                        .unit("hello")
                                        .then(h->FutureWs.monad().flatMap((String v) ->FutureWs.unit().unit(v.length()), h))
                                        .convert(FutureType::narrowK);
        
        //FutureW("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for FutureWs
     */
    public static <T,R> Monad<FutureType.µ> monad(){
  
        BiFunction<Higher<FutureType.µ,T>,Function<? super T, ? extends Higher<FutureType.µ,R>>,Higher<FutureType.µ,R>> flatMap = FutureWs::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FutureType<String> future = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
       //FutureW["hello"]
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FutureType.µ> monadZero(){
        
        return General.monadZero(monad(), FutureType.future());
    }
    /**
     * <pre>
     * {@code 
     *  FutureType<Integer> future = FutureWs.<Integer>monadPlus()
                                      .plus(FutureType.widen(FutureW.future()), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        //FutureW[10]
     * 
     * }
     * </pre>
     * @return Type class for combining FutureWs by concatenation
     */
    public static <T> MonadPlus<FutureType.µ,T> monadPlus(){
        Monoid<FutureW<T>> mn = Monoids.firstSuccessfulFuture();
        Monoid<FutureType<T>> m = Monoid.of(FutureType.widen(mn.zero()), (f,g)-> FutureType.widen(
                                                                                                                                   mn.apply(FutureType.narrow(f), FutureType.narrow(g))));
                
        Monoid<Higher<FutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FutureType<Integer>> m = Monoid.of(FutureType.widen(FutureW.future()()), (a,b)->a.isDone() ? b : a);
        FutureType<Integer> future = FutureWs.<Integer>monadPlus(m)
                                      .plus(FutureType.widen(FutureW.ofResult(5)), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        //FutureW(5)
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining FutureWs
     * @return Type class for combining FutureWs
     */
    public static <T> MonadPlus<FutureType.µ,T> monadPlus(Monoid<FutureType<T>> m){
        Monoid<Higher<FutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FutureType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), FutureWs::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = FutureWs.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureType.widen(FutureW.ofResult(4)));
        
        //4
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<FutureType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<FutureType.µ,T>,T> foldRightFn =  (m,l)-> m.apply(m.zero(), FutureType.narrow(l).get());
        BiFunction<Monoid<T>,Higher<FutureType.µ,T>,T> foldLeftFn = (m,l)->  m.apply(m.zero(), FutureType.narrow(l).get());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<FutureType.µ> comonad(){
        Function<? super Higher<FutureType.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(FutureType::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> FutureType<T> of(T value){
        return FutureType.widen(FutureW.ofResult(value));
    }
    private static <T,R> FutureType<R> ap(FutureType<Function< T, R>> lt,  FutureType<T> future){
        return FutureType.widen(lt.combine(future, (a,b)->a.apply(b)));
        
    }
    private static <T,R> Higher<FutureType.µ,R> flatMap( Higher<FutureType.µ,T> lt, Function<? super T, ? extends  Higher<FutureType.µ,R>> fn){
        return FutureType.widen(FutureType.narrow(lt).flatMap(fn.andThen(FutureType::narrowK)));
    }
    private static <T,R> FutureType<R> map(FutureType<T> lt, Function<? super T, ? extends R> fn){
        return FutureType.widen(lt.map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<FutureType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<FutureType.µ, T> ds){
        FutureW<T> future = FutureType.narrow(ds);
        return applicative.map(FutureType::ofResult, fn.apply(future.get()));
    }
   
}
