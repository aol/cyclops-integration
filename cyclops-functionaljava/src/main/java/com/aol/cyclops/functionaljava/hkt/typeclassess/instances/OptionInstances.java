package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.Function;


import com.aol.cyclops.functionaljava.FJ;
import com.aol.cyclops.functionaljava.hkt.OptionKind;


import com.aol.cyclops2.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import fj.data.Option;
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
     *  OptionKind<Integer> option = Options.functor()
     *                                      .map(i->i*2, OptionKind.widen(Option.some(1));
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
        
        //Option.some("hello"))
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
     * 
     * 
       Options.applicative()
              .ap(widen(Option.some(l1(this::multiplyByTwo))),widen(Option.some(1)));
     * 
     * //[2]
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
        
        //Arrays.asOption("hello".length()*2))
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
                                      .flatMap(i->widen(OptionX.range(0,i)), widen(Option.some(1,2,3)))
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
        
        //Arrays.asOption("hello".length())
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
        
       //Arrays.asOption("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<OptionKind.µ> monadZero(){
        
        return General.monadZero(monad(), OptionKind.empty());
    }
    /**
     * <pre>
     * {@code 
     *  OptionKind<Integer> option = Options.<Integer>monadPlus()
                                      .plus(OptionKind.widen(Arrays.asOption()), OptionKind.widen(Arrays.asOption(10)))
                                      .convert(OptionKind::narrowK);
        //Arrays.asOption(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Options by concatenation
     */
    public static <T> MonadPlus<OptionKind.µ> monadPlus(){
        Monoid<Option<T>> mn = Monoid.of(Option.none(), (a, b) -> a.isSome() ? a : b);
        Monoid<OptionKind<T>> m = Monoid.of(OptionKind.widen(mn.zero()), (f, g)-> OptionKind.widen(
                                                                                mn.apply(OptionKind.narrow(f), OptionKind.narrow(g))));
                
        Monoid<Higher<OptionKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<OptionKind<Integer>> m = Monoid.of(OptionKind.widen(Arrays.asOption()), (a,b)->a.isEmpty() ? b : a);
        OptionKind<Integer> option = Options.<Integer>monadPlus(m)
                                      .plus(OptionKind.widen(Arrays.asOption(5)), OptionKind.widen(Arrays.asOption(10)))
                                      .convert(OptionKind::narrowK);
        //Arrays.asOption(5))
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
                           .foldLeft(0, (a,b)->a+b, OptionKind.widen(Option.some(2)));
        
        //2
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<OptionKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<OptionKind.µ,T>,T> foldRightFn =  (m, l)-> OptionKind.narrow(l).orSome(m.zero());
        BiFunction<Monoid<T>,Higher<OptionKind.µ,T>,T> foldLeftFn = (m, l)-> OptionKind.narrow(l).orSome(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
    public static <T> Comonad<OptionKind.µ> comonad(){
        Function<? super Higher<OptionKind.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(OptionKind::narrow).some();
        return General.comonad(functor(), unit(), extractFn);
    }
    
    private <T> OptionKind<T> of(T value){
        return OptionKind.widen(Option.some(value));
    }
    private static <T,R> OptionKind<R> ap(OptionKind<Function< T, R>> lt, OptionKind<T> option){

        Maybe<R> mb = FJ.maybe(lt.narrow()).combine(FJ.maybe(option.narrow()),
                                                    (a,b)->a.apply(b));
        return OptionKind.widen(mb);
        
    }
    private static <T,R> Higher<OptionKind.µ,R> flatMap(Higher<OptionKind.µ,T> lt, Function<? super T, ? extends  Higher<OptionKind.µ,R>> fn){
        return OptionKind.widen(OptionKind.narrow(lt).bind(in->fn.andThen(OptionKind::narrow).apply(in)));
    }
    private static <T,R> OptionKind<R> map(OptionKind<T> lt, Function<? super T, ? extends R> fn){
        
        return OptionKind.widen(OptionKind.narrow(lt).map(t->fn.apply(t)));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<OptionKind.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn,
                                                                          Higher<OptionKind.µ, T> ds){
        Option<T> opt = OptionKind.narrow(ds);
        return opt.isSome()?   applicative.map(OptionKind::of, fn.apply(opt.some())) :
                                    applicative.unit(OptionKind.empty());
    }
   
}
