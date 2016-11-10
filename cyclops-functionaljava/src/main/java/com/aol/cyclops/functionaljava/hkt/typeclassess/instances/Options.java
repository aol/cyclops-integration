package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.functionaljava.FJ;
import com.aol.cyclops.functionaljava.hkt.OptionType;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;

import fj.data.Option;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Options
 * @author johnmcclean
 *
 */
@UtilityClass
public class Options {

    
    /**
     * 
     * Transform a option, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  OptionType<Integer> option = Options.functor()
     *                                      .map(i->i*2, OptionType.widen(Option.some(1));
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
     *   OptionType<Integer> option = Options.unit()
                                       .unit("hello")
                                       .then(h->Options.functor().map((String v) ->v.length(), h))
                                       .convert(OptionType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Options
     */
    public static <T,R>Functor<OptionType.µ> functor(){
        BiFunction<OptionType<T>,Function<? super T, ? extends R>,OptionType<R>> map = Options::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * OptionType<String> option = Options.unit()
                                          .unit("hello")
                                          .convert(OptionType::narrowK);
        
        //Option.some("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Options
     */
    public static Unit<OptionType.µ> unit(){
        return General.unit(Options::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionType.widen;
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
     * OptionType<Function<Integer,Integer>> optionFn =Options.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(OptionType::narrowK);
        
        OptionType<Integer> option = Options.unit()
                                      .unit("hello")
                                      .then(h->Options.functor().map((String v) ->v.length(), h))
                                      .then(h->Options.applicative().ap(optionFn, h))
                                      .convert(OptionType::narrowK);
        
        //Arrays.asOption("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Options
     */
    public static <T,R> Applicative<OptionType.µ> applicative(){
        BiFunction<OptionType< Function<T, R>>,OptionType<T>,OptionType<R>> ap = Options::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.OptionType.widen;
     * OptionType<Integer> option  = Options.monad()
                                      .flatMap(i->widen(OptionX.range(0,i)), widen(Option.some(1,2,3)))
                                      .convert(OptionType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    OptionType<Integer> option = Options.unit()
                                        .unit("hello")
                                        .then(h->Options.monad().flatMap((String v) ->Options.unit().unit(v.length()), h))
                                        .convert(OptionType::narrowK);
        
        //Arrays.asOption("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Options
     */
    public static <T,R> Monad<OptionType.µ> monad(){
  
        BiFunction<Higher<OptionType.µ,T>,Function<? super T, ? extends Higher<OptionType.µ,R>>,Higher<OptionType.µ,R>> flatMap = Options::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  OptionType<String> option = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionType::narrowK);
        
       //Arrays.asOption("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<OptionType.µ> monadZero(){
        
        return General.monadZero(monad(), OptionType.empty());
    }
    /**
     * <pre>
     * {@code 
     *  OptionType<Integer> option = Options.<Integer>monadPlus()
                                      .plus(OptionType.widen(Arrays.asOption()), OptionType.widen(Arrays.asOption(10)))
                                      .convert(OptionType::narrowK);
        //Arrays.asOption(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Options by concatenation
     */
    public static <T> MonadPlus<OptionType.µ,T> monadPlus(){
        Monoid<Option<T>> mn = Monoid.of(Option.none(), (a, b) -> a.isSome() ? a : b);
        Monoid<OptionType<T>> m = Monoid.of(OptionType.widen(mn.zero()), (f,g)-> OptionType.widen(
                                                                                mn.apply(OptionType.narrow(f), OptionType.narrow(g))));
                
        Monoid<Higher<OptionType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<OptionType<Integer>> m = Monoid.of(OptionType.widen(Arrays.asOption()), (a,b)->a.isEmpty() ? b : a);
        OptionType<Integer> option = Options.<Integer>monadPlus(m)
                                      .plus(OptionType.widen(Arrays.asOption(5)), OptionType.widen(Arrays.asOption(10)))
                                      .convert(OptionType::narrowK);
        //Arrays.asOption(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Options
     * @return Type class for combining Options
     */
    public static <T> MonadPlus<OptionType.µ,T> monadPlus(Monoid<OptionType<T>> m){
        Monoid<Higher<OptionType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<OptionType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), Options::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Options.foldable()
                           .foldLeft(0, (a,b)->a+b, OptionType.widen(Option.some(2)));
        
        //2
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<OptionType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<OptionType.µ,T>,T> foldRightFn =  (m,l)-> OptionType.narrow(l).orSome(m.zero());
        BiFunction<Monoid<T>,Higher<OptionType.µ,T>,T> foldLeftFn = (m,l)-> OptionType.narrow(l).orSome(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> OptionType<T> of(T value){
        return OptionType.widen(Option.some(value));
    }
    private static <T,R> OptionType<R> ap(OptionType<Function< T, R>> lt,  OptionType<T> option){
        Maybe<R> mb = MaybeType.widen(FJ.maybe(lt.narrow())).combine(MaybeType.widen(FJ.maybe(option.narrow())), 
                                                    (a,b)->a.apply(b));
        return OptionType.widen(mb);
        
    }
    private static <T,R> Higher<OptionType.µ,R> flatMap( Higher<OptionType.µ,T> lt, Function<? super T, ? extends  Higher<OptionType.µ,R>> fn){
        return OptionType.widen(OptionType.narrow(lt).bind(in->fn.andThen(OptionType::narrow).apply(in)));
    }
    private static <T,R> OptionType<R> map(OptionType<T> lt, Function<? super T, ? extends R> fn){
        
        return OptionType.widen(OptionType.narrow(lt).map(t->fn.apply(t)));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<OptionType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<OptionType.µ, T> ds){
        Option<T> opt = OptionType.narrow(ds);
        return opt.isSome()?   applicative.map(OptionType::of, fn.apply(opt.some())) : 
                                    applicative.unit(OptionType.empty());
    }
   
}
