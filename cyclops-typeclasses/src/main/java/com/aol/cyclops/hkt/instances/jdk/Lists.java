package com.aol.cyclops.hkt.instances.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.ListType;
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
 * Companion class for creating Type Class instances for working with Lists
 * @author johnmcclean
 *
 */
@UtilityClass
public class Lists {

    public static void main(String[] args){
        List<Integer> small = Arrays.asList(1,2,3);
        ListType<Integer> list = Lists.functor()
                                     .map(i->i*2, ListType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->ListType.widen(Arrays.asList(1)), h))
                                     .convert(ListType::narrowK);
          ListType<Integer> string = list.convert(ListType::narrowK);
                    
        System.out.println(Lists.functor().map(i->i*2, ListType.widen(small)));
    }
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
        BiFunction<ListType<T>,Function<? super T, ? extends R>,ListType<R>> map = Lists::map;
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
    public static Unit<ListType.µ> unit(){
        return General.unit(Lists::of);
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
        BiFunction<ListType< Function<T, R>>,ListType<T>,ListType<R>> ap = Lists::ap;
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
  
        BiFunction<Higher<ListType.µ,T>,Function<? super T, ? extends Higher<ListType.µ,R>>,Higher<ListType.µ,R>> flatMap = Lists::flatMap;
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
        
        return General.monadZero(monad(), ListType.widen(new ArrayList<T>()));
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
    public static <T> MonadPlus<ListType.µ,T> monadPlus(){
        Monoid<ListType<T>> m = Monoid.of(ListType.widen(new ArrayList<T>()), Lists::concat);
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * @param m Monoid to use for combining Lists
     * @return Type class for combining Lists
     */
    public static <T> MonadPlus<ListType.µ,T> monadPlus(Monoid<ListType<T>> m){
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ListType.µ> traverse(){
        BiFunction<Applicative<C2>,ListType<Higher<C2, T>>,Higher<C2, ListType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,ListType<T>> identity = ap.unit(ListType.widen(Arrays.asList()));

            BiFunction<Higher<C2,ListType<T>>,Higher<C2,T>,Higher<C2,ListType<T>>> combineToList =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> { a.add(b); return a;}),acc,next);

            BinaryOperator<Higher<C2,ListType<T>>> combineLists = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { l1.addAll(l2); return l1;}),a,b); ;  

            return list.stream()
                      .reduce(identity,
                              combineToList,
                              combineLists);  

   
        };
        BiFunction<Applicative<C2>,Higher<ListType.µ,Higher<C2, T>>,Higher<C2, Higher<ListType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> ListType.widen2(sequenceFn.apply(a, ListType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ListType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ListType.µ,T>,T> foldRightFn =  (m,l)-> ListX.fromIterable(ListType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ListType.µ,T>,T> foldLeftFn = (m,l)-> ListX.fromIterable(ListType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ListType<T> concat(List<T> l1, List<T> l2){
        return ListType.widen(Stream.concat(l1.stream(),l2.stream()).collect(Collectors.toList()));
    }
    private <T> ListType<T> of(T value){
        return ListType.widen(Arrays.asList(value));
    }
    private static <T,R> ListType<R> ap(ListType<Function< T, R>> lt,  ListType<T> list){
        return ListType.widen(ListX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<ListType.µ,R> flatMap( Higher<ListType.µ,T> lt, Function<? super T, ? extends  Higher<ListType.µ,R>> fn){
        return ListType.widen(ListX.fromIterable(ListType.narrowK(lt)).flatMap(fn.andThen(ListType::narrowK)));
    }
    private static <T,R> ListType<R> map(ListType<T> lt, Function<? super T, ? extends R> fn){
        return ListType.widen(ListX.fromIterable(lt).map(fn));
    }
}
