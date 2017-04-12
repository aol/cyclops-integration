package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.javaslang.FromCyclopsReact;
import com.aol.cyclops.javaslang.hkt.VectorType;

import javaslang.collection.Vector;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Vectors
 * @author johnmcclean
 *
 */
@UtilityClass
public class VectorInstances {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  VectorType<Integer> list = Vectors.functor().map(i->i*2, VectorType.widen(Arrays.asVector(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Vectors
     * <pre>
     * {@code 
     *   VectorType<Integer> list = Vectors.unit()
                                       .unit("hello")
                                       .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                       .convert(VectorType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Vectors
     */
    public static <T,R>Functor<VectorType.µ> functor(){
        BiFunction<VectorType<T>,Function<? super T, ? extends R>,VectorType<R>> map = VectorInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * VectorType<String> list = Vectors.unit()
                                     .unit("hello")
                                     .convert(VectorType::narrowK);
        
        //Arrays.asVector("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Vectors
     */
    public static <T> Unit<VectorType.µ> unit(){
        return General.<VectorType.µ,T>unit(VectorType::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.VectorType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asVector;
     * 
       Vectors.zippingApplicative()
            .ap(widen(asVector(l1(this::multiplyByTwo))),widen(asVector(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * VectorType<Function<Integer,Integer>> listFn =Vectors.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(VectorType::narrowK);
        
        VectorType<Integer> list = Vectors.unit()
                                      .unit("hello")
                                      .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                      .then(h->Vectors.zippingApplicative().ap(listFn, h))
                                      .convert(VectorType::narrowK);
        
        //Arrays.asVector("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Vectors
     */
    public static <T,R> Applicative<VectorType.µ> zippingApplicative(){
        BiFunction<VectorType< Function<T, R>>,VectorType<T>,VectorType<R>> ap = VectorInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.VectorType.widen;
     * VectorType<Integer> list  = Vectors.monad()
                                      .flatMap(i->widen(VectorX.range(0,i)), widen(Arrays.asVector(1,2,3)))
                                      .convert(VectorType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    VectorType<Integer> list = Vectors.unit()
                                        .unit("hello")
                                        .then(h->Vectors.monad().flatMap((String v) ->Vectors.unit().unit(v.length()), h))
                                        .convert(VectorType::narrowK);
        
        //Arrays.asVector("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Vectors
     */
    public static <T,R> Monad<VectorType.µ> monad(){
  
        BiFunction<Higher<VectorType.µ,T>,Function<? super T, ? extends Higher<VectorType.µ,R>>,Higher<VectorType.µ,R>> flatMap = VectorInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  VectorType<String> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(VectorType::narrowK);
        
       //Arrays.asVector("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<VectorType.µ> monadZero(){
        
        return General.monadZero(monad(), VectorType.widen(Vector.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  VectorType<Integer> list = Vectors.<Integer>monadPlus()
                                      .plus(VectorType.widen(Arrays.asVector()), VectorType.widen(Arrays.asVector(10)))
                                      .convert(VectorType::narrowK);
        //Arrays.asVector(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Vectors by concatenation
     */
    public static <T> MonadPlus<VectorType.µ> monadPlus(){
        Monoid<VectorType<T>> m = Monoid.of(VectorType.widen(Vector.empty()), VectorInstances::concat);
        Monoid<Higher<VectorType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<VectorType<Integer>> m = Monoid.of(VectorType.widen(Arrays.asVector()), (a,b)->a.isEmpty() ? b : a);
        VectorType<Integer> list = Vectors.<Integer>monadPlus(m)
                                      .plus(VectorType.widen(Arrays.asVector(5)), VectorType.widen(Arrays.asVector(10)))
                                      .convert(VectorType::narrowK);
        //Arrays.asVector(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Vectors
     * @return Type class for combining Vectors
     */
    public static <T> MonadPlus<VectorType.µ> monadPlus(Monoid<VectorType<T>> m){
        Monoid<Higher<VectorType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<VectorType.µ> traverse(){
     
        BiFunction<Applicative<C2>,VectorType<Higher<C2, T>>,Higher<C2, VectorType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,VectorType<T>> identity = ap.unit(VectorType.widen(Vector.empty()));

            BiFunction<Higher<C2,VectorType<T>>,Higher<C2,T>,Higher<C2,VectorType<T>>> combineToVector =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> VectorType.widen(VectorType.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,VectorType<T>>> combineVectors = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> VectorType.widen(VectorType.narrow(l1).appendAll(l2))),a,b); ;  

            return ReactiveSeq.fromIterable(VectorType.narrow(list))
                      .reduce(identity,
                              combineToVector,
                              combineVectors);  

   
        };
        BiFunction<Applicative<C2>,Higher<VectorType.µ,Higher<C2, T>>,Higher<C2, Higher<VectorType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> VectorType.widen2(sequenceFn.apply(a, VectorType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Vectors.foldable()
                        .foldLeft(0, (a,b)->a+b, VectorType.widen(Arrays.asVector(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<VectorType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<VectorType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromIterable(VectorType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<VectorType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromIterable(VectorType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> VectorType<T> concat(VectorType<T> l1, VectorType<T> l2){

        return VectorType.widen(l1.appendAll(VectorType.narrow(l2)));
       
    }
    
    private static <T,R> VectorType<R> ap(VectorType<Function< T, R>> lt,  VectorType<T> list){
        return VectorType.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a,b)->a.apply(b))).toVector());
    }
    private static <T,R> Higher<VectorType.µ,R> flatMap( Higher<VectorType.µ,T> lt, Function<? super T, ? extends  Higher<VectorType.µ,R>> fn){
        return VectorType.widen(VectorType.narrow(lt).flatMap(fn.andThen(VectorType::narrow)));
    }
    private static <T,R> VectorType<R> map(VectorType<T> lt, Function<? super T, ? extends R> fn){
        return VectorType.widen(VectorType.narrow(lt).map(in->fn.apply(in)));
    }
}
