package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import com.aol.cyclops.javaslang.FromCyclopsReact;
import com.aol.cyclops.javaslang.hkt.ArrayType;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Unit;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import javaslang.collection.Array;
import lombok.experimental.UtilityClass;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Companion class for creating Type Class instances for working with Arrays
 * @author johnmcclean
 *
 */
@UtilityClass
public class ArrayInstances {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  ArrayType<Integer> list = Arrays.functor().map(i->i*2, ArrayType.widen(Arrays.asArray(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Arrays
     * <pre>
     * {@code 
     *   ArrayType<Integer> list = Arrays.unit()
                                       .unit("hello")
                                       .then(h->Arrays.functor().map((String v) ->v.length(), h))
                                       .convert(ArrayType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Arrays
     */
    public static <T,R>Functor<ArrayType.µ> functor(){
        BiFunction<ArrayType<T>,Function<? super T, ? extends R>,ArrayType<R>> map = ArrayInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * ArrayType<String> list = Arrays.unit()
                                     .unit("hello")
                                     .convert(ArrayType::narrowK);
        
        //Arrays.asArray("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Arrays
     */
    public static <T> Pure<ArrayType.µ> unit(){
        return General.<ArrayType.µ,T>unit(ArrayType::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ArrayType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asArray;
     * 
       Arrays.zippingApplicative()
            .ap(widen(asArray(l1(this::multiplyByTwo))),widen(asArray(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * ArrayType<Function<Integer,Integer>> listFn =Arrays.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(ArrayType::narrowK);
        
        ArrayType<Integer> list = Arrays.unit()
                                      .unit("hello")
                                      .then(h->Arrays.functor().map((String v) ->v.length(), h))
                                      .then(h->Arrays.zippingApplicative().ap(listFn, h))
                                      .convert(ArrayType::narrowK);
        
        //Arrays.asArray("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Arrays
     */
    public static <T,R> Applicative<ArrayType.µ> zippingApplicative(){
        BiFunction<ArrayType< Function<T, R>>,ArrayType<T>,ArrayType<R>> ap = ArrayInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ArrayType.widen;
     * ArrayType<Integer> list  = Arrays.monad()
                                      .flatMap(i->widen(ArrayX.range(0,i)), widen(Arrays.asArray(1,2,3)))
                                      .convert(ArrayType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    ArrayType<Integer> list = Arrays.unit()
                                        .unit("hello")
                                        .then(h->Arrays.monad().flatMap((String v) ->Arrays.unit().unit(v.length()), h))
                                        .convert(ArrayType::narrowK);
        
        //Arrays.asArray("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Arrays
     */
    public static <T,R> Monad<ArrayType.µ> monad(){
  
        BiFunction<Higher<ArrayType.µ,T>,Function<? super T, ? extends Higher<ArrayType.µ,R>>,Higher<ArrayType.µ,R>> flatMap = ArrayInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  ArrayType<String> list = Arrays.unit()
                                     .unit("hello")
                                     .then(h->Arrays.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ArrayType::narrowK);
        
       //Arrays.asArray("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<ArrayType.µ> monadZero(){
        
        return General.monadZero(monad(), ArrayType.widen(Array.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  ArrayType<Integer> list = Arrays.<Integer>monadPlus()
                                      .plus(ArrayType.widen(Arrays.asArray()), ArrayType.widen(Arrays.asArray(10)))
                                      .convert(ArrayType::narrowK);
        //Arrays.asArray(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Arrays by concatenation
     */
    public static <T> MonadPlus<ArrayType.µ> monadPlus(){
        Monoid<ArrayType<T>> m = Monoid.of(ArrayType.widen(Array.empty()), ArrayInstances::concat);
        Monoid<Higher<ArrayType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<ArrayType<Integer>> m = Monoid.of(ArrayType.widen(Arrays.asArray()), (a,b)->a.isEmpty() ? b : a);
        ArrayType<Integer> list = Arrays.<Integer>monadPlus(m)
                                      .plus(ArrayType.widen(Arrays.asArray(5)), ArrayType.widen(Arrays.asArray(10)))
                                      .convert(ArrayType::narrowK);
        //Arrays.asArray(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Arrays
     * @return Type class for combining Arrays
     */
    public static <T> MonadPlus<ArrayType.µ> monadPlus(Monoid<ArrayType<T>> m){
        Monoid<Higher<ArrayType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ArrayType.µ> traverse(){
     
        BiFunction<Applicative<C2>,ArrayType<Higher<C2, T>>,Higher<C2, ArrayType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,ArrayType<T>> identity = ap.unit(ArrayType.widen(Array.empty()));

            BiFunction<Higher<C2,ArrayType<T>>,Higher<C2,T>,Higher<C2,ArrayType<T>>> combineToArray =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> ArrayType.widen(ArrayType.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,ArrayType<T>>> combineArrays = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> ArrayType.widen(ArrayType.narrow(l1).appendAll(l2))),a,b); ;  

            return ReactiveSeq.fromIterable(ArrayType.narrow(list))
                      .reduce(identity,
                              combineToArray,
                              combineArrays);  

   
        };
        BiFunction<Applicative<C2>,Higher<ArrayType.µ,Higher<C2, T>>,Higher<C2, Higher<ArrayType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> ArrayType.widen2(sequenceFn.apply(a, ArrayType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Arrays.foldable()
                        .foldLeft(0, (a,b)->a+b, ArrayType.widen(Arrays.asArray(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ArrayType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ArrayType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromIterable(ArrayType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ArrayType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromIterable(ArrayType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ArrayType<T> concat(ArrayType<T> l1, ArrayType<T> l2){

        return ArrayType.widen(l1.appendAll(ArrayType.narrow(l2)));
       
    }
    
    private static <T,R> ArrayType<R> ap(ArrayType<Function< T, R>> lt,  ArrayType<T> list){
        return ArrayType.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a,b)->a.apply(b))).toArray());
    }
    private static <T,R> Higher<ArrayType.µ,R> flatMap( Higher<ArrayType.µ,T> lt, Function<? super T, ? extends  Higher<ArrayType.µ,R>> fn){
        return ArrayType.widen(ArrayType.narrow(lt).flatMap(fn.andThen(ArrayType::narrow)));
    }
    private static <T,R> ArrayType<R> map(ArrayType<T> lt, Function<? super T, ? extends R> fn){
        return ArrayType.widen(ArrayType.narrow(lt).map(in->fn.apply(in)));
    }
}
