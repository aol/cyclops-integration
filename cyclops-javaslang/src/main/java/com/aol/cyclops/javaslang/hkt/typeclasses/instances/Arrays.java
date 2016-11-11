package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
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
import com.aol.cyclops.javaslang.FromCyclopsReact;
import com.aol.cyclops.javaslang.hkt.ArrayType;

import javaslang.collection.Array;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Arrays
 * @author johnmcclean
 *
 */
@UtilityClass
public class Arrays {

    public static void main(String[] args){
        Array<Integer> small = Array.of(1,2,3);
        ArrayType<Integer> list = Arrays.functor()
                                     .map(i->i*2, ArrayType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->ArrayType.widen(Array.of(1)), h))
                                     .convert(ArrayType::narrowK);
          ArrayType<Integer> string = list.convert(ArrayType::narrowK);
                    
        System.out.println(Arrays.functor().map(i->i*2, ArrayType.widen(small)));
    }
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
        BiFunction<ArrayType<T>,Function<? super T, ? extends R>,ArrayType<R>> map = Arrays::map;
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
    public static Unit<ArrayType.µ> unit(){
        return General.unit(ArrayType::of);
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
        BiFunction<ArrayType< Function<T, R>>,ArrayType<T>,ArrayType<R>> ap = Arrays::ap;
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
  
        BiFunction<Higher<ArrayType.µ,T>,Function<? super T, ? extends Higher<ArrayType.µ,R>>,Higher<ArrayType.µ,R>> flatMap = Arrays::flatMap;
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
    public static <T> MonadPlus<ArrayType.µ,T> monadPlus(){
        Monoid<ArrayType<T>> m = Monoid.of(ArrayType.widen(Array.empty()), Arrays::concat);
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
    public static <T> MonadPlus<ArrayType.µ,T> monadPlus(Monoid<ArrayType<T>> m){
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
