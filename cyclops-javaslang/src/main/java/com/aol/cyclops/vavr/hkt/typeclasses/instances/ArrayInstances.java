package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import com.aol.cyclops.vavr.FromCyclopsReact;
import com.aol.cyclops.vavr.hkt.ArrayKind;
import com.aol.cyclops2.hkt.Higher;
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
     *  ArrayKind<Integer> list = Arrays.functor().map(i->i*2, ArrayKind.widen(Arrays.asArray(1,2,3));
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
     *   ArrayKind<Integer> list = Arrays.unit()
                                       .unit("hello")
                                       .then(h->Arrays.functor().map((String v) ->v.length(), h))
                                       .convert(ArrayKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Arrays
     */
    public static <T,R>Functor<ArrayKind.µ> functor(){
        BiFunction<ArrayKind<T>,Function<? super T, ? extends R>,ArrayKind<R>> map = ArrayInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * ArrayKind<String> list = Arrays.unit()
                                     .unit("hello")
                                     .convert(ArrayKind::narrowK);
        
        //Arrays.asArray("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Arrays
     */
    public static <T> Pure<ArrayKind.µ> unit(){
        return General.<ArrayKind.µ,T>unit(ArrayKind::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ArrayKind.widen;
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
     * ArrayKind<Function<Integer,Integer>> listFn =Arrays.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(ArrayKind::narrowK);
        
        ArrayKind<Integer> list = Arrays.unit()
                                      .unit("hello")
                                      .then(h->Arrays.functor().map((String v) ->v.length(), h))
                                      .then(h->Arrays.zippingApplicative().ap(listFn, h))
                                      .convert(ArrayKind::narrowK);
        
        //Arrays.asArray("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Arrays
     */
    public static <T,R> Applicative<ArrayKind.µ> zippingApplicative(){
        BiFunction<ArrayKind< Function<T, R>>,ArrayKind<T>,ArrayKind<R>> ap = ArrayInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ArrayKind.widen;
     * ArrayKind<Integer> list  = Arrays.monad()
                                      .flatMap(i->widen(ArrayX.range(0,i)), widen(Arrays.asArray(1,2,3)))
                                      .convert(ArrayKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    ArrayKind<Integer> list = Arrays.unit()
                                        .unit("hello")
                                        .then(h->Arrays.monad().flatMap((String v) ->Arrays.unit().unit(v.length()), h))
                                        .convert(ArrayKind::narrowK);
        
        //Arrays.asArray("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Arrays
     */
    public static <T,R> Monad<ArrayKind.µ> monad(){
  
        BiFunction<Higher<ArrayKind.µ,T>,Function<? super T, ? extends Higher<ArrayKind.µ,R>>,Higher<ArrayKind.µ,R>> flatMap = ArrayInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  ArrayKind<String> list = Arrays.unit()
                                     .unit("hello")
                                     .then(h->Arrays.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ArrayKind::narrowK);
        
       //Arrays.asArray("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<ArrayKind.µ> monadZero(){
        
        return General.monadZero(monad(), ArrayKind.widen(Array.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  ArrayKind<Integer> list = Arrays.<Integer>monadPlus()
                                      .plus(ArrayKind.widen(Arrays.asArray()), ArrayKind.widen(Arrays.asArray(10)))
                                      .convert(ArrayKind::narrowK);
        //Arrays.asArray(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Arrays by concatenation
     */
    public static <T> MonadPlus<ArrayKind.µ> monadPlus(){
        Monoid<ArrayKind<T>> m = Monoid.of(ArrayKind.widen(Array.empty()), ArrayInstances::concat);
        Monoid<Higher<ArrayKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<ArrayKind<Integer>> m = Monoid.of(ArrayKind.widen(Arrays.asArray()), (a,b)->a.isEmpty() ? b : a);
        ArrayKind<Integer> list = Arrays.<Integer>monadPlus(m)
                                      .plus(ArrayKind.widen(Arrays.asArray(5)), ArrayKind.widen(Arrays.asArray(10)))
                                      .convert(ArrayKind::narrowK);
        //Arrays.asArray(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Arrays
     * @return Type class for combining Arrays
     */
    public static <T> MonadPlus<ArrayKind.µ> monadPlus(Monoid<ArrayKind<T>> m){
        Monoid<Higher<ArrayKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ArrayKind.µ> traverse(){
     
        BiFunction<Applicative<C2>,ArrayKind<Higher<C2, T>>,Higher<C2, ArrayKind<T>>> sequenceFn = (ap, list) -> {
        
            Higher<C2,ArrayKind<T>> identity = ap.unit(ArrayKind.widen(Array.empty()));

            BiFunction<Higher<C2,ArrayKind<T>>,Higher<C2,T>,Higher<C2,ArrayKind<T>>> combineToArray =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> ArrayKind.widen(ArrayKind.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,ArrayKind<T>>> combineArrays = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> ArrayKind.widen(ArrayKind.narrow(l1).appendAll(l2))),a,b); ;

            return ReactiveSeq.fromIterable(ArrayKind.narrow(list))
                      .reduce(identity,
                              combineToArray,
                              combineArrays);  

   
        };
        BiFunction<Applicative<C2>,Higher<ArrayKind.µ,Higher<C2, T>>,Higher<C2, Higher<ArrayKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> ArrayKind.widen2(sequenceFn.apply(a, ArrayKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Arrays.foldable()
                        .foldLeft(0, (a,b)->a+b, ArrayKind.widen(Arrays.asArray(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ArrayKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ArrayKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromIterable(ArrayKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ArrayKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromIterable(ArrayKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ArrayKind<T> concat(ArrayKind<T> l1, ArrayKind<T> l2){

        return ArrayKind.widen(l1.appendAll(ArrayKind.narrow(l2)));
       
    }
    
    private static <T,R> ArrayKind<R> ap(ArrayKind<Function< T, R>> lt, ArrayKind<T> list){
        return ArrayKind.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a, b)->a.apply(b))).toArray());
    }
    private static <T,R> Higher<ArrayKind.µ,R> flatMap(Higher<ArrayKind.µ,T> lt, Function<? super T, ? extends  Higher<ArrayKind.µ,R>> fn){
        return ArrayKind.widen(ArrayKind.narrow(lt).flatMap(fn.andThen(ArrayKind::narrow)));
    }
    private static <T,R> ArrayKind<R> map(ArrayKind<T> lt, Function<? super T, ? extends R> fn){
        return ArrayKind.widen(ArrayKind.narrow(lt).map(in->fn.apply(in)));
    }
}
