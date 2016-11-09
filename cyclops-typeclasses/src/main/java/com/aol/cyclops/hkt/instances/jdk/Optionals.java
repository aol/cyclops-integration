package com.aol.cyclops.hkt.instances.jdk;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Monoids;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.OptionalType;
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
 * Companion class for creating Type Class instances for working with Optionals
 * @author johnmcclean
 *
 */
@UtilityClass
public class Optionals {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  OptionalType<Integer> list = Optionals.functor().map(i->i*2, OptionalType.widen(Arrays.asOptional(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Optionals
     * <pre>
     * {@code 
     *   OptionalType<Integer> list = Optionals.unit()
                                       .unit("hello")
                                       .then(h->Optionals.functor().map((String v) ->v.length(), h))
                                       .convert(OptionalType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Optionals
     */
    public static <T,R>Functor<OptionalType.µ> functor(){
        BiFunction<OptionalType<T>,Function<? super T, ? extends R>,OptionalType<R>> map = Optionals::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * OptionalType<String> list = Optionals.unit()
                                     .unit("hello")
                                     .convert(OptionalType::narrowK);
        
        //Arrays.asOptional("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Optionals
     */
    public static Unit<OptionalType.µ> unit(){
        return General.unit(Optionals::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionalType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asOptional;
     * 
       Optionals.zippingApplicative()
            .ap(widen(asOptional(l1(this::multiplyByTwo))),widen(asOptional(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * OptionalType<Function<Integer,Integer>> listFn =Optionals.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(OptionalType::narrowK);
        
        OptionalType<Integer> list = Optionals.unit()
                                      .unit("hello")
                                      .then(h->Optionals.functor().map((String v) ->v.length(), h))
                                      .then(h->Optionals.applicative().ap(listFn, h))
                                      .convert(OptionalType::narrowK);
        
        //Arrays.asOptional("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Optionals
     */
    public static <T,R> Applicative<OptionalType.µ> applicative(){
        BiFunction<OptionalType< Function<T, R>>,OptionalType<T>,OptionalType<R>> ap = Optionals::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionalType.widen;
     * OptionalType<Integer> list  = Optionals.monad()
                                      .flatMap(i->widen(OptionalX.range(0,i)), widen(Arrays.asOptional(1,2,3)))
                                      .convert(OptionalType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    OptionalType<Integer> list = Optionals.unit()
                                        .unit("hello")
                                        .then(h->Optionals.monad().flatMap((String v) ->Optionals.unit().unit(v.length()), h))
                                        .convert(OptionalType::narrowK);
        
        //Arrays.asOptional("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Optionals
     */
    public static <T,R> Monad<OptionalType.µ> monad(){
  
        BiFunction<Higher<OptionalType.µ,T>,Function<? super T, ? extends Higher<OptionalType.µ,R>>,Higher<OptionalType.µ,R>> flatMap = Optionals::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  OptionalType<String> list = Optionals.unit()
                                     .unit("hello")
                                     .then(h->Optionals.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionalType::narrowK);
        
       //Arrays.asOptional("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<OptionalType.µ> monadZero(){
        
        return General.monadZero(monad(), OptionalType.empty());
    }
    /**
     * <pre>
     * {@code 
     *  OptionalType<Integer> list = Optionals.<Integer>monadPlus()
                                      .plus(OptionalType.widen(Arrays.asOptional()), OptionalType.widen(Arrays.asOptional(10)))
                                      .convert(OptionalType::narrowK);
        //Arrays.asOptional(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Optionals by concatenation
     */
    public static <T> MonadPlus<OptionalType.µ,T> monadPlus(){
        Monoid<Optional<T>> mn = Monoids.firstPresentOptional();
        Monoid<OptionalType<T>> m = Monoid.of(OptionalType.widen(mn.zero()), (f,g)-> OptionalType.widen(
                                                                                mn.apply(OptionalType.narrow(f), OptionalType.narrow(g))));
                
        Monoid<Higher<OptionalType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<OptionalType<Integer>> m = Monoid.of(OptionalType.widen(Arrays.asOptional()), (a,b)->a.isEmpty() ? b : a);
        OptionalType<Integer> list = Optionals.<Integer>monadPlus(m)
                                      .plus(OptionalType.widen(Arrays.asOptional(5)), OptionalType.widen(Arrays.asOptional(10)))
                                      .convert(OptionalType::narrowK);
        //Arrays.asOptional(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Optionals
     * @return Type class for combining Optionals
     */
    public static <T> MonadPlus<OptionalType.µ,T> monadPlus(Monoid<OptionalType<T>> m){
        Monoid<Higher<OptionalType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<OptionalType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), Optionals::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Optionals.foldable()
                           .foldLeft(0, (a,b)->a+b, OptionalType.widen(Arrays.asOptional(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<OptionalType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<OptionalType.µ,T>,T> foldRightFn =  (m,l)-> OptionalType.narrow(l).orElse(m.zero());
        BiFunction<Monoid<T>,Higher<OptionalType.µ,T>,T> foldLeftFn = (m,l)-> OptionalType.narrow(l).orElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> OptionalType<T> of(T value){
        return OptionalType.widen(Optional.of(value));
    }
    private static <T,R> OptionalType<R> ap(OptionalType<Function< T, R>> lt,  OptionalType<T> list){
        return OptionalType.widen(MaybeType.fromOptional(lt).combine(MaybeType.fromOptional(list), (a,b)->a.apply(b)).toOptional());
        
    }
    private static <T,R> Higher<OptionalType.µ,R> flatMap( Higher<OptionalType.µ,T> lt, Function<? super T, ? extends  Higher<OptionalType.µ,R>> fn){
        return OptionalType.widen(OptionalType.narrow(lt).flatMap(fn.andThen(OptionalType::narrow)));
    }
    private static <T,R> OptionalType<R> map(OptionalType<T> lt, Function<? super T, ? extends R> fn){
        return OptionalType.widen(OptionalType.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<OptionalType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<OptionalType.µ, T> ds){
        Optional<T> opt = OptionalType.narrow(ds);
        return opt.isPresent() ?   applicative.map(OptionalType::of, fn.apply(opt.get())) : 
                                    applicative.unit(OptionalType.empty());
    }
   
}
