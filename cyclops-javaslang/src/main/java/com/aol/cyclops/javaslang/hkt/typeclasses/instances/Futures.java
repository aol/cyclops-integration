package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Monoids;
import com.aol.cyclops.control.FutureW;
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
import com.aol.cyclops.javaslang.Javaslang;
import com.aol.cyclops.javaslang.hkt.FutureType;

import javaslang.concurrent.Future;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Futures
 * @author johnmcclean
 *
 */
@UtilityClass
public class Futures {

    
    /**
     * 
     * Transform a Future, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  FutureType<Integer> future = Futures.functor().map(i->i*2, FutureType.widen(Arrays.asFuture(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Futures
     * <pre>
     * {@code 
     *   FutureType<Integer> ft = Futures.unit()
                                       .unit("hello")
                                       .then(h->Futures.functor().map((String v) ->v.length(), h))
                                       .convert(FutureType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Futures
     */
    public static <T,R>Functor<FutureType.µ> functor(){
        BiFunction<FutureType<T>,Function<? super T, ? extends R>,FutureType<R>> map = Futures::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FutureType<String> ft = Futures.unit()
                                     .unit("hello")
                                     .convert(FutureType::narrowK);
        
        //Arrays.asFuture("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Futures
     */
    public static Unit<FutureType.µ> unit(){
        return General.unit(Futures::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asFuture;
     * 
       Futures.zippingApplicative()
            .ap(widen(asFuture(l1(this::multiplyByTwo))),widen(asFuture(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * FutureType<Function<Integer,Integer>> ftFn =Futures.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FutureType::narrowK);
        
        FutureType<Integer> ft = Futures.unit()
                                      .unit("hello")
                                      .then(h->Futures.functor().map((String v) ->v.length(), h))
                                      .then(h->Futures.applicative().ap(ftFn, h))
                                      .convert(FutureType::narrowK);
        
        //Arrays.asFuture("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Futures
     */
    public static <T,R> Applicative<FutureType.µ> applicative(){
        BiFunction<FutureType< Function<T, R>>,FutureType<T>,FutureType<R>> ap = Futures::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureType.widen;
     * FutureType<Integer> ft  = Futures.monad()
                                      .flatMap(i->widen(FutureX.range(0,i)), widen(Arrays.asFuture(1,2,3)))
                                      .convert(FutureType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FutureType<Integer> ft = Futures.unit()
                                        .unit("hello")
                                        .then(h->Futures.monad().flatMap((String v) ->Futures.unit().unit(v.length()), h))
                                        .convert(FutureType::narrowK);
        
        //Arrays.asFuture("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Futures
     */
    public static <T,R> Monad<FutureType.µ> monad(){
  
        BiFunction<Higher<FutureType.µ,T>,Function<? super T, ? extends Higher<FutureType.µ,R>>,Higher<FutureType.µ,R>> flatMap = Futures::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FutureType<String> ft = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
       //Arrays.asFuture("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FutureType.µ> monadZero(){
        
        return General.monadZero(monad(), FutureType.promise());
    }
    /**
     * <pre>
     * {@code 
     *  FutureType<Integer> ft = Futures.<Integer>monadPlus()
                                      .plus(FutureType.widen(Arrays.asFuture()), FutureType.widen(Arrays.asFuture(10)))
                                      .convert(FutureType::narrowK);
        //Arrays.asFuture(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Futures by concatenation
     */
    public static <T> MonadPlus<FutureType.µ,T> monadPlus(){
        Monoid<FutureW<T>> mn = Monoids.firstSuccessfulFuture();
        Monoid<FutureType<T>> m = Monoid.of(FutureType.widen(mn.zero()), (f,g)-> FutureType.widen(
                                                                             mn.apply(Javaslang.futureW(f), Javaslang.futureW(g))));
                
        Monoid<Higher<FutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FutureType<Integer>> m = Monoid.of(FutureType.widen(Arrays.asFuture()), (a,b)->a.isEmpty() ? b : a);
        FutureType<Integer> ft = Futures.<Integer>monadPlus(m)
                                      .plus(FutureType.widen(Arrays.asFuture(5)), FutureType.widen(Arrays.asFuture(10)))
                                      .convert(FutureType::narrowK);
        //Arrays.asFuture(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Futures
     * @return Type class for combining Futures
     */
    public static <T> MonadPlus<FutureType.µ,T> monadPlus(Monoid<FutureType<T>> m){
        Monoid<Higher<FutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FutureType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), Futures::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Futures.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureType.widen(Arrays.asFuture(1,2,3,4)));
        
        //10
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
  
    
    private <T> FutureType<T> of(T value){
        return FutureType.widen(Future.successful(value));
    }
    private static <T,R> FutureType<R> ap(FutureType<Function< T, R>> lt,  FutureType<T> list){
        return FutureType.widen(Javaslang.futureW(lt).combine(Javaslang.futureW(list), (a,b)->a.apply(b)));
        
    }
    private static <T,R> Higher<FutureType.µ,R> flatMap( Higher<FutureType.µ,T> lt, Function<? super T, ? extends  Higher<FutureType.µ,R>> fn){
        return FutureType.widen(FutureType.narrow(lt).flatMap(fn.andThen(FutureType::narrowK)));
    }
    private static <T,R> FutureType<R> map(FutureType<T> lt, Function<? super T, ? extends R> fn){
        return FutureType.widen(lt.map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<FutureType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<FutureType.µ, T> ds){
        Future<T> future = FutureType.narrow(ds);
        return applicative.map(FutureType::successful, fn.apply(future.get()));
    }
   
}
