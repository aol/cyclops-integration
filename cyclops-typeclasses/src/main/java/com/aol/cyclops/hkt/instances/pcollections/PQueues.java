package com.aol.cyclops.hkt.instances.pcollections;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.pcollections.PQueue;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.persistent.PQueueX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.pcollections.PQueueType;
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
 * Companion class for creating Type Class instances for working with PQueues
 * @author johnmcclean
 *
 */
@UtilityClass
public class PQueues {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  PQueueType<Integer> list = PQueues.functor().map(i->i*2, PQueueType.widen(Arrays.asPQueue(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with PQueues
     * <pre>
     * {@code 
     *   PQueueType<Integer> list = PQueues.unit()
                                       .unit("hello")
                                       .then(h->PQueues.functor().map((String v) ->v.length(), h))
                                       .convert(PQueueType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for PQueues
     */
    public static <T,R>Functor<PQueueType.µ> functor(){
        BiFunction<PQueueType<T>,Function<? super T, ? extends R>,PQueueType<R>> map = PQueues::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * PQueueType<String> list = PQueues.unit()
                                     .unit("hello")
                                     .convert(PQueueType::narrowK);
        
        //Arrays.asPQueue("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for PQueues
     */
    public static Unit<PQueueType.µ> unit(){
        return General.unit(PQueues::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PQueueType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asPQueue;
     * 
       PQueues.zippingApplicative()
            .ap(widen(asPQueue(l1(this::multiplyByTwo))),widen(asPQueue(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * PQueueType<Function<Integer,Integer>> listFn =PQueues.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(PQueueType::narrowK);
        
        PQueueType<Integer> list = PQueues.unit()
                                      .unit("hello")
                                      .then(h->PQueues.functor().map((String v) ->v.length(), h))
                                      .then(h->PQueues.zippingApplicative().ap(listFn, h))
                                      .convert(PQueueType::narrowK);
        
        //Arrays.asPQueue("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for PQueues
     */
    public static <T,R> Applicative<PQueueType.µ> zippingApplicative(){
        BiFunction<PQueueType< Function<T, R>>,PQueueType<T>,PQueueType<R>> ap = PQueues::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PQueueType.widen;
     * PQueueType<Integer> list  = PQueues.monad()
                                      .flatMap(i->widen(PQueueX.range(0,i)), widen(Arrays.asPQueue(1,2,3)))
                                      .convert(PQueueType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    PQueueType<Integer> list = PQueues.unit()
                                        .unit("hello")
                                        .then(h->PQueues.monad().flatMap((String v) ->PQueues.unit().unit(v.length()), h))
                                        .convert(PQueueType::narrowK);
        
        //Arrays.asPQueue("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for PQueues
     */
    public static <T,R> Monad<PQueueType.µ> monad(){
  
        BiFunction<Higher<PQueueType.µ,T>,Function<? super T, ? extends Higher<PQueueType.µ,R>>,Higher<PQueueType.µ,R>> flatMap = PQueues::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  PQueueType<String> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PQueueType::narrowK);
        
       //Arrays.asPQueue("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<PQueueType.µ> monadZero(){
        
        return General.monadZero(monad(), PQueueType.widen(PQueueX.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  PQueueType<Integer> list = PQueues.<Integer>monadPlus()
                                      .plus(PQueueType.widen(Arrays.asPQueue()), PQueueType.widen(Arrays.asPQueue(10)))
                                      .convert(PQueueType::narrowK);
        //Arrays.asPQueue(10))
     * 
     * }
     * </pre>
     * @return Type class for combining PQueues by concatenation
     */
    public static <T> MonadPlus<PQueueType.µ,T> monadPlus(){
        Monoid<PQueueType<T>> m = Monoid.of(PQueueType.widen(PQueueX.empty()), PQueues::concat);
        Monoid<Higher<PQueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<PQueueType<Integer>> m = Monoid.of(PQueueType.widen(Arrays.asPQueue()), (a,b)->a.isEmpty() ? b : a);
        PQueueType<Integer> list = PQueues.<Integer>monadPlus(m)
                                      .plus(PQueueType.widen(Arrays.asPQueue(5)), PQueueType.widen(Arrays.asPQueue(10)))
                                      .convert(PQueueType::narrowK);
        //Arrays.asPQueue(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining PQueues
     * @return Type class for combining PQueues
     */
    public static <T> MonadPlus<PQueueType.µ,T> monadPlus(Monoid<PQueueType<T>> m){
        Monoid<Higher<PQueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<PQueueType.µ> traverse(){
        BiFunction<Applicative<C2>,PQueueType<Higher<C2, T>>,Higher<C2, PQueueType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,PQueueType<T>> identity = ap.unit(PQueueType.widen(PQueueX.empty()));

            BiFunction<Higher<C2,PQueueType<T>>,Higher<C2,T>,Higher<C2,PQueueType<T>>> combineToPQueue =   (acc,next) -> ap.apBiFn(ap.unit((a,b) ->PQueueType.widen(a.plus(b))),acc,next);

            BinaryOperator<Higher<C2,PQueueType<T>>> combinePQueues = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> PQueueType.widen(l1.plusAll(l2))),a,b); ;  

            return list.stream()
                      .reduce(identity,
                              combineToPQueue,
                              combinePQueues);  

   
        };
        BiFunction<Applicative<C2>,Higher<PQueueType.µ,Higher<C2, T>>,Higher<C2, Higher<PQueueType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> PQueueType.widen2(sequenceFn.apply(a, PQueueType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = PQueues.foldable()
                        .foldLeft(0, (a,b)->a+b, PQueueType.widen(Arrays.asPQueue(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<PQueueType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<PQueueType.µ,T>,T> foldRightFn =  (m,l)-> PQueueX.fromIterable(PQueueType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<PQueueType.µ,T>,T> foldLeftFn = (m,l)-> PQueueX.fromIterable(PQueueType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> PQueueType<T> concat(PQueue<T> l1, PQueue<T> l2){
       
        return PQueueType.widen(l1.plusAll(l2));
    }
    private <T> PQueueType<T> of(T value){
        return PQueueType.widen(PQueueX.of(value));
    }
    private static <T,R> PQueueType<R> ap(PQueueType<Function< T, R>> lt,  PQueueType<T> list){
        return PQueueType.widen(PQueueX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<PQueueType.µ,R> flatMap( Higher<PQueueType.µ,T> lt, Function<? super T, ? extends  Higher<PQueueType.µ,R>> fn){
        return PQueueType.widen(PQueueX.fromIterable(PQueueType.narrowK(lt)).flatMap(fn.andThen(PQueueType::narrowK)));
    }
    private static <T,R> PQueueType<R> map(PQueueType<T> lt, Function<? super T, ? extends R> fn){
        return PQueueType.widen(PQueueX.fromIterable(lt).map(fn));
    }
}
