package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import com.aol.cyclops.vavr.Vavr;
import com.aol.cyclops.vavr.hkt.FutureKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.Monoids;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import javaslang.concurrent.Future;
import lombok.experimental.UtilityClass;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Companion class for creating Type Class instances for working with Futures
 * @author johnmcclean
 *
 */
@UtilityClass
public class FutureInstances {

    
    /**
     * 
     * Transform a Future, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  FutureKind<Integer> future = Futures.functor().map(i->i*2, FutureKind.widen(Future.successful(1));
     *  
     *  //[2]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Futures
     * <pre>
     * {@code 
     *   FutureKind<Integer> ft = Futures.unit()
                                       .unit("hello")
                                       .then(h->Futures.functor().map((String v) ->v.length(), h))
                                       .convert(FutureKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Futures
     */
    public static <T,R>Functor<FutureKind.µ> functor(){
        BiFunction<FutureKind<T>,Function<? super T, ? extends R>,FutureKind<R>> map = FutureInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * FutureKind<String> ft = Futures.unit()
                                     .unit("hello")
                                     .convert(FutureKind::narrowK);
        
        //Arrays.asFuture("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Futures
     */
    public static <T> Pure<FutureKind.µ> unit(){
        return General.<FutureKind.µ,T>unit(FutureInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * 
       Futures.applicative()
            .ap(widen(Future.successful(l1(this::multiplyByTwo))),widen(asFuture(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * FutureKind<Function<Integer,Integer>> ftFn =Futures.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(FutureKind::narrowK);
        
        FutureKind<Integer> ft = Futures.unit()
                                      .unit("hello")
                                      .then(h->Futures.functor().map((String v) ->v.length(), h))
                                      .then(h->Futures.applicative().ap(ftFn, h))
                                      .convert(FutureKind::narrowK);
        
        //Arrays.asFuture("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Futures
     */
    public static <T,R> Applicative<FutureKind.µ> applicative(){
        BiFunction<FutureKind< Function<T, R>>,FutureKind<T>,FutureKind<R>> ap = FutureInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.FutureKind.widen;
     * FutureKind<Integer> ft  = Futures.monad()
                                      .flatMap(i->widen(Future.successful(i), widen(Future.successful(3))
                                      .convert(FutureKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    FutureKind<Integer> ft = Futures.unit()
                                        .unit("hello")
                                        .then(h->Futures.monad().flatMap((String v) ->Futures.unit().unit(v.length()), h))
                                        .convert(FutureKind::narrowK);
        
        //Arrays.asFuture("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Futures
     */
    public static <T,R> Monad<FutureKind.µ> monad(){
  
        BiFunction<Higher<FutureKind.µ,T>,Function<? super T, ? extends Higher<FutureKind.µ,R>>,Higher<FutureKind.µ,R>> flatMap = FutureInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  FutureKind<String> ft = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureKind::narrowK);
        
       //Arrays.asFuture("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<FutureKind.µ> monadZero(){
        
        return General.monadZero(monad(), FutureKind.promise());
    }
    /**
     * <pre>
     * {@code 
     *  FutureKind<Integer> ft = Futures.<Integer>monadPlus()
                                      .plus(FutureKind.widen(Arrays.asFuture()), FutureKind.widen(Future.successful((10)))
                                      .convert(FutureKind::narrowK);
        //Future(10)
     * 
     * }
     * </pre>
     * @return Type class for combining Futures by concatenation
     */
    public static <T> MonadPlus<FutureKind.µ> monadPlus(){
        Monoid<cyclops.async.Future<T>> mn = Monoids.firstSuccessfulFuture();
        Monoid<FutureKind<T>> m = Monoid.of(FutureKind.widen(mn.zero()), (f, g)-> FutureKind.widen(
                                                                             mn.apply(Vavr.future(f), Vavr.future(g))));
                
        Monoid<Higher<FutureKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<FutureKind<Integer>> m = Monoid.of(FutureKind.widen(Future.failed(e), (a,b)->a.isEmpty() ? b : a);
        FutureKind<Integer> ft = Futures.<Integer>monadPlus(m)
                                      .plus(FutureKind.widen(Future.successful(5), FutureKind.widen(Future.successful(10))
                                      .convert(FutureKind::narrowK);
        //Future(5)
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Futures
     * @return Type class for combining Futures
     */
    public static <T> MonadPlus<FutureKind.µ> monadPlus(Monoid<FutureKind<T>> m){
        Monoid<Higher<FutureKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<FutureKind.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), FutureInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Futures.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureKind.widen(Future.successful(4));
        
        //4
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<FutureKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<FutureKind.µ,T>,T> foldRightFn =  (m, l)-> m.apply(m.zero(), FutureKind.narrow(l).get());
        BiFunction<Monoid<T>,Higher<FutureKind.µ,T>,T> foldLeftFn = (m, l)->  m.apply(m.zero(), FutureKind.narrow(l).get());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<FutureKind.µ> comonad(){
        Function<? super Higher<FutureKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(FutureKind::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> FutureKind<T> of(T value){
        return FutureKind.widen(Future.successful(value));
    }
    private static <T,R> FutureKind<R> ap(FutureKind<Function< T, R>> lt, FutureKind<T> list){
        return FutureKind.widen(Vavr.future(lt).combine(Vavr.future(list), (a, b)->a.apply(b)));
        
    }
    private static <T,R> Higher<FutureKind.µ,R> flatMap(Higher<FutureKind.µ,T> lt, Function<? super T, ? extends  Higher<FutureKind.µ,R>> fn){
        return FutureKind.widen(FutureKind.narrow(lt).flatMap(fn.andThen(FutureKind::narrowK)));
    }
    private static <T,R> FutureKind<R> map(FutureKind<T> lt, Function<? super T, ? extends R> fn){
        return FutureKind.widen(lt.map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<FutureKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                          Higher<FutureKind.µ, T> ds){
        Future<T> future = FutureKind.narrow(ds);
        return applicative.map(FutureKind::successful, fn.apply(future.get()));
    }
   
}
