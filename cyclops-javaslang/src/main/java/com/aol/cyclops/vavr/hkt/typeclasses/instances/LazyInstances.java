package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.LazyKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.vavr.FromCyclopsReact;
import com.aol.cyclops.vavr.Vavr;

import javaslang.Lazy;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Lazys
 * @author johnmcclean
 *
 */
@UtilityClass
public class LazyInstances {

    
    /**
     * 
     * Transform a lazy, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  LazyKind<Integer> lazy = LazyInstances.functor().map(i->i*2, LazyKind.widen(Lazy.of(()->1));
     *  
     *  //[2]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Lazys
     * <pre>
     * {@code 
     *   LazyKind<Integer> lazy = Lazys.unit()
                                       .unit("hello")
                                       .then(h->Lazys.functor().map((String v) ->v.length(), h))
                                       .convert(LazyKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lazys
     */
    public static <T,R>Functor<LazyKind.µ> functor(){
        BiFunction<LazyKind<T>,Function<? super T, ? extends R>,LazyKind<R>> map = LazyInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * LazyKind<String> lazy = Lazys.unit()
                                     .unit("hello")
                                     .convert(LazyKind::narrowK);
        
        //Lazy.just("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lazys
     */
    public static <T> Pure<LazyKind.µ> unit(){
        return General.<LazyKind.µ,T>unit(LazyInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.LazyKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Lazy.just;
     * 
       Lazys.zippingApplicative()
            .ap(widen(asLazy(l1(this::multiplyByTwo))),widen(asLazy(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * LazyKind<Function<Integer,Integer>> lazyFn =Lazys.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(LazyKind::narrowK);
        
        LazyKind<Integer> lazy = Lazys.unit()
                                      .unit("hello")
                                      .then(h->Lazys.functor().map((String v) ->v.length(), h))
                                      .then(h->Lazys.applicative().ap(lazyFn, h))
                                      .convert(LazyKind::narrowK);
        
        //Lazy.just("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Lazys
     */
    public static <T,R> Applicative<LazyKind.µ> applicative(){
        BiFunction<LazyKind< Function<T, R>>,LazyKind<T>,LazyKind<R>> ap = LazyInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.LazyKind.widen;
     * LazyKind<Integer> lazy  = Lazys.monad()
                                      .flatMap(i->widen(LazyX.range(0,i)), widen(Lazy.just(1,2,3)))
                                      .convert(LazyKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    LazyKind<Integer> lazy = Lazys.unit()
                                        .unit("hello")
                                        .then(h->Lazys.monad().flatMap((String v) ->Lazys.unit().unit(v.length()), h))
                                        .convert(LazyKind::narrowK);
        
        //Lazy.just("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Lazys
     */
    public static <T,R> Monad<LazyKind.µ> monad(){
  
        BiFunction<Higher<LazyKind.µ,T>,Function<? super T, ? extends Higher<LazyKind.µ,R>>,Higher<LazyKind.µ,R>> flatMap = LazyInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  LazyKind<String> lazy = Lazys.unit()
                                     .unit("hello")
                                     .then(h->Lazys.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(LazyKind::narrowK);
        
       //Lazy.just("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<LazyKind.µ> monadZero(){
        
        return General.monadZero(monad(), LazyKind.of(()->null));
    }
    /**
     * <pre>
     * {@code 
     *  LazyKind<Integer> lazy = Lazys.<Integer>monadPlus()
                                      .plus(LazyKind.widen(Lazy.just()), LazyKind.widen(Lazy.just(10)))
                                      .convert(LazyKind::narrowK);
        //Lazy.just(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Lazys by concatenation
     */
    public static <T> MonadPlus<LazyKind.µ> monadPlus(){
        Monoid<LazyKind<T>> m = Monoid.of( LazyKind.of(()->null),
                                            (a,b)-> a.get()==null? b: a);        
        Monoid<Higher<LazyKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<LazyKind<Integer>> m = Monoid.of(LazyKind.widen(Lazy.just()), (a,b)->a.isEmpty() ? b : a);
        LazyKind<Integer> lazy = Lazys.<Integer>monadPlus(m)
                                      .plus(LazyKind.widen(Lazy.just(5)), LazyKind.widen(Lazy.just(10)))
                                      .convert(LazyKind::narrowK);
        //Lazy[5]
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Lazys
     * @return Type class for combining Lazys
     */
    public static <T> MonadPlus<LazyKind.µ> monadPlus(Monoid<LazyKind<T>> m){
        Monoid<Higher<LazyKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<LazyKind.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), LazyInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lazys.foldable()
                           .foldLeft(0, (a,b)->a+b, LazyKind.widen(Lazy.just(1)));
        
        //1
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<LazyKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<LazyKind.µ,T>,T> foldRightFn =  (m, l)-> LazyKind.narrow(l).getOrElse(m.zero());
        BiFunction<Monoid<T>,Higher<LazyKind.µ,T>,T> foldLeftFn = (m, l)-> LazyKind.narrow(l).getOrElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<LazyKind.µ> comonad(){
        Function<? super Higher<LazyKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(LazyKind::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> LazyKind<T> of(T value){
        return LazyKind.widen(Lazy.of(()->value));
    }
    private static <T,R> LazyKind<R> ap(LazyKind<Function< T, R>> lt, LazyKind<T> lazy){
        return LazyKind.widen(FromCyclopsReact.lazy(Vavr.eval(lt.narrow()).combine(Vavr.eval(lazy.narrow()), (a, b)->a.apply(b))));
        
    }
    private static <T,R> Higher<LazyKind.µ,R> flatMap(Higher<LazyKind.µ,T> lt, Function<? super T, ? extends  Higher<LazyKind.µ,R>> fn){
        return LazyKind.widen(LazyKind.narrowEval(lt).flatMap(fn.andThen(LazyKind::narrowEval)));
    }
    private static <T,R> LazyKind<R> map(LazyKind<T> lt, Function<? super T, ? extends R> fn){
        return LazyKind.widen(LazyKind.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<LazyKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                        Higher<LazyKind.µ, T> ds){
       
        Lazy<T> eval = LazyKind.narrow(ds);
        Higher<C2, R> ds2 = fn.apply(eval.get());
        return applicative.map(v-> LazyKind.of(()->v), ds2);

    }
   
}
