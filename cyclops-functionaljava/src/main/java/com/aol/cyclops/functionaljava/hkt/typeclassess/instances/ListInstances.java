package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.functionaljava.hkt.ListKind;


import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.ListX;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
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
     *  ListKind<Integer> list = Lists.functor().map(i->i*2, ListKind.widen(Arrays.asList(1,2,3));
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
     *   ListKind<Integer> list = Lists.unit()
                                       .unit("hello")
                                       .then(h->Lists.functor().map((String v) ->v.length(), h))
                                       .convert(ListKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lists
     */
    public static <T,R>Functor<ListKind.µ> functor(){
        BiFunction<ListKind<T>,Function<? super T, ? extends R>,ListKind<R>> map = ListInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * ListKind<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(ListKind::narrowK);
        
        //Arrays.asList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lists
     */
    public static <T> Pure<ListKind.µ> unit(){
        return General.<ListKind.µ,T>unit(ListInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ListKind.widen;
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
     * ListKind<Function<Integer,Integer>> listFn =Lists.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(ListKind::narrowK);
        
        ListKind<Integer> list = Lists.unit()
                                      .unit("hello")
                                      .then(h->Lists.functor().map((String v) ->v.length(), h))
                                      .then(h->Lists.zippingApplicative().ap(listFn, h))
                                      .convert(ListKind::narrowK);
        
        //Arrays.asList("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Lists
     */
    public static <T,R> Applicative<ListKind.µ> zippingApplicative(){
        BiFunction<ListKind< Function<T, R>>,ListKind<T>,ListKind<R>> ap = ListInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ListKind.widen;
     * ListKind<Integer> list  = Lists.monad()
                                      .flatMap(i->widen(ListX.range(0,i)), widen(Arrays.asList(1,2,3)))
                                      .convert(ListKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    ListKind<Integer> list = Lists.unit()
                                        .unit("hello")
                                        .then(h->Lists.monad().flatMap((String v) ->Lists.unit().unit(v.length()), h))
                                        .convert(ListKind::narrowK);
        
        //Arrays.asList("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Lists
     */
    public static <T,R> Monad<ListKind.µ> monad(){
  
        BiFunction<Higher<ListKind.µ,T>,Function<? super T, ? extends Higher<ListKind.µ,R>>,Higher<ListKind.µ,R>> flatMap = ListInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  ListKind<String> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListKind::narrowK);
        
       //Arrays.asList("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<ListKind.µ> monadZero(){
        
        return General.monadZero(monad(), ListKind.widen(List.list()));
    }
    /**
     * <pre>
     * {@code 
     *  ListKind<Integer> list = Lists.<Integer>monadPlus()
                                      .plus(ListKind.widen(Arrays.asList()), ListKind.widen(Arrays.asList(10)))
                                      .convert(ListKind::narrowK);
        //Arrays.asList(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Lists by concatenation
     */
    public static <T> MonadPlus<ListKind.µ> monadPlus(){
        Monoid<ListKind<T>> m = Monoid.of(ListKind.widen(List.list()), ListInstances::concat);
        Monoid<Higher<ListKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<ListKind<Integer>> m = Monoid.of(ListKind.widen(Arrays.asList()), (a,b)->a.isEmpty() ? b : a);
        ListKind<Integer> list = Lists.<Integer>monadPlus(m)
                                      .plus(ListKind.widen(Arrays.asList(5)), ListKind.widen(Arrays.asList(10)))
                                      .convert(ListKind::narrowK);
        //Arrays.asList(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Lists
     * @return Type class for combining Lists
     */
    public static <T> MonadPlus<ListKind.µ> monadPlus(Monoid<ListKind<T>> m){
        Monoid<Higher<ListKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ListKind.µ> traverse(){
     
        BiFunction<Applicative<C2>,ListKind<Higher<C2, T>>,Higher<C2, ListKind<T>>> sequenceFn = (ap, list) -> {
        
            Higher<C2,ListKind<T>> identity = ap.unit(ListKind.widen(List.list()));

            BiFunction<Higher<C2,ListKind<T>>,Higher<C2,T>,Higher<C2,ListKind<T>>> combineToList =
                    (acc,next) -> ap.apBiFn(ap.unit((a,b) -> ListKind.widen(ListKind.narrow(a).cons(b))), acc,next);

            BinaryOperator<Higher<C2,ListKind<T>>> combineLists = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> ListKind.widen(ListKind.narrow(l1).append(ListKind.narrow(l2)))),a,b); ;
           
            return ReactiveSeq.fromIterable(ListKind.narrow(list))
                      .reduce(identity,
                              combineToList,
                              combineLists);  

   
        };
        BiFunction<Applicative<C2>,Higher<ListKind.µ,Higher<C2, T>>,Higher<C2, Higher<ListKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> ListKind.widen2(sequenceFn.apply(a, ListKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }

    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lists.foldable()
                        .foldLeft(0, (a,b)->a+b, ListKind.widen(Arrays.asList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ListKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ListKind.µ,T>,T> foldRightFn =  (m, l)-> ListX.fromIterable(ListKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ListKind.µ,T>,T> foldLeftFn = (m, l)-> ListX.fromIterable(ListKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ListKind<T> concat(ListKind<T> l1, ListKind<T> l2){
        return ListKind.widen(l1.append(ListKind.narrow(l2)));
       
    }
    private <T> ListKind<T> of(T value){
        return ListKind.widen(List.list(value));
    }
    private static <T,R> ListKind<R> ap(ListKind<Function< T, R>> lt, ListKind<T> list){
        
        return ListKind.widen(lt.zipWith(list.narrow(),(a, b)->a.apply(b)));
    }
    private static <T,R> Higher<ListKind.µ,R> flatMap(Higher<ListKind.µ,T> lt, Function<? super T, ? extends  Higher<ListKind.µ,R>> fn){
        return ListKind.widen(ListKind.narrow(lt).bind(in->fn.andThen(ListKind::narrow).apply(in)));
    }
    private static <T,R> ListKind<R> map(ListKind<T> lt, Function<? super T, ? extends R> fn){
        return ListKind.widen(ListKind.narrow(lt).map(in->fn.apply(in)));
    }
}
