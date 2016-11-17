package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.functionaljava.hkt.StreamType;
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

import fj.data.Stream;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Streams
 * @author johnmcclean
 *
 */
@UtilityClass
public class StreamInstances {

    public static void main(String[] args){
        Stream<Integer> small = Stream.stream(1,2,3);
        StreamType<Integer> stream = StreamInstances.functor()
                                     .map(i->i*2, StreamType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->StreamType.widen(Stream.stream(1)), h))
                                     .convert(StreamType::narrowK);
          StreamType<Integer> string = stream.convert(StreamType::narrowK);
                    
        System.out.println(StreamInstances.functor().map(i->i*2, StreamType.widen(small)));
    }
    /**
     * 
     * Transform a stream, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  StreamType<Integer> stream = Streams.functor().map(i->i*2, StreamType.widen(Arrays.asStream(1,2,3));
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
        
        //Arrays.asStream("hello"))
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
     * StreamType<Function<Integer,Integer>> streamFn =Streams.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(StreamType::narrowK);
        
        StreamType<Integer> stream = Streams.unit()
                                      .unit("hello")
                                      .then(h->Streams.functor().map((String v) ->v.length(), h))
                                      .then(h->Streams.zippingApplicative().ap(streamFn, h))
                                      .convert(StreamType::narrowK);
        
        //Arrays.asStream("hello".length()*2))
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
                                      .flatMap(i->widen(StreamX.range(0,i)), widen(Arrays.asStream(1,2,3)))
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
        
        //Arrays.asStream("hello".length())
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
        
       //Arrays.asStream("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<StreamType.µ> monadZero(){
        
        return General.monadZero(monad(), StreamType.widen(Stream.stream()));
    }
    /**
     * <pre>
     * {@code 
     *  StreamType<Integer> stream = Streams.<Integer>monadPlus()
                                      .plus(StreamType.widen(Arrays.asStream()), StreamType.widen(Arrays.asStream(10)))
                                      .convert(StreamType::narrowK);
        //Arrays.asStream(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Streams by concatenation
     */
    public static <T> MonadPlus<StreamType.µ,T> monadPlus(){
        Monoid<StreamType<T>> m = Monoid.of(StreamType.widen(Stream.stream()), StreamInstances::concat);
        Monoid<Higher<StreamType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<StreamType<Integer>> m = Monoid.of(StreamType.widen(Arrays.asStream()), (a,b)->a.isEmpty() ? b : a);
        StreamType<Integer> stream = Streams.<Integer>monadPlus(m)
                                      .plus(StreamType.widen(Arrays.asStream(5)), StreamType.widen(Arrays.asStream(10)))
                                      .convert(StreamType::narrowK);
        //Arrays.asStream(5))
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
        
            Higher<C2,StreamType<T>> identity = ap.unit(StreamType.widen(Stream.stream()));

            BiFunction<Higher<C2,StreamType<T>>,Higher<C2,T>,Higher<C2,StreamType<T>>> combineToStream =   
                    (acc,next) -> ap.apBiFn(ap.unit((a,b) -> StreamType.widen(StreamType.narrow(a).cons(b))), acc,next);

            BinaryOperator<Higher<C2,StreamType<T>>> combineStreams = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> StreamType.widen(StreamType.narrow(l1).append(StreamType.narrow(l2)))),a,b); ;  
           
            return ReactiveSeq.fromIterable(StreamType.narrow(stream))
                      .reduce(identity,
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
                        .foldLeft(0, (a,b)->a+b, StreamType.widen(Arrays.asStream(1,2,3,4)));
        
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
        return StreamType.widen(l1.append(StreamType.narrow(l2)));
       
    }
    private <T> StreamType<T> of(T value){
        return StreamType.widen(Stream.stream(value));
    }
    private static <T,R> StreamType<R> ap(StreamType<Function< T, R>> lt,  StreamType<T> stream){
        
        return StreamType.widen(lt.zipWith(stream.narrow(),(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<StreamType.µ,R> flatMap( Higher<StreamType.µ,T> lt, Function<? super T, ? extends  Higher<StreamType.µ,R>> fn){
        return StreamType.widen(StreamType.narrow(lt).bind(in->fn.andThen(StreamType::narrow).apply(in)));
    }
    private static <T,R> StreamType<R> map(StreamType<T> lt, Function<? super T, ? extends R> fn){
        return StreamType.widen(StreamType.narrow(lt).map(in->fn.apply(in)));
    }
}
