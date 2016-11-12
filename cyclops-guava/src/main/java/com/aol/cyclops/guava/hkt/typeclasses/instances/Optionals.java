package com.aol.cyclops.guava.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.guava.Guava;
import com.aol.cyclops.guava.hkt.OptionalType;
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
import com.google.common.base.Optional;

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
     * Transform a option, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  OptionalType<Integer> option = Optionals.functor()
     *                                      .map(i->i*2, OptionalType.widen(Optional.some(1));
     *  
     *  //[2]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Optionals
     * <pre>
     * {@code 
     *   OptionalType<Integer> option = Optionals.unit()
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
     * OptionalType<String> option = Optionals.unit()
                                          .unit("hello")
                                          .convert(OptionalType::narrowK);
        
        //Optional.some("hello"))
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
     * 
     * 
       Optionals.applicative()
              .ap(widen(Optional.some(l1(this::multiplyByTwo))),widen(Optional.some(1)));
     * 
     * //[2]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * OptionalType<Function<Integer,Integer>> optionFn =Optionals.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(OptionalType::narrowK);
        
        OptionalType<Integer> option = Optionals.unit()
                                      .unit("hello")
                                      .then(h->Optionals.functor().map((String v) ->v.length(), h))
                                      .then(h->Optionals.applicative().ap(optionFn, h))
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
     * OptionalType<Integer> option  = Optionals.monad()
                                      .flatMap(i->widen(OptionalX.range(0,i)), widen(Optional.some(1,2,3)))
                                      .convert(OptionalType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    OptionalType<Integer> option = Optionals.unit()
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
     *  OptionalType<String> option = Optionals.unit()
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
        
        return General.monadZero(monad(), OptionalType.absent());
    }
    /**
     * <pre>
     * {@code 
     *  OptionalType<Integer> option = Optionals.<Integer>monadPlus()
                                      .plus(OptionalType.widen(Arrays.asOptional()), OptionalType.widen(Arrays.asOptional(10)))
                                      .convert(OptionalType::narrowK);
        //Arrays.asOptional(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Optionals by concatenation
     */
    public static <T> MonadPlus<OptionalType.µ,T> monadPlus(){
        Monoid<Optional<T>> mn = Monoid.of(Optional.absent(), (a, b) -> a.isPresent() ? a : b);
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
        OptionalType<Integer> option = Optionals.<Integer>monadPlus(m)
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
                           .foldLeft(0, (a,b)->a+b, OptionalType.widen(Optional.some(2)));
        
        //2
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<OptionalType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<OptionalType.µ,T>,T> foldRightFn =  (m,l)-> OptionalType.narrow(l).or(m.zero());
        BiFunction<Monoid<T>,Higher<OptionalType.µ,T>,T> foldLeftFn = (m,l)-> OptionalType.narrow(l).or(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<OptionalType.µ> comonad(){
        Function<? super Higher<OptionalType.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(OptionalType::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> OptionalType<T> of(T value){
        return OptionalType.widen(Optional.of(value));
    }
    private static <T,R> OptionalType<R> ap(OptionalType<Function< T, R>> lt,  OptionalType<T> option){
        Maybe<R> mb = MaybeType.widen(Guava.asMaybe(lt.narrow())).combine(MaybeType.widen(Guava.asMaybe(option.narrow())), 
                                                    (a,b)->a.apply(b));
        return OptionalType.widen(mb);
        
    }
    private static <T,R> Higher<OptionalType.µ,R> flatMap( Higher<OptionalType.µ,T> lt, Function<? super T, ? extends  Higher<OptionalType.µ,R>> fn){
        return OptionalType.widen(OptionalType.narrowOptional(lt).flatMap(in->fn.andThen(OptionalType::narrowOptional).apply(in)));
    }
    private static <T,R> OptionalType<R> map(OptionalType<T> lt, Function<? super T, ? extends R> fn){
        
        return OptionalType.widen(OptionalType.narrow(lt).transform(t->fn.apply(t)));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<OptionalType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<OptionalType.µ, T> ds){
        Optional<T> opt = OptionalType.narrow(ds);
        return opt.isPresent()?   applicative.map(OptionalType::just, fn.apply(opt.get())) : 
                                    applicative.unit(OptionalType.absent());
    }
   
}
