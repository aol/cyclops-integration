package com.aol.cyclops.hkt.instances.jdk;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aol.cyclops.CyclopsCollectors;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.DequeX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.DequeType;
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
 * Companion class for creating Type Class instances for working with Deques
 * @author johnmcclean
 *
 */
@UtilityClass
public class DequeInstances {

    public static void main(String[] args){
        Deque<Integer> small = DequeX.of(1,2,3);
        DequeType<Integer> list = DequeInstances.functor()
                                     .map(i->i*2, DequeType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->DequeType.widen(DequeX.of(1)), h))
                                     .convert(DequeType::narrowK);
          DequeType<Integer> string = list.convert(DequeType::narrowK);
                    
        System.out.println(DequeInstances.functor().map(i->i*2, DequeType.widen(small)));
    }
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  DequeType<Integer> list = Deques.functor().map(i->i*2, DequeType.widen(DequeX.of(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Deques
     * <pre>
     * {@code 
     *   DequeType<Integer> list = Deques.unit()
                                       .unit("hello")
                                       .then(h->Deques.functor().map((String v) ->v.length(), h))
                                       .convert(DequeType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Deques
     */
    public static <T,R>Functor<DequeType.µ> functor(){
        BiFunction<DequeType<T>,Function<? super T, ? extends R>,DequeType<R>> map = DequeInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * DequeType<String> list = Deques.unit()
                                     .unit("hello")
                                     .convert(DequeType::narrowK);
        
        //DequeX.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Deques
     */
    public static <T> Unit<DequeType.µ> unit(){
        return General.<DequeType.µ,T>unit(DequeInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.DequeType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.DequeX.of;
     * 
       Deques.zippingApplicative()
            .ap(widen(asDeque(l1(this::multiplyByTwo))),widen(asDeque(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * DequeType<Function<Integer,Integer>> listFn =Deques.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(DequeType::narrowK);
        
        DequeType<Integer> list = Deques.unit()
                                      .unit("hello")
                                      .then(h->Deques.functor().map((String v) ->v.length(), h))
                                      .then(h->Deques.zippingApplicative().ap(listFn, h))
                                      .convert(DequeType::narrowK);
        
        //DequeX.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Deques
     */
    public static <T,R> Applicative<DequeType.µ> zippingApplicative(){
        BiFunction<DequeType< Function<T, R>>,DequeType<T>,DequeType<R>> ap = DequeInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.DequeType.widen;
     * DequeType<Integer> list  = Deques.monad()
                                      .flatMap(i->widen(DequeX.range(0,i)), widen(DequeX.of(1,2,3)))
                                      .convert(DequeType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    DequeType<Integer> list = Deques.unit()
                                        .unit("hello")
                                        .then(h->Deques.monad().flatMap((String v) ->Deques.unit().unit(v.length()), h))
                                        .convert(DequeType::narrowK);
        
        //DequeX.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Deques
     */
    public static <T,R> Monad<DequeType.µ> monad(){
  
        BiFunction<Higher<DequeType.µ,T>,Function<? super T, ? extends Higher<DequeType.µ,R>>,Higher<DequeType.µ,R>> flatMap = DequeInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  DequeType<String> list = Deques.unit()
                                     .unit("hello")
                                     .then(h->Deques.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(DequeType::narrowK);
        
       //DequeX.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<DequeType.µ> monadZero(){
        
        return General.monadZero(monad(), DequeType.widen(new ArrayDeque<T>()));
    }
    /**
     * <pre>
     * {@code 
     *  DequeType<Integer> list = Deques.<Integer>monadPlus()
                                      .plus(DequeType.widen(DequeX.of()), DequeType.widen(DequeX.of(10)))
                                      .convert(DequeType::narrowK);
        //DequeX.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Deques by concatenation
     */
    public static <T> MonadPlus<DequeType.µ> monadPlus(){
        Monoid<DequeType<T>> m = Monoid.of(DequeType.widen(new ArrayDeque<T>()), DequeInstances::concat);
        Monoid<Higher<DequeType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<DequeType<Integer>> m = Monoid.of(DequeType.widen(DequeX.of()), (a,b)->a.isEmpty() ? b : a);
        DequeType<Integer> list = Deques.<Integer>monadPlus(m)
                                      .plus(DequeType.widen(DequeX.of(5)), DequeType.widen(DequeX.of(10)))
                                      .convert(DequeType::narrowK);
        //DequeX.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Deques
     * @return Type class for combining Deques
     */
    public static <T> MonadPlus<DequeType.µ> monadPlus(Monoid<DequeType<T>> m){
        Monoid<Higher<DequeType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<DequeType.µ> traverse(){
        BiFunction<Applicative<C2>,DequeType<Higher<C2, T>>,Higher<C2, DequeType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,DequeType<T>> identity = ap.unit(DequeType.widen(DequeX.of()));

            BiFunction<Higher<C2,DequeType<T>>,Higher<C2,T>,Higher<C2,DequeType<T>>> combineToDeque =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> { a.add(b); return a;}),acc,next);

            BinaryOperator<Higher<C2,DequeType<T>>> combineDeques = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { l1.addAll(l2); return l1;}),a,b); ;  

            return list.stream()
                      .reduce(identity,
                              combineToDeque,
                              combineDeques);  

   
        };
        BiFunction<Applicative<C2>,Higher<DequeType.µ,Higher<C2, T>>,Higher<C2, Higher<DequeType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> DequeType.widen2(sequenceFn.apply(a, DequeType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Deques.foldable()
                        .foldLeft(0, (a,b)->a+b, DequeType.widen(DequeX.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<DequeType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<DequeType.µ,T>,T> foldRightFn =  (m,l)-> DequeX.fromIterable(DequeType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<DequeType.µ,T>,T> foldLeftFn = (m,l)-> DequeX.fromIterable(DequeType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> DequeType<T> concat(Deque<T> l1, Deque<T> l2){
        return DequeType.widen(Stream.concat(l1.stream(),l2.stream()).collect(CyclopsCollectors.toDequeX()));
    }
    private <T> DequeType<T> of(T value){
        return DequeType.widen(DequeX.of(value));
    }
    private static <T,R> DequeType<R> ap(DequeType<Function< T, R>> lt,  DequeType<T> list){
        return DequeType.widen(DequeX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<DequeType.µ,R> flatMap( Higher<DequeType.µ,T> lt, Function<? super T, ? extends  Higher<DequeType.µ,R>> fn){
        return DequeType.widen(DequeX.fromIterable(DequeType.narrowK(lt)).flatMap(fn.andThen(DequeType::narrowK)));
    }
    private static <T,R> DequeType<R> map(DequeType<T> lt, Function<? super T, ? extends R> fn){
        return DequeType.widen(DequeX.fromIterable(lt).map(fn));
    }
}
