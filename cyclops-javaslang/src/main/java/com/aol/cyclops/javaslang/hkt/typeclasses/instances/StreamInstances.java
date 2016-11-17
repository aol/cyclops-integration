package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
import com.aol.cyclops.javaslang.hkt.StreamType;

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
     *  StreamType<Integer> stream = Streams.functor().map(i->i*2, StreamType.widen(Stream.of(1,2,3));
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
     *   StreamType<Integer> stream = Streams.unit()
                                       .unit("hello")
                                       .then(h->Streams.functor().map((String v) ->v.length(), h))
                                       .convert(StreamType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Streams
     */
    public static <T,R>Functor<StreamType.µ> functor(){
        BiFunction<StreamType<T>,Function<? super T, ? extends R>,StreamType<R>> map = StreamInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * StreamType<String> stream = Streams.unit()
                                     .unit("hello")
                                     .convert(StreamType::narrowK);
        
        //Stream.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Streams
     */
    public static Unit<StreamType.µ> unit(){
        return General.unit(StreamInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.StreamType.widen;
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
     * StreamType<Function<Integer,Integer>> streamFn =Streams.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(StreamType::narrowK);
        
        StreamType<Integer> stream = Streams.unit()
                                      .unit("hello")
                                      .then(h->Streams.functor().map((String v) ->v.length(), h))
                                      .then(h->Streams.zippingApplicative().ap(streamFn, h))
                                      .convert(StreamType::narrowK);
        
        //Stream.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Streams
     */
    public static <T,R> Applicative<StreamType.µ> zippingApplicative(){
        BiFunction<StreamType< Function<T, R>>,StreamType<T>,StreamType<R>> ap = StreamInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.StreamType.widen;
     * StreamType<Integer> stream  = Streams.monad()
                                      .flatMap(i->widen(StreamX.range(0,i)), widen(Stream.of(1,2,3)))
                                      .convert(StreamType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    StreamType<Integer> stream = Streams.unit()
                                        .unit("hello")
                                        .then(h->Streams.monad().flatMap((String v) ->Streams.unit().unit(v.length()), h))
                                        .convert(StreamType::narrowK);
        
        //Stream.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Streams
     */
    public static <T,R> Monad<StreamType.µ> monad(){
  
        BiFunction<Higher<StreamType.µ,T>,Function<? super T, ? extends Higher<StreamType.µ,R>>,Higher<StreamType.µ,R>> flatMap = StreamInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  StreamType<String> stream = Streams.unit()
                                         .unit("hello")
                                         .then(h->Streams.monadZero().filter((String t)->t.startsWith("he"), h))
                                         .convert(StreamType::narrowK);
        
       //Stream.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<StreamType.µ> monadZero(){
        BiFunction<Higher<StreamType.µ,T>,Predicate<? super T>,Higher<StreamType.µ,T>> filter = StreamInstances::filter;
        Supplier<Higher<StreamType.µ, T>> zero = ()->StreamType.widen(Stream.empty());
        return General.<StreamType.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  StreamType<Integer> stream = Streams.<Integer>monadPlus()
                                      .plus(StreamType.widen(Stream.of()), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        //Stream.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Streams by concatenation
     */
    public static <T> MonadPlus<StreamType.µ,T> monadPlus(){
        Monoid<StreamType<T>> m = Monoid.of(StreamType.widen(Stream.<T>empty()), StreamInstances::concat);
        Monoid<Higher<StreamType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<StreamType<Integer>> m = Monoid.of(StreamType.widen(Stream.of()), (a,b)->a.isEmpty() ? b : a);
        StreamType<Integer> stream = Streams.<Integer>monadPlus(m)
                                      .plus(StreamType.widen(Stream.of(5)), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        //Stream.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Streams
     * @return Type class for combining Streams
     */
    public static <T> MonadPlus<StreamType.µ,T> monadPlus(Monoid<StreamType<T>> m){
        Monoid<Higher<StreamType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<StreamType.µ> traverse(){
        BiFunction<Applicative<C2>,StreamType<Higher<C2, T>>,Higher<C2, StreamType<T>>> sequenceFn = (ap,stream) -> {
        
            Higher<C2,StreamType<T>> identity = ap.unit(StreamType.widen(Stream.empty()));

            BiFunction<Higher<C2,StreamType<T>>,Higher<C2,T>,Higher<C2,StreamType<T>>> combineToStream =   (acc,next) -> ap.apBiFn(ap.unit((a,b) ->StreamInstances.concat(a,StreamType.just(b))),acc,next);

            BinaryOperator<Higher<C2,StreamType<T>>> combineStreams = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { return StreamInstances.concat(l1,l2);}),a,b); ;  

            return ReactiveSeq.fromPublisher(stream).reduce(identity,
                                                            combineToStream,
                                                            combineStreams);  

   
        };
        BiFunction<Applicative<C2>,Higher<StreamType.µ,Higher<C2, T>>,Higher<C2, Higher<StreamType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> StreamType.widen2(sequenceFn.apply(a, StreamType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Streams.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamType.widen(Stream.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<StreamType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<StreamType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromIterable(StreamType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<StreamType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromIterable(StreamType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> StreamType<T> concat(StreamType<T> l1, StreamType<T> l2){
        return StreamType.widen(l1.appendAll(l2));
    }
    private <T> StreamType<T> of(T value){
        return StreamType.widen(Stream.of(value));
    }
    private static <T,R> StreamType<R> ap(StreamType<Function< T, R>> lt,  StreamType<T> stream){
       return StreamType.widen(lt.toReactiveSeq().zip(stream,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<StreamType.µ,R> flatMap( Higher<StreamType.µ,T> lt, Function<? super T, ? extends  Higher<StreamType.µ,R>> fn){
        return StreamType.widen(StreamType.narrowK(lt).flatMap(fn.andThen(StreamType::narrowK)));
    }
    private static <T,R> StreamType<R> map(StreamType<T> lt, Function<? super T, ? extends R> fn){
        return StreamType.widen(lt.map(fn));
    }
    private static <T> StreamType<T> filter(Higher<StreamType.µ,T> lt, Predicate<? super T> fn){
        return StreamType.widen(StreamType.narrow(lt).filter(fn));
    }
}
