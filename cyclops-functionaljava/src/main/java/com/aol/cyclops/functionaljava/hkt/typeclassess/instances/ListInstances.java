package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.functionaljava.hkt.ListType;
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

import fj.data.List;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Lists
 * @author johnmcclean
 *
 */
@UtilityClass
public class ListInstances {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  ListType<Integer> list = Lists.functor().map(i->i*2, ListType.widen(Arrays.asList(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Lists
     * <pre>
     * {@code 
     *   ListType<Integer> list = Lists.unit()
                                       .unit("hello")
                                       .then(h->Lists.functor().map((String v) ->v.length(), h))
                                       .convert(ListType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lists
     */
    public static <T,R>Functor<ListType.µ> functor(){
        BiFunction<ListType<T>,Function<? super T, ? extends R>,ListType<R>> map = ListInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(ListType::narrowK);
        
        //Arrays.asList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lists
     */
    public static <T> Unit<ListType.µ> unit(){
        return General.<ListType.µ,T>unit(ListInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ListType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asList;
     * 
       Lists.zippingApplicative()
            .ap(widen(asList(l1(this::multiplyByTwo))),widen(asList(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * ListType<Function<Integer,Integer>> listFn =Lists.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(ListType::narrowK);
        
        ListType<Integer> list = Lists.unit()
                                      .unit("hello")
                                      .then(h->Lists.functor().map((String v) ->v.length(), h))
                                      .then(h->Lists.zippingApplicative().ap(listFn, h))
                                      .convert(ListType::narrowK);
        
        //Arrays.asList("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Lists
     */
    public static <T,R> Applicative<ListType.µ> zippingApplicative(){
        BiFunction<ListType< Function<T, R>>,ListType<T>,ListType<R>> ap = ListInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ListType.widen;
     * ListType<Integer> list  = Lists.monad()
                                      .flatMap(i->widen(ListX.range(0,i)), widen(Arrays.asList(1,2,3)))
                                      .convert(ListType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    ListType<Integer> list = Lists.unit()
                                        .unit("hello")
                                        .then(h->Lists.monad().flatMap((String v) ->Lists.unit().unit(v.length()), h))
                                        .convert(ListType::narrowK);
        
        //Arrays.asList("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Lists
     */
    public static <T,R> Monad<ListType.µ> monad(){
  
        BiFunction<Higher<ListType.µ,T>,Function<? super T, ? extends Higher<ListType.µ,R>>,Higher<ListType.µ,R>> flatMap = ListInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
       //Arrays.asList("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<ListType.µ> monadZero(){
        
        return General.monadZero(monad(), ListType.widen(List.list()));
    }
    /**
     * <pre>
     * {@code 
     *  ListType<Integer> list = Lists.<Integer>monadPlus()
                                      .plus(ListType.widen(Arrays.asList()), ListType.widen(Arrays.asList(10)))
                                      .convert(ListType::narrowK);
        //Arrays.asList(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Lists by concatenation
     */
    public static <T> MonadPlus<ListType.µ> monadPlus(){
        Monoid<ListType<T>> m = Monoid.of(ListType.widen(List.list()), ListInstances::concat);
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<ListType<Integer>> m = Monoid.of(ListType.widen(Arrays.asList()), (a,b)->a.isEmpty() ? b : a);
        ListType<Integer> list = Lists.<Integer>monadPlus(m)
                                      .plus(ListType.widen(Arrays.asList(5)), ListType.widen(Arrays.asList(10)))
                                      .convert(ListType::narrowK);
        //Arrays.asList(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Lists
     * @return Type class for combining Lists
     */
    public static <T> MonadPlus<ListType.µ> monadPlus(Monoid<ListType<T>> m){
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ListType.µ> traverse(){
     
        BiFunction<Applicative<C2>,ListType<Higher<C2, T>>,Higher<C2, ListType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,ListType<T>> identity = ap.unit(ListType.widen(List.list()));

            BiFunction<Higher<C2,ListType<T>>,Higher<C2,T>,Higher<C2,ListType<T>>> combineToList =   
                    (acc,next) -> ap.apBiFn(ap.unit((a,b) -> ListType.widen(ListType.narrow(a).cons(b))), acc,next);

            BinaryOperator<Higher<C2,ListType<T>>> combineLists = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> ListType.widen(ListType.narrow(l1).append(ListType.narrow(l2)))),a,b); ;  
           
            return ReactiveSeq.fromIterable(ListType.narrow(list))
                      .reduce(identity,
                              combineToList,
                              combineLists);  

   
        };
        BiFunction<Applicative<C2>,Higher<ListType.µ,Higher<C2, T>>,Higher<C2, Higher<ListType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> ListType.widen2(sequenceFn.apply(a, ListType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }

    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lists.foldable()
                        .foldLeft(0, (a,b)->a+b, ListType.widen(Arrays.asList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ListType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ListType.µ,T>,T> foldRightFn =  (m,l)-> ListX.fromIterable(ListType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ListType.µ,T>,T> foldLeftFn = (m,l)-> ListX.fromIterable(ListType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ListType<T> concat(ListType<T> l1, ListType<T> l2){
        return ListType.widen(l1.append(ListType.narrow(l2)));
       
    }
    private <T> ListType<T> of(T value){
        return ListType.widen(List.list(value));
    }
    private static <T,R> ListType<R> ap(ListType<Function< T, R>> lt,  ListType<T> list){
        
        return ListType.widen(lt.zipWith(list.narrow(),(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<ListType.µ,R> flatMap( Higher<ListType.µ,T> lt, Function<? super T, ? extends  Higher<ListType.µ,R>> fn){
        return ListType.widen(ListType.narrow(lt).bind(in->fn.andThen(ListType::narrow).apply(in)));
    }
    private static <T,R> ListType<R> map(ListType<T> lt, Function<? super T, ? extends R> fn){
        return ListType.widen(ListType.narrow(lt).map(in->fn.apply(in)));
    }
}
