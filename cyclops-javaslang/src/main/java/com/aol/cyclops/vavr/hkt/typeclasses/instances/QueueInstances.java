package com.aol.cyclops.vavr.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.QueueKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.function.Monoid;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import com.aol.cyclops.vavr.FromCyclopsReact;

import javaslang.collection.Queue;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Queues
 * @author johnmcclean
 *
 */
@UtilityClass
public class QueueInstances {

   
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  QueueKind<Integer> list = Queues.functor().map(i->i*2, QueueKind.widen(Arrays.asQueue(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Queues
     * <pre>
     * {@code 
     *   QueueKind<Integer> list = Queues.unit()
                                       .unit("hello")
                                       .then(h->Queues.functor().map((String v) ->v.length(), h))
                                       .convert(QueueKind::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Queues
     */
    public static <T,R>Functor<QueueKind.µ> functor(){
        BiFunction<QueueKind<T>,Function<? super T, ? extends R>,QueueKind<R>> map = QueueInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * QueueKind<String> list = Queues.unit()
                                     .unit("hello")
                                     .convert(QueueKind::narrowK);
        
        //Arrays.asQueue("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Queues
     */
    public static <T> Pure<QueueKind.µ> unit(){
        return General.<QueueKind.µ,T>unit(QueueKind::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueKind.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asQueue;
     * 
       Queues.zippingApplicative()
            .ap(widen(asQueue(l1(this::multiplyByTwo))),widen(asQueue(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * QueueKind<Function<Integer,Integer>> listFn =Queues.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(QueueKind::narrowK);
        
        QueueKind<Integer> list = Queues.unit()
                                      .unit("hello")
                                      .then(h->Queues.functor().map((String v) ->v.length(), h))
                                      .then(h->Queues.zippingApplicative().ap(listFn, h))
                                      .convert(QueueKind::narrowK);
        
        //Arrays.asQueue("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Queues
     */
    public static <T,R> Applicative<QueueKind.µ> zippingApplicative(){
        BiFunction<QueueKind< Function<T, R>>,QueueKind<T>,QueueKind<R>> ap = QueueInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueKind.widen;
     * QueueKind<Integer> list  = Queues.monad()
                                      .flatMap(i->widen(QueueX.range(0,i)), widen(Arrays.asQueue(1,2,3)))
                                      .convert(QueueKind::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    QueueKind<Integer> list = Queues.unit()
                                        .unit("hello")
                                        .then(h->Queues.monad().flatMap((String v) ->Queues.unit().unit(v.length()), h))
                                        .convert(QueueKind::narrowK);
        
        //Arrays.asQueue("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Queues
     */
    public static <T,R> Monad<QueueKind.µ> monad(){
  
        BiFunction<Higher<QueueKind.µ,T>,Function<? super T, ? extends Higher<QueueKind.µ,R>>,Higher<QueueKind.µ,R>> flatMap = QueueInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  QueueKind<String> list = Queues.unit()
                                     .unit("hello")
                                     .then(h->Queues.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueKind::narrowK);
        
       //Arrays.asQueue("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<QueueKind.µ> monadZero(){
        
        return General.monadZero(monad(), QueueKind.widen(Queue.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  QueueKind<Integer> list = Queues.<Integer>monadPlus()
                                      .plus(QueueKind.widen(Arrays.asQueue()), QueueKind.widen(Arrays.asQueue(10)))
                                      .convert(QueueKind::narrowK);
        //Arrays.asQueue(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Queues by concatenation
     */
    public static <T> MonadPlus<QueueKind.µ> monadPlus(){
        Monoid<QueueKind<T>> m = Monoid.of(QueueKind.widen(Queue.empty()), QueueInstances::concat);
        Monoid<Higher<QueueKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<QueueKind<Integer>> m = Monoid.of(QueueKind.widen(Arrays.asQueue()), (a,b)->a.isEmpty() ? b : a);
        QueueKind<Integer> list = Queues.<Integer>monadPlus(m)
                                      .plus(QueueKind.widen(Arrays.asQueue(5)), QueueKind.widen(Arrays.asQueue(10)))
                                      .convert(QueueKind::narrowK);
        //Arrays.asQueue(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Queues
     * @return Type class for combining Queues
     */
    public static <T> MonadPlus<QueueKind.µ> monadPlus(Monoid<QueueKind<T>> m){
        Monoid<Higher<QueueKind.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<QueueKind.µ> traverse(){
     
        BiFunction<Applicative<C2>,QueueKind<Higher<C2, T>>,Higher<C2, QueueKind<T>>> sequenceFn = (ap, list) -> {
        
            Higher<C2,QueueKind<T>> identity = ap.unit(QueueKind.widen(Queue.empty()));

            BiFunction<Higher<C2,QueueKind<T>>,Higher<C2,T>,Higher<C2,QueueKind<T>>> combineToQueue =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> QueueKind.widen(QueueKind.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,QueueKind<T>>> combineQueues = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> QueueKind.widen(QueueKind.narrow(l1).appendAll(l2))),a,b); ;

            return ReactiveSeq.fromIterable(QueueKind.narrow(list))
                      .reduce(identity,
                              combineToQueue,
                              combineQueues);  

   
        };
        BiFunction<Applicative<C2>,Higher<QueueKind.µ,Higher<C2, T>>,Higher<C2, Higher<QueueKind.µ,T>>> sequenceNarrow  =
                                                        (a,b) -> QueueKind.widen2(sequenceFn.apply(a, QueueKind.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Queues.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueKind.widen(Arrays.asQueue(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<QueueKind.µ> foldable(){
        BiFunction<Monoid<T>,Higher<QueueKind.µ,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromIterable(QueueKind.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<QueueKind.µ,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromIterable(QueueKind.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> QueueKind<T> concat(QueueKind<T> l1, QueueKind<T> l2){

        return QueueKind.widen(l1.appendAll(QueueKind.narrow(l2)));
       
    }
    
    private static <T,R> QueueKind<R> ap(QueueKind<Function< T, R>> lt, QueueKind<T> list){
        return QueueKind.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a, b)->a.apply(b))).toQueue());
    }
    private static <T,R> Higher<QueueKind.µ,R> flatMap(Higher<QueueKind.µ,T> lt, Function<? super T, ? extends  Higher<QueueKind.µ,R>> fn){
        return QueueKind.widen(QueueKind.narrow(lt).flatMap(fn.andThen(QueueKind::narrow)));
    }
    private static <T,R> QueueKind<R> map(QueueKind<T> lt, Function<? super T, ? extends R> fn){
        return QueueKind.widen(QueueKind.narrow(lt).map(in->fn.apply(in)));
    }
}
