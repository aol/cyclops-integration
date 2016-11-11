package com.aol.cyclops.hkt.instances.jdk;


import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aol.cyclops.CyclopsCollectors;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.QueueX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.QueueType;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;

import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Queues
 * @author johnmcclean
 *
 */
@UtilityClass
public class Queues {

    public static void main(String[] args){
        Queue<Integer> small = QueueX.of(1,2,3);
        QueueType<Integer> queue = Queues.functor()
                                     .map(i->i*2, QueueType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->QueueType.widen(QueueX.of(1)), h))
                                     .convert(QueueType::narrowK);
          QueueType<Integer> string = queue.convert(QueueType::narrowK);
                    
        System.out.println(Queues.functor().map(i->i*2, QueueType.widen(small)));
    }
    /**
     * 
     * Transform a queue, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  QueueType<Integer> queue = Queues.functor().map(i->i*2, QueueType.widen(QueueX.of(1,2,3));
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
     *   QueueType<Integer> queue = Queues.unit()
                                       .unit("hello")
                                       .then(h->Queues.functor().map((String v) ->v.length(), h))
                                       .convert(QueueType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Queues
     */
    public static <T,R>Functor<QueueType.µ> functor(){
        BiFunction<QueueType<T>,Function<? super T, ? extends R>,QueueType<R>> map = Queues::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * QueueType<String> queue = Queues.unit()
                                     .unit("hello")
                                     .convert(QueueType::narrowK);
        
        //QueueX.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Queues
     */
    public static Unit<QueueType.µ> unit(){
        return General.unit(Queues::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.QueueX.of;
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
     * QueueType<Function<Integer,Integer>> queueFn =Queues.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(QueueType::narrowK);
        
        QueueType<Integer> queue = Queues.unit()
                                      .unit("hello")
                                      .then(h->Queues.functor().map((String v) ->v.length(), h))
                                      .then(h->Queues.zippingApplicative().ap(queueFn, h))
                                      .convert(QueueType::narrowK);
        
        //QueueX.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Queues
     */
    public static <T,R> Applicative<QueueType.µ> zippingApplicative(){
        BiFunction<QueueType< Function<T, R>>,QueueType<T>,QueueType<R>> ap = Queues::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueType.widen;
     * QueueType<Integer> queue  = Queues.monad()
                                      .flatMap(i->widen(QueueX.range(0,i)), widen(QueueX.of(1,2,3)))
                                      .convert(QueueType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    QueueType<Integer> queue = Queues.unit()
                                        .unit("hello")
                                        .then(h->Queues.monad().flatMap((String v) ->Queues.unit().unit(v.length()), h))
                                        .convert(QueueType::narrowK);
        
        //QueueX.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Queues
     */
    public static <T,R> Monad<QueueType.µ> monad(){
  
        BiFunction<Higher<QueueType.µ,T>,Function<? super T, ? extends Higher<QueueType.µ,R>>,Higher<QueueType.µ,R>> flatMap = Queues::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  QueueType<String> queue = Queues.unit()
                                     .unit("hello")
                                     .then(h->Queues.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
       //QueueX.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<QueueType.µ> monadZero(){
        
        return General.monadZero(monad(), QueueType.widen(QueueX.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  QueueType<Integer> queue = Queues.<Integer>monadPlus()
                                      .plus(QueueType.widen(QueueX.of()), QueueType.widen(QueueX.of(10)))
                                      .convert(QueueType::narrowK);
        //QueueX.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Queues by concatenation
     */
    public static <T> MonadPlus<QueueType.µ,T> monadPlus(){
        Monoid<QueueType<T>> m = Monoid.of(QueueType.widen(QueueX.empty()), Queues::concat);
        Monoid<Higher<QueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<QueueType<Integer>> m = Monoid.of(QueueType.widen(QueueX.of()), (a,b)->a.isEmpty() ? b : a);
        QueueType<Integer> queue = Queues.<Integer>monadPlus(m)
                                      .plus(QueueType.widen(QueueX.of(5)), QueueType.widen(QueueX.of(10)))
                                      .convert(QueueType::narrowK);
        //QueueX.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Queues
     * @return Type class for combining Queues
     */
    public static <T> MonadPlus<QueueType.µ,T> monadPlus(Monoid<QueueType<T>> m){
        Monoid<Higher<QueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<QueueType.µ> traverse(){
        BiFunction<Applicative<C2>,QueueType<Higher<C2, T>>,Higher<C2, QueueType<T>>> sequenceFn = (ap,queue) -> {
        
            Higher<C2,QueueType<T>> identity = ap.unit(QueueType.widen(QueueX.of()));

            BiFunction<Higher<C2,QueueType<T>>,Higher<C2,T>,Higher<C2,QueueType<T>>> combineToQueue =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> { a.add(b); return a;}),acc,next);

            BinaryOperator<Higher<C2,QueueType<T>>> combineQueues = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { l1.addAll(l2); return l1;}),a,b); ;  

            return queue.stream()
                      .reduce(identity,
                              combineToQueue,
                              combineQueues);  

   
        };
        BiFunction<Applicative<C2>,Higher<QueueType.µ,Higher<C2, T>>,Higher<C2, Higher<QueueType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> QueueType.widen2(sequenceFn.apply(a, QueueType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Queues.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueType.widen(QueueX.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<QueueType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<QueueType.µ,T>,T> foldRightFn =  (m,l)-> QueueX.fromIterable(QueueType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<QueueType.µ,T>,T> foldLeftFn = (m,l)-> QueueX.fromIterable(QueueType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> QueueType<T> concat(Queue<T> l1, Queue<T> l2){
        return QueueType.widen(Stream.concat(l1.stream(),l2.stream()).collect(CyclopsCollectors.toQueueX()));
    }
    private <T> QueueType<T> of(T value){
        return QueueType.widen(QueueX.of(value));
    }
    private static <T,R> QueueType<R> ap(QueueType<Function< T, R>> lt,  QueueType<T> queue){
        return QueueType.widen(QueueX.fromIterable(lt).zip(queue,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<QueueType.µ,R> flatMap( Higher<QueueType.µ,T> lt, Function<? super T, ? extends  Higher<QueueType.µ,R>> fn){
        return QueueType.widen(QueueX.fromIterable(QueueType.narrowK(lt)).flatMap(fn.andThen(QueueType::narrowK)));
    }
    private static <T,R> QueueType<R> map(QueueType<T> lt, Function<? super T, ? extends R> fn){
        return QueueType.widen(QueueX.fromIterable(lt).map(fn));
    }
}
