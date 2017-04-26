package com.aol.cyclops.reactor.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;


import com.aol.cyclops.reactor.Monos;
import com.aol.cyclops.reactor.hkt.MonoKind;

import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

/**
 * Companion class for creating Type Class instances for working with Monos
 * @author johnmcclean
 *
 */
@UtilityClass
public class MonoInstances {

    
    /**
     * 
     * Transform a Mono, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  MonoKind<Integer> future = Monos.functor().map(i->i*2, MonoKind.widen(Mono.just(3));
     *  
     *  //[6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Monos
     * <pre>
     * {@code 
     *   MonoKind<Integer> ft = Monos.unit()
                                       .unit("hello")
                                       .then(h->Monos.functor().map((String v) ->v.length(), h))
                                       .convert(MonoKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Monos
     */
    public static <T,R>Functor<MonoKind.µ> functor(){
        BiFunction<MonoKind<T>,Function<? super T, ? extends R>,MonoKind<R>> map = MonoInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * MonoKind<String> ft = Monos.unit()
                                     .unit("hello")
                                     .convert(MonoKind::narrowK);
        
        //Mono["hello"]
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Monos
     */
    public static <T> Pure<MonoKind.µ> unit(){
        return General.<MonoKind.µ,T>unit(MonoInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MonoKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asMono;
     * 
       Monos.applicative()
            .ap(widen(asMono(l1(this::multiplyByTwo))),widen(Mono.just(3)));
     * 
     * //[6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * MonoKind<Function<Integer,Integer>> ftFn =Monos.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(MonoKind::narrowK);
        
        MonoKind<Integer> ft = Monos.unit()
                                      .unit("hello")
                                      .then(h->Monos.functor().map((String v) ->v.length(), h))
                                      .then(h->Monos.applicative().ap(ftFn, h))
                                      .convert(MonoKind::narrowK);
        
        //Mono.just("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Monos
     */
    public static <T,R> Applicative<MonoKind.µ> applicative(){
        BiFunction<MonoKind< Function<T, R>>,MonoKind<T>,MonoKind<R>> ap = MonoInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MonoKind.widen;
     * MonoKind<Integer> ft  = Monos.monad()
                                      .flatMap(i->widen(Mono.just(i)), widen(Mono.just(3)))
                                      .convert(MonoKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    MonoKind<Integer> ft = Monos.unit()
                                        .unit("hello")
                                        .then(h->Monos.monad().flatMap((String v) ->Monos.unit().unit(v.length()), h))
                                        .convert(MonoKind::narrowK);
        
        //Mono.just("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Monos
     */
    public static <T,R> Monad<MonoKind.µ> monad(){
  
        BiFunction<Higher<MonoKind.µ,T>,Function<? super T, ? extends Higher<MonoKind.µ,R>>,Higher<MonoKind.µ,R>> flatMap = MonoInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  MonoKind<String> ft = Monos.unit()
                                     .unit("hello")
                                     .then(h->Monos.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(MonoKind::narrowK);
        
       //Mono.just("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<MonoKind.µ> monadZero(){
        
        return General.monadZero(monad(), MonoKind.empty());
    }
    /**
     * Combines Monos by selecting the first result returned
     * 
     * <pre>
     * {@code 
     *  MonoKind<Integer> ft = Monos.<Integer>monadPlus()
                                      .plus(MonoKind.widen(Mono.empty()), MonoKind.widen(Mono.just(10)))
                                      .convert(MonoKind::narrowK);
        //Mono.empty()
     * 
     * }
     * </pre>
     * @return Type class for combining Monos by concatenation
     */
    public static <T> MonadPlus<MonoKind.µ> monadPlus(){
 
        
        Monoid<MonoKind<T>> m = Monoid.of(MonoKind.<T>widen(Mono.empty()),
                                              (f,g)-> MonoKind.widen(Mono.first(f.narrow(),g.narrow())));
                
        Monoid<Higher<MonoKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<MonoKind<Integer>> m = Monoid.of(MonoKind.widen(Arrays.asMono()), (a,b)->a.isEmpty() ? b : a);
        MonoKind<Integer> ft = Monos.<Integer>monadPlus(m)
                                      .plus(MonoKind.widen(Arrays.asMono(5)), MonoKind.widen(Arrays.asMono(10)))
                                      .convert(MonoKind::narrowK);
        //Arrays.asMono(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Monos
     * @return Type class for combining Monos
     */
    public static <T> MonadPlus<MonoKind.µ> monadPlus(Monoid<MonoKind<T>> m){
        Monoid<Higher<MonoKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<MonoKind.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), MonoInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Monos.foldable()
                        .foldLeft(0, (a,b)->a+b, MonoKind.widen(Arrays.asMono(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<MonoKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<MonoKind.µ,T>,T> foldRightFn =  (m, l)-> m.apply(m.zero(), MonoKind.narrow(l).block());
        BiFunction<Monoid<T>,Higher<MonoKind.µ,T>,T> foldLeftFn = (m, l)->  m.apply(m.zero(), MonoKind.narrow(l).block());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<MonoKind.µ> comonad(){
        Function<? super Higher<MonoKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(MonoKind::narrow).block();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> MonoKind<T> of(T value){
        return MonoKind.widen(Mono.just(value));
    }
    private static <T,R> MonoKind<R> ap(MonoKind<Function< T, R>> lt, MonoKind<T> list){
     
        
        return MonoKind.widen(Monos.combine(lt.narrow(),list.narrow(), (a, b)->a.apply(b)));
        
    }
    private static <T,R> Higher<MonoKind.µ,R> flatMap(Higher<MonoKind.µ,T> lt, Function<? super T, ? extends  Higher<MonoKind.µ,R>> fn){
        return MonoKind.widen(MonoKind.narrow(lt).flatMap(fn.andThen(MonoKind::narrow)));
    }
    private static <T,R> MonoKind<R> map(MonoKind<T> lt, Function<? super T, ? extends R> fn){
        return MonoKind.widen(lt.narrow().map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<MonoKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                        Higher<MonoKind.µ, T> ds){
        Mono<T> future = MonoKind.narrow(ds);
        return applicative.map(MonoKind::just, fn.apply(future.block()));
    }
   
}
