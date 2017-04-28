package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.functionaljava.hkt.NonEmptyListKind;


import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.ListX;
import cyclops.function.Monoid;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.Applicative;
import cyclops.typeclasses.monad.Monad;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with NonEmptyLists
 * @author johnmcclean
 *
 */
@UtilityClass
public class NonEmptyListInstances {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  NonEmptyListKind<Integer> list = NonEmptyLists.functor().map(i->i*2, NonEmptyListKind.widen(Arrays.asNonEmptyList(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with NonEmptyLists
     * <pre>
     * {@code 
     *   NonEmptyListKind<Integer> list = NonEmptyLists.unit()
                                       .unit("hello")
                                       .then(h->NonEmptyLists.functor().map((String v) ->v.length(), h))
                                       .convert(NonEmptyListKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for NonEmptyLists
     */
    public static <T,R>Functor<NonEmptyListKind.µ> functor(){
        BiFunction<NonEmptyListKind<T>,Function<? super T, ? extends R>,NonEmptyListKind<R>> map = NonEmptyListInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * NonEmptyListKind<String> list = NonEmptyLists.unit()
                                     .unit("hello")
                                     .convert(NonEmptyListKind::narrowK);
        
        //Arrays.asNonEmptyList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for NonEmptyLists
     */
    public static <T> Pure<NonEmptyListKind.µ> unit(){
        return General.<NonEmptyListKind.µ,T>unit(NonEmptyListInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.NonEmptyListKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asNonEmptyList;
     * 
       NonEmptyLists.zippingApplicative()
            .ap(widen(asNonEmptyList(l1(this::multiplyByTwo))),widen(asNonEmptyList(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * NonEmptyListKind<Function<Integer,Integer>> listFn =NonEmptyLists.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(NonEmptyListKind::narrowK);
        
        NonEmptyListKind<Integer> list = NonEmptyLists.unit()
                                      .unit("hello")
                                      .then(h->NonEmptyLists.functor().map((String v) ->v.length(), h))
                                      .then(h->NonEmptyLists.zippingApplicative().ap(listFn, h))
                                      .convert(NonEmptyListKind::narrowK);
        
        //Arrays.asNonEmptyList("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for NonEmptyLists
     */
    public static <T,R> Applicative<NonEmptyListKind.µ> zippingApplicative(){
        BiFunction<NonEmptyListKind< Function<T, R>>,NonEmptyListKind<T>,NonEmptyListKind<R>> ap = NonEmptyListInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.NonEmptyListKind.widen;
     * NonEmptyListKind<Integer> list  = NonEmptyLists.monad()
                                      .flatMap(i->widen(NonEmptyListX.range(0,i)), widen(Arrays.asNonEmptyList(1,2,3)))
                                      .convert(NonEmptyListKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    NonEmptyListKind<Integer> list = NonEmptyLists.unit()
                                        .unit("hello")
                                        .then(h->NonEmptyLists.monad().flatMap((String v) ->NonEmptyLists.unit().unit(v.length()), h))
                                        .convert(NonEmptyListKind::narrowK);
        
        //Arrays.asNonEmptyList("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for NonEmptyLists
     */
    public static <T,R> Monad<NonEmptyListKind.µ> monad(){
  
        BiFunction<Higher<NonEmptyListKind.µ,T>,Function<? super T, ? extends Higher<NonEmptyListKind.µ,R>>,Higher<NonEmptyListKind.µ,R>> flatMap = NonEmptyListInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
   
    
   
 
   
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = NonEmptyLists.foldable()
                        .foldLeft(0, (a,b)->a+b, NonEmptyListKind.widen(Arrays.asNonEmptyList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<NonEmptyListKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<NonEmptyListKind.µ,T>,T> foldRightFn =  (m, l)-> ListX.fromIterable(NonEmptyListKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<NonEmptyListKind.µ,T>,T> foldLeftFn = (m, l)-> ListX.fromIterable(NonEmptyListKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> NonEmptyListKind<T> of(T value){
        return NonEmptyListKind.of(value);
    }
    private static <T,R> NonEmptyListKind<R> ap(NonEmptyListKind<Function< T, R>> lt, NonEmptyListKind<T> list){
        
        return NonEmptyListKind.widen(lt.zipWith(list.narrow().toList(),(a, b)->a.apply(b)));
    }
    private static <T,R> Higher<NonEmptyListKind.µ,R> flatMap(Higher<NonEmptyListKind.µ,T> lt, Function<? super T, ? extends  Higher<NonEmptyListKind.µ,R>> fn){
        return NonEmptyListKind.widen(NonEmptyListKind.narrow(lt).bind(in->fn.andThen(NonEmptyListKind::narrow).apply(in)));
    }
    private static <T,R> NonEmptyListKind<R> map(NonEmptyListKind<T> lt, Function<? super T, ? extends R> fn){
        return NonEmptyListKind.widen(NonEmptyListKind.narrow(lt).map(in->fn.apply(in)));
    }
}
