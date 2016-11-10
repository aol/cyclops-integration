package com.aol.cyclops.reactor.hkt.typeclasses.instances;

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
import com.aol.cyclops.reactor.Monos;
import com.aol.cyclops.reactor.hkt.MonoType;

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
     *  MonoType<Integer> future = Monos.functor().map(i->i*2, MonoType.widen(Arrays.asMono(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Monos
     * <pre>
     * {@code 
     *   MonoType<Integer> ft = Monos.unit()
                                       .unit("hello")
                                       .then(h->Monos.functor().map((String v) ->v.length(), h))
                                       .convert(MonoType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Monos
     */
    public static <T,R>Functor<MonoType.µ> functor(){
        BiFunction<MonoType<T>,Function<? super T, ? extends R>,MonoType<R>> map = MonoInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * MonoType<String> ft = Monos.unit()
                                     .unit("hello")
                                     .convert(MonoType::narrowK);
        
        //Arrays.asMono("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Monos
     */
    public static Unit<MonoType.µ> unit(){
        return General.unit(MonoInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MonoType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asMono;
     * 
       Monos.zippingApplicative()
            .ap(widen(asMono(l1(this::multiplyByTwo))),widen(asMono(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * MonoType<Function<Integer,Integer>> ftFn =Monos.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(MonoType::narrowK);
        
        MonoType<Integer> ft = Monos.unit()
                                      .unit("hello")
                                      .then(h->Monos.functor().map((String v) ->v.length(), h))
                                      .then(h->Monos.applicative().ap(ftFn, h))
                                      .convert(MonoType::narrowK);
        
        //Arrays.asMono("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Monos
     */
    public static <T,R> Applicative<MonoType.µ> applicative(){
        BiFunction<MonoType< Function<T, R>>,MonoType<T>,MonoType<R>> ap = MonoInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.MonoType.widen;
     * MonoType<Integer> ft  = Monos.monad()
                                      .flatMap(i->widen(MonoX.range(0,i)), widen(Arrays.asMono(1,2,3)))
                                      .convert(MonoType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    MonoType<Integer> ft = Monos.unit()
                                        .unit("hello")
                                        .then(h->Monos.monad().flatMap((String v) ->Monos.unit().unit(v.length()), h))
                                        .convert(MonoType::narrowK);
        
        //Arrays.asMono("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Monos
     */
    public static <T,R> Monad<MonoType.µ> monad(){
  
        BiFunction<Higher<MonoType.µ,T>,Function<? super T, ? extends Higher<MonoType.µ,R>>,Higher<MonoType.µ,R>> flatMap = MonoInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  MonoType<String> ft = Monos.unit()
                                     .unit("hello")
                                     .then(h->Monos.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(MonoType::narrowK);
        
       //Arrays.asMono("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<MonoType.µ> monadZero(){
        
        return General.monadZero(monad(), MonoType.empty());
    }
    /**
     * <pre>
     * {@code 
     *  MonoType<Integer> ft = Monos.<Integer>monadPlus()
                                      .plus(MonoType.widen(Arrays.asMono()), MonoType.widen(Arrays.asMono(10)))
                                      .convert(MonoType::narrowK);
        //Arrays.asMono(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Monos by concatenation
     */
    public static <T> MonadPlus<MonoType.µ,T> monadPlus(){
        Monoid<FutureW<T>> mn = Monoids.firstSuccessfulFuture();
        Monoid<MonoType<T>> m = Monoid.of(MonoType.widen(mn.zero()), (f,g)-> MonoType.widen(
                                                                             mn.apply(f.toFuture(), g.toFuture())));
                
        Monoid<Higher<MonoType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<MonoType<Integer>> m = Monoid.of(MonoType.widen(Arrays.asMono()), (a,b)->a.isEmpty() ? b : a);
        MonoType<Integer> ft = Monos.<Integer>monadPlus(m)
                                      .plus(MonoType.widen(Arrays.asMono(5)), MonoType.widen(Arrays.asMono(10)))
                                      .convert(MonoType::narrowK);
        //Arrays.asMono(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Monos
     * @return Type class for combining Monos
     */
    public static <T> MonadPlus<MonoType.µ,T> monadPlus(Monoid<MonoType<T>> m){
        Monoid<Higher<MonoType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<MonoType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), MonoInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Monos.foldable()
                        .foldLeft(0, (a,b)->a+b, MonoType.widen(Arrays.asMono(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<MonoType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<MonoType.µ,T>,T> foldRightFn =  (m,l)-> m.apply(m.zero(), MonoType.narrow(l).block());
        BiFunction<Monoid<T>,Higher<MonoType.µ,T>,T> foldLeftFn = (m,l)->  m.apply(m.zero(), MonoType.narrow(l).block());
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> MonoType<T> of(T value){
        return MonoType.widen(Mono.just(value));
    }
    private static <T,R> MonoType<R> ap(MonoType<Function< T, R>> lt,  MonoType<T> list){
     
        
        return MonoType.widen(Monos.combine(lt.narrow(),list.narrow(), (a,b)->a.apply(b)));
        
    }
    private static <T,R> Higher<MonoType.µ,R> flatMap( Higher<MonoType.µ,T> lt, Function<? super T, ? extends  Higher<MonoType.µ,R>> fn){
        return MonoType.widen(MonoType.narrow(lt).flatMap(fn.andThen(MonoType::narrow)));
    }
    private static <T,R> MonoType<R> map(MonoType<T> lt, Function<? super T, ? extends R> fn){
        return MonoType.widen(lt.narrow().map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<MonoType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<MonoType.µ, T> ds){
        Mono<T> future = MonoType.narrow(ds);
        return applicative.map(MonoType::just, fn.apply(future.block()));
    }
   
}
