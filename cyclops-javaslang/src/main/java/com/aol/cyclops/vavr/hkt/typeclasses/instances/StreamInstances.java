package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.vavr.hkt.StreamKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;

import javaslang.collection.Stream;
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
     *  StreamKind<Integer> stream = Streams.functor().map(i->i*2, StreamKind.widen(Stream.of(1,2,3));
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
        
        //Stream.of("hello"))
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
     * 
       Streams.zippingApplicative()
            .ap(widen(Stream.of(l1(this::multiplyByTwo))),widen(Stream.of(1,2,3)));
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
        
        //Stream.of("hello".length()*2))
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
                                      .flatMap(i->widen(StreamX.range(0,i)), widen(Stream.of(1,2,3)))
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
        
        //Stream.of("hello".length())
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
        
       //Stream.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<StreamKind.µ> monadZero(){
        BiFunction<Higher<StreamKind.µ,T>,Predicate<? super T>,Higher<StreamKind.µ,T>> filter = StreamInstances::filter;
        Supplier<Higher<StreamKind.µ, T>> zero = ()-> StreamKind.widen(Stream.empty());
        return General.<StreamKind.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  StreamKind<Integer> stream = Streams.<Integer>monadPlus()
                                      .plus(StreamKind.widen(Stream.of()), StreamKind.widen(Stream.of(10)))
                                      .convert(StreamKind::narrowK);
        //Stream.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Streams by concatenation
     */
    public static <T> MonadPlus<StreamKind.µ> monadPlus(){
        Monoid<StreamKind<T>> m = Monoid.of(StreamKind.widen(Stream.<T>empty()), StreamInstances::concat);
        Monoid<Higher<StreamKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<StreamKind<Integer>> m = Monoid.of(StreamKind.widen(Stream.of()), (a,b)->a.isEmpty() ? b : a);
        StreamKind<Integer> stream = Streams.<Integer>monadPlus(m)
                                      .plus(StreamKind.widen(Stream.of(5)), StreamKind.widen(Stream.of(10)))
                                      .convert(StreamKind::narrowK);
        //Stream.of(5))
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
        
            Higher<C2,StreamKind<T>> identity = ap.unit(StreamKind.widen(Stream.empty()));

            BiFunction<Higher<C2,StreamKind<T>>,Higher<C2,T>,Higher<C2,StreamKind<T>>> combineToStream =   (acc, next) -> ap.apBiFn(ap.unit((a, b) ->StreamInstances.concat(a, StreamKind.just(b))),acc,next);

            BinaryOperator<Higher<C2,StreamKind<T>>> combineStreams = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return StreamInstances.concat(l1,l2);}),a,b); ;

            return ReactiveSeq.fromIterable(stream).reduce(identity,
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
                        .foldLeft(0, (a,b)->a+b, StreamKind.widen(Stream.of(1,2,3,4)));
        
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
        return StreamKind.widen(l1.appendAll(l2));
    }
    private <T> StreamKind<T> of(T value){
        return StreamKind.widen(Stream.of(value));
    }
    private static <T,R> StreamKind<R> ap(StreamKind<Function< T, R>> lt, StreamKind<T> stream){
       return StreamKind.widen(lt.toReactiveSeq().zip(stream,(a, b)->a.apply(b)));
    }
    private static <T,R> Higher<StreamKind.µ,R> flatMap(Higher<StreamKind.µ,T> lt, Function<? super T, ? extends  Higher<StreamKind.µ,R>> fn){
        return StreamKind.widen(StreamKind.narrowK(lt).flatMap(fn.andThen(StreamKind::narrowK)));
    }
    private static <T,R> StreamKind<R> map(StreamKind<T> lt, Function<? super T, ? extends R> fn){
        return StreamKind.widen(lt.map(fn));
    }
    private static <T> StreamKind<T> filter(Higher<StreamKind.µ,T> lt, Predicate<? super T> fn){
        return StreamKind.widen(StreamKind.narrow(lt).filter(fn));
    }
}
