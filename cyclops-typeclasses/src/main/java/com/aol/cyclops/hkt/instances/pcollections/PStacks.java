package com.aol.cyclops.hkt.instances.pcollections;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.pcollections.PStack;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.pcollections.PStackType;
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
 * Companion class for creating Type Class instances for working with PStacks
 * @author johnmcclean
 *
 */
@UtilityClass
public class PStacks {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  PStackType<Integer> list = PStacks.functor().map(i->i*2, PStackType.widen(Arrays.asPStack(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with PStacks
     * <pre>
     * {@code 
     *   PStackType<Integer> list = PStacks.unit()
                                       .unit("hello")
                                       .then(h->PStacks.functor().map((String v) ->v.length(), h))
                                       .convert(PStackType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for PStacks
     */
    public static <T,R>Functor<PStackType.µ> functor(){
        BiFunction<PStackType<T>,Function<? super T, ? extends R>,PStackType<R>> map = PStacks::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * PStackType<String> list = PStacks.unit()
                                     .unit("hello")
                                     .convert(PStackType::narrowK);
        
        //Arrays.asPStack("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for PStacks
     */
    public static Unit<PStackType.µ> unit(){
        return General.unit(PStacks::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PStackType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asPStack;
     * 
       PStacks.zippingApplicative()
            .ap(widen(asPStack(l1(this::multiplyByTwo))),widen(asPStack(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * PStackType<Function<Integer,Integer>> listFn =PStacks.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(PStackType::narrowK);
        
        PStackType<Integer> list = PStacks.unit()
                                      .unit("hello")
                                      .then(h->PStacks.functor().map((String v) ->v.length(), h))
                                      .then(h->PStacks.zippingApplicative().ap(listFn, h))
                                      .convert(PStackType::narrowK);
        
        //Arrays.asPStack("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for PStacks
     */
    public static <T,R> Applicative<PStackType.µ> zippingApplicative(){
        BiFunction<PStackType< Function<T, R>>,PStackType<T>,PStackType<R>> ap = PStacks::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PStackType.widen;
     * PStackType<Integer> list  = PStacks.monad()
                                      .flatMap(i->widen(PStackX.range(0,i)), widen(Arrays.asPStack(1,2,3)))
                                      .convert(PStackType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    PStackType<Integer> list = PStacks.unit()
                                        .unit("hello")
                                        .then(h->PStacks.monad().flatMap((String v) ->PStacks.unit().unit(v.length()), h))
                                        .convert(PStackType::narrowK);
        
        //Arrays.asPStack("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for PStacks
     */
    public static <T,R> Monad<PStackType.µ> monad(){
  
        BiFunction<Higher<PStackType.µ,T>,Function<? super T, ? extends Higher<PStackType.µ,R>>,Higher<PStackType.µ,R>> flatMap = PStacks::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  PStackType<String> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PStackType::narrowK);
        
       //Arrays.asPStack("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<PStackType.µ> monadZero(){
        
        return General.monadZero(monad(), PStackType.widen(PStackX.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  PStackType<Integer> list = PStacks.<Integer>monadPlus()
                                      .plus(PStackType.widen(Arrays.asPStack()), PStackType.widen(Arrays.asPStack(10)))
                                      .convert(PStackType::narrowK);
        //Arrays.asPStack(10))
     * 
     * }
     * </pre>
     * @return Type class for combining PStacks by concatenation
     */
    public static <T> MonadPlus<PStackType.µ,T> monadPlus(){
        Monoid<PStackType<T>> m = Monoid.of(PStackType.widen(PStackX.empty()), PStacks::concat);
        Monoid<Higher<PStackType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<PStackType<Integer>> m = Monoid.of(PStackType.widen(Arrays.asPStack()), (a,b)->a.isEmpty() ? b : a);
        PStackType<Integer> list = PStacks.<Integer>monadPlus(m)
                                      .plus(PStackType.widen(Arrays.asPStack(5)), PStackType.widen(Arrays.asPStack(10)))
                                      .convert(PStackType::narrowK);
        //Arrays.asPStack(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining PStacks
     * @return Type class for combining PStacks
     */
    public static <T> MonadPlus<PStackType.µ,T> monadPlus(Monoid<PStackType<T>> m){
        Monoid<Higher<PStackType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<PStackType.µ> traverse(){
        BiFunction<Applicative<C2>,PStackType<Higher<C2, T>>,Higher<C2, PStackType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,PStackType<T>> identity = ap.unit(PStackType.widen(PStackX.empty()));

            BiFunction<Higher<C2,PStackType<T>>,Higher<C2,T>,Higher<C2,PStackType<T>>> combineToPStack =   (acc,next) -> ap.apBiFn(ap.unit((a,b) ->PStackType.widen(a.plus(b))),acc,next);

            BinaryOperator<Higher<C2,PStackType<T>>> combinePStacks = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> PStackType.widen(l1.plusAll(l2))),a,b); ;  

            return list.stream()
                      .reduce(identity,
                              combineToPStack,
                              combinePStacks);  

   
        };
        BiFunction<Applicative<C2>,Higher<PStackType.µ,Higher<C2, T>>,Higher<C2, Higher<PStackType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> PStackType.widen2(sequenceFn.apply(a, PStackType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = PStacks.foldable()
                        .foldLeft(0, (a,b)->a+b, PStackType.widen(Arrays.asPStack(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<PStackType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<PStackType.µ,T>,T> foldRightFn =  (m,l)-> PStackX.fromIterable(PStackType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<PStackType.µ,T>,T> foldLeftFn = (m,l)-> PStackX.fromIterable(PStackType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> PStackType<T> concat(PStack<T> l1, PStack<T> l2){
       
        return PStackType.widen(l1.plusAll(l2));
    }
    private <T> PStackType<T> of(T value){
        return PStackType.widen(PStackX.of(value));
    }
    private static <T,R> PStackType<R> ap(PStackType<Function< T, R>> lt,  PStackType<T> list){
        return PStackType.widen(PStackX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<PStackType.µ,R> flatMap( Higher<PStackType.µ,T> lt, Function<? super T, ? extends  Higher<PStackType.µ,R>> fn){
        return PStackType.widen(PStackX.fromIterable(PStackType.narrowK(lt)).flatMap(fn.andThen(PStackType::narrowK)));
    }
    private static <T,R> PStackType<R> map(PStackType<T> lt, Function<? super T, ? extends R> fn){
        return PStackType.widen(PStackX.fromIterable(lt).map(fn));
    }
}
