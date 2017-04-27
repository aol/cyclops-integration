package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.OptionKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.Monoids;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.vavr.FromCyclopsReact;
import com.aol.cyclops.vavr.Vavr;

import javaslang.control.Option;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Options
 * @author johnmcclean
 *
 */
@UtilityClass
public class OptionInstances {

    
    /**
     * 
     * Transform a option, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  OptionKind<Integer> option = Options.functor().map(i->i*2, OptionKind.widen(Option.just(1));
     *  
     *  //[2]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Options
     * <pre>
     * {@code 
     *   OptionKind<Integer> option = Options.unit()
                                       .unit("hello")
                                       .then(h->Options.functor().map((String v) ->v.length(), h))
                                       .convert(OptionKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Options
     */
    public static <T,R>Functor<OptionKind.µ> functor(){
        BiFunction<OptionKind<T>,Function<? super T, ? extends R>,OptionKind<R>> map = OptionInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * OptionKind<String> option = Options.unit()
                                     .unit("hello")
                                     .convert(OptionKind::narrowK);
        
        //Option.just("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Options
     */
    public static <T> Pure<OptionKind.µ> unit(){
        return General.<OptionKind.µ,T>unit(OptionInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Option.just;
     * 
       Options.zippingApplicative()
            .ap(widen(asOption(l1(this::multiplyByTwo))),widen(asOption(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * OptionKind<Function<Integer,Integer>> optionFn =Options.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(OptionKind::narrowK);
        
        OptionKind<Integer> option = Options.unit()
                                      .unit("hello")
                                      .then(h->Options.functor().map((String v) ->v.length(), h))
                                      .then(h->Options.applicative().ap(optionFn, h))
                                      .convert(OptionKind::narrowK);
        
        //Option.just("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Options
     */
    public static <T,R> Applicative<OptionKind.µ> applicative(){
        BiFunction<OptionKind< Function<T, R>>,OptionKind<T>,OptionKind<R>> ap = OptionInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionKind.widen;
     * OptionKind<Integer> option  = Options.monad()
                                      .flatMap(i->widen(OptionX.range(0,i)), widen(Option.just(1,2,3)))
                                      .convert(OptionKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    OptionKind<Integer> option = Options.unit()
                                        .unit("hello")
                                        .then(h->Options.monad().flatMap((String v) ->Options.unit().unit(v.length()), h))
                                        .convert(OptionKind::narrowK);
        
        //Option.just("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Options
     */
    public static <T,R> Monad<OptionKind.µ> monad(){
  
        BiFunction<Higher<OptionKind.µ,T>,Function<? super T, ? extends Higher<OptionKind.µ,R>>,Higher<OptionKind.µ,R>> flatMap = OptionInstances::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  OptionKind<String> option = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionKind::narrowK);
        
       //Option.just("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<OptionKind.µ> monadZero(){
        
        return General.monadZero(monad(), OptionKind.none());
    }
    /**
     * <pre>
     * {@code 
     *  OptionKind<Integer> option = Options.<Integer>monadPlus()
                                      .plus(OptionKind.widen(Option.just()), OptionKind.widen(Option.just(10)))
                                      .convert(OptionKind::narrowK);
        //Option.just(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Options by concatenation
     */
    public static <T> MonadPlus<OptionKind.µ> monadPlus(){
        Monoid<OptionKind<T>> m = Monoid.of( OptionKind.ofOptional(Monoids.<T>firstPresentOptional().zero()),
                                            (a,b)-> OptionKind.ofOptional(Monoids.<T>firstPresentOptional().apply(a.toJavaOptional(),b.toJavaOptional())));
        Monoid<Higher<OptionKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<OptionKind<Integer>> m = Monoid.of(OptionKind.widen(Option.just()), (a,b)->a.isEmpty() ? b : a);
        OptionKind<Integer> option = Options.<Integer>monadPlus(m)
                                      .plus(OptionKind.widen(Option.just(5)), OptionKind.widen(Option.just(10)))
                                      .convert(OptionKind::narrowK);
        //Option[5]
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Options
     * @return Type class for combining Options
     */
    public static <T> MonadPlus<OptionKind.µ> monadPlus(Monoid<OptionKind<T>> m){
        Monoid<Higher<OptionKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<OptionKind.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), OptionInstances::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Options.foldable()
                           .foldLeft(0, (a,b)->a+b, OptionKind.widen(Option.just(1)));
        
        //1
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<OptionKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<OptionKind.µ,T>,T> foldRightFn =  (m, l)-> OptionKind.narrow(l).getOrElse(m.zero());
        BiFunction<Monoid<T>,Higher<OptionKind.µ,T>,T> foldLeftFn = (m, l)-> OptionKind.narrow(l).getOrElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<OptionKind.µ> comonad(){
        Function<? super Higher<OptionKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(OptionKind::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> OptionKind<T> of(T value){
        return OptionKind.widen(Option.of(value));
    }
    private static <T,R> OptionKind<R> ap(OptionKind<Function< T, R>> lt, OptionKind<T> option){
        return OptionKind.widen(FromCyclopsReact.option(Vavr.maybe(lt).combine(Vavr.maybe(option), (a, b)->a.apply(b))));
        
    }
    private static <T,R> Higher<OptionKind.µ,R> flatMap(Higher<OptionKind.µ,T> lt, Function<? super T, ? extends  Higher<OptionKind.µ,R>> fn){
        return OptionKind.widen(OptionKind.narrow(lt).flatMap(fn.andThen(OptionKind::narrow)));
    }
    private static <T,R> OptionKind<R> map(OptionKind<T> lt, Function<? super T, ? extends R> fn){
        return OptionKind.widen(OptionKind.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<OptionKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                          Higher<OptionKind.µ, T> ds){
       
        Option<T> option = OptionKind.narrow(ds);
         Higher<C2, OptionKind<R>> res = Vavr.maybe(option).visit(some-> applicative.map(m-> OptionKind.of(m), fn.apply(some)),
                                                    ()->applicative.unit(OptionKind.widen(OptionKind.<R>none())));
        
        return OptionKind.widen2(res);
    }
   
}
