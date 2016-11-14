package com.aol.cyclops.hkt.instances.pcollections;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.pcollections.PVector;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.persistent.PVectorX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.pcollections.PVectorType;
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
 * Companion class for creating Type Class instances for working with PVectors
 * @author johnmcclean
 *
 */
@UtilityClass
public class PVectors {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  PVectorType<Integer> list = PVectors.functor().map(i->i*2, PVectorType.widen(Arrays.asPVector(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with PVectors
     * <pre>
     * {@code 
     *   PVectorType<Integer> list = PVectors.unit()
                                       .unit("hello")
                                       .then(h->PVectors.functor().map((String v) ->v.length(), h))
                                       .convert(PVectorType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for PVectors
     */
    public static <T,R>Functor<PVectorType.µ> functor(){
        BiFunction<PVectorType<T>,Function<? super T, ? extends R>,PVectorType<R>> map = PVectors::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * PVectorType<String> list = PVectors.unit()
                                     .unit("hello")
                                     .convert(PVectorType::narrowK);
        
        //Arrays.asPVector("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for PVectors
     */
    public static Unit<PVectorType.µ> unit(){
        return General.unit(PVectors::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PVectorType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asPVector;
     * 
       PVectors.zippingApplicative()
            .ap(widen(asPVector(l1(this::multiplyByTwo))),widen(asPVector(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * PVectorType<Function<Integer,Integer>> listFn =PVectors.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(PVectorType::narrowK);
        
        PVectorType<Integer> list = PVectors.unit()
                                      .unit("hello")
                                      .then(h->PVectors.functor().map((String v) ->v.length(), h))
                                      .then(h->PVectors.zippingApplicative().ap(listFn, h))
                                      .convert(PVectorType::narrowK);
        
        //Arrays.asPVector("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for PVectors
     */
    public static <T,R> Applicative<PVectorType.µ> zippingApplicative(){
        BiFunction<PVectorType< Function<T, R>>,PVectorType<T>,PVectorType<R>> ap = PVectors::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.PVectorType.widen;
     * PVectorType<Integer> list  = PVectors.monad()
                                      .flatMap(i->widen(PVectorX.range(0,i)), widen(Arrays.asPVector(1,2,3)))
                                      .convert(PVectorType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    PVectorType<Integer> list = PVectors.unit()
                                        .unit("hello")
                                        .then(h->PVectors.monad().flatMap((String v) ->PVectors.unit().unit(v.length()), h))
                                        .convert(PVectorType::narrowK);
        
        //Arrays.asPVector("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for PVectors
     */
    public static <T,R> Monad<PVectorType.µ> monad(){
  
        BiFunction<Higher<PVectorType.µ,T>,Function<? super T, ? extends Higher<PVectorType.µ,R>>,Higher<PVectorType.µ,R>> flatMap = PVectors::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  PVectorType<String> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PVectorType::narrowK);
        
       //Arrays.asPVector("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<PVectorType.µ> monadZero(){
        
        return General.monadZero(monad(), PVectorType.widen(PVectorX.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  PVectorType<Integer> list = PVectors.<Integer>monadPlus()
                                      .plus(PVectorType.widen(Arrays.asPVector()), PVectorType.widen(Arrays.asPVector(10)))
                                      .convert(PVectorType::narrowK);
        //Arrays.asPVector(10))
     * 
     * }
     * </pre>
     * @return Type class for combining PVectors by concatenation
     */
    public static <T> MonadPlus<PVectorType.µ,T> monadPlus(){
        Monoid<PVectorType<T>> m = Monoid.of(PVectorType.widen(PVectorX.empty()), PVectors::concat);
        Monoid<Higher<PVectorType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<PVectorType<Integer>> m = Monoid.of(PVectorType.widen(Arrays.asPVector()), (a,b)->a.isEmpty() ? b : a);
        PVectorType<Integer> list = PVectors.<Integer>monadPlus(m)
                                      .plus(PVectorType.widen(Arrays.asPVector(5)), PVectorType.widen(Arrays.asPVector(10)))
                                      .convert(PVectorType::narrowK);
        //Arrays.asPVector(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining PVectors
     * @return Type class for combining PVectors
     */
    public static <T> MonadPlus<PVectorType.µ,T> monadPlus(Monoid<PVectorType<T>> m){
        Monoid<Higher<PVectorType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<PVectorType.µ> traverse(){
        BiFunction<Applicative<C2>,PVectorType<Higher<C2, T>>,Higher<C2, PVectorType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,PVectorType<T>> identity = ap.unit(PVectorType.widen(PVectorX.empty()));

            BiFunction<Higher<C2,PVectorType<T>>,Higher<C2,T>,Higher<C2,PVectorType<T>>> combineToPVector =   (acc,next) -> ap.apBiFn(ap.unit((a,b) ->PVectorType.widen(a.plus(b))),acc,next);

            BinaryOperator<Higher<C2,PVectorType<T>>> combinePVectors = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> PVectorType.widen(l1.plusAll(l2))),a,b); ;  

            return list.stream()
                      .reduce(identity,
                              combineToPVector,
                              combinePVectors);  

   
        };
        BiFunction<Applicative<C2>,Higher<PVectorType.µ,Higher<C2, T>>,Higher<C2, Higher<PVectorType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> PVectorType.widen2(sequenceFn.apply(a, PVectorType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = PVectors.foldable()
                        .foldLeft(0, (a,b)->a+b, PVectorType.widen(Arrays.asPVector(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<PVectorType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<PVectorType.µ,T>,T> foldRightFn =  (m,l)-> PVectorX.fromIterable(PVectorType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<PVectorType.µ,T>,T> foldLeftFn = (m,l)-> PVectorX.fromIterable(PVectorType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> PVectorType<T> concat(PVector<T> l1, PVector<T> l2){
       
        return PVectorType.widen(l1.plusAll(l2));
    }
    private <T> PVectorType<T> of(T value){
        return PVectorType.widen(PVectorX.of(value));
    }
    private static <T,R> PVectorType<R> ap(PVectorType<Function< T, R>> lt,  PVectorType<T> list){
        return PVectorType.widen(PVectorX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<PVectorType.µ,R> flatMap( Higher<PVectorType.µ,T> lt, Function<? super T, ? extends  Higher<PVectorType.µ,R>> fn){
        return PVectorType.widen(PVectorX.fromIterable(PVectorType.narrowK(lt)).flatMap(fn.andThen(PVectorType::narrowK)));
    }
    private static <T,R> PVectorType<R> map(PVectorType<T> lt, Function<? super T, ? extends R> fn){
        return PVectorType.widen(PVectorX.fromIterable(lt).map(fn));
    }
}
