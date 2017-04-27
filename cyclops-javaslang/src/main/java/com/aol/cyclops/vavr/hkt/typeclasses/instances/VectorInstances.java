package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.VectorKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.vavr.FromCyclopsReact;

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
     *  VectorKind<Integer> list = Vectors.functor().map(i->i*2, VectorKind.widen(Arrays.asVector(1,2,3));
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
     *   VectorKind<Integer> list = Vectors.unit()
                                       .unit("hello")
                                       .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                       .convert(VectorKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Vectors
     */
    public static <T,R>Functor<VectorKind.µ> functor(){
        BiFunction<VectorKind<T>,Function<? super T, ? extends R>,VectorKind<R>> map = VectorInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * VectorKind<String> list = Vectors.unit()
                                     .unit("hello")
                                     .convert(VectorKind::narrowK);
        
        //Arrays.asVector("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Vectors
     */
    public static <T> Pure<VectorKind.µ> unit(){
        return General.<VectorKind.µ,T>unit(VectorKind::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.VectorKind.widen;
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
     * VectorKind<Function<Integer,Integer>> listFn =Vectors.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(VectorKind::narrowK);
        
        VectorKind<Integer> list = Vectors.unit()
                                      .unit("hello")
                                      .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                      .then(h->Vectors.zippingApplicative().ap(listFn, h))
                                      .convert(VectorKind::narrowK);
        
        //Arrays.asVector("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Vectors
     */
    public static <T,R> Applicative<VectorKind.µ> zippingApplicative(){
        BiFunction<VectorKind< Function<T, R>>,VectorKind<T>,VectorKind<R>> ap = VectorInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.VectorKind.widen;
     * VectorKind<Integer> list  = Vectors.monad()
                                      .flatMap(i->widen(VectorX.range(0,i)), widen(Arrays.asVector(1,2,3)))
                                      .convert(VectorKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    VectorKind<Integer> list = Vectors.unit()
                                        .unit("hello")
                                        .then(h->Vectors.monad().flatMap((String v) ->Vectors.unit().unit(v.length()), h))
                                        .convert(VectorKind::narrowK);
        
        //Arrays.asVector("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Vectors
     */
    public static <T,R> Monad<VectorKind.µ> monad(){
  
        BiFunction<Higher<VectorKind.µ,T>,Function<? super T, ? extends Higher<VectorKind.µ,R>>,Higher<VectorKind.µ,R>> flatMap = VectorInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  VectorKind<String> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(VectorKind::narrowK);
        
       //Arrays.asVector("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<VectorKind.µ> monadZero(){
        
        return General.monadZero(monad(), VectorKind.widen(Vector.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  VectorKind<Integer> list = Vectors.<Integer>monadPlus()
                                      .plus(VectorKind.widen(Arrays.asVector()), VectorKind.widen(Arrays.asVector(10)))
                                      .convert(VectorKind::narrowK);
        //Arrays.asVector(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Vectors by concatenation
     */
    public static <T> MonadPlus<VectorKind.µ> monadPlus(){
        Monoid<VectorKind<T>> m = Monoid.of(VectorKind.widen(Vector.empty()), VectorInstances::concat);
        Monoid<Higher<VectorKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<VectorKind<Integer>> m = Monoid.of(VectorKind.widen(Arrays.asVector()), (a,b)->a.isEmpty() ? b : a);
        VectorKind<Integer> list = Vectors.<Integer>monadPlus(m)
                                      .plus(VectorKind.widen(Arrays.asVector(5)), VectorKind.widen(Arrays.asVector(10)))
                                      .convert(VectorKind::narrowK);
        //Arrays.asVector(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Vectors
     * @return Type class for combining Vectors
     */
    public static <T> MonadPlus<VectorKind.µ> monadPlus(Monoid<VectorKind<T>> m){
        Monoid<Higher<VectorKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<VectorKind.µ> traverse(){
     
        BiFunction<Applicative<C2>,VectorKind<Higher<C2, T>>,Higher<C2, VectorKind<T>>> sequenceFn = (ap, list) -> {
        
            Higher<C2,VectorKind<T>> identity = ap.unit(VectorKind.widen(Vector.empty()));

            BiFunction<Higher<C2,VectorKind<T>>,Higher<C2,T>,Higher<C2,VectorKind<T>>> combineToVector =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> VectorKind.widen(VectorKind.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,VectorKind<T>>> combineVectors = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> VectorKind.widen(VectorKind.narrow(l1).appendAll(l2))),a,b); ;

            return ReactiveSeq.fromIterable(VectorKind.narrow(list))
                      .reduce(identity,
                              combineToVector,
                              combineVectors);  

   
        };
        BiFunction<Applicative<C2>,Higher<VectorKind.µ,Higher<C2, T>>,Higher<C2, Higher<VectorKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> VectorKind.widen2(sequenceFn.apply(a, VectorKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Vectors.foldable()
                        .foldLeft(0, (a,b)->a+b, VectorKind.widen(Arrays.asVector(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<VectorKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<VectorKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromIterable(VectorKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<VectorKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromIterable(VectorKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> VectorKind<T> concat(VectorKind<T> l1, VectorKind<T> l2){

        return VectorKind.widen(l1.appendAll(VectorKind.narrow(l2)));
       
    }
    
    private static <T,R> VectorKind<R> ap(VectorKind<Function< T, R>> lt, VectorKind<T> list){
        return VectorKind.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a, b)->a.apply(b))).toVector());
    }
    private static <T,R> Higher<VectorKind.µ,R> flatMap(Higher<VectorKind.µ,T> lt, Function<? super T, ? extends  Higher<VectorKind.µ,R>> fn){
        return VectorKind.widen(VectorKind.narrow(lt).flatMap(fn.andThen(VectorKind::narrow)));
    }
    private static <T,R> VectorKind<R> map(VectorKind<T> lt, Function<? super T, ? extends R> fn){
        return VectorKind.widen(VectorKind.narrow(lt).map(in->fn.apply(in)));
    }
}
