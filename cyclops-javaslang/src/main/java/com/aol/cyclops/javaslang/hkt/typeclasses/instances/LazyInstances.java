package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.javaslang.FromCyclopsReact;
import com.aol.cyclops.javaslang.Javaslang;
import com.aol.cyclops.javaslang.hkt.LazyType;

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
     *  LazyType<Integer> lazy = LazyInstances.functor().map(i->i*2, LazyType.widen(Lazy.of(()->1));
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
     *   LazyType<Integer> lazy = Lazys.unit()
                                       .unit("hello")
                                       .then(h->Lazys.functor().map((String v) ->v.length(), h))
                                       .convert(LazyType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lazys
     */
    public static <T,R>Functor<LazyType.µ> functor(){
        BiFunction<LazyType<T>,Function<? super T, ? extends R>,LazyType<R>> map = LazyInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * LazyType<String> lazy = Lazys.unit()
                                     .unit("hello")
                                     .convert(LazyType::narrowK);
        
        //Lazy.just("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lazys
     */
    public static <T> Pure<LazyType.µ> unit(){
        return General.<LazyType.µ,T>unit(LazyInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.LazyType.widen;
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
     * LazyType<Function<Integer,Integer>> lazyFn =Lazys.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(LazyType::narrowK);
        
        LazyType<Integer> lazy = Lazys.unit()
                                      .unit("hello")
                                      .then(h->Lazys.functor().map((String v) ->v.length(), h))
                                      .then(h->Lazys.applicative().ap(lazyFn, h))
                                      .convert(LazyType::narrowK);
        
        //Lazy.just("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Lazys
     */
    public static <T,R> Applicative<LazyType.µ> applicative(){
        BiFunction<LazyType< Function<T, R>>,LazyType<T>,LazyType<R>> ap = LazyInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.LazyType.widen;
     * LazyType<Integer> lazy  = Lazys.monad()
                                      .flatMap(i->widen(LazyX.range(0,i)), widen(Lazy.just(1,2,3)))
                                      .convert(LazyType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    LazyType<Integer> lazy = Lazys.unit()
                                        .unit("hello")
                                        .then(h->Lazys.monad().flatMap((String v) ->Lazys.unit().unit(v.length()), h))
                                        .convert(LazyType::narrowK);
        
        //Lazy.just("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Lazys
     */
    public static <T,R> Monad<LazyType.µ> monad(){
  
        BiFunction<Higher<LazyType.µ,T>,Function<? super T, ? extends Higher<LazyType.µ,R>>,Higher<LazyType.µ,R>> flatMap = LazyInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  LazyType<String> lazy = Lazys.unit()
                                     .unit("hello")
                                     .then(h->Lazys.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(LazyType::narrowK);
        
       //Lazy.just("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<LazyType.µ> monadZero(){
        
        return General.monadZero(monad(), LazyType.of(()->null));
    }
    /**
     * <pre>
     * {@code 
     *  LazyType<Integer> lazy = Lazys.<Integer>monadPlus()
                                      .plus(LazyType.widen(Lazy.just()), LazyType.widen(Lazy.just(10)))
                                      .convert(LazyType::narrowK);
        //Lazy.just(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Lazys by concatenation
     */
    public static <T> MonadPlus<LazyType.µ> monadPlus(){
        Monoid<LazyType<T>> m = Monoid.of( LazyType.of(()->null),
                                            (a,b)-> a.get()==null? b: a);        
        Monoid<Higher<LazyType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<LazyType<Integer>> m = Monoid.of(LazyType.widen(Lazy.just()), (a,b)->a.isEmpty() ? b : a);
        LazyType<Integer> lazy = Lazys.<Integer>monadPlus(m)
                                      .plus(LazyType.widen(Lazy.just(5)), LazyType.widen(Lazy.just(10)))
                                      .convert(LazyType::narrowK);
        //Lazy[5]
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Lazys
     * @return Type class for combining Lazys
     */
    public static <T> MonadPlus<LazyType.µ> monadPlus(Monoid<LazyType<T>> m){
        Monoid<Higher<LazyType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<LazyType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), LazyInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lazys.foldable()
                           .foldLeft(0, (a,b)->a+b, LazyType.widen(Lazy.just(1)));
        
        //1
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<LazyType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<LazyType.µ,T>,T> foldRightFn =  (m,l)-> LazyType.narrow(l).getOrElse(m.zero());
        BiFunction<Monoid<T>,Higher<LazyType.µ,T>,T> foldLeftFn = (m,l)-> LazyType.narrow(l).getOrElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<LazyType.µ> comonad(){
        Function<? super Higher<LazyType.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(LazyType::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> LazyType<T> of(T value){
        return LazyType.widen(Lazy.of(()->value));
    }
    private static <T,R> LazyType<R> ap(LazyType<Function< T, R>> lt,  LazyType<T> lazy){
        return LazyType.widen(FromCyclopsReact.lazy(Javaslang.eval(lt.narrow()).combine(Javaslang.eval(lazy.narrow()), (a,b)->a.apply(b))));
        
    }
    private static <T,R> Higher<LazyType.µ,R> flatMap( Higher<LazyType.µ,T> lt, Function<? super T, ? extends  Higher<LazyType.µ,R>> fn){
        return LazyType.widen(LazyType.narrowEval(lt).flatMap(fn.andThen(LazyType::narrowEval)));
    }
    private static <T,R> LazyType<R> map(LazyType<T> lt, Function<? super T, ? extends R> fn){
        return LazyType.widen(LazyType.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<LazyType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<LazyType.µ, T> ds){
       
        Lazy<T> eval = LazyType.narrow(ds);
        Higher<C2, R> ds2 = fn.apply(eval.get());
        return applicative.map(v->LazyType.of(()->v), ds2);

    }
   
}
