package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.functionaljava.hkt.NonEmptyListType;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;

import fj.data.List;
import fj.data.NonEmptyList;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with NonEmptyLists
 * @author johnmcclean
 *
 */
@UtilityClass
public class NonEmptyLists {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  NonEmptyListType<Integer> list = NonEmptyLists.functor().map(i->i*2, NonEmptyListType.widen(Arrays.asNonEmptyList(1,2,3));
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
     *   NonEmptyListType<Integer> list = NonEmptyLists.unit()
                                       .unit("hello")
                                       .then(h->NonEmptyLists.functor().map((String v) ->v.length(), h))
                                       .convert(NonEmptyListType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for NonEmptyLists
     */
    public static <T,R>Functor<NonEmptyListType.µ> functor(){
        BiFunction<NonEmptyListType<T>,Function<? super T, ? extends R>,NonEmptyListType<R>> map = NonEmptyLists::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * NonEmptyListType<String> list = NonEmptyLists.unit()
                                     .unit("hello")
                                     .convert(NonEmptyListType::narrowK);
        
        //Arrays.asNonEmptyList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for NonEmptyLists
     */
    public static Unit<NonEmptyListType.µ> unit(){
        return General.unit(NonEmptyLists::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.NonEmptyListType.widen;
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
     * NonEmptyListType<Function<Integer,Integer>> listFn =NonEmptyLists.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(NonEmptyListType::narrowK);
        
        NonEmptyListType<Integer> list = NonEmptyLists.unit()
                                      .unit("hello")
                                      .then(h->NonEmptyLists.functor().map((String v) ->v.length(), h))
                                      .then(h->NonEmptyLists.zippingApplicative().ap(listFn, h))
                                      .convert(NonEmptyListType::narrowK);
        
        //Arrays.asNonEmptyList("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for NonEmptyLists
     */
    public static <T,R> Applicative<NonEmptyListType.µ> zippingApplicative(){
        BiFunction<NonEmptyListType< Function<T, R>>,NonEmptyListType<T>,NonEmptyListType<R>> ap = NonEmptyLists::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.NonEmptyListType.widen;
     * NonEmptyListType<Integer> list  = NonEmptyLists.monad()
                                      .flatMap(i->widen(NonEmptyListX.range(0,i)), widen(Arrays.asNonEmptyList(1,2,3)))
                                      .convert(NonEmptyListType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    NonEmptyListType<Integer> list = NonEmptyLists.unit()
                                        .unit("hello")
                                        .then(h->NonEmptyLists.monad().flatMap((String v) ->NonEmptyLists.unit().unit(v.length()), h))
                                        .convert(NonEmptyListType::narrowK);
        
        //Arrays.asNonEmptyList("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for NonEmptyLists
     */
    public static <T,R> Monad<NonEmptyListType.µ> monad(){
  
        BiFunction<Higher<NonEmptyListType.µ,T>,Function<? super T, ? extends Higher<NonEmptyListType.µ,R>>,Higher<NonEmptyListType.µ,R>> flatMap = NonEmptyLists::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
   
    
   
 
   
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = NonEmptyLists.foldable()
                        .foldLeft(0, (a,b)->a+b, NonEmptyListType.widen(Arrays.asNonEmptyList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<NonEmptyListType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<NonEmptyListType.µ,T>,T> foldRightFn =  (m,l)-> ListX.fromIterable(NonEmptyListType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<NonEmptyListType.µ,T>,T> foldLeftFn = (m,l)-> ListX.fromIterable(NonEmptyListType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    
    private <T> NonEmptyListType<T> of(T value){
        return NonEmptyListType.of(value);
    }
    private static <T,R> NonEmptyListType<R> ap(NonEmptyListType<Function< T, R>> lt,  NonEmptyListType<T> list){
        
        return NonEmptyListType.widen(lt.zipWith(list.narrow().toList(),(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<NonEmptyListType.µ,R> flatMap( Higher<NonEmptyListType.µ,T> lt, Function<? super T, ? extends  Higher<NonEmptyListType.µ,R>> fn){
        return NonEmptyListType.widen(NonEmptyListType.narrow(lt).bind(in->fn.andThen(NonEmptyListType::narrow).apply(in)));
    }
    private static <T,R> NonEmptyListType<R> map(NonEmptyListType<T> lt, Function<? super T, ? extends R> fn){
        return NonEmptyListType.widen(NonEmptyListType.narrow(lt).map(in->fn.apply(in)));
    }
}
