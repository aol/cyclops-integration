package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.functionaljava.hkt.StreamKind;


import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import fj.data.Stream;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Streams
 * @author johnmcclean
 *
 */
@UtilityClass
public class StreamInstances {

    
    /**
     * 
     * Transform a stream, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  StreamKind<Integer> stream = Streams.functor().map(i->i*2, StreamKind.widen(Arrays.asStream(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Streams
     * <pre>
     * {@code 
     *   StreamKind<Integer> stream = Streams.unit()
                                       .unit("hello")
                                       .then(h->Streams.functor().map((String v) ->v.length(), h))
                                       .convert(StreamKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Streams
     */
    public static <T,R>Functor<StreamKind.µ> functor(){
        BiFunction<StreamKind<T>,Function<? super T, ? extends R>,StreamKind<R>> map = StreamInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * StreamKind<String> stream = Streams.unit()
                                     .unit("hello")
                                     .convert(StreamKind::narrowK);
        
        //Arrays.asStream("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Streams
     */
    public static <T> Pure<StreamKind.µ> unit(){
        return General.<StreamKind.µ,T>unit(StreamInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.StreamKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asStream;
     * 
       Streams.zippingApplicative()
            .ap(widen(asStream(l1(this::multiplyByTwo))),widen(asStream(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * StreamKind<Function<Integer,Integer>> streamFn =Streams.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(StreamKind::narrowK);
        
        StreamKind<Integer> stream = Streams.unit()
                                      .unit("hello")
                                      .then(h->Streams.functor().map((String v) ->v.length(), h))
                                      .then(h->Streams.zippingApplicative().ap(streamFn, h))
                                      .convert(StreamKind::narrowK);
        
        //Arrays.asStream("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Streams
     */
    public static <T,R> Applicative<StreamKind.µ> zippingApplicative(){
        BiFunction<StreamKind< Function<T, R>>,StreamKind<T>,StreamKind<R>> ap = StreamInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.StreamKind.widen;
     * StreamKind<Integer> stream  = Streams.monad()
                                      .flatMap(i->widen(StreamX.range(0,i)), widen(Arrays.asStream(1,2,3)))
                                      .convert(StreamKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    StreamKind<Integer> stream = Streams.unit()
                                        .unit("hello")
                                        .then(h->Streams.monad().flatMap((String v) ->Streams.unit().unit(v.length()), h))
                                        .convert(StreamKind::narrowK);
        
        //Arrays.asStream("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Streams
     */
    public static <T,R> Monad<StreamKind.µ> monad(){
  
        BiFunction<Higher<StreamKind.µ,T>,Function<? super T, ? extends Higher<StreamKind.µ,R>>,Higher<StreamKind.µ,R>> flatMap = StreamInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  StreamKind<String> stream = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(StreamKind::narrowK);
        
       //Arrays.asStream("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<StreamKind.µ> monadZero(){
        
        return General.monadZero(monad(), StreamKind.widen(Stream.stream()));
    }
    /**
     * <pre>
     * {@code 
     *  StreamKind<Integer> stream = Streams.<Integer>monadPlus()
                                      .plus(StreamKind.widen(Arrays.asStream()), StreamKind.widen(Arrays.asStream(10)))
                                      .convert(StreamKind::narrowK);
        //Arrays.asStream(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Streams by concatenation
     */
    public static <T> MonadPlus<StreamKind.µ> monadPlus(){
        Monoid<StreamKind<T>> m = Monoid.of(StreamKind.widen(Stream.stream()), StreamInstances::concat);
        Monoid<Higher<StreamKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<StreamKind<Integer>> m = Monoid.of(StreamKind.widen(Arrays.asStream()), (a,b)->a.isEmpty() ? b : a);
        StreamKind<Integer> stream = Streams.<Integer>monadPlus(m)
                                      .plus(StreamKind.widen(Arrays.asStream(5)), StreamKind.widen(Arrays.asStream(10)))
                                      .convert(StreamKind::narrowK);
        //Arrays.asStream(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Streams
     * @return Type class for combining Streams
     */
    public static <T> MonadPlus<StreamKind.µ> monadPlus(Monoid<StreamKind<T>> m){
        Monoid<Higher<StreamKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<StreamKind.µ> traverse(){
     
        BiFunction<Applicative<C2>,StreamKind<Higher<C2, T>>,Higher<C2, StreamKind<T>>> sequenceFn = (ap, stream) -> {
        
            Higher<C2,StreamKind<T>> identity = ap.unit(StreamKind.widen(Stream.stream()));

            BiFunction<Higher<C2,StreamKind<T>>,Higher<C2,T>,Higher<C2,StreamKind<T>>> combineToStream =
                    (acc,next) -> ap.apBiFn(ap.unit((a,b) -> StreamKind.widen(StreamKind.narrow(a).cons(b))), acc,next);

            BinaryOperator<Higher<C2,StreamKind<T>>> combineStreams = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> StreamKind.widen(StreamKind.narrow(l1).append(StreamKind.narrow(l2)))),a,b); ;
           
            return ReactiveSeq.fromIterable(StreamKind.narrow(stream))
                      .reduce(identity,
                              combineToStream,
                              combineStreams);  

   
        };
        BiFunction<Applicative<C2>,Higher<StreamKind.µ,Higher<C2, T>>,Higher<C2, Higher<StreamKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> StreamKind.widen2(sequenceFn.apply(a, StreamKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }

    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Streams.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamKind.widen(Arrays.asStream(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<StreamKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<StreamKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromIterable(StreamKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<StreamKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromIterable(StreamKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> StreamKind<T> concat(StreamKind<T> l1, StreamKind<T> l2){
        return StreamKind.widen(l1.append(StreamKind.narrow(l2)));
       
    }
    private <T> StreamKind<T> of(T value){
        return StreamKind.widen(Stream.stream(value));
    }
    private static <T,R> StreamKind<R> ap(StreamKind<Function< T, R>> lt, StreamKind<T> stream){
        
        return StreamKind.widen(lt.zipWith(stream.narrow(),(a, b)->a.apply(b)));
    }
    private static <T,R> Higher<StreamKind.µ,R> flatMap(Higher<StreamKind.µ,T> lt, Function<? super T, ? extends  Higher<StreamKind.µ,R>> fn){
        return StreamKind.widen(StreamKind.narrow(lt).bind(in->fn.andThen(StreamKind::narrow).apply(in)));
    }
    private static <T,R> StreamKind<R> map(StreamKind<T> lt, Function<? super T, ? extends R> fn){
        return StreamKind.widen(StreamKind.narrow(lt).map(in->fn.apply(in)));
    }
}
