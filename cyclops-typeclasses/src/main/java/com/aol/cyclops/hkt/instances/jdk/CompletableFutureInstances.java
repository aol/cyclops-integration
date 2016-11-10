package com.aol.cyclops.hkt.instances.jdk;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Monoids;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.CompletableFutureType;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;

import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with CompletableFutures
 * @author johnmcclean
 *
 */
@UtilityClass
public class CompletableFutureInstances {

    
    /**
     * 
     * Transform a future, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  CompletableFutureType<Integer> future = CompletableFutures.functor().map(i->i*2, CompletableFutureType.widen(CompletableFuture.completedFuture(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with CompletableFutures
     * <pre>
     * {@code 
     *   CompletableFutureType<Integer> future = CompletableFutures.unit()
                                       .unit("hello")
                                       .then(h->CompletableFutures.functor().map((String v) ->v.length(), h))
                                       .convert(CompletableFutureType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for CompletableFutures
     */
    public static <T,R>Functor<CompletableFutureType.µ> functor(){
        BiFunction<CompletableFutureType<T>,Function<? super T, ? extends R>,CompletableFutureType<R>> map = CompletableFutureInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * CompletableFutureType<String> future = CompletableFutures.unit()
                                     .unit("hello")
                                     .convert(CompletableFutureType::narrowK);
        
        //CompletableFuture.completedFuture("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for CompletableFutures
     */
    public static Unit<CompletableFutureType.µ> unit(){
        return General.unit(CompletableFutureInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.CompletableFutureType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * 
       CompletableFutures.applicative()
            .ap(widen(asCompletableFuture(l1(this::multiplyByTwo))),widen(asCompletableFuture(3)));
     * 
     * //[6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * CompletableFutureType<Function<Integer,Integer>> futureFn =CompletableFutures.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(CompletableFutureType::narrowK);
        
        CompletableFutureType<Integer> future = CompletableFutures.unit()
                                      .unit("hello")
                                      .then(h->CompletableFutures.functor().map((String v) ->v.length(), h))
                                      .then(h->CompletableFutures.applicative().ap(futureFn, h))
                                      .convert(CompletableFutureType::narrowK);
        
        //CompletableFuture.completedFuture("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for CompletableFutures
     */
    public static <T,R> Applicative<CompletableFutureType.µ> applicative(){
        BiFunction<CompletableFutureType< Function<T, R>>,CompletableFutureType<T>,CompletableFutureType<R>> ap = CompletableFutureInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.CompletableFutureType.widen;
     * CompletableFutureType<Integer> future  = CompletableFutures.monad()
                                      .flatMap(i->widen(CompletableFutureX.range(0,i)), widen(CompletableFuture.completedFuture(3)))
                                      .convert(CompletableFutureType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    CompletableFutureType<Integer> future = CompletableFutures.unit()
                                        .unit("hello")
                                        .then(h->CompletableFutures.monad().flatMap((String v) ->CompletableFutures.unit().unit(v.length()), h))
                                        .convert(CompletableFutureType::narrowK);
        
        //CompletableFuture.completedFuture("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for CompletableFutures
     */
    public static <T,R> Monad<CompletableFutureType.µ> monad(){
  
        BiFunction<Higher<CompletableFutureType.µ,T>,Function<? super T, ? extends Higher<CompletableFutureType.µ,R>>,Higher<CompletableFutureType.µ,R>> flatMap = CompletableFutureInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  CompletableFutureType<String> future = CompletableFutures.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutures.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(CompletableFutureType::narrowK);
        
       //CompletableFuture.completedFuture("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<CompletableFutureType.µ> monadZero(){
        
        return General.monadZero(monad(), CompletableFutureType.widen(new CompletableFuture<T>()));
    }
    /**
     * <pre>
     * {@code 
     *  CompletableFutureType<Integer> future = CompletableFutures.<Integer>monadPlus()
                                      .plus(CompletableFutureType.widen(CompletableFuture.completedFuture()), CompletableFutureType.widen(CompletableFuture.completedFuture(10)))
                                      .convert(CompletableFutureType::narrowK);
        //CompletableFuture.completedFuture(10))
     * 
     * }
     * </pre>
     * @return Type class for combining CompletableFutures by concatenation
     */
    public static <T> MonadPlus<CompletableFutureType.µ,T> monadPlus(){
        Monoid<CompletableFuture<T>> mn = Monoids.firstCompleteCompletableFuture();
        Monoid<CompletableFutureType<T>> m = Monoid.of(CompletableFutureType.widen(mn.zero()), (f,g)-> CompletableFutureType.widen(
                                                                                                                                   mn.apply(CompletableFutureType.narrow(f), CompletableFutureType.narrow(g))));
                
        Monoid<Higher<CompletableFutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<CompletableFutureType<Integer>> m = Monoid.of(CompletableFutureType.widen(CompletableFuture.completedFuture()), (a,b)->a.isEmpty() ? b : a);
        CompletableFutureType<Integer> future = CompletableFutures.<Integer>monadPlus(m)
                                      .plus(CompletableFutureType.widen(CompletableFuture.completedFuture(5)), CompletableFutureType.widen(CompletableFuture.completedFuture(10)))
                                      .convert(CompletableFutureType::narrowK);
        //CompletableFuture.completedFuture(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining CompletableFutures
     * @return Type class for combining CompletableFutures
     */
    public static <T> MonadPlus<CompletableFutureType.µ,T> monadPlus(Monoid<CompletableFutureType<T>> m){
        Monoid<Higher<CompletableFutureType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<CompletableFutureType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), CompletableFutureInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = CompletableFutures.foldable()
                        .foldLeft(0, (a,b)->a+b, CompletableFutureType.widen(CompletableFuture.completedFuture(3)));
        
        //3
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<CompletableFutureType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<CompletableFutureType.µ,T>,T> foldRightFn =  (m,l)-> m.apply(m.zero(), CompletableFutureType.narrow(l).join());
        BiFunction<Monoid<T>,Higher<CompletableFutureType.µ,T>,T> foldLeftFn = (m,l)->  m.apply(m.zero(), CompletableFutureType.narrow(l).join());
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> CompletableFutureType<T> of(T value){
        return CompletableFutureType.widen(CompletableFuture.completedFuture(value));
    }
    private static <T,R> CompletableFutureType<R> ap(CompletableFutureType<Function< T, R>> lt,  CompletableFutureType<T> future){
        return CompletableFutureType.widen(lt.thenCombine(future, (a,b)->a.apply(b)));
        
    }
    private static <T,R> Higher<CompletableFutureType.µ,R> flatMap( Higher<CompletableFutureType.µ,T> lt, Function<? super T, ? extends  Higher<CompletableFutureType.µ,R>> fn){
        return CompletableFutureType.widen(CompletableFutureType.narrow(lt).thenCompose(fn.andThen(CompletableFutureType::narrowK)));
    }
    private static <T,R> CompletableFutureType<R> map(CompletableFutureType<T> lt, Function<? super T, ? extends R> fn){
        return CompletableFutureType.widen(lt.thenApply(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<CompletableFutureType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<CompletableFutureType.µ, T> ds){
        CompletableFuture<T> future = CompletableFutureType.narrow(ds);
        return applicative.map(CompletableFutureType::completedFuture, fn.apply(future.join()));
    }
   
}
