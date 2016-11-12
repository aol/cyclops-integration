package com.aol.cyclops.hkt.instances.cyclops;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.EvalType;
import com.aol.cyclops.hkt.cyclops.FutureType;
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

import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Evals
 * @author johnmcclean
 *
 */
@UtilityClass
public class Evals {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  EvalType<Integer> list = Evals.functor().map(i->i*2, EvalType.widen(Arrays.asEval(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Evals
     * <pre>
     * {@code 
     *   EvalType<Integer> list = Evals.unit()
                                       .unit("hello")
                                       .then(h->Evals.functor().map((String v) ->v.length(), h))
                                       .convert(EvalType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Evals
     */
    public static <T,R>Functor<EvalType.µ> functor(){
        BiFunction<EvalType<T>,Function<? super T, ? extends R>,EvalType<R>> map = Evals::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * EvalType<String> list = Evals.unit()
                                     .unit("hello")
                                     .convert(EvalType::narrowK);
        
        //Arrays.asEval("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Evals
     */
    public static Unit<EvalType.µ> unit(){
        return General.unit(Evals::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.EvalType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asEval;
     * 
       Evals.zippingApplicative()
            .ap(widen(asEval(l1(this::multiplyByTwo))),widen(asEval(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * EvalType<Function<Integer,Integer>> listFn =Evals.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(EvalType::narrowK);
        
        EvalType<Integer> list = Evals.unit()
                                      .unit("hello")
                                      .then(h->Evals.functor().map((String v) ->v.length(), h))
                                      .then(h->Evals.applicative().ap(listFn, h))
                                      .convert(EvalType::narrowK);
        
        //Arrays.asEval("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Evals
     */
    public static <T,R> Applicative<EvalType.µ> applicative(){
        BiFunction<EvalType< Function<T, R>>,EvalType<T>,EvalType<R>> ap = Evals::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.EvalType.widen;
     * EvalType<Integer> list  = Evals.monad()
                                      .flatMap(i->widen(EvalX.range(0,i)), widen(Arrays.asEval(1,2,3)))
                                      .convert(EvalType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    EvalType<Integer> list = Evals.unit()
                                        .unit("hello")
                                        .then(h->Evals.monad().flatMap((String v) ->Evals.unit().unit(v.length()), h))
                                        .convert(EvalType::narrowK);
        
        //Arrays.asEval("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Evals
     */
    public static <T,R> Monad<EvalType.µ> monad(){
  
        BiFunction<Higher<EvalType.µ,T>,Function<? super T, ? extends Higher<EvalType.µ,R>>,Higher<EvalType.µ,R>> flatMap = Evals::flatMap;
        return General.monad(applicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  EvalType<String> list = Evals.unit()
                                     .unit("hello")
                                     .then(h->Evals.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(EvalType::narrowK);
        
       //Arrays.asEval("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<EvalType.µ> monadZero(){
        
        return General.monadZero(monad(), EvalType.now(null));
    }
    /**
     * <pre>
     * {@code 
     *  EvalType<Integer> list = Evals.<Integer>monadPlus()
                                      .plus(EvalType.widen(Arrays.asEval()), EvalType.widen(Arrays.asEval(10)))
                                      .convert(EvalType::narrowK);
        //Arrays.asEval(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Evals by concatenation
     */
    public static <T> MonadPlus<EvalType.µ,T> monadPlus(){
        Monoid<Eval<T>> mn = Monoid.of(Eval.now(null), (a,b)->a.get()!=null?a :b);
        Monoid<EvalType<T>> m = Monoid.of(EvalType.widen(mn.zero()), (f,g)-> EvalType.widen(
                                                                                mn.apply(EvalType.narrow(f), EvalType.narrow(g))));
                
        Monoid<Higher<EvalType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<EvalType<Integer>> m = Monoid.of(EvalType.widen(Arrays.asEval()), (a,b)->a.isEmpty() ? b : a);
        EvalType<Integer> list = Evals.<Integer>monadPlus(m)
                                      .plus(EvalType.widen(Arrays.asEval(5)), EvalType.widen(Arrays.asEval(10)))
                                      .convert(EvalType::narrowK);
        //Arrays.asEval(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Evals
     * @return Type class for combining Evals
     */
    public static <T> MonadPlus<EvalType.µ,T> monadPlus(Monoid<EvalType<T>> m){
        Monoid<Higher<EvalType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<EvalType.µ> traverse(){
      
        return General.traverseByTraverse(applicative(), Evals::traverseA);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Evals.foldable()
                           .foldLeft(0, (a,b)->a+b, EvalType.widen(Arrays.asEval(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<EvalType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<EvalType.µ,T>,T> foldRightFn =  (m,l)-> EvalType.narrow(l).orElse(m.zero());
        BiFunction<Monoid<T>,Higher<EvalType.µ,T>,T> foldLeftFn = (m,l)-> EvalType.narrow(l).orElse(m.zero());
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    public static <T> Comonad<EvalType.µ> comonad(){
        Function<? super Higher<EvalType.µ, T>, ? extends T> extractFn = maybe -> maybe.convert(EvalType::narrow).get();
        return General.comonad(functor(), unit(), extractFn);
    }
    private <T> EvalType<T> of(T value){
        return EvalType.widen(Eval.now(value));
    }
    private static <T,R> EvalType<R> ap(EvalType<Function< T, R>> lt,  EvalType<T> maybe){
        return EvalType.widen(lt.combine(maybe, (a,b)->a.apply(b)));
        
    }
    private static <T,R> Higher<EvalType.µ,R> flatMap( Higher<EvalType.µ,T> lt, Function<? super T, ? extends  Higher<EvalType.µ,R>> fn){
        return EvalType.widen(EvalType.narrow(lt).flatMap(fn.andThen(EvalType::narrow)));
    }
    private static <T,R> EvalType<R> map(EvalType<T> lt, Function<? super T, ? extends R> fn){
        return EvalType.widen(EvalType.narrow(lt).map(fn));
    }
  
 
    private static <C2,T,R> Higher<C2, Higher<EvalType.µ, R>> traverseA(Applicative<C2> applicative, Function<? super T, ? extends Higher<C2, R>> fn, 
            Higher<EvalType.µ, T> ds){
       
        Eval<T> eval = EvalType.narrow(ds);
        return applicative.map(EvalType::now, fn.apply(eval.get()));
    }
   
}
